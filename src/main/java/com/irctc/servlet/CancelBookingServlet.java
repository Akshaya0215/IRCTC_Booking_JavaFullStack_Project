package com.irctc.servlet;

import com.irctc.dao.BookingDAO;
import com.irctc.dao.TrainDAO;
import com.irctc.dao.DBConnection;
import com.irctc.dao.impl.BookingDAOImpl;
import com.irctc.dao.impl.TrainDAOImpl;
import com.irctc.model.Booking;
import com.irctc.model.Train;
import com.irctc.model.User;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/CancelBookingServlet")
public class CancelBookingServlet extends HttpServlet {

    private BookingDAO bookingDAO = new BookingDAOImpl();
    private TrainDAO trainDAO = new TrainDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // We will use GET for simplicity from a link, though POST is also acceptable
        HttpSession session = request.getSession(false);

        // 1. --- Security Check: User Logged In? ---
        if (session == null || session.getAttribute("loggedInUser") == null) {
            request.setAttribute("errorMessage", "Please login to manage your bookings.");
            RequestDispatcher rd = request.getRequestDispatcher("/jsp/login.jsp");
            rd.forward(request, response);
            return;
        }

        User user = (User) session.getAttribute("loggedInUser");
        Connection conn = null;
        int bookingId = 0;

        try {
            bookingId = Integer.parseInt(request.getParameter("bookingId"));

            // --- START CANCELLATION TRANSACTION ---
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 2. Get Booking and Train details (and lock the rows for update)
            Booking booking = bookingDAO.getBookingByIdForUpdate(bookingId, conn);
            
            // 3. --- Security Check: Booking belongs to this user? ---
            if (booking == null || booking.getUserId() != user.getUserId()) {
                throw new ServletException("Booking not found or you do not have permission to cancel it.");
            }
            
            // 4. Check if already cancelled
            if ("CANCELLED".equals(booking.getStatus())) {
                 throw new ServletException("This booking is already cancelled.");
            }
            
            // 5. Get the train to add seats back
            Train train = trainDAO.getTrainByIdForUpdate(booking.getTrainId(), conn);
            if (train == null) {
                 throw new SQLException("Associated train not found.");
            }

            // 6. Update Train's available seats
            int newSeatCount = train.getAvailableSeats() + booking.getPassengers();
            boolean seatsUpdated = trainDAO.updateAvailableSeats(train.getTrainId(), newSeatCount, conn);

            // 7. Update Booking status to CANCELLED
            boolean bookingCancelled = bookingDAO.updateBookingStatus(bookingId, "CANCELLED", conn);

            // 8. Check if all steps succeeded
            if (!seatsUpdated || !bookingCancelled) {
                throw new SQLException("Cancellation failed, a database update failed. Rolling back.");
            }

            // 9. COMMIT TRANSACTION
            conn.commit();
            System.out.println("CancelBookingServlet: Transaction committed for cancellation of booking ID: " + bookingId);
            session.setAttribute("successMessage", "Booking PNR " + bookingId + " has been successfully cancelled.");
            // --- END CANCELLATION TRANSACTION ---

        } catch (Exception e) {
            System.err.println("CancelBookingServlet: Exception during cancellation, attempting rollback.");
            e.printStackTrace();
            try { if (conn != null) { conn.rollback(); } } catch (SQLException se) { se.printStackTrace(); }

            // Send user back to booking history with an error
            session.setAttribute("errorMessage", "Cancellation failed: " + e.getMessage());

        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) { e.printStackTrace(); }
            
            // 10. Redirect back to the booking history page
            response.sendRedirect(request.getContextPath() + "/BookingHistoryServlet");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response); // Handle POST the same as GET
    }
}