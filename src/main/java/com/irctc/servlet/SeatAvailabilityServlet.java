package com.irctc.servlet;

import com.google.gson.Gson;
import com.irctc.dao.BookingDAO;
import com.irctc.dao.impl.BookingDAOImpl;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/SeatAvailabilityServlet")
public class SeatAvailabilityServlet extends HttpServlet {

    private BookingDAO bookingDAO = new BookingDAOImpl();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        try {
            // 1. Get parameters from the AJAX request
            String trainIdStr = request.getParameter("trainId");
            String dateStr = request.getParameter("date");
            String coachCode = request.getParameter("coach"); // e.g., "S1"

            if (trainIdStr == null || dateStr == null || coachCode == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int trainId = Integer.parseInt(trainIdStr);
            Date journeyDate = Date.valueOf(dateStr);

            // 2. Fetch the list of booked seat numbers
            List<Integer> bookedSeats = bookingDAO.getBookedSeats(trainId, journeyDate, coachCode);

            // 3. Return as JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(gson.toJson(bookedSeats));

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}