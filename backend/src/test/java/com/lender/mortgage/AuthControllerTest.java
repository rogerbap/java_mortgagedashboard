package com.lender.mortgage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lender.mortgage.dto.request.LoginRequest;
import com.lender.mortgage.dto.response.AuthResponse;
import com.lender.mortgage.dto.response.UserResponse;
import com.lender.mortgage.entity.enums.UserRole;
import com.lender.mortgage.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("test@example.com");
        userResponse.setRole(UserRole.LOAN_OFFICER);

        authResponse = new AuthResponse();
        authResponse.setToken("mock-jwt-token");
        authResponse.setRefreshToken("mock-refresh-token");
        authResponse.setExpiresIn(86400L);
        authResponse.setUser(userResponse);
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenValidCredentials() throws Exception {
        // Arrange
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.data.user.email").value("test@example.com"));
    }

    @Test
    void login_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Arrange
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail("invalid-email");
        // Missing password

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void logout_ShouldReturnSuccess_WhenValidToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/logout")
                .with(csrf())
                .header("Authorization", "Bearer mock-jwt-token"))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.success").value(true));
    }
}