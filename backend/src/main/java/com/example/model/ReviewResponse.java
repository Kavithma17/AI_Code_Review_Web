package com.example.model;

public class ReviewResponse {
    private String message;
    private Integer score;

    public ReviewResponse() {} // default constructor

    public ReviewResponse(String message, Integer score) {
        this.message = message;
        this.score = score;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
}