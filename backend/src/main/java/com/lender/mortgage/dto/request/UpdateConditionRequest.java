package com.lender.mortgage.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

import com.lender.mortgage.entity.enums.Priority;

public class UpdateConditionRequest {
    
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    private Priority priority;
    
    private LocalDate dueDate;
    
    @Size(max = 2000, message = "Comments cannot exceed 2000 characters")
    private String comments;
    
    @Size(max = 2000, message = "Internal notes cannot exceed 2000 characters")
    private String internalNotes;
    
    private Long assignedToId;
    
    private String status;
    
    @Size(max = 1000, message = "Resolution notes cannot exceed 1000 characters")
    private String resolutionNotes;
    
    // Default constructor
    public UpdateConditionRequest() {}

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
    
    public Priority getPriority() { 
        return priority; 
    }
    public void setPriority(Priority priority) { 
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
    
    public String getInternalNotes() { 
        return internalNotes; 
    }
    public void setInternalNotes(String internalNotes) { 
        this.internalNotes = internalNotes; 
    }
    
    public Long getAssignedToId() { 
        return assignedToId; 
    }
    public void setAssignedToId(Long assignedToId) { 
        this.assignedToId = assignedToId; 
    }
    
    public String getStatus() { 
        return status; 
    }
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    public String getResolutionNotes() { 
        return resolutionNotes; 
    }
    public void setResolutionNotes(String resolutionNotes) { 
        this.resolutionNotes = resolutionNotes; 
    }
}