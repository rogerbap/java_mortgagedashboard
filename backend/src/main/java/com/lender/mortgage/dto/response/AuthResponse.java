package com.lender.mortgage.dto.response;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private UserResponse user;
    private long expiresIn;

    public AuthResponse() {}

    public AuthResponse(String token, UserResponse user, long expiresIn) {
        this.token = token;
        this.user = user;
        this.expiresIn = expiresIn;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
}