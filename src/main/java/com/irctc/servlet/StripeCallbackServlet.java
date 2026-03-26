package com.irctc.servlet;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
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

@WebServlet("/StripeCallbackServlet")
public class StripeCallbackServlet extends HttpServlet {

   private static final String STRIPE_SECRET_KEY = "YOUR_STRIPE_SECRET_KEY";
    private BookingDAO bookingDAO = new BookingDAOImpl();
    private PaymentDAO paymentDAO = new PaymentDAOImpl();
    private TrainDAO trainDAO = new TrainDAOImpl();
    private PassengerDAO passengerDAO = new PassengerDAOImpl();

    @Override
    public void init() {
        Stripe.apiKey = STRIPE_SECRET_KEY;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("pendingBookingId") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/home.jsp");
            return;
        }

        String sessionId = request.getParameter("session_id");
        Connection conn = null;

        try {
            Session stripeSession = Session.retrieve(sessionId);
            
            if (!"paid".equals(stripeSession.getPaymentStatus())) {
                throw new Exception("Payment not successful. Status: " + stripeSession.getPaymentStatus());
            }

            Integer bookingId = (Integer) session.getAttribute("pendingBookingId");
            BigDecimal amount = (BigDecimal) session.getAttribute("pendingBookingAmount");
            Integer trainId = (Integer) session.getAttribute("pendingTrainId");
            String dateStr = (String) session.getAttribute("pendingDate");
            String selectedSeatsData = (String) session.getAttribute("pendingSelectedSeats");
            String classCode = (String) session.getAttribute("pendingClassCode"); 

            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            Booking booking = bookingDAO.getBookingByIdForUpdate(bookingId, conn); 
            Train train = trainDAO.getTrainByIdForUpdate(trainId, conn); 

            int newSeatCount = train.getAvailableSeats() - booking.getPassengers();
            boolean seatsUpdated = trainDAO.updateAvailableSeats(trainId, newSeatCount, conn);

            boolean bookingConfirmed = bookingDAO.updateBookingStatus(bookingId, "CONFIRMED", conn);

            Payment payment = new Payment();
            payment.setBookingId(bookingId);
            payment.setAmount(amount);
            payment.setPaymentMode("STRIPE");
            payment.setPaymentModeDetails("Stripe Session: " + sessionId);
            payment.setPaymentStatus("SUCCESS");
            payment.setTransactionId(stripeSession.getPaymentIntent());
            payment.setPayerEmail(stripeSession.getCustomerDetails() != null ? stripeSession.getCustomerDetails().getEmail() : "N/A");
            
            boolean paymentAdded = paymentDAO.addPayment(payment, conn);

            boolean seatsAssigned = assignSeats(bookingId, trainId, Date.valueOf(dateStr), selectedSeatsData, classCode, conn);

            if (!seatsUpdated || !bookingConfirmed || !paymentAdded || !seatsAssigned) {
                throw new SQLException("Database update failed during Stripe callback.");
            }
            
            conn.commit();

            sendTicketAsync(bookingId, session);

            session.removeAttribute("pendingBookingId");
            session.removeAttribute("pendingBookingAmount");
            session.removeAttribute("pendingSelectedSeats");
            session.removeAttribute("pendingClassCode");
            session.removeAttribute("pendingSource");
            session.removeAttribute("pendingDestination");
            session.removeAttribute("pendingSegmentDepTime");
            session.removeAttribute("pendingSegmentArrTime");
            session.removeAttribute("pendingTrainId");
            session.removeAttribute("pendingDate");
            session.removeAttribute("pendingPassengerCount");
            session.removeAttribute("pendingTrainName");

            response.sendRedirect(request.getContextPath() + "/jsp/bookingSuccess.jsp?bookingId=" + bookingId);

        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException se) { se.printStackTrace(); }
            
            response.sendRedirect(request.getContextPath() + "/jsp/payment.jsp?error=true");
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private boolean assignSeats(int bookingId, int trainId, Date journeyDate, String seatData, String classCode, Connection conn) throws SQLException {
        List<Passenger> passengers = passengerDAO.getPassengersByBookingId(bookingId);
        
        String coachCode = "S1"; 
        if (classCode != null) {
            switch (classCode) {
                case "1A": coachCode = "H1"; break;
                case "2A": coachCode = "A1"; break;
                case "3A": coachCode = "B1"; break;
                case "3E": coachCode = "M1"; break;
                case "CC": coachCode = "C1"; break;
                default:   coachCode = "S1"; break;
            }
        }
        
        if (seatData == null || seatData.trim().isEmpty()) {
            List<Integer> bookedSeats = bookingDAO.getBookedSeats(trainId, journeyDate, coachCode);
            List<String> autoSeats = new ArrayList<>();
            int seatsNeeded = passengers.size();
            int seatCounter = 1;
            int maxCoachSeats = "1A".equals(classCode) ? 24 : 72;
            
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
    
    private void sendTicketAsync(int bookingId, HttpSession session) {
        try {
            Booking finalBooking = bookingDAO.getBookingById(bookingId);
            Payment finalPayment = paymentDAO.getPaymentByBookingId(bookingId);
            List<Passenger> passengers = passengerDAO.getPassengersByBookingId(bookingId);
            finalBooking.setPassengersList(passengers);
            
            String source = (String) session.getAttribute("pendingSource");
            String destination = (String) session.getAttribute("pendingDestination");
            Time segmentDepTime = (Time) session.getAttribute("pendingSegmentDepTime");
            Time segmentArrTime = (Time) session.getAttribute("pendingSegmentArrTime");
            
            ServletContext ctx = getServletContext();
            byte[] pdf = PdfTicketUtil.generateTicketPdf(finalBooking, finalPayment, ctx, source, destination, segmentDepTime, segmentArrTime);
            
            if (pdf != null) {
                EmailUtil.sendBookingConfirmation(finalBooking.getPrimaryEmail(), finalBooking, pdf, source, destination, segmentDepTime, segmentArrTime);
            } else {
                EmailUtil.sendBookingConfirmation(finalBooking.getPrimaryEmail(), finalBooking, null, source, destination, segmentDepTime, segmentArrTime);
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
}