package com.irctc.servlet.admin;

import com.irctc.dao.DBConnection;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/AdminActionServlet")
public class AdminActionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. Security Check: Ensure Admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInAdmin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/adminLogin.jsp");
            return;
        }

        // 2. Capture parameters
        String action = request.getParameter("action");
        String id = request.getParameter("id"); // This can be userId, trainId, or bookingId depending on action
        
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "";

            switch (action) {
                case "BLOCK_USER":
                    // Prevent user from logging in
                    sql = "UPDATE users SET is_blocked = TRUE WHERE user_id = ?";
                    break;
                    
                case "UNBLOCK_USER":
                    // Restore access
                    sql = "UPDATE users SET is_blocked = FALSE WHERE user_id = ?";
                    break;
                    
                case "DELETE_USER":
                    // Permanently remove user. 
                    // Note: Because we added ON DELETE CASCADE in SQL, this also deletes their bookings.
                    sql = "DELETE FROM users WHERE user_id = ?";
                    break;

                case "FORCE_CANCEL_BOOKING":
                    // Admin override to cancel any booking immediately
                    sql = "UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ?";
                    break;
                    
                case "DELETE_TRAIN":
                    // Delete a train (Only works if no active bookings exist, or if you added cascade)
                    sql = "DELETE FROM trains WHERE train_id = ?";
                    break;
            }

            // 3. Execute the Action
            if (!sql.isEmpty() && id != null) {
                ps = conn.prepareStatement(sql);
                ps.setString(1, id);
                int rows = ps.executeUpdate();
                System.out.println("GOD MODE ACTION [" + action + "] on ID [" + id + "]: " + (rows > 0 ? "Success" : "Failed"));
            }

            // 4. Redirect back to the page they came from (Instant Refresh)
            String referer = request.getHeader("Referer");
            response.sendRedirect(referer != null ? referer : request.getContextPath() + "/admin/dashboard.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            // In case of foreign key constraint error (e.g., trying to delete a train with active bookings)
            response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp?error=ActionFailed");
        } finally {
            try { if (ps != null) ps.close(); if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}