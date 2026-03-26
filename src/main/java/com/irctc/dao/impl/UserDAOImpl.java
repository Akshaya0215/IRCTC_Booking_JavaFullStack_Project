package com.irctc.dao.impl;

import com.irctc.dao.DBConnection;
import com.irctc.dao.UserDAO;
import com.irctc.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public boolean addUser(User user) throws SQLException {
        if (isEmailRegistered(user.getEmail())) {
            return false; 
        }

        // Updated SQL to include default columns if necessary, but standard insert is fine
        String sql = "INSERT INTO users (full_name, email, password, phone, gender, address, is_verified, verification_token, is_blocked) VALUES (?, ?, ?, ?, ?, ?, ?, ?, FALSE)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getGender());
            ps.setString(6, user.getAddress());
            ps.setBoolean(7, user.isVerified());
            ps.setString(8, user.getVerificationToken());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public User loginUser(String email, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        User user = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
        }
        return user;
    }

    @Override
    public User getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        User user = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY full_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                userList.add(mapResultSetToUser(rs));
            }
        }
        return userList;
    }
    
    @Override
    public boolean isEmailRegistered(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { return rs.getInt(1) > 0; }
            }
        }
        return false;
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ?, address = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getAddress());
            ps.setInt(5, user.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // --- EMAIL VERIFICATION METHODS ---

    @Override
    public User getUserByToken(String token) throws SQLException {
        String sql = "SELECT * FROM users WHERE verification_token = ?";
        User user = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, token);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
        }
        return user;
    }

    @Override
    public boolean verifyUser(int userId) throws SQLException {
        String sql = "UPDATE users SET is_verified = TRUE, verification_token = NULL WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    // --- GOD MODE: IMPLEMENTING THE BLOCK CHECK ---
    
    @Override
    public boolean isUserBlocked(String email) throws SQLException {
        String sql = "SELECT is_blocked FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Returns true if database value is 1 (TRUE), false otherwise
                    return rs.getBoolean("is_blocked");
                }
            }
        }
        return false; // Default: Not blocked if user not found
    }
    // ----------------------------------------------

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setGender(rs.getString("gender"));
        user.setAddress(rs.getString("address"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setVerified(rs.getBoolean("is_verified"));
        
        // Map the blocked status to the object (Important for Admin Panel View)
        // If your User model does not have 'setBlocked', you should add it to User.java
        try {
            user.setBlocked(rs.getBoolean("is_blocked"));
        } catch (SQLException e) {
            // Ignore if column doesn't exist in result set (backward compatibility)
        }
        
        return user;
    }
}