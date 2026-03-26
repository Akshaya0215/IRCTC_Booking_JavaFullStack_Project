package com.irctc.servlet;

import com.irctc.dao.BookingDAO;
import com.irctc.dao.PassengerDAO;
import com.irctc.dao.PaymentDAO;
import com.irctc.dao.TrainDAO;
import com.irctc.dao.impl.BookingDAOImpl;
import com.irctc.dao.impl.PassengerDAOImpl;
import com.irctc.dao.impl.PaymentDAOImpl;
import com.irctc.dao.impl.TrainDAOImpl;
import com.irctc.model.Booking;
import com.irctc.model.Passenger;
import com.irctc.model.Payment;
import com.irctc.model.TrainStop;
import com.irctc.model.User;
import com.irctc.util.PdfTicketUtil;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

@WebServlet("/DownloadTicketServlet")
public class DownloadTicketServlet extends HttpServlet {

    private BookingDAO bookingDAO = new BookingDAOImpl();
    private PassengerDAO passengerDAO = new PassengerDAOImpl();
    private PaymentDAO paymentDAO = new PaymentDAOImpl();
    private TrainDAO trainDAO = new TrainDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // 1. --- Security Check: User Logged In? ---
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }
        User user = (User) session.getAttribute("loggedInUser");

        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));

            // 2. Fetch all required data
            Booking booking = bookingDAO.getBookingById(bookingId);
            
            // 3. --- Security Check: User owns this booking? ---
            if (booking == null || booking.getUserId() != user.getUserId()) {
                session.setAttribute("errorMessage", "Booking not found or you do not have permission to view it.");
                response.sendRedirect(request.getContextPath() + "/BookingHistoryServlet");
                return;
            }

            // 4. Get all other associated data
            List<Passenger> passengers = passengerDAO.getPassengersByBookingId(bookingId);
            Payment payment = paymentDAO.getPaymentByBookingId(bookingId);
            
            // 5. Get the segment-specific times from train_stops
            TrainStop sourceStop = trainDAO.getStopDetails(booking.getTrainId(), booking.getSegmentSource());
            TrainStop destStop = trainDAO.getStopDetails(booking.getTrainId(), booking.getSegmentDestination());
            
            if (passengers == null || payment == null || sourceStop == null || destStop == null) {
                throw new ServletException("Could not retrieve all booking details to generate PDF.");
            }

            // Add passengers to the booking object
            booking.setPassengersList(passengers);
            
            // 6. Generate the PDF
            ServletContext servletContext = getServletContext();
            byte[] ticketPdf = PdfTicketUtil.generateTicketPdf(
                booking, 
                payment, 
                servletContext, 
                booking.getSegmentSource(), 
                booking.getSegmentDestination(), 
                sourceStop.getDepartureTime(), 
                destStop.getArrivalTime()
            );

            if (ticketPdf == null) {
                throw new ServletException("PDF generation failed.");
            }

            // 7. Set response headers to trigger download
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"IRCTC_Ticket_PNR_" + bookingId + ".pdf\"");
            response.setContentLength(ticketPdf.length);

            // 8. Write the PDF byte array to the response output stream
            OutputStream os = response.getOutputStream();
            os.write(ticketPdf);
            os.flush();
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error downloading ticket: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/BookingHistoryServlet");
        }
    }
}