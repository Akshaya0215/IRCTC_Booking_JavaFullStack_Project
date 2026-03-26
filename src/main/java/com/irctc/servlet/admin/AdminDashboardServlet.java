package com.irctc.servlet.admin;

import com.irctc.dao.DBConnection;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);

        // 1. Security Check
        if (session == null || session.getAttribute("loggedInAdmin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/adminLogin.jsp");
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            
            // --- 2. CALCULATE STATS ---
            
            // Total Revenue (Sum of all CONFIRMED bookings)
            double totalRevenue = getRevenue(conn);
            
            // Total Users
            int totalUsers = getCount(conn, "SELECT COUNT(*) FROM users");
            
            // Blocked Users
            int blockedUsers = getCount(conn, "SELECT COUNT(*) FROM users WHERE is_blocked = TRUE");
            
            // Active Trains
            int activeTrains = getCount(conn, "SELECT COUNT(*) FROM trains");

            // --- 3. SEND TO JSP ---
            request.setAttribute("stat_revenue", totalRevenue);
            request.setAttribute("stat_users", totalUsers);
            request.setAttribute("stat_blocked", blockedUsers);
            request.setAttribute("stat_trains", activeTrains);

            // 4. Forward to Dashboard
            RequestDispatcher rd = request.getRequestDispatcher("/admin/dashboard.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Redirect to login on DB error
            response.sendRedirect(request.getContextPath() + "/admin/adminLogin.jsp?error=DBError");
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // Helper: Run Count Queries
    private int getCount(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    // Helper: Run Revenue Query
    private double getRevenue(Connection conn) throws SQLException {
        String sql = "SELECT SUM(total_amount) FROM bookings WHERE status = 'CONFIRMED'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}