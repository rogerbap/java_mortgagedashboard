package com.lender.mortgage.service.impl;

import com.lender.mortgage.dto.request.CreateUserRequest;
import com.lender.mortgage.dto.request.UpdateUserRequest;
import com.lender.mortgage.dto.response.UserResponse;
import com.lender.mortgage.entity.User;
import com.lender.mortgage.entity.enums.UserRole;
import com.lender.mortgage.exception.BadRequestException;
import com.lender.mortgage.exception.ResourceNotFoundException;
import com.lender.mortgage.repository.UserRepository;
import com.lender.mortgage.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserResponse createUser(CreateUserRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }
        
        // Create new user
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setActive(true);
        
        User savedUser = userRepository.save(user);
        
        logger.info("Created new user: {} with role: {}", savedUser.getEmail(), savedUser.getRole());
        
        return new UserResponse(savedUser);
    }
    
    @Override
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = getUserEntity(userId);
        
        // Check email uniqueness if being updated
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
                throw new BadRequestException("Email already exists: " + request.getEmail());
            }
        }
        
        // Update fields if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        
        User savedUser = userRepository.save(user);
        
        logger.info("Updated user: {}", savedUser.getEmail());
        
        return new UserResponse(savedUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = getUserEntity(userId);
        return new UserResponse(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = getUserEntityByEmail(email);
        return new UserResponse(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findByActiveTrue(pageable)
                .map(UserResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String searchTerm, Pageable pageable) {
        return userRepository.findActiveUsersBySearch(searchTerm, pageable)
                .map(UserResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(UserRole role) {
        return userRepository.findByRoleAndActiveTrue(role)
                .stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deactivateUser(Long userId) {
        User user = getUserEntity(userId);
        user.setActive(false);
        userRepository.save(user);
        
        logger.info("Deactivated user: {}", user.getEmail());
    }
    
    @Override
    public void activateUser(Long userId) {
        User user = getUserEntity(userId);
        user.setActive(true);
        userRepository.save(user);
        
        logger.info("Activated user: {}", user.getEmail());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public void updateLastLogin(String email) {
        User user = getUserEntityByEmail(email);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}