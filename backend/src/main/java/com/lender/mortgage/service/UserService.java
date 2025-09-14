package com.lender.mortgage.service;

import com.lender.mortgage.dto.request.CreateUserRequest;
import com.lender.mortgage.dto.request.UpdateUserRequest;
import com.lender.mortgage.dto.response.UserResponse;
import com.lender.mortgage.entity.User;
import com.lender.mortgage.entity.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    
    /**
     * Create a new user
     */
    UserResponse createUser(CreateUserRequest request);
    
    /**
     * Update an existing user
     */
    UserResponse updateUser(Long userId, UpdateUserRequest request);
    
    /**
     * Get user by ID
     */
    UserResponse getUserById(Long userId);
    
    /**
     * Get user by email
     */
    UserResponse getUserByEmail(String email);
    
    /**
     * Get all active users with pagination
     */
    Page<UserResponse> getAllUsers(Pageable pageable);
    
    /**
     * Search users by name or email
     */
    Page<UserResponse> searchUsers(String searchTerm, Pageable pageable);
    
    /**
     * Get users by role
     */
    List<UserResponse> getUsersByRole(UserRole role);
    
    /**
     * Deactivate a user
     */
    void deactivateUser(Long userId);
    
    /**
     * Activate a user
     */
    void activateUser(Long userId);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Update user's last login time
     */
    void updateLastLogin(String email);
    
    /**
     * Get entity by ID (for internal use)
     */
    User getUserEntity(Long userId);
    
    /**
     * Get entity by email (for internal use)
     */
    User getUserEntityByEmail(String email);
}