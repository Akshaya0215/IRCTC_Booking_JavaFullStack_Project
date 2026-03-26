package com.irctc.servlet;

import com.irctc.dao.UserDAO;
import com.irctc.dao.impl.UserDAOImpl;
import com.irctc.model.User;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/VerifyOtpServlet")
public class VerifyOtpServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("registeringEmail") == null) {
            response.sendRedirect("jsp/register.jsp");
            return;
        }

        String email = (String) session.getAttribute("registeringEmail");
        String enteredOtp = request.getParameter("otp");

        try {
            // 1. We need to fetch the user to check the token.
            // Since we don't have 'getUserByEmail', we can use 'getUserByToken' 
            // BUT to be safe, we should add 'getUserByEmail' to DAO. 
            // For now, let's rely on the token being unique enough or fetch via login logic (hacky).
            
            // BETTER WAY: Let's fetch by the token directly.
            User user = userDAO.getUserByToken(enteredOtp);

            if (user != null && user.getEmail().equalsIgnoreCase(email)) {
                // OTP Matches and Email matches!
                
                boolean verified = userDAO.verifyUser(user.getUserId());
                
                if (verified) {
                    session.removeAttribute("registeringEmail");
                    session.setAttribute("successMessage", "Account verified successfully! Please login.");
                    response.sendRedirect("jsp/login.jsp");
                } else {
                    request.setAttribute("errorMessage", "Verification failed. Try again.");
                    request.getRequestDispatcher("jsp/verifyOtp.jsp").forward(request, response);
                }

            } else {
                request.setAttribute("errorMessage", "Invalid OTP. Please check and try again.");
                request.getRequestDispatcher("jsp/verifyOtp.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error.");
            request.getRequestDispatcher("jsp/verifyOtp.jsp").forward(request, response);
        }
    }
}