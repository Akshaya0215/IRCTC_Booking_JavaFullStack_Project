package com.irctc.dao.impl;

import com.irctc.dao.DBConnection;
import com.irctc.dao.PassengerDAO;
import com.irctc.model.Passenger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the PassengerDAO interface.
 * Handles JDBC operations for the 'passenger_details' table.
 */
public class PassengerDAOImpl implements PassengerDAO {

    @Override
    public boolean addPassenger(Passenger passenger) throws SQLException {
        String sql = "INSERT INTO passenger_details (booking_id, name, age, gender, phone, email) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        // This method uses its own connection
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, passenger.getBookingId());
            ps.setString(2, passenger.getName());
            ps.setInt(3, passenger.getAge());
            ps.setString(4, passenger.getGender());
            ps.setString(5, passenger.getPhone());
            ps.setString(6, passenger.getEmail());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    // Transactional method (Used during Booking)
    @Override
    public boolean addPassenger(Passenger passenger, Connection conn) throws SQLException {
        String sql = "INSERT INTO passenger_details (booking_id, name, age, gender, phone, email) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        // This method uses the connection passed to it and does NOT close it.
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, passenger.getBookingId());
            ps.setString(2, passenger.getName());
            ps.setInt(3, passenger.getAge());
            ps.setString(4, passenger.getGender());
            ps.setString(5, passenger.getPhone());
            ps.setString(6, passenger.getEmail());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // --- UPDATED METHOD TO READ SEAT DETAILS ---
    @Override
    public List<Passenger> getPassengersByBookingId(int bookingId) throws SQLException {
        List<Passenger> passengerList = new ArrayList<>();
        String sql = "SELECT * FROM passenger_details WHERE booking_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, bookingId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Passenger passenger = new Passenger();
                    passenger.setPassengerId(rs.getInt("passenger_id"));
                    passenger.setBookingId(rs.getInt("booking_id"));
                    passenger.setName(rs.getString("name"));
                    passenger.setAge(rs.getInt("age"));
                    passenger.setGender(rs.getString("gender"));
                    passenger.setPhone(rs.getString("phone"));
                    passenger.setEmail(rs.getString("email"));
                    
                    // --- NEW: Populate Seat Info ---
                    passenger.setCoachCode(rs.getString("coach_code"));
                    passenger.setSeatNumber(rs.getInt("seat_number"));
                    passenger.setBerthType(rs.getString("berth_type"));
                    // -------------------------------
                    
                    passengerList.add(passenger);
                }
            }
        }
        return passengerList;
    }
}