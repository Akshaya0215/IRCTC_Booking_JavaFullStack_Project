package com.irctc.servlet;

import com.irctc.dao.TrainDAO;
import com.irctc.dao.impl.TrainDAOImpl;
import com.irctc.model.Train;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/TrainListServlet")
public class TrainListServlet extends HttpServlet {

    private TrainDAO trainDAO = new TrainDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            request.setAttribute("errorMessage", "Please login first to search for trains.");
            RequestDispatcher rd = request.getRequestDispatcher("jsp/login.jsp");
            rd.forward(request, response);
            return;
        }

        String sourceRaw = request.getParameter("source");
        String destinationRaw = request.getParameter("destination");
        String journeyDateStr = request.getParameter("journeyDate");

        try {
            // --- SMART EXTRACTION LOGIC ---
            // If user selected "New Delhi - NDLS", we want to search using "NDLS"
            // because that matches all trains regardless of how "New Delhi" is spelled.
            
            String searchSource = extractCodeOrName(sourceRaw);
            String searchDest = extractCodeOrName(destinationRaw);
            
            // Search using the optimized strings
            List<Train> trainList = trainDAO.findTrains(searchSource, searchDest);

            request.setAttribute("trainList", trainList);
            
            // Pass the ORIGINAL raw strings back to the JSP so the inputs look correct
            request.setAttribute("source", sourceRaw);
            request.setAttribute("destination", destinationRaw);
            
            if (journeyDateStr != null && !journeyDateStr.isEmpty()) {
                request.setAttribute("journeyDate", Date.valueOf(journeyDateStr));
            }

            RequestDispatcher rd = request.getRequestDispatcher("jsp/trains.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while searching.");
            RequestDispatcher rd = request.getRequestDispatcher("jsp/home.jsp");
            rd.forward(request, response);
        }
    }
    
    // Helper: Extracts "NDLS" from "New Delhi - NDLS"
    private String extractCodeOrName(String input) {
        if (input != null && input.contains(" - ")) {
            String[] parts = input.split(" - ");
            if (parts.length > 1) {
                return parts[1].trim(); // Return the Code part
            }
        }
        return input != null ? input.trim() : "";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}