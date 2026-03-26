package com.irctc.dao;

import com.irctc.model.Passenger;
import java.sql.Connection; // <-- NEW IMPORT
import java.sql.SQLException;
import java.util.List;

/**
 * PassengerDAO interface.
 * Defines the rules (methods) for interacting with the 'passenger_details' table.
 */
public interface PassengerDAO {

    /**
     * Adds a new passenger to the database, linked to a booking (uses its own connection).
     * @param passenger The Passenger object to be saved.
     * @return true if the passenger was added, false otherwise.
     * @throws SQLException
     */
    boolean addPassenger(Passenger passenger) throws SQLException;
    
    // --- NEW METHOD FOR TRANSACTIONS ---
    /**
     * Adds a new passenger using an existing connection (for transactions).
     * @param passenger The Passenger object to be saved.
     * @param conn The database connection to use for the transaction.
     * @return true if the passenger was added, false otherwise.
     * @throws SQLException
     */
    boolean addPassenger(Passenger passenger, Connection conn) throws SQLException;
    // --- END NEW METHOD ---

    /**
     * Finds all passengers associated with a specific booking ID.
     * @param bookingId The ID of the booking.
     * @return A List of Passenger objects.
     * @throws SQLException
     */
    List<Passenger> getPassengersByBookingId(int bookingId) throws SQLException;
    
}