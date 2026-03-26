package com.irctc.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TrainService {

    // ✅ Keep your existing key here
    private static final String API_KEY = "5bdbf25a0emshda1cd33d1214ee6p1abd69jsn31f816d47d02"; 
    private static final String API_HOST = "irctc-api2.p.rapidapi.com"; 
    private static final String BASE_URL = "https://" + API_HOST;

    private final HttpClient client;

    public TrainService() {
        this.client = HttpClient.newHttpClient();
    }

    // Method 1: PNR Status
    public JsonObject getPnrStatus(String pnr) {
        return callApi("/pnrStatus?pnr=" + pnr);
    }

    /**
     * Updated Method: Live Train Status
     * @param trainNo - The 5 digit train number
     * @param startDay - 0 for Today, 1 for Yesterday, 2 for Day Before
     */
    public JsonObject getLiveTrainStatus(String trainNo, String startDay) {
        // We use the startDay parameter dynamically now
        return callApi("/liveTrain?trainNumber=" + trainNo + "&startDay=" + startDay);
    }

    // Overloaded method for backward compatibility (defaults to Today)
    public JsonObject getLiveTrainStatus(String trainNo) {
        return getLiveTrainStatus(trainNo, "0");
    }
    
    // Method 3: Train Schedule
    public JsonObject getTrainSchedule(String trainNo) {
        return callApi("/trainSchedule?trainNumber=" + trainNo);
    }

    private JsonObject callApi(String endpoint) {
        try {
            String url = BASE_URL + endpoint;
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-rapidapi-key", API_KEY)
                    .header("x-rapidapi-host", API_HOST)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return JsonParser.parseString(response.body()).getAsJsonObject();
            } else {
                System.out.println("API Error: Status Code " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}