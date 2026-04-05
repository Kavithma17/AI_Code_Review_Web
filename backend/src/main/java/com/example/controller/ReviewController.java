package com.example.controller;

import com.example.model.ReviewRequest;
import com.example.model.ReviewResponse;
import com.example.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
@CrossOrigin(origins = "http://localhost:5173") // React dev server
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ReviewResponse reviewCode(@RequestBody ReviewRequest request) {
        return reviewService.reviewCode(request);
    }
}