package com.irctc.servlet;

import com.irctc.dao.UserDAO;
import com.irctc.dao.impl.UserDAOImpl;
import com.irctc.model.User;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/VerifyEmail")
public class VerifyEmailServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String token = request.getParameter("token");

        if (token == null || token.isEmpty()) {
            request.setAttribute("errorMessage", "Invalid verification link.");
            RequestDispatcher rd = request.getRequestDispatcher("jsp/login.jsp");
            rd.forward(request, response);
            return;
        }

        try {
            // 1. Find user by token
            User user = userDAO.getUserByToken(token);

            if (user != null) {
                // 2. Verify the user
                boolean verified = userDAO.verifyUser(user.getUserId());

                if (verified) {
                    // SUCCESS: Account verified
                    request.getSession().setAttribute("successMessage", "Email verified successfully! You can now login.");
                    response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
                } else {
                    // Database error during update
                    request.setAttribute("errorMessage", "Verification failed due to a database error. Please try again.");
                    RequestDispatcher rd = request.getRequestDispatcher("jsp/login.jsp");
                    rd.forward(request, response);
                }

            } else {
                // Token not found (invalid or already used)
                request.setAttribute("errorMessage", "Invalid or expired verification link.");
                RequestDispatcher rd = request.getRequestDispatcher("jsp/login.jsp");
                rd.forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error during verification.");
            RequestDispatcher rd = request.getRequestDispatcher("jsp/login.jsp");
            rd.forward(request, response);
        }
    }
}