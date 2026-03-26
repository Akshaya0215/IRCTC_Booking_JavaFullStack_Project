package com.irctc.servlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.irctc.service.TrainService;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/LiveStatusServlet")
public class LiveStatusServlet extends HttpServlet {

    private TrainService trainService = new TrainService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String trainNo = request.getParameter("trainNo");
        String startDay = request.getParameter("startDay");

        // Default to Today (0) if no startDay is provided in the request
        if (startDay == null || startDay.isEmpty()) {
            startDay = "0";
        }

        if (trainNo == null || trainNo.trim().isEmpty()) {
            response.sendRedirect("jsp/liveStatus.jsp");
            return;
        }

        try {
            // 1. Fetch Data using the updated Service (passing the startDay)
            JsonObject liveResponse = trainService.getLiveTrainStatus(trainNo, startDay);
            JsonObject scheduleResponse = trainService.getTrainSchedule(trainNo);

            boolean isLiveSuccess = liveResponse != null && 
                                    liveResponse.has("success") && 
                                    liveResponse.get("success").getAsBoolean();

            if (isLiveSuccess) {
                JsonObject liveData = liveResponse.getAsJsonObject("data");
                request.setAttribute("liveData", liveData);

                // --- Process Schedule ---
                List<JsonObject> stationList = new ArrayList<>();
                boolean hasSchedule = false;

                if (scheduleResponse != null && scheduleResponse.has("success") && scheduleResponse.get("success").getAsBoolean()) {
                    JsonElement scheduleDataElement = scheduleResponse.get("data");

                    if (scheduleDataElement.isJsonArray()) {
                        JsonArray routeArr = scheduleDataElement.getAsJsonArray();
                        for (JsonElement e : routeArr) {
                            stationList.add(e.getAsJsonObject());
                        }
                        hasSchedule = true;
                    } 
                    else if (scheduleDataElement.isJsonObject()) {
                        JsonObject scheduleObj = scheduleDataElement.getAsJsonObject();
                        if (scheduleObj.has("route")) {
                            JsonArray routeArr = scheduleObj.getAsJsonArray("route");
                            for (JsonElement e : routeArr) {
                                stationList.add(e.getAsJsonObject());
                            }
                            hasSchedule = true;
                        }
                    }
                }

                request.setAttribute("stationList", stationList);
                request.setAttribute("hasSchedule", hasSchedule);
                request.setAttribute("historyList", new ArrayList<>()); 

                request.getRequestDispatcher("jsp/liveStatusResult.jsp").forward(request, response);

            } else {
                String errorMsg = "Could not track train " + trainNo;
                if (liveResponse != null && liveResponse.has("message")) {
                    errorMsg = liveResponse.get("message").getAsString();
                }
                request.setAttribute("errorMessage", errorMsg);
                request.getRequestDispatcher("jsp/liveStatus.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "System Error: " + e.getMessage());
            request.getRequestDispatcher("jsp/liveStatus.jsp").forward(request, response);
        }
    }
}