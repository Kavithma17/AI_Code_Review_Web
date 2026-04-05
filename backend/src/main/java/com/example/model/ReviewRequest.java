// src/main/java/com/example/model/ReviewRequest.java
package com.example.model;

public class ReviewRequest {
    private String code;

    // Constructors
    public ReviewRequest() {}
    
    public ReviewRequest(String code) {
        this.code = code;
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}