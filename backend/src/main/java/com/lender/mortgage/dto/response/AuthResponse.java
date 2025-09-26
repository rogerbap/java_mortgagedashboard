package com.lender.mortgage.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    
    private String token;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private UserResponse user;
    
    // Default constructor
    public AuthResponse() {}
    
    // Constructor that matches what your code expects
    public AuthResponse(String token, String refreshToken, Long expiresIn, UserResponse user) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn != null ? expiresIn : 3600L; // Default 1 hour
        this.user = user;
    }
    
    // Alternative constructor
    public AuthResponse(String token, UserResponse user, long expiresIn) {
        this.token = token;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.user = user;
    }
    
    // All getters and setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    
    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
    
    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }
}