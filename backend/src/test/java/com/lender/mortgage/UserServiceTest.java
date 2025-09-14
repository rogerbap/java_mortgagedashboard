package com.lender.mortgage.service;

import com.lender.mortgage.dto.request.CreateUserRequest;
import com.lender.mortgage.dto.response.UserResponse;
import com.lender.mortgage.entity.User;
import com.lender.mortgage.entity.enums.UserRole;
import com.lender.mortgage.exception.BadRequestException;
import com.lender.mortgage.exception.ResourceNotFoundException;
import com.lender.mortgage.repository.UserRepository;
import com.lender.mortgage.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private CreateUserRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setRole(UserRole.LOAN_OFFICER);
        testUser.setActive(true);

        createRequest = new CreateUserRequest();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setEmail("john.doe@example.com");
        createRequest.setPassword("password123");
        createRequest.setRole(UserRole.LOAN_OFFICER);
    }

    @Test
    void createUser_ShouldReturnUserResponse_WhenValidRequest() {
        // Arrange
        when(userRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(createRequest.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserResponse result = userService.createUser(createRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(createRequest.getEmail());
        assertThat(result.getFullName()).isEqualTo("John Doe");
        assertThat(result.getRole()).isEqualTo(UserRole.LOAN_OFFICER);
        
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        // Arrange
        when(userRepository.existsByEmail(createRequest.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(createRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email already exists");
        
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_ShouldReturnUserResponse_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        UserResponse result = userService.getUserById(userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with id: 999");
    }
}