package com.lender.mortgage.service.impl;

import com.lender.mortgage.dto.request.LoginRequest;
import com.lender.mortgage.dto.response.AuthResponse;
import com.lender.mortgage.dto.response.UserResponse;
import com.lender.mortgage.entity.User;
import com.lender.mortgage.exception.UnauthorizedException;
import com.lender.mortgage.security.JwtTokenProvider;
import com.lender.mortgage.service.AuthService;
import com.lender.mortgage.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserService userService;
    
    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
            
            // Generate tokens
            String token = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);
            Long expiresIn = tokenProvider.getTokenExpiration();
            
            // Get user details
            UserResponse user = userService.getUserByEmail(request.getEmail());
            
            // Update last login time
            userService.updateLastLogin(request.getEmail());
            
            logger.info("User {} logged in successfully", request.getEmail());
            
            return new AuthResponse(token, refreshToken, expiresIn, user);
            
        } catch (AuthenticationException e) {
            logger.warn("Failed login attempt for email: {}", request.getEmail());
            throw new UnauthorizedException("Invalid email or password");
        }
    }
    
    @Override
    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token);
    }
    
    @Override
    public String getEmailFromToken(String token) {
        return tokenProvider.getEmailFromToken(token);
    }
    
    @Override
    public String generateToken(String email) {
        User user = userService.getUserEntityByEmail(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user, null, user.getAuthorities()
        );
        return tokenProvider.generateToken(authentication);
    }
    
    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        
        String email = tokenProvider.getEmailFromRefreshToken(refreshToken);
        UserResponse user = userService.getUserByEmail(email);
        
        String newToken = generateToken(email);
        String newRefreshToken = tokenProvider.generateRefreshToken(
            new UsernamePasswordAuthenticationToken(email, null)
        );
        Long expiresIn = tokenProvider.getTokenExpiration();
        
        return new AuthResponse(newToken, newRefreshToken, expiresIn, user);
    }
    
    @Override
    public void logout(String token) {
        // Add token to blacklist (implement if needed)
        tokenProvider.invalidateToken(token);
        logger.info("User logged out, token invalidated");
    }
}