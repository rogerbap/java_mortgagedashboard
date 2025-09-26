package com.lender.mortgage.entity;

import com.lender.mortgage.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;
    
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
    
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;
    
    @Column(name = "active", nullable = false)
    private Boolean active = true;
    
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;
    
    @Column(name = "email_verification_token", length = 255)
    private String emailVerificationToken;
    
    @Column(name = "password_reset_token", length = 255)
    private String passwordResetToken;
    
    @Column(name = "password_reset_expires_at")
    private LocalDateTime passwordResetExpiresAt;
    
    // Audit fields
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    // Profile information
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;
    
    @Column(name = "department", length = 100)
    private String department;
    
    @Column(name = "title", length = 100)
    private String title;
    
    @Column(name = "employee_id", unique = true, length = 50)
    private String employeeId;
    
    @Column(name = "manager_id")
    private Long managerId;
    
    // Convenience methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getDisplayName() {
        return getFullName() + " (" + role.getDisplayName() + ")";
    }
    
    public boolean isManager() {
        return role == UserRole.LOAN_OFFICER || role == UserRole.UNDERWRITER || role == UserRole.ADMIN;
    }
    
    public boolean canViewLoan(Loan loan) {
        if (role == UserRole.ADMIN) return true;
        if (role == UserRole.LOAN_OFFICER && loan.getLoanOfficer() != null && loan.getLoanOfficer().getId().equals(this.id)) return true;
        if (role == UserRole.PROCESSOR && loan.getProcessor() != null && loan.getProcessor().getId().equals(this.id)) return true;
        if (role == UserRole.UNDERWRITER && loan.getUnderwriter() != null && loan.getUnderwriter().getId().equals(this.id)) return true;
        return false;
    }
    
    public boolean canEditLoan(Loan loan) {
        if (role == UserRole.ADMIN) return true;
        if (role == UserRole.LOAN_OFFICER && loan.getLoanOfficer() != null && loan.getLoanOfficer().getId().equals(this.id)) return true;
        return false;
    }
    
    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    @Override
    public String getPassword() {
        return passwordHash;
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return active;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return active;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        // Check if password needs to be changed (e.g., after 90 days)
        if (passwordChangedAt == null) return true;
        return passwordChangedAt.isAfter(LocalDateTime.now().minusDays(90));
    }
    
    @Override
    public boolean isEnabled() {
        return active && emailVerified;
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (passwordChangedAt == null) {
            passwordChangedAt = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Password management
    public void updatePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.passwordChangedAt = LocalDateTime.now();
        // Clear any reset tokens
        this.passwordResetToken = null;
        this.passwordResetExpiresAt = null;
    }
    
    // This method was missing and causing the setPassword error!
    public void setPassword(String password) {
        this.passwordHash = password;
        this.passwordChangedAt = LocalDateTime.now();
    }
    
    public boolean isPasswordResetTokenValid() {
        return passwordResetToken != null && 
               passwordResetExpiresAt != null && 
               passwordResetExpiresAt.isAfter(LocalDateTime.now());
    }
    
    public void setPasswordResetToken(String token, int expirationHours) {
        this.passwordResetToken = token;
        this.passwordResetExpiresAt = LocalDateTime.now().plusHours(expirationHours);
    }
    
    public void clearPasswordReset() {
        this.passwordResetToken = null;
        this.passwordResetExpiresAt = null;
    }
    
    // Login tracking
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }
    
    // Email verification
    public void verifyEmail() {
        this.emailVerified = true;
        this.emailVerificationToken = null;
    }
    
    public boolean hasRole(UserRole requiredRole) {
        return this.role == requiredRole;
    }
    
    public boolean hasAnyRole(UserRole... roles) {
        for (UserRole requiredRole : roles) {
            if (this.role == requiredRole) {
                return true;
            }
        }
        return false;
    }
}