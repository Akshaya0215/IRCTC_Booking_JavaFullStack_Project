package com.irctc.servlet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.irctc.dao.DBConnection; // To fetch prices

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Type;

@WebServlet("/FoodStripeSessionServlet")
public class FoodStripeSessionServlet extends HttpServlet {

    // 🔑 PASTE YOUR STRIPE SECRET KEY HERE
   private static final String STRIPE_SECRET_KEY = "YOUR_STRIPE_SECRET_KEY";
    @Override
    public void init() {
        Stripe.apiKey = STRIPE_SECRET_KEY;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. Get Parameters
        String pnr = request.getParameter("pnr");
        String contactName = request.getParameter("contactName");
        String contactPhone = request.getParameter("contactPhone");
        String cartJson = request.getParameter("cartJson"); // {"1": 2, "5": 1} (ItemId: Qty)

        try {
            // 2. Calculate Total Amount Securely (Fetch prices from DB)
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Integer>>(){}.getType();
            Map<String, Integer> cart = gson.fromJson(cartJson, type);
            
            double totalAmount = 0;
            StringBuilder descriptionBuilder = new StringBuilder();

            try (Connection conn = DBConnection.getConnection()) {
                for (Map.Entry<String, Integer> entry : cart.entrySet()) {
                    int itemId = Integer.parseInt(entry.getKey());
                    int qty = entry.getValue();
                    
                    if(qty > 0) {
                        String sql = "SELECT price, item_name FROM food_menu WHERE item_id = ?";
                        try(PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setInt(1, itemId);
                            ResultSet rs = ps.executeQuery();
                            if(rs.next()) {
                                double price = rs.getDouble("price");
                                String name = rs.getString("item_name");
                                totalAmount += (price * qty);
                                descriptionBuilder.append(qty).append("x ").append(name).append(", ");
                            }
                        }
                    }
                }
            }
            
            long amountInPaise = (long) (totalAmount * 100);
            String description = descriptionBuilder.toString();
            if(description.length() > 2) description = description.substring(0, description.length() - 2); // Remove last comma

            // 3. Save Pending Order Data to Session (to retrieve on callback)
            HttpSession session = request.getSession();
            session.setAttribute("food_pnr", pnr);
            session.setAttribute("food_contact_name", contactName);
            session.setAttribute("food_contact_phone", contactPhone);
            session.setAttribute("food_items_json", description);
            session.setAttribute("food_total_amount", totalAmount);

            // 4. Create Stripe Session
            String baseURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            
            SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(baseURL + "/FoodPaymentCallback?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(baseURL + "/jsp/foodOrdering.jsp?pnr=" + pnr + "&error=cancelled")
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("inr")
                                .setUnitAmount(amountInPaise)
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("e-Catering Order (PNR: " + pnr + ")")
                                        .setDescription(description)
                                        .build())
                                .build())
                        .build())
                .build();

            Session stripeSession = Session.create(params);

            // 5. Return URL
            Map<String, String> respData = new HashMap<>();
            respData.put("url", stripeSession.getUrl());
            
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(respData));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}