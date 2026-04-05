// src/main/java/com/example/model/ReviewResponse.java
package com.example.model;

public class ReviewResponse {
    private String staticAnalysis;
    private String aiReview;

    // Constructors
    public ReviewResponse() {}

    public ReviewResponse(String staticAnalysis, String aiReview) {
        this.staticAnalysis = staticAnalysis;
        this.aiReview = aiReview;
    }

    // Getters and Setters
    public String getStaticAnalysis() {
        return staticAnalysis;
    }

    public void setStaticAnalysis(String staticAnalysis) {
        this.staticAnalysis = staticAnalysis;
    }

    public String getAiReview() {
        return aiReview;
    }

    public void setAiReview(String aiReview) {
        this.aiReview = aiReview;
    }
}