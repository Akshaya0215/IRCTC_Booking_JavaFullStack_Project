package com.irctc.servlet.admin; // Notice the '.admin' subpackage

import com.irctc.dao.AdminDAO;
import com.irctc.dao.impl.AdminDAOImpl;
import com.irctc.model.Admin;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/adminLogin") // URL mapping for the admin login action
public class AdminLoginServlet extends HttpServlet {

    private AdminDAO adminDAO = new AdminDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Redirect GET requests to the admin login page
        response.sendRedirect(request.getContextPath() + "/admin/adminLogin.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Get username and password from the form
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            // 2. Call the DAO to attempt login
            Admin admin = adminDAO.loginAdmin(username, password);

            // 3. Check if login was successful
            if (admin != null) {
                // SUCCESS: Admin object returned

                // 4. Create a new session for the admin
                HttpSession session = request.getSession();

                // 5. Store admin info in session (use a distinct attribute name)
                session.setAttribute("loggedInAdmin", admin);

                // 6. Redirect to the admin dashboard
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");

            } else {
                // FAILURE: Invalid credentials

                // 7. Set error message for the login page
                request.setAttribute("adminErrorMessage", "Invalid username or password.");

                // 8. Forward back to the admin login page
                RequestDispatcher rd = request.getRequestDispatcher("/admin/adminLogin.jsp");
                rd.forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
            request.setAttribute("adminErrorMessage", "Database error during login.");
            RequestDispatcher rd = request.getRequestDispatcher("/admin/adminLogin.jsp");
            rd.forward(request, response);
        }
    }
}