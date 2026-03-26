package com.irctc.servlet;

import com.irctc.dao.UserDAO;
import com.irctc.dao.impl.UserDAOImpl;
import com.irctc.model.User;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Redirect GET requests to the actual login page
        response.sendRedirect("jsp/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            // --- 1. GOD MODE CHECK: Is the user blocked? ---
            if (userDAO.isUserBlocked(email)) {
                request.setAttribute("errorMessage", "Access Denied: Your account has been BLOCKED by the Administrator.");
                RequestDispatcher rd = request.getRequestDispatcher("jsp/login.jsp");
                rd.forward(request, response);
                return; // Stop execution here
            }
            // -----------------------------------------------

            // 2. Normal Login Flow
            User user = userDAO.loginUser(email, password);

            if (user != null) {
                
                // 3. Verification Check
                if (!user.isVerified()) {
                    request.setAttribute("errorMessage", "Please verify your email address before logging in.");
                    RequestDispatcher rd = request.getRequestDispatcher("jsp/login.jsp");
                    rd.forward(request, response);
                    return;
                }

                // 4. Login Success
                HttpSession session = request.getSession();
                session.setAttribute("loggedInUser", user);
                response.sendRedirect(request.getContextPath() + "/jsp/home.jsp");

            } else {
                // Failure: Invalid credentials
                request.setAttribute("errorMessage", "Invalid email or password. Please try again.");
                RequestDispatcher rd = request.getRequestDispatcher("jsp/login.jsp");
                rd.forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "A database error occurred.");
            RequestDispatcher rd = request.getRequestDispatcher("jsp/login.jsp");
            rd.forward(request, response);
        }
    }
}