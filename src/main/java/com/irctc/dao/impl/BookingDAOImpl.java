package com.irctc.dao.impl;

import com.irctc.dao.BookingDAO;
import com.irctc.dao.DBConnection;
import com.irctc.model.Booking;
import com.irctc.model.Train; 
import com.irctc.model.User;  

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the implementation of the BookingDAO interface.
 * It contains all the real JDBC/SQL code to interact with the 'bookings' table.
 */
public class BookingDAOImpl implements BookingDAO {

    /**
     * This is the original method that creates its own connection.
     */
    @Override
    public int createBooking(Booking booking) throws SQLException {
        
        // --- MODIFIED: Added new columns to SQL ---
        String sql = "INSERT INTO bookings (user_id, train_id, booking_date, journey_date, passengers, total_amount, status, primary_email, segment_source, segment_destination) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, booking.getUserId());
            ps.setInt(2, booking.getTrainId());
            ps.setDate(3, booking.getBookingDate());
            ps.setDate(4, booking.getJourneyDate());
            ps.setInt(5, booking.getPassengers()); 
            ps.setBigDecimal(6, booking.getTotalAmount());
            ps.setString(7, booking.getStatus());
            ps.setString(8, booking.getPrimaryEmail()); 
            ps.setString(9, booking.getSegmentSource()); // <-- NEW
            ps.setString(10, booking.getSegmentDestination()); // <-- NEW

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Return the new ID
                    }
                }
            }
        }
        return -1; // Return -1 on failure
    }

    /**
     * This is the original method that creates its own connection.
     */
    @Override
    public boolean updateBookingStatus(int bookingId, String newStatus) throws SQLException {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, newStatus);
            ps.setInt(2, bookingId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }


    // --- NEW METHODS FOR TRANSACTIONS ---

    /**
     * Creates a new booking in the database using an existing connection.
     */
    @Override
    public int createBooking(Booking booking, Connection conn) throws SQLException {
        // --- MODIFIED: Added new columns to SQL ---
        String sql = "INSERT INTO bookings (user_id, train_id, booking_date, journey_date, passengers, total_amount, status, primary_email, segment_source, segment_destination) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // We use the provided connection, but do NOT close it here.
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, booking.getUserId());
            ps.setInt(2, booking.getTrainId());
            ps.setDate(3, booking.getBookingDate());
            ps.setDate(4, booking.getJourneyDate());
            ps.setInt(5, booking.getPassengers());
            ps.setBigDecimal(6, booking.getTotalAmount());
            ps.setString(7, booking.getStatus());
            ps.setString(8, booking.getPrimaryEmail());
            ps.setString(9, booking.getSegmentSource()); // <-- NEW
            ps.setString(10, booking.getSegmentDestination()); // <-- NEW

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Return the new ID
                    }
                }
            }
        }
        return -1; // Return -1 on failure
    }

    /**
     * Gets a single booking by its ID using an existing connection and locks the row.
     * 'FOR UPDATE' locks the row so no other transaction can change it.
     */
    @Override
    public Booking getBookingByIdForUpdate(int bookingId, Connection conn) throws SQLException {
        // Note: We join with trains to get train object
        String sql = "SELECT b.*, t.train_name, t.train_number, t.source, t.destination, t.departure_time, t.arrival_time, t.fare_per_km " +
                     "FROM bookings b " +
                     "JOIN trains t ON b.train_id = t.train_id " +
                     "WHERE b.booking_id = ? FOR UPDATE";
        Booking booking = null;

        // We use the provided connection, but do NOT close it here.
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, bookingId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Use the main helper method to map the result
                    booking = mapResultSetToBookingWithTrain(rs);
                }
            }
        }
        return booking;
    }

    /**
     * Updates the status of a booking using an existing connection.
     */
    @Override
    public boolean updateBookingStatus(int bookingId, String newStatus, Connection conn) throws SQLException {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        
        // We use the provided connection, but do NOT close it here.
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, bookingId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // --- END NEW METHODS ---


    // --- Other methods (MODIFIED to read new columns) ---

    @Override
    public List<Booking> getBookingsByUserId(int userId) throws SQLException {
        List<Booking> bookingList = new ArrayList<>();
        
        // --- THE FIX: Added filter for status ---
        // We only show CONFIRMED or CANCELLED. 
        // PENDING bookings (abandoned drafts) are hidden from the user.
        String sql = "SELECT b.*, t.train_name, t.train_number, t.source, t.destination, t.departure_time, t.arrival_time, t.fare_per_km " +
                     "FROM bookings b " +
                     "JOIN trains t ON b.train_id = t.train_id " +
                     "WHERE b.user_id = ? " +
                     "AND b.status IN ('CONFIRMED', 'CANCELLED') " + // <--- NEW LINE
                     "ORDER BY b.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookingList.add(mapResultSetToBookingWithTrain(rs));
                }
            }
        }
        return bookingList;
    }

    @Override
    public Booking getBookingById(int bookingId) throws SQLException {
        // --- MODIFIED: Added fare_per_km to select ---
        String sql = "SELECT b.*, t.train_name, t.train_number, t.source, t.destination, t.departure_time, t.arrival_time, t.fare_per_km " +
                     "FROM bookings b " +
                     "JOIN trains t ON b.train_id = t.train_id " +
                     "WHERE b.booking_id = ?";
        Booking booking = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, bookingId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    booking = mapResultSetToBookingWithTrain(rs);
                }
            }
        }
        return booking;
    }

    @Override
    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookingList = new ArrayList<>();
        // --- MODIFIED: Added fare_per_km to select ---
        String sql = "SELECT b.*, t.train_name, t.train_number, t.source, t.destination, t.departure_time, t.arrival_time, t.fare_per_km, u.full_name, u.email " +
                     "FROM bookings b " +
                     "JOIN trains t ON b.train_id = t.train_id " +
                     "JOIN users u ON b.user_id = u.user_id " +
                     "ORDER BY b.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Booking booking = mapResultSetToBookingWithTrain(rs);
                
                User user = new User();
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                booking.setUser(user);
                
                bookingList.add(booking);
            }
        }
        return bookingList;
    }
    @Override
    public List<Integer> getBookedSeats(int trainId, java.sql.Date journeyDate, String coachCode) throws SQLException {
        List<Integer> bookedSeats = new ArrayList<>();
        
        // We only care about bookings that are CONFIRMED or PENDING (locked)
        // We exclude CANCELLED bookings so those seats become free again.
        String sql = "SELECT p.seat_number " +
                     "FROM passenger_details p " +
                     "JOIN bookings b ON p.booking_id = b.booking_id " +
                     "WHERE b.train_id = ? " +
                     "AND b.journey_date = ? " +
                     "AND p.coach_code = ? " +
                     "AND b.status IN ('CONFIRMED', 'PENDING')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, trainId);
            ps.setDate(2, journeyDate);
            ps.setString(3, coachCode);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int seatNo = rs.getInt("seat_number");
                    if (seatNo > 0) { // valid seat numbers only
                        bookedSeats.add(seatNo);
                    }
                }
            }
        }
        return bookedSeats;
    }
    /**
     * A private helper method to map a ResultSet row to a Booking object.
     * This one also maps the joined Train data.
     */
    // --- MODIFIED: Helper updated to read ALL new columns ---
    private Booking mapResultSetToBookingWithTrain(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setTrainId(rs.getInt("train_id"));
        booking.setBookingDate(rs.getDate("booking_date"));
        booking.setJourneyDate(rs.getDate("journey_date"));
        booking.setPassengers(rs.getInt("passengers"));
        booking.setTotalAmount(rs.getBigDecimal("total_amount"));
        booking.setPrimaryEmail(rs.getString("primary_email"));
        booking.setSegmentSource(rs.getString("segment_source")); // <-- NEW
        booking.setSegmentDestination(rs.getString("segment_destination")); // <-- NEW
        booking.setStatus(rs.getString("status"));
        booking.setCreatedAt(rs.getTimestamp("created_at"));
        
        Train train = new Train();
        train.setTrainId(rs.getInt("train_id"));
        train.setTrainName(rs.getString("train_name"));
        train.setTrainNumber(rs.getString("train_number"));
        train.setSource(rs.getString("source"));
        train.setDestination(rs.getString("destination"));
        train.setDepartureTime(rs.getTime("departure_time"));
        train.setArrivalTime(rs.getTime("arrival_time"));
        train.setFarePerKm(rs.getBigDecimal("fare_per_km")); // <-- NEW
        
        booking.setTrain(train);

        // Note: This helper does NOT map the User, as not all queries join it.
        // It is mapped separately in getAllBookings().
        
        return booking;
    }
}