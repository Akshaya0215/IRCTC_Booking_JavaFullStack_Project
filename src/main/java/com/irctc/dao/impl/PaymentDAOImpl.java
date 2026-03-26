package com.irctc.dao.impl;

import com.irctc.dao.DBConnection;
import com.irctc.dao.PaymentDAO;
import com.irctc.model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the implementation of the PaymentDAO interface.
 * It contains all the real JDBC/SQL code to interact with the 'payments' table.
 */
public class PaymentDAOImpl implements PaymentDAO {

    /**
     * This is the original method that creates its own connection.
     */
    @Override
    public boolean addPayment(Payment payment) throws SQLException {
        // This query now includes the new columns
        String sql = "INSERT INTO payments (booking_id, amount, payment_mode, payment_mode_details, payment_status, transaction_id, payer_email) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, payment.getBookingId());
            ps.setBigDecimal(2, payment.getAmount());
            ps.setString(3, payment.getPaymentMode());
            ps.setString(4, payment.getPaymentModeDetails()); // New field
            ps.setString(5, payment.getPaymentStatus());
            ps.setString(6, payment.getTransactionId());
            ps.setString(7, payment.getPayerEmail()); // New field

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // --- NEW METHOD FOR TRANSACTIONS ---

    /**
     * Adds a new payment record to the database using an existing connection.
     */
    @Override
    public boolean addPayment(Payment payment, Connection conn) throws SQLException {
        // This query also includes the new columns
        String sql = "INSERT INTO payments (booking_id, amount, payment_mode, payment_mode_details, payment_status, transaction_id, payer_email) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        // We use the provided connection, but do NOT close it here.
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, payment.getBookingId());
            ps.setBigDecimal(2, payment.getAmount());
            ps.setString(3, payment.getPaymentMode());
            ps.setString(4, payment.getPaymentModeDetails()); // New field
            ps.setString(5, payment.getPaymentStatus());
            ps.setString(6, payment.getTransactionId());
            ps.setString(7, payment.getPayerEmail()); // New field

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    // --- END NEW METHOD ---


    @Override
    public Payment getPaymentByBookingId(int bookingId) throws SQLException {
        String sql = "SELECT * FROM payments WHERE booking_id = ?";
        Payment payment = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, bookingId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    payment = mapResultSetToPayment(rs);
                }
            }
        }
        return payment;
    }

    @Override
    public Payment getPaymentById(int paymentId) throws SQLException {
        String sql = "SELECT * FROM payments WHERE payment_id = ?";
        Payment payment = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, paymentId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    payment = mapResultSetToPayment(rs);
                }
            }
        }
        return payment;
    }

    @Override
    public List<Payment> getAllPayments() throws SQLException {
        List<Payment> paymentList = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                paymentList.add(mapResultSetToPayment(rs));
            }
        }
        return paymentList;
    }

    
    /**
     * A private helper method to map a ResultSet row to a Payment object.
     */
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setBookingId(rs.getInt("booking_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentMode(rs.getString("payment_mode"));
        payment.setPaymentModeDetails(rs.getString("payment_mode_details")); // New field
        payment.setPaymentStatus(rs.getString("payment_status"));
        payment.setTransactionId(rs.getString("transaction_id"));
        payment.setPayerEmail(rs.getString("payer_email")); // New field
        payment.setCreatedAt(rs.getTimestamp("created_at"));
        return payment;
    }
}