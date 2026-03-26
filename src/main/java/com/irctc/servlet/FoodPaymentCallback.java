package com.irctc.servlet;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.irctc.dao.DBConnection;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/FoodPaymentCallback")
public class FoodPaymentCallback extends HttpServlet {

    // 🔑 PASTE YOUR STRIPE SECRET KEY HERE
    private static final String STRIPE_SECRET_KEY = "YOUR_STRIPE_SECRET_KEY";

    @Override
    public void init() {
        Stripe.apiKey = STRIPE_SECRET_KEY;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String sessionId = request.getParameter("session_id");
        HttpSession session = request.getSession(false);
        
        if (session == null || sessionId == null) {
            response.sendRedirect("jsp/home.jsp");
            return;
        }

        try {
            // 1. Verify Payment
            Session stripeSession = Session.retrieve(sessionId);
            if ("paid".equals(stripeSession.getPaymentStatus())) {
                
                // 2. Get Data
                String pnr = (String) session.getAttribute("food_pnr");
                String name = (String) session.getAttribute("food_contact_name");
                String phone = (String) session.getAttribute("food_contact_phone");
                String items = (String) session.getAttribute("food_items_json");
                Double amount = (Double) session.getAttribute("food_total_amount");
                
                // 3. Save to DB
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "INSERT INTO food_orders (booking_id, contact_name, contact_phone, total_amount, items_json, payment_id, order_status) VALUES (?, ?, ?, ?, ?, ?, 'PLACED')";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, Integer.parseInt(pnr)); 
                        ps.setString(2, name);
                        ps.setString(3, phone);
                        ps.setDouble(4, amount);
                        ps.setString(5, items);
                        ps.setString(6, stripeSession.getPaymentIntent());
                        ps.executeUpdate();
                    }
                }

                // --- THE FIX IS HERE ---
                // Redirect to the SERVLET (The Brain), not the JSP (The View).
                // This ensures the menu is re-loaded correctly.
                response.sendRedirect(request.getContextPath() + "/FoodOrderingServlet?pnr=" + pnr + "&status=success");
                // -----------------------
                
            } else {
                // Redirect to Servlet on error too, so menu loads
                String pnr = (String) session.getAttribute("food_pnr");
                response.sendRedirect(request.getContextPath() + "/FoodOrderingServlet?pnr=" + pnr + "&error=payment_failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/jsp/home.jsp");
        }
    }
}