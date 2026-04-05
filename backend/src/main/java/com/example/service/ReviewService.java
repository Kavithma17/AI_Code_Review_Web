package com.example.service;

import com.example.analyzers.JavaAnalyzer;
import com.example.model.ReviewRequest;
import com.example.model.ReviewResponse;
import com.example.utils.TempFileWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class ReviewService {

    private final JavaAnalyzer javaAnalyzer;

    @Value("${gemini.api.key}")
    private String apiKey;

    public ReviewService() {
        this.javaAnalyzer = new JavaAnalyzer();
    }

    // THIS is the method your ReviewController is looking for!
    public ReviewResponse reviewCode(ReviewRequest request) {
        File tempFile = null;
        try {
            // 1. Safely write the incoming string code to a unique temp file
            tempFile = TempFileWriter.writeToFile(request.getCode());

            // 2. Run Checkstyle & PMD
            String staticReport = javaAnalyzer.analyze(tempFile);

            // 3. Get AI Insights
            String aiReview = getAiReview(request.getCode(), staticReport);

            // 4. Return both to the frontend
            return new ReviewResponse(staticReport, aiReview);

        } catch (Exception e) {
            e.printStackTrace();
            return new ReviewResponse("Static Analysis Failed", "Error processing request: " + e.getMessage());
        } finally {
            // 5. Clean up the temp file
            TempFileWriter.deleteTempFileAndDir(tempFile);
        }
    }
private String getAiReview(String code, String staticReport) throws Exception {
    String prompt = "You are an expert Java code reviewer. Review the following code and report.\n\n" +
                    "=== STATIC ANALYSIS ===\n" + staticReport + "\n\n" +
                    "=== JAVA CODE ===\n" + code;

    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();
    ArrayNode contentsArray = rootNode.putArray("contents");
    ObjectNode contentNode = contentsArray.addObject();
    ArrayNode partsArray = contentNode.putArray("parts");
    partsArray.addObject().put("text", prompt);

    String jsonPayload = mapper.writeValueAsString(rootNode);

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
            .build();

    HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    
    // Parse the response body
    JsonNode root = mapper.readTree(response.body());
    
    // 1. Check if Gemini returned an API error (like invalid key or model)
    if (root.has("error")) {
        return "Gemini API Error: " + root.get("error").get("message").asText();
    }

    // 2. Safely navigate the JSON tree
    if (root.has("candidates") && root.get("candidates").isArray() && !root.get("candidates").isEmpty()) {
        JsonNode firstCandidate = root.get("candidates").get(0);
        
        // Sometimes Gemini blocks a response for safety; check if content exists
        if (firstCandidate.has("content") && firstCandidate.get("content").has("parts")) {
            return firstCandidate.get("content")
                                 .get("parts")
                                 .get(0)
                                 .get("text")
                                 .asText();
        }
    }
    
    // 3. Fallback if the structure is unexpected
    return "AI was unable to generate a review. Response received: " + response.body();
}
}