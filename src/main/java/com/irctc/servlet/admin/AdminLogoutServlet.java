package com.irctc.servlet.admin;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/AdminLogoutServlet") // Matches the link in dashboard.jsp
public class AdminLogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Get the current session (if one exists)
        HttpSession session = request.getSession(false);

        if (session != null) {
            // 2. Remove the admin attribute specifically
            session.removeAttribute("loggedInAdmin");

            // OR Invalidate the entire session (logs out regular user too if same browser)
            // session.invalidate();
        }

        // 3. Optional: Set a message for the login page
        // request.getSession().setAttribute("adminMessage", "Logout successful.");

        // 4. Redirect the user back to the admin login page
        response.sendRedirect(request.getContextPath() + "/admin/adminLogin.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Logout should typically be a GET request
        doGet(request, response);
    }
}