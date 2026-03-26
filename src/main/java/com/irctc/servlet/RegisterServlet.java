package com.irctc.servlet;

import com.irctc.dao.UserDAO;
import com.irctc.dao.impl.UserDAOImpl;
import com.irctc.model.User;
import com.irctc.util.EmailUtil;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. Capture Standard Data
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String phone = request.getParameter("phone");
        String gender = request.getParameter("gender");
        String address = request.getParameter("address");
        
        // 2. Capture Verification Choice (Default to OTP if missing)
        String method = request.getParameter("verificationMethod");
        if (method == null) method = "OTP";

        if (password == null || !password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match.");
            request.getRequestDispatcher("jsp/register.jsp").forward(request, response);
            return;
        }

        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setPassword(password); 
        newUser.setPhone(phone);
        newUser.setGender(gender);
        newUser.setAddress(address);
        newUser.setVerified(false);
        
        // 3. Generate Token based on Method
        String token;
        if ("OTP".equals(method)) {
            token = String.format("%06d", new Random().nextInt(999999)); // 6-Digit Code
        } else {
            token = UUID.randomUUID().toString(); // Long UUID String
        }
        newUser.setVerificationToken(token);

        try {
            boolean isRegistered = userDAO.addUser(newUser);

            if (isRegistered) {
                
                // 4. Branching Logic for Email & Redirect
                if ("OTP".equals(method)) {
                    // --- OPTION A: OTP FLOW ---
                    new Thread(() -> EmailUtil.sendOtpEmail(newUser.getEmail(), token)).start();
                    
                    // Store email for the next page
                    HttpSession session = request.getSession();
                    session.setAttribute("registeringEmail", email);
                    
                    // Go to OTP Entry Page
                    response.sendRedirect("jsp/verifyOtp.jsp");
                    
                } else {
                    // --- OPTION B: LINK FLOW ---
                    String baseURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
                    String verifyLink = baseURL + "/VerifyEmail?token=" + token;
                    
                    new Thread(() -> EmailUtil.sendVerificationEmail(newUser.getEmail(), verifyLink)).start();
                    
                    // Go to Login Page with Message
                    String msg = "Registration successful! A verification link has been sent to " + email + ". Please check your inbox.";
                    request.getSession().setAttribute("successMessage", msg);
                    response.sendRedirect("jsp/login.jsp");
                }

            } else {
                request.setAttribute("errorMessage", "Email already in use. Please try another.");
                request.getRequestDispatcher("jsp/register.jsp").forward(request, response);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error. Please try again.");
            request.getRequestDispatcher("jsp/register.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("jsp/register.jsp");
    }
}