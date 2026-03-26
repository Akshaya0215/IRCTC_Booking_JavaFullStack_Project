package com.irctc.servlet.admin;

import com.irctc.dao.UserDAO;
import com.irctc.dao.impl.UserDAOImpl;
import com.irctc.model.User;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/manageUsers") // Maps to the link in dashboard.jsp
public class ManageUsersServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // --- Security Check ---
        if (session == null || session.getAttribute("loggedInAdmin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/adminLogin.jsp");
            return;
        }

        try {
            // 1. Get all users from the DAO
            List<User> userList = userDAO.getAllUsers(); // Assumes getAllUsers() exists in UserDAO

            // 2. Set the list as an attribute for the JSP
            request.setAttribute("userList", userList);

            // 3. Forward to the JSP page
            RequestDispatcher rd = request.getRequestDispatcher("/admin/manageUsers.jsp");
            rd.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
            request.setAttribute("adminErrorMessage", "Database error fetching users: " + e.getMessage());
            // Forward back to the dashboard with an error
            RequestDispatcher rd = request.getRequestDispatcher("/admin/dashboard.jsp");
            rd.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // This page is view-only for now, so just call doGet
        doGet(request, response);
    }
}