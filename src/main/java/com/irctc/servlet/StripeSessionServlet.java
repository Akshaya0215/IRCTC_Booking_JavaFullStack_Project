package com.irctc.servlet;

import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/StripeSessionServlet")
public class StripeSessionServlet extends HttpServlet {

    // 🔴 STEP 1: PASTE YOUR STRIPE SECRET KEY HERE (starts with sk_test_)
    private static final String STRIPE_SECRET_KEY = "YOUR_STRIPE_SECRET_KEY";
    @Override
    public void init() {
        Stripe.apiKey = STRIPE_SECRET_KEY;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("pendingBookingAmount") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            // --- 2. CAPTURE SEAT DATA (Important for your seat map feature) ---
            String selectedSeatsData = request.getParameter("selectedSeatsData");
            
            // Debug Print
            System.out.println("StripeSessionServlet: RAW SEAT DATA -> [" + selectedSeatsData + "]");

            if (selectedSeatsData != null && !selectedSeatsData.trim().isEmpty()) {
                session.setAttribute("pendingSelectedSeats", selectedSeatsData);
            } else {
                session.removeAttribute("pendingSelectedSeats"); // Clear if empty
            }
            // ------------------------------------------------------------------

            // 3. Get Data from Session
            BigDecimal amount = (BigDecimal) session.getAttribute("pendingBookingAmount");
            String trainName = (String) session.getAttribute("pendingTrainName");
            Integer bookingId = (Integer) session.getAttribute("pendingBookingId");
            
            // Stripe expects amount in paise (Long)
            long amountInPaise = amount.multiply(new BigDecimal("100")).longValue();

            // 4. Construct Base URL
            String baseURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

            // 5. Create Checkout Session Params
            SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(baseURL + "/StripeCallbackServlet?session_id={CHECKOUT_SESSION_ID}") 
                .setCancelUrl(baseURL + "/jsp/payment.jsp?error=cancelled")
                .setClientReferenceId(String.valueOf(bookingId)) 
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("inr")
                                .setUnitAmount(amountInPaise)
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Train Booking #" + bookingId)
                                        .setDescription("Ticket for " + trainName)
                                        .build())
                                .build())
                        .build())
                .build();

            // 6. Create Session & Send URL
            Session stripeSession = Session.create(params);

            Map<String, String> respData = new HashMap<>();
            respData.put("url", stripeSession.getUrl());
            
            response.setContentType("application/json");
            response.getWriter().write(new Gson().toJson(respData));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}