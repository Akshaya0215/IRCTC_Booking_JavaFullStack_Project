package com.irctc.servlet;

import com.irctc.dao.BookingDAO;
import com.irctc.dao.impl.BookingDAOImpl;
import com.irctc.model.Booking;
import com.irctc.model.User;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet to fetch and display a user's booking history.
 * This maps to the "My Bookings" link in the navbar.
 */
@WebServlet("/BookingHistoryServlet")
public class BookingHistoryServlet extends HttpServlet {
    
    private BookingDAO bookingDAO = new BookingDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);

        // 1. Check if user is logged in
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp"); // Not logged in
            return;
        }

        User user = (User) session.getAttribute("loggedInUser");

        try {
            // 2. Fetch all bookings for this user from the DAO
            // (Our DAOImpl's getBookingsByUserId already joins with the trains table)
            List<Booking> bookingList = bookingDAO.getBookingsByUserId(user.getUserId());

            // 3. Set the list as an attribute for the JSP
            request.setAttribute("bookingList", bookingList);

            // 4. Forward to the new bookingHistory.jsp page
            RequestDispatcher rd = request.getRequestDispatcher("jsp/bookingHistory.jsp");
            rd.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
            request.setAttribute("errorMessage", "Could not retrieve booking history: " + e.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("jsp/home.jsp"); // Send back to home
            rd.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}