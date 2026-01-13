package com.example.sql_admin_auth.service;

import com.example.sql_admin_auth.config.GeminiConfig;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final GeminiConfig geminiConfig;
    private final RestTemplate restTemplate;
    private final Gson gson;

    public GeminiService(GeminiConfig geminiConfig) {
        this.geminiConfig = geminiConfig;
        this.restTemplate = new RestTemplate();
        this.gson = new Gson();
    }

    public String extractJobDetails(String rawText) {
        String prompt = "Extract job details from the text below and return a strictly valid JSON object. \n" +
                "Rules:\n" +
                "1. companyName: Extract the company name (e.g., 'Cisco', 'TCS'). If not found, use 'N/A'.\n" +
                "2. role: Extract the Job Role (Max 5 words). If not found, use 'N/A'.\n" +
                "3. eligibility: Short summary (Max 15 words). Focus on degrees (B.Tech, BCA, MCA, Non-tech).\n" +
                "4. salary: Extract salary/stipend (e.g., '₹4.5 LPA', '30k/month'). If not found, use 'N/A'.\n" +
                "5. location: City, Country (e.g., 'Bangalore, India'). If remote, say 'Remote'.\n" +
                "6. lastDateToApply: 'YYYY-MM-DD' or 'ASAP'.\n" +
                "7. duration: (For Internships only) e.g., '6 Months'. If not found, use 'N/A'.\n" +
                "8. batch: e.g., '2025', '2026'.\n" +
                "Return ONLY JSON. No markdown.\n\n" +
                "Raw Text: " + rawText.substring(0, Math.min(rawText.length(), 6000));

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();

        part.put("text", prompt);
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // --- RETRY LOGIC START ---
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        geminiConfig.getFullUrl(),
                        org.springframework.http.HttpMethod.POST,
                        entity,
                        String.class
                );
                return parseGeminiResponse(response.getBody());

            } catch (HttpClientErrorException.TooManyRequests e) {
                // If we hit the rate limit (429), WAIT and RETRY
                System.out.println("⚠️ Quota Exceeded (429). Waiting 60 seconds before retry...");
                try { Thread.sleep(70000); } catch (InterruptedException ie) {} // Wait 1 min
                attempt++;
            } catch (Exception e) {
                e.printStackTrace();
                return "{}"; // Other errors, fail immediately
            }
        }
        return "{}"; // Failed after 3 retries
    }

    private String parseGeminiResponse(String responseBody) {
        try {
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
            if (candidates.size() > 0) {
                JsonObject content = candidates.get(0).getAsJsonObject().getAsJsonObject("content");
                JsonArray parts = content.getAsJsonArray("parts");
                String text = parts.get(0).getAsJsonObject().get("text").getAsString();
                return text.replace("```json", "").replace("```", "").trim();
            }
        } catch (Exception e) {
            System.err.println("Gemini Parse Error: " + e.getMessage());
        }
        return "{}";
    }
}