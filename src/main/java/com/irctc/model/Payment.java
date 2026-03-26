package com.irctc.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model class for a Payment.
 * This is a DTO that maps to the 'payments' table.
 */
public class Payment {

    // Fields based on the 'payments' table schema
    private int paymentId;
    private int bookingId;
    private BigDecimal amount;
    private String paymentMode;
    private String paymentStatus;
    private String transactionId;
    private Timestamp createdAt;
    
    // --- NEW FIELDS we added to the DB ---
    private String paymentModeDetails; // e.g., "Card No: 1234...", "UPI ID: ..."
    private String payerEmail;
    // --- END NEW FIELDS ---

    
    // We can also add the related Booking object
    private Booking booking;

    // No-argument constructor
    public Payment() {
    }

    // --- Getters and Setters ---

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    // --- NEW GETTERS AND SETTERS ---

    public String getPaymentModeDetails() {
        return paymentModeDetails;
    }

    public void setPaymentModeDetails(String paymentModeDetails) {
        this.paymentModeDetails = paymentModeDetails;
    }

    public String getPayerEmail() {
        return payerEmail;
    }

    public void setPayerEmail(String payerEmail) {
        this.payerEmail = payerEmail;
    }
}