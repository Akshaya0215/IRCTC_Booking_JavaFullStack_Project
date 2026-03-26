package com.irctc.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. Get the current session (if one exists)
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // 2. Invalidate the session (log the user out)
            session.invalidate();
        }
        
        // 3. Set a success message for the login page
        request.getSession().setAttribute("successMessage", "You have been logged out successfully.");
        
        // 4. Redirect the user back to the login page
        response.sendRedirect("jsp/home.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}