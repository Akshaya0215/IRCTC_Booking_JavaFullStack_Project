package com.irctc.dao.impl;

import com.irctc.dao.DBConnection;
import com.irctc.dao.TrainDAO;
import com.irctc.model.Train;
import com.irctc.model.TrainStop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrainDAOImpl implements TrainDAO {

    // --- 1. SMART STATION SEARCH (Groups duplicates) ---
    @Override
    public List<String> getStationNames(String query) throws SQLException {
        List<String> stationList = new ArrayList<>();
        
        // Group by station_code to avoid showing "Kanpur" and "Kanpur Central" as duplicates
        String sql = "SELECT station_code, MAX(station_name) as station_name FROM stations " +
                     "WHERE station_name LIKE ? OR station_code LIKE ? OR city_name LIKE ? " +
                     "GROUP BY station_code " +
                     "LIMIT 15";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String searchPattern = query + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern); // Search by City too (e.g., "Mumbai")
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("station_name");
                    String code = rs.getString("station_code");
                    if (name != null) name = name.trim();
                    // Format: "New Delhi - NDLS"
                    stationList.add(name + " - " + code);
                }
            }
        }
        return stationList;
    }

    // --- 2. FIND TRAINS (With Price Calculation) ---
    @Override
    public List<Train> findTrains(String source, String destination) throws SQLException {
        List<Train> trainList = new ArrayList<>();
        
        // Helper to extract code if format is "Name - Code"
        String srcCode = extractCode(source);
        String destCode = extractCode(destination);

        // SQL to find trains AND calculate price based on distance
        String sql = 
            "SELECT * FROM (" +
                // Query 1: Route-based (Calculate distance from stops)
                "(SELECT t.*, " +
                "       s_from.departure_time as segment_dep_time, " +
                "       s_to.arrival_time as segment_arr_time, " +
                "       (s_to.distance_from_source - s_from.distance_from_source) * t.fare_per_km as ticket_price " + // <--- CRITICAL: Price Calc
                " FROM trains t " +
                " JOIN train_stops s_from ON t.train_id = s_from.train_id " +
                " JOIN train_stops s_to ON t.train_id = s_to.train_id " +
                " WHERE (s_from.station_name = ? OR s_from.station_code = ?) " +
                " AND (s_to.station_name = ? OR s_to.station_code = ?) " +
                " AND s_from.stop_number < s_to.stop_number " +
                " AND t.available_seats > 0) " +
                
                "UNION " + 
                
                // Query 2: End-to-End (Fallback price if no stops defined yet)
                "(SELECT t.*, " +
                "       t.departure_time as segment_dep_time, " +
                "       t.arrival_time as segment_arr_time, " +
                "       500.00 as ticket_price " + // Default price for direct trains
                " FROM trains t " +
                " LEFT JOIN train_stops ts ON t.train_id = ts.train_id " +
                " WHERE t.source = ? " +     
                " AND t.destination = ? " + 
                " AND t.available_seats > 0 " +
                " AND ts.train_id IS NULL)" +
            ") AS combined_results ORDER BY segment_dep_time ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, source);
            ps.setString(2, srcCode);
            ps.setString(3, destination);
            ps.setString(4, destCode);
            ps.setString(5, source);
            ps.setString(6, destination);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Train train = mapResultSetToTrain(rs);
                    train.setSegmentDepartureTime(rs.getTime("segment_dep_time"));
                    train.setSegmentArrivalTime(rs.getTime("segment_arr_time"));
                    
                    // Set the calculated price so the JSP doesn't crash
                    train.setTicketPrice(rs.getBigDecimal("ticket_price"));
                    
                    trainList.add(train);
                }
            }
        }
        return trainList;
    }

    // --- 3. STOP DETAILS (For Booking) ---
    @Override
    public TrainStop getStopDetails(int trainId, String stationName) throws SQLException {
        String cleanName = stationName;
        String cleanCode = stationName;

        // Handle "New Delhi - NDLS" format
        if (stationName != null && stationName.contains(" - ")) {
            String[] parts = stationName.split(" - ");
            cleanName = parts[0].trim();
            if (parts.length > 1) cleanCode = parts[1].trim();
        }

        String sql = "SELECT * FROM train_stops WHERE train_id = ? AND (station_name = ? OR station_code = ?)";
        TrainStop stop = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, trainId);
            ps.setString(2, cleanName);
            ps.setString(3, cleanCode);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stop = mapResultSetToTrainStop(rs);
                }
            }
        }
        return stop; 
    }
    
    // --- BASIC CRUD & HELPERS ---

    @Override
    public Train getTrainById(int trainId) throws SQLException {
        String sql = "SELECT * FROM trains WHERE train_id = ?";
        Train train = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    train = mapResultSetToTrain(rs);
                }
            }
        }
        return train;
    }

    @Override
    public boolean updateAvailableSeats(int trainId, int newSeatCount) throws SQLException {
        String sql = "UPDATE trains SET available_seats = ? WHERE train_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newSeatCount);
            ps.setInt(2, trainId);
            return ps.executeUpdate() > 0;
        }
    }

    // Transactional version (locks row)
    @Override
    public Train getTrainByIdForUpdate(int trainId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM trains WHERE train_id = ? FOR UPDATE";
        Train train = null;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    train = mapResultSetToTrain(rs);
                }
            }
        }
        return train;
    }
    
    // Transactional version
    @Override
    public boolean updateAvailableSeats(int trainId, int newSeatCount, Connection conn) throws SQLException {
        String sql = "UPDATE trains SET available_seats = ? WHERE train_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newSeatCount);
            ps.setInt(2, trainId);
            return ps.executeUpdate() > 0;
        }
    }
    
    @Override
    public boolean addTrain(Train train) throws SQLException {
        String sql = "INSERT INTO trains (train_name, train_number, source, destination, departure_time, arrival_time, total_seats, available_seats, fare_per_km) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, train.getTrainName());
            ps.setString(2, train.getTrainNumber());
            ps.setString(3, train.getSource());
            ps.setString(4, train.getDestination());
            ps.setTime(5, train.getDepartureTime());
            ps.setTime(6, train.getArrivalTime());
            ps.setInt(7, train.getTotalSeats());
            ps.setInt(8, train.getAvailableSeats());
            ps.setBigDecimal(9, train.getFarePerKm()); 
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Train> getAllTrains() throws SQLException {
        List<Train> trainList = new ArrayList<>();
        String sql = "SELECT * FROM trains ORDER BY train_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                trainList.add(mapResultSetToTrain(rs));
            }
        }
        return trainList;
    }

    @Override
    public boolean updateTrain(Train train) throws SQLException {
        String sql = "UPDATE trains SET train_name = ?, train_number = ?, source = ?, destination = ?, " +
                     "departure_time = ?, arrival_time = ?, total_seats = ?, available_seats = ?, fare_per_km = ? " +
                     "WHERE train_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, train.getTrainName());
            ps.setString(2, train.getTrainNumber());
            ps.setString(3, train.getSource());
            ps.setString(4, train.getDestination());
            ps.setTime(5, train.getDepartureTime());
            ps.setTime(6, train.getArrivalTime());
            ps.setInt(7, train.getTotalSeats());
            ps.setInt(8, train.getAvailableSeats());
            ps.setBigDecimal(9, train.getFarePerKm()); 
            ps.setInt(10, train.getTrainId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteTrain(int trainId) throws SQLException {
        String sql = "DELETE FROM trains WHERE train_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainId);
            return ps.executeUpdate() > 0;
        }
    }

    // --- HELPERS ---

    private String extractCode(String input) {
        if (input != null && input.contains(" - ")) {
            String[] parts = input.split(" - ");
            if (parts.length > 1) return parts[1].trim();
        }
        return input;
    }

    private Train mapResultSetToTrain(ResultSet rs) throws SQLException {
        Train train = new Train();
        train.setTrainId(rs.getInt("train_id"));
        train.setTrainName(rs.getString("train_name"));
        train.setTrainNumber(rs.getString("train_number"));
        train.setSource(rs.getString("source"));
        train.setDestination(rs.getString("destination"));
        train.setDepartureTime(rs.getTime("departure_time"));
        train.setArrivalTime(rs.getTime("arrival_time"));
        train.setTotalSeats(rs.getInt("total_seats"));
        train.setAvailableSeats(rs.getInt("available_seats"));
        train.setFarePerKm(rs.getBigDecimal("fare_per_km")); 
        return train;
    }
    
    private TrainStop mapResultSetToTrainStop(ResultSet rs) throws SQLException {
        TrainStop stop = new TrainStop();
        stop.setStopId(rs.getInt("stop_id"));
        stop.setTrainId(rs.getInt("train_id"));
        stop.setStationCode(rs.getString("station_code"));
        stop.setStationName(rs.getString("station_name"));
        stop.setStopNumber(rs.getInt("stop_number"));
        stop.setArrivalTime(rs.getTime("arrival_time"));
        stop.setDepartureTime(rs.getTime("departure_time"));
        stop.setDistanceFromSource(rs.getInt("distance_from_source"));
        return stop;
    }
}