package com.irctc.servlet;

import com.google.gson.Gson;
import com.irctc.dao.TrainDAO;
import com.irctc.dao.impl.TrainDAOImpl;
import com.irctc.model.Train;
import com.irctc.service.GeminiService;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/ChatServlet")
public class ChatServlet extends HttpServlet {

    private GeminiService geminiService = new GeminiService();
    private TrainDAO trainDAO = new TrainDAOImpl();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // 1. Read JSON Input
            StringBuilder buffer = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) buffer.append(line);
            }
            
            Map<String, String> input = gson.fromJson(buffer.toString(), Map.class);
            String userMessage = (input != null) ? input.get("message") : null;

            if (userMessage == null || userMessage.trim().isEmpty()) {
                response.getWriter().write(gson.toJson(Map.of("reply", "Please type a question.")));
                return;
            }

            // 2. Build Context & Prompt
            String context = buildSmartContext();
            String finalPrompt = context + "\n\nUser Query: \"" + userMessage + "\"\nDisha's Answer:";

            // 3. Get Answer
            String aiResponse = geminiService.askGemini(finalPrompt);
            
            // 4. Format for HTML (Newlines to <br>)
            String formattedResponse = aiResponse.replace("\n", "<br>");

            // 5. Send JSON Response
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("reply", formattedResponse);
            response.getWriter().write(gson.toJson(responseMap));

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("reply", "I am currently unavailable. (Server Error)");
            response.getWriter().write(gson.toJson(errorMap));
        }
    }

    private String buildSmartContext() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("You are 'Disha', the AI assistant for the IRCTC Clone app. ");
        sb.append("You have access to a LOCAL DATABASE of trains and general travel knowledge. ");
        sb.append("Be concise, helpful, and use emojis 🚆.\n\n");

        sb.append("### 🚆 LOCAL TRAIN DATABASE:\n");
        try {
            List<Train> trains = trainDAO.getAllTrains();
            if (trains.isEmpty()) {
                sb.append("(Database empty. Rely on general knowledge).\n");
            } else {
                for (Train t : trains) {
                    sb.append(String.format("- [Train #%s] %s: %s to %s. Departs: %s, Arrives: %s. Seats: %d. Base Fare: %.2f\n",
                            t.getTrainNumber(), t.getTrainName(), t.getSource(), t.getDestination(),
                            t.getDepartureTime(), t.getArrivalTime(), t.getAvailableSeats(), t.getFarePerKm()));
                }
            }
        } catch (Exception e) {
            sb.append("(Database offline).\n");
        }

        sb.append("\n### 🧠 GUIDELINES:\n");
        sb.append("1. Check the 'Local Database' first for schedules.\n");
        sb.append("2. If a train isn't found locally, say: 'I don't have that route in my local system, but typically trains run there. Please check official sources.'\n");
        sb.append("3. You can answer general questions about tourism, food, and safety.\n");
        sb.append("4. App Features: PNR Status, Live Status, Seat Selection are available on the home page.\n");
        
        return sb.toString();
    }
}