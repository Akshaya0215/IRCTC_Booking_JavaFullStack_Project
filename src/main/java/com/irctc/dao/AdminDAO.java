package com.irctc.dao;

import com.irctc.model.Admin;
import java.sql.SQLException;

/**
 * AdminDAO interface.
 * Defines the rules (methods) for interacting with the 'admins' table.
 */
public interface AdminDAO {

    /**
     * Authenticates an admin based on username and password.
     * @param username The admin's username.
     * @param password The admin's plain-text password.
     * @return A populated Admin object if login is successful, otherwise null.
     * @throws SQLException if a database error occurs.
     */
    Admin loginAdmin(String username, String password) throws SQLException;

    /**
     * Retrieves an admin's details by their ID (optional, might be useful later).
     * @param adminId The unique ID of the admin.
     * @return A populated Admin object if found, otherwise null.
     * @throws SQLException if a database error occurs.
     */
    Admin getAdminById(int adminId) throws SQLException;

    /**
     * Retrieves an admin's details by their username (optional, might be useful later).
     * @param username The username of the admin.
     * @return A populated Admin object if found, otherwise null.
     * @throws SQLException if a database error occurs.
     */
    Admin getAdminByUsername(String username) throws SQLException;

    // Add methods for adding/updating/deleting admins if needed in the future
    // boolean addAdmin(Admin admin) throws SQLException;
    // boolean updateAdmin(Admin admin) throws SQLException;
    // boolean deleteAdmin(int adminId) throws SQLException;
}