package com.lender.mortgage.service;

import com.lender.mortgage.dto.request.CreateConditionRequest;
import com.lender.mortgage.dto.request.UpdateConditionRequest;
import com.lender.mortgage.dto.response.ConditionResponse;
import com.lender.mortgage.entity.LoanCondition;
import com.lender.mortgage.entity.enums.ConditionStatus;
import com.lender.mortgage.entity.enums.ConditionType;
import com.lender.mortgage.entity.enums.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ConditionService {
    
    /**
     * Create a new condition
     */
    ConditionResponse createCondition(CreateConditionRequest request, String createdByEmail);
    
    /**
     * Update condition details
     */
    ConditionResponse updateCondition(Long conditionId, UpdateConditionRequest request, String updatedByEmail);
    
    /**
     * Get condition by ID
     */
    ConditionResponse getConditionById(Long conditionId);
    
    /**
     * Get conditions by loan ID
     */
    List<ConditionResponse> getConditionsByLoan(Long loanId);
    
    /**
     * Get conditions assigned to user
     */
    Page<ConditionResponse> getConditionsByUser(Long userId, Pageable pageable);
    
    /**
     * Get active conditions assigned to user
     */
    Page<ConditionResponse> getActiveConditionsByUser(Long userId, Pageable pageable);
    
    /**
     * Get conditions by status
     */
    Page<ConditionResponse> getConditionsByStatus(ConditionStatus status, Pageable pageable);
    
    /**
     * Get active conditions
     */
    Page<ConditionResponse> getActiveConditions(Pageable pageable);
    
    /**
     * Get overdue conditions
     */
    List<ConditionResponse> getOverdueConditions();
    
    /**
     * Get conditions due soon
     */
    List<ConditionResponse> getConditionsDueSoon(int daysAhead);
    
    /**
     * Get high priority active conditions
     */
    List<ConditionResponse> getHighPriorityActiveConditions();
    
    /**
     * Mark condition as completed
     */
    ConditionResponse completeCondition(Long conditionId, String completedByEmail, String notes);
    
    /**
     * Mark condition as waived
     */
    ConditionResponse waiveCondition(Long conditionId, String waivedByEmail, String reason);
    
    /**
     * Assign condition to user
     */
    ConditionResponse assignCondition(Long conditionId, Long userId, String assignedByEmail);
    
    /**
     * Update condition priority
     */
    ConditionResponse updateConditionPriority(Long conditionId, Priority priority, String updatedByEmail);
    
    /**
     * Delete condition
     */
    void deleteCondition(Long conditionId, String deletedByEmail);
    
    /**
     * Get condition entity by ID (for internal use)
     */
    LoanCondition getConditionEntity(Long conditionId);
    
    /**
     * Check if all loan conditions are satisfied
     */
    boolean areAllConditionsSatisfied(Long loanId);
}
