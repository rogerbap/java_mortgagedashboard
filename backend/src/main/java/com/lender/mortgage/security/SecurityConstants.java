package com.lender.mortgage.security;

public class SecurityConstants {
    public static final String JWT_SECRET_KEY = "mySecretKey";
    public static final long JWT_EXPIRATION_TIME = 86400000; // 24 hours
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 604800000; // 7 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    
    // Private constructor to prevent instantiation
    private SecurityConstants() {}
}