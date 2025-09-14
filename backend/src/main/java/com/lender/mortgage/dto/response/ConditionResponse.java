package com.lender.mortgage.dto.response;

import com.lender.mortgage.entity.LoanCondition;
import com.lender.mortgage.entity.enums.ConditionStatus;
import com.lender.mortgage.entity.enums.ConditionType;
import com.lender.mortgage.entity.enums.Priority;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConditionResponse {
    
    private Long id;
    private Long loanId;
    private String loanNumber;
    private ConditionType type;
    private String title;
    private String description;
    private ConditionStatus status;
    private Priority priority;
    private UserResponse assignedTo;
    private LocalDateTime dueDate;
    private LocalDateTime completedDate;
    private String comments;
    private String internalNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String lastModifiedBy;
    
    // Calculated fields
    private Boolean isOverdue;
    private Boolean isDueSoon;
    private Long daysUntilDue;
    private Long daysOverdue;
    
    public ConditionResponse() {}
    
    public ConditionResponse(LoanCondition condition) {
        this.id = condition.getId();
        this.loanId = condition.getLoan().getId();
        this.loanNumber = condition.getLoan().getLoanNumber();
        this.type = condition.getType();
        this.title = condition.getTitle();
        this.description = condition.getDescription();
        this.status = condition.getStatus();
        this.priority = condition.getPriority();
        
        if (condition.getAssignedTo() != null) {
            this.assignedTo = new UserResponse(condition.getAssignedTo());
        }
        
        this.dueDate = condition.getDueDate();
        this.completedDate = condition.getCompletedDate();
        this.comments = condition.getComments();
        this.internalNotes = condition.getInternalNotes();
        this.createdAt = condition.getCreatedAt();
        this.updatedAt = condition.getUpdatedAt();
        this.createdBy = condition.getCreatedBy();
        this.lastModifiedBy = condition.getLastModifiedBy();
        
        // Calculate derived fields
        this.isOverdue = condition.isOverdue();
        
        if (condition.getDueDate() != null && condition.getStatus().isActive()) {
            long daysUntil = java.time.Duration.between(LocalDateTime.now(), condition.getDueDate()).toDays();
            this.daysUntilDue = daysUntil;
            this.isDueSoon = daysUntil <= 3 && daysUntil >= 0;
            
            if (daysUntil < 0) {
                this.daysOverdue = Math.abs(daysUntil);
            }
        }
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getLoanId() { return loanId; }
    public void setLoanId(Long loanId) { this.loanId = loanId; }
    
    public String getLoanNumber() { return loanNumber; }
    public void setLoanNumber(String loanNumber) { this.loanNumber = loanNumber; }
    
    public ConditionType getType() { return type; }
    public void setType(ConditionType type) { this.type = type; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ConditionStatus getStatus() { return status; }
    public void setStatus(ConditionStatus status) { this.status = status; }
    
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    
    public UserResponse getAssignedTo() { return assignedTo; }
    public void setAssignedTo(UserResponse assignedTo) { this.assignedTo = assignedTo; }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    
    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }
    
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    
    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    
    public Boolean getIsOverdue() { return isOverdue; }
    public void setIsOverdue(Boolean isOverdue) { this.isOverdue = isOverdue; }
    
    public Boolean getIsDueSoon() { return isDueSoon; }
    public void setIsDueSoon(Boolean isDueSoon) { this.isDueSoon = isDueSoon; }
    
    public Long getDaysUntilDue() { return daysUntilDue; }
    public void setDaysUntilDue(Long daysUntilDue) { this.daysUntilDue = daysUntilDue; }
    
    public Long getDaysOverdue() { return daysOverdue; }
    public void setDaysOverdue(Long daysOverdue) { this.daysOverdue = daysOverdue; }
}