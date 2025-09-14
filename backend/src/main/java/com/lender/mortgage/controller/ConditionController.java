package com.lender.mortgage.controller;

import com.lender.mortgage.dto.request.CreateConditionRequest;
import com.lender.mortgage.dto.request.UpdateConditionRequest;
import com.lender.mortgage.dto.response.ApiResponse;
import com.lender.mortgage.dto.response.ConditionResponse;
import com.lender.mortgage.entity.enums.ConditionStatus;
import com.lender.mortgage.entity.enums.Priority;
import com.lender.mortgage.service.ConditionService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conditions")
@Tag(name = "Condition Management", description = "Loan condition management endpoints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ConditionController {
    
    @Autowired
    private ConditionService conditionService;
    
    @PostMapping
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Create condition", description = "Create a new loan condition")
    public ResponseEntity<ApiResponse<ConditionResponse>> createCondition(
            @Valid @RequestBody CreateConditionRequest request,
            Authentication authentication) {
        ConditionResponse condition = conditionService.createCondition(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Condition created successfully", condition));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER') or hasRole('BORROWER')")
    @Operation(summary = "Get condition by ID", description = "Get condition details by ID")
    public ResponseEntity<ApiResponse<ConditionResponse>> getConditionById(
            @PathVariable @Parameter(description = "Condition ID") Long id) {
        ConditionResponse condition = conditionService.getConditionById(id);
        return ResponseEntity.ok(ApiResponse.success("Condition retrieved successfully", condition));
    }
    
    @GetMapping("/loan/{loanId}")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER') or hasRole('BORROWER')")
    @Operation(summary = "Get conditions by loan", description = "Get all conditions for a specific loan")
    public ResponseEntity<ApiResponse<List<ConditionResponse>>> getConditionsByLoan(
            @PathVariable @Parameter(description = "Loan ID") Long loanId) {
        List<ConditionResponse> conditions = conditionService.getConditionsByLoan(loanId);
        return ResponseEntity.ok(ApiResponse.success("Conditions retrieved successfully", conditions));
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get conditions by user", description = "Get conditions assigned to specific user")
    public ResponseEntity<ApiResponse<Page<ConditionResponse>>> getConditionsByUser(
            @PathVariable @Parameter(description = "User ID") Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ConditionResponse> conditions = conditionService.getConditionsByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Conditions retrieved successfully", conditions));
    }
    
    @GetMapping("/my-conditions")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get my conditions", description = "Get conditions assigned to current user")
    public ResponseEntity<ApiResponse<Page<ConditionResponse>>> getMyConditions(
            Authentication authentication,
            @RequestParam(defaultValue = "false") @Parameter(description = "Active only") boolean activeOnly,
            @PageableDefault(size = 20) Pageable pageable) {
        
        // Note: This would require getting the user ID from the authentication
        // For now, assuming we have a method to get user ID from email
        // In real implementation, you'd inject UserService to get the user ID
        
        Page<ConditionResponse> conditions;
        if (activeOnly) {
            // conditions = conditionService.getActiveConditionsByUser(userId, pageable);
            conditions = conditionService.getActiveConditions(pageable); // Simplified for example
        } else {
            // conditions = conditionService.getConditionsByUser(userId, pageable);
            conditions = conditionService.getActiveConditions(pageable); // Simplified for example
        }
        
        return ResponseEntity.ok(ApiResponse.success("Conditions retrieved successfully", conditions));
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get conditions by status", description = "Get conditions with specific status")
    public ResponseEntity<ApiResponse<Page<ConditionResponse>>> getConditionsByStatus(
            @PathVariable @Parameter(description = "Condition status") ConditionStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ConditionResponse> conditions = conditionService.getConditionsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Conditions retrieved successfully", conditions));
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get active conditions", description = "Get all active conditions")
    public ResponseEntity<ApiResponse<Page<ConditionResponse>>> getActiveConditions(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ConditionResponse> conditions = conditionService.getActiveConditions(pageable);
        return ResponseEntity.ok(ApiResponse.success("Active conditions retrieved", conditions));
    }
    
    @GetMapping("/overdue")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get overdue conditions", description = "Get conditions that are past due date")
    public ResponseEntity<ApiResponse<List<ConditionResponse>>> getOverdueConditions() {
        List<ConditionResponse> conditions = conditionService.getOverdueConditions();
        return ResponseEntity.ok(ApiResponse.success("Overdue conditions retrieved", conditions));
    }
    
    @GetMapping("/due-soon")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get conditions due soon", description = "Get conditions due within specified days")
    public ResponseEntity<ApiResponse<List<ConditionResponse>>> getConditionsDueSoon(
            @RequestParam(defaultValue = "3") @Parameter(description = "Days ahead") int daysAhead) {
        List<ConditionResponse> conditions = conditionService.getConditionsDueSoon(daysAhead);
        return ResponseEntity.ok(ApiResponse.success("Conditions due soon retrieved", conditions));
    }
    
    @GetMapping("/high-priority")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get high priority conditions", description = "Get high priority active conditions")
    public ResponseEntity<ApiResponse<List<ConditionResponse>>> getHighPriorityActiveConditions() {
        List<ConditionResponse> conditions = conditionService.getHighPriorityActiveConditions();
        return ResponseEntity.ok(ApiResponse.success("High priority conditions retrieved", conditions));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Update condition", description = "Update condition details")
    public ResponseEntity<ApiResponse<ConditionResponse>> updateCondition(
            @PathVariable @Parameter(description = "Condition ID") Long id,
            @Valid @RequestBody UpdateConditionRequest request,
            Authentication authentication) {
        ConditionResponse condition = conditionService.updateCondition(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Condition updated successfully", condition));
    }
    
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Complete condition", description = "Mark condition as completed")
    public ResponseEntity<ApiResponse<ConditionResponse>> completeCondition(
            @PathVariable @Parameter(description = "Condition ID") Long id,
            @RequestParam(required = false) @Parameter(description = "Completion notes") String notes,
            Authentication authentication) {
        ConditionResponse condition = conditionService.completeCondition(id, authentication.getName(), notes);
        return ResponseEntity.ok(ApiResponse.success("Condition completed successfully", condition));
    }
    
    @PutMapping("/{id}/waive")
    @PreAuthorize("hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Waive condition", description = "Waive condition requirement")
    public ResponseEntity<ApiResponse<ConditionResponse>> waiveCondition(
            @PathVariable @Parameter(description = "Condition ID") Long id,
            @RequestParam @Parameter(description = "Waiver reason") String reason,
            Authentication authentication) {
        ConditionResponse condition = conditionService.waiveCondition(id, authentication.getName(), reason);
        return ResponseEntity.ok(ApiResponse.success("Condition waived successfully", condition));
    }
    
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Assign condition", description = "Assign condition to user")
    public ResponseEntity<ApiResponse<ConditionResponse>> assignCondition(
            @PathVariable @Parameter(description = "Condition ID") Long id,
            @RequestParam @Parameter(description = "User ID") Long userId,
            Authentication authentication) {
        ConditionResponse condition = conditionService.assignCondition(id, userId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Condition assigned successfully", condition));
    }
    
    @PutMapping("/{id}/priority")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Update condition priority", description = "Update condition priority level")
    public ResponseEntity<ApiResponse<ConditionResponse>> updateConditionPriority(
            @PathVariable @Parameter(description = "Condition ID") Long id,
            @RequestParam @Parameter(description = "Priority level") Priority priority,
            Authentication authentication) {
        ConditionResponse condition = conditionService.updateConditionPriority(id, priority, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Condition priority updated successfully", condition));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Delete condition", description = "Delete condition (Underwriter/Manager only)")
    public ResponseEntity<ApiResponse<String>> deleteCondition(
            @PathVariable @Parameter(description = "Condition ID") Long id,
            Authentication authentication) {
        conditionService.deleteCondition(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Condition deleted successfully", null));
    }
}
