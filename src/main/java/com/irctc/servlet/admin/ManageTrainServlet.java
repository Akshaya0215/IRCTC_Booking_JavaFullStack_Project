package com.irctc.servlet.admin;

import com.irctc.dao.TrainDAO;
import com.irctc.dao.impl.TrainDAOImpl;
import com.irctc.model.Train;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

@WebServlet("/admin/manageTrains") // Maps to the link in dashboard.jsp
public class ManageTrainServlet extends HttpServlet {

    private TrainDAO trainDAO = new TrainDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // --- Security Check ---
        if (session == null || session.getAttribute("loggedInAdmin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/adminLogin.jsp");
            return;
        }

        String action = request.getParameter("action");

        try {
            if (action == null) {
                listTrains(request, response);
            } else {
                switch (action) {
                    case "add": showAddForm(request, response); break;
                    case "edit": showEditForm(request, response); break;
                    case "delete": deleteTrain(request, response); break;
                    default: listTrains(request, response);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("adminErrorMessage", "Database error: " + e.getMessage());
            // --- FIX: Wrap listTrains call in try-catch ---
            try {
                listTrains(request, response);
            } catch (Exception innerEx) {
                innerEx.printStackTrace(); // Log fallback error
                // Critical fallback if listing also fails
                response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
            }
            // --- END FIX ---
        } catch (NumberFormatException e) {
             e.printStackTrace();
             request.setAttribute("adminErrorMessage", "Invalid ID format.");
             // --- FIX: Wrap listTrains call in try-catch ---
             try {
                listTrains(request, response);
            } catch (Exception innerEx) {
                innerEx.printStackTrace(); // Log fallback error
                response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
            }
            // --- END FIX ---
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // --- Security Check ---
        if (session == null || session.getAttribute("loggedInAdmin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/adminLogin.jsp");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                addTrain(request, response);
            } else if ("edit".equals(action)) {
                updateTrain(request, response);
            } else {
                 response.sendRedirect(request.getContextPath() + "/admin/manageTrains");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("adminErrorMessage", "Database error: " + e.getMessage());
            forwardToFormOnError(request, response, action); // Use helper for forwarding
        } catch (Exception e) { // Catch other errors like NumberFormat, Time format, IllegalArgumentException
             e.printStackTrace();
             request.setAttribute("adminErrorMessage", "Invalid input: " + e.getMessage());
             forwardToFormOnError(request, response, action); // Use helper for forwarding
        }
    }

    // --- Action Methods ---

    private void listTrains(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        List<Train> trainList = trainDAO.getAllTrains();
        request.setAttribute("trainList", trainList);
        RequestDispatcher rd = request.getRequestDispatcher("/admin/manageTrains.jsp");
        rd.forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/admin/addTrain.jsp");
        rd.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException, NumberFormatException {
        int id = Integer.parseInt(request.getParameter("id"));
        Train train = trainDAO.getTrainById(id);
        if (train == null) {
            request.setAttribute("adminErrorMessage", "Train not found for ID: " + id);
            listTrains(request, response); // Use listTrains for consistency
        } else {
            request.setAttribute("train", train);
            RequestDispatcher rd = request.getRequestDispatcher("/admin/editTrain.jsp");
            rd.forward(request, response);
        }
    }

    private void deleteTrain(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException, NumberFormatException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean deleted = trainDAO.deleteTrain(id);
        if (deleted) {
            request.getSession().setAttribute("adminSuccessMessage", "Train deleted successfully.");
        } else {
             request.getSession().setAttribute("adminErrorMessage", "Failed to delete train ID: " + id);
        }
        response.sendRedirect(request.getContextPath() + "/admin/manageTrains");
    }

    private void addTrain(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        Train train = extractTrainFromRequest(request);
        boolean added = trainDAO.addTrain(train);
         if (added) {
            request.getSession().setAttribute("adminSuccessMessage", "Train added successfully.");
            response.sendRedirect(request.getContextPath() + "/admin/manageTrains");
        } else {
             throw new SQLException("Failed to add train (maybe duplicate train number?)");
        }
    }

    private void updateTrain(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        Train train = extractTrainFromRequest(request);
        train.setTrainId(Integer.parseInt(request.getParameter("trainId"))); // Get ID from hidden field

        boolean updated = trainDAO.updateTrain(train);
         if (updated) {
            request.getSession().setAttribute("adminSuccessMessage", "Train updated successfully.");
            response.sendRedirect(request.getContextPath() + "/admin/manageTrains");
        } else {
             throw new SQLException("Failed to update train ID: " + train.getTrainId());
        }
    }

    // --- MODIFIED HELPER METHOD ---
    private Train extractTrainFromRequest(HttpServletRequest request) {
        Train train = new Train();
        train.setTrainName(request.getParameter("trainName"));
        train.setTrainNumber(request.getParameter("trainNumber"));
        train.setSource(request.getParameter("source"));
        train.setDestination(request.getParameter("destination"));
        
        String depTimeStr = request.getParameter("departureTime");
        String arrTimeStr = request.getParameter("arrivalTime");
        if (depTimeStr != null && depTimeStr.length() == 5) depTimeStr += ":00"; // Add seconds if HH:mm
        if (arrTimeStr != null && arrTimeStr.length() == 5) arrTimeStr += ":00"; // Add seconds if HH:mm
        train.setDepartureTime(Time.valueOf(depTimeStr));
        train.setArrivalTime(Time.valueOf(arrTimeStr));
        
        train.setTotalSeats(Integer.parseInt(request.getParameter("totalSeats")));
        train.setAvailableSeats(Integer.parseInt(request.getParameter("availableSeats")));
        
        // --- THIS IS THE FIX ---
        // The form field is still named "fare", but we set it to the correct property.
        train.setFarePerKm(new BigDecimal(request.getParameter("fare")));
        // --- END FIX ---
        
        return train;
    }
    // --- END MODIFIED HELPER ---

    // --- FIX: Helper method for forwarding back to forms on error ---
    private void forwardToFormOnError(HttpServletRequest request, HttpServletResponse response, String action) throws ServletException, IOException {
        if ("add".equals(action)) {
             RequestDispatcher rd = request.getRequestDispatcher("/admin/addTrain.jsp");
             rd.forward(request, response);
         } else if ("edit".equals(action)) {
              // Re-fetch train data for edit form if possible
              try {
                   int id = Integer.parseInt(request.getParameter("trainId"));
                   Train train = trainDAO.getTrainById(id);
                   request.setAttribute("train", train); // Put fetched train back in request
              } catch (Exception ex) {
                   System.err.println("Error re-fetching train for edit form after error: " + ex.getMessage());
                   // If re-fetch fails, at least forward back with the ID if possible
                   request.setAttribute("trainIdForRetry", request.getParameter("trainId"));
              }
              RequestDispatcher rd = request.getRequestDispatcher("/admin/editTrain.jsp");
              rd.forward(request, response);
         } else {
              // Fallback to listing trains if action is unknown
              try {
                listTrains(request, response);
              } catch (Exception listEx){
                  listEx.printStackTrace();
                  response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp"); // Critical fallback
              }
         }
    }
    // --- END FIX ---
}