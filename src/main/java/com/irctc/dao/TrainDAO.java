package com.irctc.dao;

import com.irctc.model.Train;
import com.irctc.model.TrainStop; // <-- NEW IMPORT
import java.sql.Connection; 
import java.sql.SQLException;
import java.util.List;

/**
 * TrainDAO interface.
 * Defines the "rules" (methods) for any class that
 * wants to interact with the 'trains' and 'train_stops' tables.
 */
public interface TrainDAO {

    /**
     * Searches the stations table for names that match the query.
     * @param query The partial station name (e.g., "MUM")
     * @return A List of matching station name strings
     * @throws SQLException
     */
    List<String> getStationNames(String query) throws SQLException;

    /**
     * Finds all trains matching the source and destination.
     * (This will now search train_stops)
     * @param source The source station.
     * @param destination The destination station.
     * @return A List of matching Train objects.
     * @throws SQLException
     */
    List<Train> findTrains(String source, String destination) throws SQLException;

    /**
     * Gets a single train by its ID.
     * @param trainId The train's unique ID.
     * @return A Train object, or null if not found.
     * @throws SQLException
     */
    Train getTrainById(int trainId) throws SQLException;
    
    /**
     * Updates the available seats for a specific train (uses its own connection).
     * @param trainId The ID of the train to update.
     * @param newSeatCount The new number of available seats.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException
     */
    boolean updateAvailableSeats(int trainId, int newSeatCount) throws SQLException;

    // --- NEW METHODS FOR TRANSACTIONS ---

    /**
     * Gets a single train by its ID using an existing connection and locks the row.
     * @param trainId The train's unique ID.
     * @param conn The database connection to use for the transaction.
     * @return A Train object, or null if not found.
     * @throws SQLException
     */
    Train getTrainByIdForUpdate(int trainId, Connection conn) throws SQLException;
    
    /**
     * Updates the available seats for a specific train using an existing connection.
     * @param trainId The ID of the train to update.
     * @param newSeatCount The new number of available seats.
     * @param conn The database connection to use for the transaction.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException
     */
    boolean updateAvailableSeats(int trainId, int newSeatCount, Connection conn) throws SQLException;

    // --- END NEW METHODS ---
    
    
    // --- Admin Methods ---
    
    /**
     * Adds a new train to the database. (Admin)
     * @param train The Train object to add.
     * @return true if added successfully, false otherwise.
     * @throws SQLException
     */
    boolean addTrain(Train train) throws SQLException;

    /**
     * Gets a list of all trains in the system. (Admin)
     * @return A List of all Train objects.
     * @throws SQLException
     */
    List<Train> getAllTrains() throws SQLException;
    
    /**
     * Updates an existing train's details. (Admin)
     * @param train The Train object with updated info.
     * @return true if update was successful, false otherwise.
     * @throws SQLException
     */
    boolean updateTrain(Train train) throws SQLException;
    
    /**
     * Deletes a train by its ID. (Admin)
     * @param trainId The ID of the train to delete.
     * @return true if delete was successful, false otherwise.
     * @throws SQLException
     */
    boolean deleteTrain(int trainId) throws SQLException;
    
    
    // --- NEW METHOD FOR ROUTES ---
    
    /**
     * Gets the details of a specific stop for a specific train.
     * @param trainId The ID of the train.
     * @param stationName The name of the station (e.g., "Kota Jn")
     * @return A TrainStop object containing distance, times, etc., or null if not found.
     * @throws SQLException
     */
    TrainStop getStopDetails(int trainId, String stationName) throws SQLException;
}