package com.irctc.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiService {

    // 🔑 PASTE YOUR API KEY HERE
    private static final String API_KEY = "AIzaSyD1RSx8-YwjCH0cJEERZR4sIa1Gz8u84Ss";
    
    // Using the Stable 2.5 Flash Model
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    private final HttpClient client;

    public GeminiService() {
        this.client = HttpClient.newHttpClient();
    }

    public String askGemini(String promptText) {
        try {
            // 1. Construct JSON Payload
            JsonObject textPart = new JsonObject();
            textPart.addProperty("text", promptText);
            
            JsonArray partsArray = new JsonArray();
            partsArray.add(textPart);
            
            JsonObject contentObj = new JsonObject();
            contentObj.add("parts", partsArray);
            
            JsonArray contentsArray = new JsonArray();
            contentsArray.add(contentObj);
            
            JsonObject payload = new JsonObject();
            payload.add("contents", contentsArray);

            // 2. Send Request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 3. Parse Response
            if (response.statusCode() == 200) {
                JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                
                if (jsonResponse.has("candidates")) {
                    JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
                    if (candidates.size() > 0) {
                        JsonObject candidate = candidates.get(0).getAsJsonObject();
                        // Check for safety blocks
                        if (candidate.has("finishReason") && !"STOP".equals(candidate.get("finishReason").getAsString())) {
                            return "I cannot answer that query due to safety guidelines.";
                        }
                        if (candidate.has("content")) {
                            return candidate.getAsJsonObject("content")
                                    .getAsJsonArray("parts")
                                    .get(0).getAsJsonObject()
                                    .get("text").getAsString();
                        }
                    }
                }
                return "I couldn't generate a response. Please try again.";
            } else {
                System.err.println("Gemini API Error: " + response.body());
                return "AI Service Unavailable (Status " + response.statusCode() + ").";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error connecting to AI service: " + e.getMessage();
        }
    }
}