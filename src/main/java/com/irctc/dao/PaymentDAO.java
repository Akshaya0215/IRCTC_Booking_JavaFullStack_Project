package com.irctc.dao;

import com.irctc.model.Payment;
import java.sql.Connection; // Import Connection
import java.sql.SQLException;
import java.util.List;

/**
 * PaymentDAO interface.
 * Defines the "rules" (methods) for any class that
 * wants to interact with the 'payments' table.
 */
public interface PaymentDAO {

    /**
     * Adds a new payment record to the database (uses its own connection).
     * @param payment The Payment object to be saved.
     * @return true if the payment was added, false otherwise.
     * @throws SQLException
     */
    boolean addPayment(Payment payment) throws SQLException;

    /**
     * Finds a payment record by its associated booking ID.
     * @param bookingId The ID of the booking.
     * @return A Payment object, or null if not found.
     * @throws SQLException
     */
    Payment getPaymentByBookingId(int bookingId) throws SQLException;
    
    /**
     * Finds a payment record by its own ID.
     * @param paymentId The ID of the payment.
     * @return A Payment object, or null if not found.
     * @throws SQLException
     */
    Payment getPaymentById(int paymentId) throws SQLException;

    
    // --- NEW METHOD FOR TRANSACTIONS ---
    
    /**
     * Adds a new payment record to the database using an existing connection.
     * @param payment The Payment object to be saved.
     * @param conn The database connection to use for the transaction.
     * @return true if the payment was added, false otherwise.
     * @throws SQLException
     */
    boolean addPayment(Payment payment, Connection conn) throws SQLException;
    
    // --- END NEW METHOD ---


    // --- Admin Methods ---

    /**
     * Gets a list of all payments in the system. (Admin)
     * @return A List of all Payment objects.
     * @throws SQLException
     */
    List<Payment> getAllPayments() throws SQLException;
}