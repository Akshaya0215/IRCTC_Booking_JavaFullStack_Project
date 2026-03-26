package com.irctc.servlet;

import com.irctc.dao.BookingDAO;
import com.irctc.dao.PassengerDAO; 
import com.irctc.dao.TrainDAO;
import com.irctc.dao.DBConnection;
import com.irctc.dao.impl.BookingDAOImpl;
import com.irctc.dao.impl.PassengerDAOImpl; 
import com.irctc.dao.impl.TrainDAOImpl;
import com.irctc.model.Booking;
import com.irctc.model.Passenger; 
import com.irctc.model.Train;
import com.irctc.model.TrainStop; 
import com.irctc.model.User;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

@WebServlet("/BookingServlet")
public class BookingServlet extends HttpServlet {
    
    private TrainDAO trainDAO = new TrainDAOImpl();
    private BookingDAO bookingDAO = new BookingDAOImpl();
    private PassengerDAO passengerDAO = new PassengerDAOImpl(); 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            request.setAttribute("errorMessage", "Please login to book a ticket.");
            RequestDispatcher rd = request.getRequestDispatcher("jsp/login.jsp");
            rd.forward(request, response);
            return;
        }

        try {
            int trainId = Integer.parseInt(request.getParameter("trainId"));
            String journeyDateStr = request.getParameter("journeyDate");
            String source = request.getParameter("source");
            String destination = request.getParameter("destination");
            
            // 1. CAPTURE CLASS CODE
            String classCode = request.getParameter("classCode"); 
            if (classCode == null || classCode.isEmpty()) classCode = "SL";

            Train train = trainDAO.getTrainById(trainId);
            if (train == null) {
                throw new ServletException("Train details not found for ID: " + trainId);
            }
            
            BigDecimal calculatedFare = BigDecimal.ZERO;
            TrainStop sourceStop = trainDAO.getStopDetails(trainId, source);
            TrainStop destStop = trainDAO.getStopDetails(trainId, destination);

            if (sourceStop != null && destStop != null) {
                int distance = destStop.getDistanceFromSource() - sourceStop.getDistanceFromSource();
                BigDecimal baseFare = train.getFarePerKm().multiply(new BigDecimal(distance));
                
                // --- 2. UPDATED FARE MULTIPLIERS (Lowered to be realistic) ---
                if ("1A".equals(classCode)) {
                    calculatedFare = baseFare.multiply(new BigDecimal("4.0")); // Was 6.5
                } else if ("2A".equals(classCode)) {
                    calculatedFare = baseFare.multiply(new BigDecimal("2.5")); // Was 3.8
                } else if ("3A".equals(classCode)) {
                    calculatedFare = baseFare.multiply(new BigDecimal("1.8")); // Was 2.6
                } else {
                    calculatedFare = baseFare; // SL
                }
                
            } else {
                if (train.getSource().equalsIgnoreCase(source) && train.getDestination().equalsIgnoreCase(destination)) {
                    calculatedFare = new BigDecimal("0.00"); 
                } else {
                    throw new ServletException("Route details not found for this train.");
                }
            }
            
            request.setAttribute("train", train);
            request.setAttribute("journeyDate", Date.valueOf(journeyDateStr));
            request.setAttribute("source", source);
            request.setAttribute("destination", destination);
            request.setAttribute("calculatedFare", calculatedFare); 
            request.setAttribute("classCode", classCode);

            RequestDispatcher rd = request.getRequestDispatcher("jsp/booking.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error fetching train or route details.");
            RequestDispatcher rd = request.getRequestDispatcher("jsp/home.jsp");
            rd.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("loggedInUser");
        Connection conn = null;
        
        String source = request.getParameter("source");
        String destination = request.getParameter("destination");
        String journeyDateStr = request.getParameter("journeyDate");
        String classCode = request.getParameter("classCode"); 
        if (classCode == null || classCode.isEmpty()) classCode = "SL";

        try {
            int trainId = Integer.parseInt(request.getParameter("trainId"));
            int passengerCount = Integer.parseInt(request.getParameter("passengers"));
            Date journeyDate = Date.valueOf(journeyDateStr);
            String primaryEmail = request.getParameter("primaryEmail"); 

            Train train = trainDAO.getTrainById(trainId);
            TrainStop sourceStop = trainDAO.getStopDetails(trainId, source);
            TrainStop destStop = trainDAO.getStopDetails(trainId, destination);
            
            if (train == null || sourceStop == null || destStop == null) {
                throw new ServletException("Train or route details not found.");
            }

            if (train.getAvailableSeats() < passengerCount) {
                throw new SQLException("Not enough available seats.");
            }

            int distance = destStop.getDistanceFromSource() - sourceStop.getDistanceFromSource();
            BigDecimal baseFare = train.getFarePerKm().multiply(new BigDecimal(distance));
            
            // --- 3. RECALCULATE TOTAL WITH NEW MULTIPLIERS ---
            BigDecimal multiplier = new BigDecimal("1.0");
            if ("1A".equals(classCode)) multiplier = new BigDecimal("4.0"); // Lowered
            else if ("2A".equals(classCode)) multiplier = new BigDecimal("2.5");
            else if ("3A".equals(classCode)) multiplier = new BigDecimal("1.8");
            
            BigDecimal farePerPassenger = baseFare.multiply(multiplier);
            BigDecimal totalAmount = farePerPassenger.multiply(new BigDecimal(passengerCount));
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); 

            Booking booking = new Booking();
            booking.setUserId(user.getUserId());
            booking.setTrainId(trainId);
            booking.setBookingDate(new Date(System.currentTimeMillis())); 
            booking.setJourneyDate(journeyDate);
            booking.setPassengers(passengerCount); 
            booking.setTotalAmount(totalAmount); 
            booking.setPrimaryEmail(primaryEmail); 
            booking.setStatus("PENDING"); 
            booking.setSegmentSource(source);
            booking.setSegmentDestination(destination);

            int bookingId = bookingDAO.createBooking(booking, conn); 
            
            if (bookingId == -1) {
                throw new SQLException("Booking creation failed, rolling back.");
            }

            String[] paxNames = request.getParameterValues("paxName");
            String[] paxAges = request.getParameterValues("paxAge");
            String[] paxGenders = request.getParameterValues("paxGender");
            String[] paxPhones = request.getParameterValues("paxPhone");
            String[] paxEmails = request.getParameterValues("paxEmail");

            for (int i = 0; i < passengerCount; i++) {
                Passenger p = new Passenger();
                p.setBookingId(bookingId);
                p.setName(paxNames[i]);
                p.setAge(Integer.parseInt(paxAges[i]));
                p.setGender(paxGenders[i]);
                p.setPhone(paxPhones[i]);
                p.setEmail(paxEmails[i]);
                
                if (!passengerDAO.addPassenger(p, conn)) { 
                    throw new SQLException("Failed to add passenger, rolling back.");
                }
            }
            
            conn.commit();

            session.setAttribute("pendingBookingId", bookingId);
            session.setAttribute("pendingBookingAmount", totalAmount); 
            session.setAttribute("pendingSource", source);
            session.setAttribute("pendingDestination", destination);
            session.setAttribute("pendingSegmentDepTime", sourceStop.getDepartureTime());
            session.setAttribute("pendingSegmentArrTime", destStop.getArrivalTime());
            session.setAttribute("pendingTrainId", trainId);
            session.setAttribute("pendingTrainName", train.getTrainName());
            session.setAttribute("pendingDate", journeyDate.toString());
            session.setAttribute("pendingPassengerCount", passengerCount);
            session.setAttribute("pendingClassCode", classCode);

            response.sendRedirect(request.getContextPath() + "/jsp/seatSelection.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) { conn.rollback(); } } catch (SQLException se) { se.printStackTrace(); }

            request.setAttribute("errorMessage", "Booking failed: " + e.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("jsp/booking.jsp");
            rd.forward(request, response);

        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}