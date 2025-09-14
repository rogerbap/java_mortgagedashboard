package com.lender.mortgage.service;

import com.lender.mortgage.dto.request.LoginRequest;
import com.lender.mortgage.dto.response.AuthResponse;

public interface AuthService {
    
    /**
     * Authenticate user and generate JWT token
     */
    AuthResponse login(LoginRequest request);
    
    /**
     * Validate JWT token
     */
    boolean validateToken(String token);
    
    /**
     * Get email from JWT token
     */
    String getEmailFromToken(String token);
    
    /**
     * Generate JWT token for user
     */
    String generateToken(String email);
    
    /**
     * Refresh JWT token
     */
    AuthResponse refreshToken(String refreshToken);
    
    /**
     * Logout user (invalidate token)
     */
    void logout(String token);
}