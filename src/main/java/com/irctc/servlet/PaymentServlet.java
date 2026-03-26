package com.irctc.servlet;

import com.irctc.dao.BookingDAO;
import com.irctc.dao.PassengerDAO;
import com.irctc.dao.PaymentDAO;
import com.irctc.dao.TrainDAO;
import com.irctc.dao.DBConnection;
import com.irctc.dao.impl.BookingDAOImpl;
import com.irctc.dao.impl.PassengerDAOImpl;
import com.irctc.dao.impl.PaymentDAOImpl;
import com.irctc.dao.impl.TrainDAOImpl;
import com.irctc.model.Booking;
import com.irctc.model.Passenger;
import com.irctc.model.Payment;
import com.irctc.model.Train;
import com.irctc.util.EmailUtil;
import com.irctc.util.PdfTicketUtil;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/PaymentServlet")
public class PaymentServlet extends HttpServlet {
    
    private BookingDAO bookingDAO = new BookingDAOImpl();
    private PaymentDAO paymentDAO = new PaymentDAOImpl();
    private TrainDAO trainDAO = new TrainDAOImpl();
    private PassengerDAO passengerDAO = new PassengerDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/jsp/home.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }
        
        Integer bookingId = (Integer) session.getAttribute("pendingBookingId");
        BigDecimal totalAmount = (BigDecimal) session.getAttribute("pendingBookingAmount");
        String source = (String) session.getAttribute("pendingSource");
        String destination = (String) session.getAttribute("pendingDestination");
        Time segmentDepTime = (Time) session.getAttribute("pendingSegmentDepTime");
        Time segmentArrTime = (Time) session.getAttribute("pendingSegmentArrTime");
        
        Integer trainId = (Integer) session.getAttribute("pendingTrainId");
        String dateStr = (String) session.getAttribute("pendingDate");
        String classCode = (String) session.getAttribute("pendingClassCode"); 

        if (bookingId == null || totalAmount == null) {
            request.setAttribute("errorMessage", "Session expired.");
            RequestDispatcher rd = request.getRequestDispatcher("jsp/home.jsp");
            rd.forward(request, response);
            return;
        }

        String paymentMode = request.getParameter("paymentMode");
        String payerEmail = request.getParameter("payerEmail");
        String paymentDetails = "UPI".equals(paymentMode) ? "UPI ID: " + request.getParameter("upiId") : "Card Payment";

        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); 

            Booking booking = bookingDAO.getBookingByIdForUpdate(bookingId, conn); 
            Train train = trainDAO.getTrainByIdForUpdate(booking.getTrainId(), conn); 

            if (booking == null || train == null) throw new ServletException("Booking invalid.");
            
            long timeElapsed = System.currentTimeMillis() - booking.getCreatedAt().getTime();
            long timeoutLimit = 10 * 60 * 1000; 

            if (timeElapsed > timeoutLimit) {
                bookingDAO.updateBookingStatus(bookingId, "CANCELLED", conn);
                conn.commit(); 
                
                session.removeAttribute("pendingBookingId");
                
                request.setAttribute("errorMessage", "Session Expired. You exceeded the 10-minute booking limit.");
                RequestDispatcher rd = request.getRequestDispatcher("jsp/home.jsp");
                rd.forward(request, response);
                return;
            }
            
            if (train.getAvailableSeats() < booking.getPassengers()) {
                throw new SQLException("Seats no longer available.");
            }

            int newSeatCount = train.getAvailableSeats() - booking.getPassengers();
            boolean seatsUpdated = trainDAO.updateAvailableSeats(train.getTrainId(), newSeatCount, conn);

            boolean bookingConfirmed = bookingDAO.updateBookingStatus(bookingId, "CONFIRMED", conn);

            Payment payment = new Payment();
            payment.setBookingId(bookingId);
            payment.setAmount(totalAmount);
            payment.setPaymentMode(paymentMode);
            payment.setPaymentModeDetails(paymentDetails);
            payment.setPaymentStatus("SUCCESS");
            payment.setTransactionId("txn_" + System.currentTimeMillis());
            payment.setPayerEmail(payerEmail);
            
            boolean paymentAdded = paymentDAO.addPayment(payment, conn); 

            String selectedSeatsData = request.getParameter("selectedSeatsData");
            Date journeyDate = Date.valueOf(dateStr);
            
            boolean seatsAssigned = assignSeats(bookingId, trainId, journeyDate, selectedSeatsData, classCode, conn);

            if (!seatsUpdated || !bookingConfirmed || !paymentAdded || !seatsAssigned) {
                throw new SQLException("Transaction failed at step 8.");
            }

            conn.commit(); 
            System.out.println("PaymentServlet: Success! Booking ID: " + bookingId);

            session.removeAttribute("pendingBookingId");
            session.removeAttribute("pendingBookingAmount");
            session.removeAttribute("pendingSource");
            session.removeAttribute("pendingDestination");
            session.removeAttribute("pendingSegmentDepTime");
            session.removeAttribute("pendingSegmentArrTime");
            session.removeAttribute("pendingTrainId");
            session.removeAttribute("pendingDate");
            session.removeAttribute("pendingPassengerCount");
            session.removeAttribute("pendingTrainName");
            session.removeAttribute("pendingClassCode");

            try {
                Booking finalBooking = bookingDAO.getBookingById(bookingId);
                Payment finalPayment = paymentDAO.getPaymentByBookingId(bookingId);
                List<Passenger> passengers = passengerDAO.getPassengersByBookingId(bookingId);
                finalBooking.setPassengersList(passengers); 
                
                ServletContext ctx = getServletContext();
                byte[] pdf = PdfTicketUtil.generateTicketPdf(finalBooking, finalPayment, ctx, source, destination, segmentDepTime, segmentArrTime);
                
                if (pdf != null) {
                    EmailUtil.sendBookingConfirmation(finalBooking.getPrimaryEmail(), finalBooking, pdf, source, destination, segmentDepTime, segmentArrTime); 
                } else {
                    EmailUtil.sendBookingConfirmation(finalBooking.getPrimaryEmail(), finalBooking, null, source, destination, segmentDepTime, segmentArrTime);
                }
            } catch (Exception emailOrPdfEx) {
                 emailOrPdfEx.printStackTrace(); 
            }

            response.sendRedirect(request.getContextPath() + "/jsp/bookingSuccess.jsp?bookingId=" + bookingId);

        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException se) { se.printStackTrace(); }
            request.setAttribute("errorMessage", "Payment Failed: " + e.getMessage());
            request.getRequestDispatcher("jsp/payment.jsp").forward(request, response);
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private boolean assignSeats(int bookingId, int trainId, Date journeyDate, String seatData, String classCode, Connection conn) throws SQLException {
        List<Passenger> passengers = passengerDAO.getPassengersByBookingId(bookingId);
        
        // 1. Determine Coach Code based on Class
        String coachCode = "S1"; 
        if (classCode != null) {
            switch (classCode) {
                case "1A": coachCode = "H1"; break; // MAP 1A -> H1
                case "2A": coachCode = "A1"; break;
                case "3A": coachCode = "B1"; break;
                case "3E": coachCode = "M1"; break;
                case "CC": coachCode = "C1"; break;
                default:   coachCode = "S1"; break;
            }
        }
        
        // 2. Auto-Assign Strategy
        if (seatData == null || seatData.trim().isEmpty()) {
            List<Integer> bookedSeats = bookingDAO.getBookedSeats(trainId, journeyDate, coachCode);
            List<String> autoSeats = new ArrayList<>();
            int seatsNeeded = passengers.size();
            int seatCounter = 1;
            int maxCoachSeats = "1A".equals(classCode) ? 24 : 72; // H1 is smaller
            
            while (autoSeats.size() < seatsNeeded && seatCounter <= maxCoachSeats) {
                if (!bookedSeats.contains(seatCounter)) {
                    String berth = getBerthType(seatCounter); 
                    autoSeats.add(seatCounter + "-" + berth);
                }
                seatCounter++;
            }
            seatData = String.join(",", autoSeats);
        }

        String[] seats = seatData.split(",");
        String sql = "UPDATE passenger_details SET seat_number = ?, berth_type = ?, coach_code = ? WHERE passenger_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < seats.length && i < passengers.size(); i++) {
                String[] parts = seats[i].split("-");
                int seatNo = Integer.parseInt(parts[0]);
                String berth = parts[1];
                
                ps.setInt(1, seatNo);
                ps.setString(2, berth);
                ps.setString(3, coachCode);
                ps.setInt(4, passengers.get(i).getPassengerId());
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            return results.length > 0;
        }
    }

    private String getBerthType(int seatNo) {
        int mod = seatNo % 8;
        switch (mod) {
            case 1: case 4: return "LB"; 
            case 2: case 5: return "MB"; 
            case 3: case 6: return "UB"; 
            case 7: return "SL";         
            case 0: return "SU";         
            default: return "GN";
        }
    }
}