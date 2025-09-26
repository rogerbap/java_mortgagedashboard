package com.lender.mortgage.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import com.lender.mortgage.entity.enums.ConditionType;
import com.lender.mortgage.entity.enums.Priority;

public class CreateConditionRequest {
    
    private Long loanId;  // Moved to top for better organization
    
    @NotNull(message = "Condition type is required")
    private ConditionType type;  // Changed from String to ConditionType
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Priority is required")
    private Priority priority;  // Changed from String to Priority
    
    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;
    
    @Size(max = 2000, message = "Comments cannot exceed 2000 characters")
    private String comments;
    
    private Long assignedToId;
    
    @Size(max = 2000, message = "Internal notes cannot exceed 2000 characters")
    private String internalNotes;
    
    // Default constructor
    public CreateConditionRequest() {}
    
    // All getters and setters with correct types
    public Long getLoanId() { 
        return loanId; 
    }
    public void setLoanId(Long loanId) { 
        this.loanId = loanId; 
    }
    
    public ConditionType getType() {  // Changed return type to ConditionType
        return type; 
    }
    public void setType(ConditionType type) {  // Changed parameter type to ConditionType
        this.type = type; 
    }
    
    public String getTitle() { 
        return title; 
    }
    public void setTitle(String title) { 
        this.title = title; 
    }
    
    public String getDescription() { 
        return description; 
    }
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public Priority getPriority() {  // Changed return type to Priority
        return priority; 
    }
    public void setPriority(Priority priority) {  // Changed parameter type to Priority
        this.priority = priority; 
    }
    
    public LocalDate getDueDate() { 
        return dueDate; 
    }
    public void setDueDate(LocalDate dueDate) { 
        this.dueDate = dueDate; 
    }
    
    public String getComments() { 
        return comments; 
    }
    public void setComments(String comments) { 
        this.comments = comments; 
    }
    
    public Long getAssignedToId() { 
        return assignedToId; 
    }
    public void setAssignedToId(Long assignedToId) { 
        this.assignedToId = assignedToId; 
    }
    
    public String getInternalNotes() { 
        return internalNotes; 
    }
    public void setInternalNotes(String internalNotes) { 
        this.internalNotes = internalNotes; 
    }
}