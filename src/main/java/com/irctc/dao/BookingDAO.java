package com.irctc.dao;

import com.irctc.model.Booking;
import java.sql.Connection; // Import Connection
import java.sql.SQLException;
import java.util.List;

/**
 * BookingDAO interface.
 * Defines the "rules" (methods) for any class that
 * wants to interact with the 'bookings' table.
 */
public interface BookingDAO {

    /**
     * Creates a new booking in the database (uses its own connection).
     * @param booking The Booking object to be saved.
     * @return The auto-generated booking_id if successful, or -1 on failure.
     * @throws SQLException
     */
    int createBooking(Booking booking) throws SQLException;

    /**
     * Finds all bookings made by a specific user.
     * @param userId The ID of the user.
     * @return A List of Booking objects (for booking history).
     * @throws SQLException
     */
    List<Booking> getBookingsByUserId(int userId) throws SQLException;

    /**
     * Finds a single booking by its ID.
     * @param bookingId The ID of the booking.
     * @return A Booking object, or null if not found.
     * @throws SQLException
     */
    Booking getBookingById(int bookingId) throws SQLException;

    /**
     * Updates the status of a booking (uses its own connection).
     * @param bookingId The ID of the booking to update.
     * @param newStatus The new status string.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException
     */
    boolean updateBookingStatus(int bookingId, String newStatus) throws SQLException;
    
    
    // --- NEW METHODS FOR TRANSACTIONS ---

    /**
     * Creates a new booking in the database using an existing connection.
     * @param booking The Booking object to be saved.
     * @param conn The database connection to use for the transaction.
     * @return The auto-generated booking_id if successful, or -1 on failure.
     * @throws SQLException
     */
    int createBooking(Booking booking, Connection conn) throws SQLException;

    /**
     * Gets a single booking by its ID using an existing connection and locks the row.
     * @param bookingId The ID of the booking.
     * @param conn The database connection to use for the transaction.
     * @return A Booking object, or null if not found.
     * @throws SQLException
     */
    Booking getBookingByIdForUpdate(int bookingId, Connection conn) throws SQLException;

    /**
     * Updates the status of a booking using an existing connection.
     * @param bookingId The ID of the booking to update.
     * @param newStatus The new status string.
     * @param conn The database connection to use for the transaction.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException
     */
    boolean updateBookingStatus(int bookingId, String newStatus, Connection conn) throws SQLException;
    
    // --- END NEW METHODS ---
    

    // --- Admin Methods ---

    /**
     * Gets a list of all bookings in the system. (Admin)
     * @return A List of all Booking objects.
     * @throws SQLException
     */
    List<Booking> getAllBookings() throws SQLException;
// --- NEW METHOD FOR SEAT SELECTION ---
    
    /**
     * Gets a list of seat numbers that are already booked for a specific coach.
     * @param trainId The ID of the train.
     * @param journeyDate The date of the journey.
     * @param coachCode The coach number (e.g., "S1", "B2").
     * @return A List of Integers representing occupied seat numbers.
     * @throws SQLException
     */
    List<Integer> getBookedSeats(int trainId, java.sql.Date journeyDate, String coachCode) throws SQLException;
}