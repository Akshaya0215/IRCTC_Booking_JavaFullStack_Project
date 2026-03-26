package com.irctc.servlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.irctc.dao.BookingDAO;
import com.irctc.dao.PassengerDAO;
import com.irctc.dao.impl.BookingDAOImpl;
import com.irctc.dao.impl.PassengerDAOImpl;
import com.irctc.model.Booking;
import com.irctc.model.Passenger;
import com.irctc.service.TrainService;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/PublicPnrServlet")
public class PublicPnrServlet extends HttpServlet {

    private BookingDAO bookingDAO = new BookingDAOImpl();
    private PassengerDAO passengerDAO = new PassengerDAOImpl();
    private TrainService trainService = new TrainService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pnr = request.getParameter("pnr");

        if (pnr == null || pnr.trim().isEmpty()) {
            response.sendRedirect("jsp/pnrStatus.jsp");
            return;
        }

        // 1. Strategy A: Check Local Database (Internal Bookings)
        try {
            // Local PNRs are short integers (e.g., 56, 101)
            if (pnr.length() < 10) {
                int localPnr = Integer.parseInt(pnr);
                Booking localBooking = bookingDAO.getBookingById(localPnr);

                // --- FIX: Hide PENDING bookings (Abandoned/Failed payments) ---
                // We only show CONFIRMED or CANCELLED tickets.
                if (localBooking != null && !"PENDING".equalsIgnoreCase(localBooking.getStatus())) {
                    
                    // Fetch Passengers
                    List<Passenger> passengers = passengerDAO.getPassengersByBookingId(localPnr);
                    localBooking.setPassengersList(passengers);

                    request.setAttribute("sourceType", "LOCAL");
                    request.setAttribute("booking", localBooking);
                    request.getRequestDispatcher("jsp/pnrResult.jsp").forward(request, response);
                    return;
                } else {
                    // If it exists but is PENDING, or doesn't exist at all -> Not Found
                    request.setAttribute("errorMessage", "PNR not found.");
                    request.getRequestDispatcher("jsp/pnrStatus.jsp").forward(request, response);
                    return;
                }
            }
        } catch (NumberFormatException e) {
            // Not a number, ignore and fall through to API check
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Strategy B: Check External API (Real World 10-digit PNRs)
        if (pnr.length() == 10) {
            JsonObject apiResponse = trainService.getPnrStatus(pnr);
            
            boolean isSuccess = false;
            if (apiResponse != null) {
                if (apiResponse.has("success")) isSuccess = apiResponse.get("success").getAsBoolean();
                else if (apiResponse.has("status")) isSuccess = apiResponse.get("status").getAsBoolean();
            }

            if (isSuccess && apiResponse.has("data")) {
                request.setAttribute("sourceType", "API");
                
                JsonObject data = apiResponse.getAsJsonObject("data");
                request.setAttribute("apiData", data);

                // Convert JsonArray to Java List for JSP
                List<JsonObject> passengerList = new ArrayList<>();
                if (data.has("passengers")) {
                    JsonArray jsonArr = data.getAsJsonArray("passengers");
                    for (JsonElement e : jsonArr) {
                        passengerList.add(e.getAsJsonObject());
                    }
                }
                request.setAttribute("passengerList", passengerList); 

                request.getRequestDispatcher("jsp/pnrResult.jsp").forward(request, response);
                return;
            } else {
                request.setAttribute("errorMessage", "PNR not found.");
            }
        } else {
             request.setAttribute("errorMessage", "Invalid PNR Format. Use 10 digits for real trains.");
        }

        request.getRequestDispatcher("jsp/pnrStatus.jsp").forward(request, response);
    }
}