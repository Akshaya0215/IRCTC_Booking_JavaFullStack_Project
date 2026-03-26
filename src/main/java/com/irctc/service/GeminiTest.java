package com.irctc.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiTest {

    // 🔑 PASTE YOUR API KEY HERE
    private static final String API_KEY = "AIzaSyD1RSx8-YwjCH0cJEERZR4sIa1Gz8u84Ss";
    
    // Endpoint to LIST all available models
    private static final String LIST_URL = "https://generativelanguage.googleapis.com/v1beta/models?key=" + API_KEY;

    public static void main(String[] args) {
        try {
            System.out.println("--- Testing Gemini API Connection ---");
            
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LIST_URL))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response Code: " + response.statusCode());
            
            if (response.statusCode() == 200) {
                System.out.println("✅ SUCCESS! Found these models:");
                // Simple string print to see valid names (look for "name": "models/...")
                System.out.println(response.body());
            } else {
                System.err.println("❌ FAILED. API Error:");
                System.out.println(response.body());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}