package com.lender.mortgage.controller;

import com.lender.mortgage.dto.request.LoginRequest;
import com.lender.mortgage.dto.response.ApiResponse;
import com.lender.mortgage.dto.response.AuthResponse;
import com.lender.mortgage.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refresh JWT token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestParam String refreshToken) {
        AuthResponse authResponse = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", authResponse));
    }
    
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user and invalidate token")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
        // Remove "Bearer " prefix
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        authService.logout(jwtToken);
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }
    
    @GetMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate JWT token")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestParam String token) {
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(ApiResponse.success("Token validation result", isValid));
    }
}