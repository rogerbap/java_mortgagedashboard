package com.lender.mortgage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "*")
public class HealthController {
    
    @GetMapping("/api/health")
    public String health() {
        return "Mortgage Backend API is running!";
    }
}