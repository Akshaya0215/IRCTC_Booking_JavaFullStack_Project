package com.irctc.servlet;

import com.irctc.dao.TrainDAO;
import com.irctc.dao.impl.TrainDAOImpl;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

// We need a JSON library. We'll build a simple one manually for now
// to avoid adding new dependencies.

@WebServlet("/StationSearchServlet")
public class StationSearchServlet extends HttpServlet {
    
    private TrainDAO trainDAO = new TrainDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        try {
            // 1. Get the partial text (the "term") from the query
            String query = request.getParameter("term");
            
            if (query != null && !query.trim().isEmpty()) {
                // 2. Call the DAO to get the list of matching station names
                List<String> stationList = trainDAO.getStationNames(query.trim());
                
                // 3. Convert the Java List to a JSON array string
                String jsonResponse = convertListToJson(stationList);
                
                // 4. Set the response type to JSON and write the data back
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.print(jsonResponse);
                out.flush();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Send an error response if something goes wrong
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\":\"Database error\"}");
        }
    }

    /**
     * A simple helper method to convert a List<String> to a JSON array.
     * e.g., ["MUMBAI", "MADURAI"]
     */
    private String convertListToJson(List<String> list) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        
        for (int i = 0; i < list.size(); i++) {
            // Add quotes around the string and escape any special chars
            json.append("\"").append(list.get(i).replace("\"", "\\\"")).append("\"");
            if (i < list.size() - 1) {
                json.append(",");
            }
        }
        
        json.append("]");
        return json.toString();
    }

}