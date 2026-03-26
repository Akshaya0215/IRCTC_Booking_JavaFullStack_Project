package com.irctc.servlet.admin;

import com.irctc.dao.BookingDAO;
import com.irctc.dao.impl.BookingDAOImpl;
import com.irctc.model.Booking;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/viewBookings") // Maps to the link in dashboard.jsp
public class ViewBookingsServlet extends HttpServlet {

    private BookingDAO bookingDAO = new BookingDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // --- Security Check ---
        if (session == null || session.getAttribute("loggedInAdmin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/adminLogin.jsp");
            return;
        }

        try {
            // 1. Get all bookings from the DAO
            // (The DAOImpl's getAllBookings() already joins with trains and users)
            List<Booking> bookingList = bookingDAO.getAllBookings();

            // 2. Set the list as an attribute for the JSP
            request.setAttribute("bookingList", bookingList);

            // 3. Forward to the JSP page
            RequestDispatcher rd = request.getRequestDispatcher("/admin/viewBookings.jsp");
            rd.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
            request.setAttribute("adminErrorMessage", "Database error fetching bookings: " + e.getMessage());
            // Forward back to the dashboard with an error
            RequestDispatcher rd = request.getRequestDispatcher("/admin/dashboard.jsp");
            rd.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // This page is view-only, so just call doGet
        doGet(request, response);
    }
}