package com.irctc.servlet;

import com.irctc.dao.UserDAO;
import com.irctc.dao.impl.UserDAOImpl;
import com.irctc.model.User;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/UpdateProfileServlet")
public class UpdateProfileServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("loggedInUser");

        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        
        User userToUpdate = new User();
        userToUpdate.setUserId(currentUser.getUserId());
        userToUpdate.setEmail(currentUser.getEmail()); 
        userToUpdate.setFullName(fullName);
        userToUpdate.setPhone(phone);
        userToUpdate.setAddress(address);
        
        try {
            boolean updated = userDAO.updateUser(userToUpdate);

            if (updated) {
                // CRITICAL: Update session object immediately so UI reflects changes
                currentUser.setFullName(fullName);
                currentUser.setPhone(phone);
                currentUser.setAddress(address);
                session.setAttribute("loggedInUser", currentUser);

                session.setAttribute("successMessage", "Profile updated successfully!");
            } else {
                session.setAttribute("errorMessage", "Failed to update profile. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Database error: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/jsp/profile.jsp");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("jsp/profile.jsp").forward(request, response);
    }
}