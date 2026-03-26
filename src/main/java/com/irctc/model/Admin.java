package com.irctc.model;

/**
 * Model class for an Admin user.
 * Maps to the 'admins' table.
 */
public class Admin {

    // Fields based on database schema
    private int adminId;
    private String username;
    private String password; // Only used briefly during login
    private String fullName;

    // No-argument constructor
    public Admin() {}

    // --- Getters and Setters ---

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}