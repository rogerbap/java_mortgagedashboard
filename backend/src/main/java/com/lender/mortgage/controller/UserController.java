package com.lender.mortgage.controller;

import com.lender.mortgage.dto.request.CreateUserRequest;
import com.lender.mortgage.dto.request.UpdateUserRequest;
import com.lender.mortgage.dto.response.ApiResponse;
import com.lender.mortgage.dto.response.UserResponse;
import com.lender.mortgage.entity.enums.UserRole;
import com.lender.mortgage.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User management endpoints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('UNDERWRITER')")
    @Operation(summary = "Create user", description = "Create a new user (Manager/Underwriter only)")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", user));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('UNDERWRITER')")
    @Operation(summary = "Get all users", description = "Get all active users with pagination")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('MANAGER') or hasRole('UNDERWRITER') or hasRole('PROCESSOR')")
    @Operation(summary = "Search users", description = "Search users by name or email")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUsers(
            @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserResponse> users = userService.searchUsers(q, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search results", users));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('UNDERWRITER') or hasRole('PROCESSOR')")
    @Operation(summary = "Get user by ID", description = "Get user details by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable @Parameter(description = "User ID") Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }
    
    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('UNDERWRITER') or hasRole('PROCESSOR')")
    @Operation(summary = "Get users by role", description = "Get all users with specific role")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(
            @PathVariable @Parameter(description = "User role") UserRole role) {
        List<UserResponse> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('UNDERWRITER')")
    @Operation(summary = "Update user", description = "Update user details (Manager/Underwriter only)")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable @Parameter(description = "User ID") Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", user));
    }
    
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Deactivate user", description = "Deactivate user account (Manager only)")
    public ResponseEntity<ApiResponse<String>> deactivateUser(
            @PathVariable @Parameter(description = "User ID") Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", null));
    }
    
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Activate user", description = "Activate user account (Manager only)")
    public ResponseEntity<ApiResponse<String>> activateUser(
            @PathVariable @Parameter(description = "User ID") Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User activated successfully", null));
    }
}