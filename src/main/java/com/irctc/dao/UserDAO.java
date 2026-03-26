package com.irctc.dao;

import com.irctc.model.User;
import java.sql.SQLException;
import java.util.List;

/**
 * UserDAO interface.
 * Defines the "rules" (methods) for interacting with the 'users' table.
 */
public interface UserDAO {

    boolean addUser(User user) throws SQLException;
    User loginUser(String email, String password) throws SQLException;
    boolean isEmailRegistered(String email) throws SQLException;
    User getUserById(int id) throws SQLException;
    List<User> getAllUsers() throws SQLException;
    boolean updateUser(User user) throws SQLException;
    boolean deleteUser(int id) throws SQLException;
    
    // --- EMAIL VERIFICATION METHODS ---
    User getUserByToken(String token) throws SQLException;
    boolean verifyUser(int userId) throws SQLException;
    
    // --- NEW METHOD FOR GOD MODE ---
    /**
     * Checks if a user is blocked by Admin.
     * @param email The email of the user to check.
     * @return true if the user is blocked, false otherwise.
     * @throws SQLException
     */
    boolean isUserBlocked(String email) throws SQLException;
}