package com.irctc.dao.impl;

import com.irctc.dao.AdminDAO;
import com.irctc.dao.DBConnection; // Your DB connection utility
import com.irctc.model.Admin;    // The Admin model

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of the AdminDAO interface.
 * Handles JDBC operations for the 'admins' table.
 */
public class AdminDAOImpl implements AdminDAO {

    // --- SECURITY WARNING ---
    // Passwords are checked in plain text. Use hashing in a real application.
    // --- END WARNING ---

    @Override
    public Admin loginAdmin(String username, String password) throws SQLException {
        String sql = "SELECT * FROM admins WHERE username = ? AND password = ?";
        Admin admin = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password); // Plain text check

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    admin = new Admin();
                    admin.setAdminId(rs.getInt("admin_id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setFullName(rs.getString("full_name"));
                    // Do not store the password in the session object
                }
            }
        }
        return admin; // Will be null if login fails
    }

    @Override
    public Admin getAdminById(int adminId) throws SQLException {
        String sql = "SELECT * FROM admins WHERE admin_id = ?";
        Admin admin = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, adminId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    admin = new Admin();
                    admin.setAdminId(rs.getInt("admin_id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setFullName(rs.getString("full_name"));
                }
            }
        }
        return admin;
    }

    @Override
    public Admin getAdminByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM admins WHERE username = ?";
        Admin admin = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    admin = new Admin();
                    admin.setAdminId(rs.getInt("admin_id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setFullName(rs.getString("full_name"));
                }
            }
        }
        return admin;
    }

    // Implement addAdmin, updateAdmin, deleteAdmin methods here if needed later
    // public boolean addAdmin(Admin admin) throws SQLException { ... }
    // public boolean updateAdmin(Admin admin) throws SQLException { ... }
    // public boolean deleteAdmin(int adminId) throws SQLException { ... }
}