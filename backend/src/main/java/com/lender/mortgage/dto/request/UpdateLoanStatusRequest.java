package com.lender.mortgage.dto.request;

import com.lender.mortgage.entity.enums.LoanStatus;
import jakarta.validation.constraints.*;

public class UpdateLoanStatusRequest {
    
    @NotNull(message = "New status is required")
    private LoanStatus newStatus;
    
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;
    
    @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
    private String notes;
    
    @Size(max = 2000, message = "Internal notes cannot exceed 2000 characters")
    private String internalNotes;
    
    // Default constructor
    public UpdateLoanStatusRequest() {}
    
    // Constructor with required fields
    public UpdateLoanStatusRequest(LoanStatus newStatus, String reason) {
        this.newStatus = newStatus;
        this.reason = reason;
    }
    
    // All getter and setter methods - getNewStatus() was missing!
    public LoanStatus getNewStatus() { 
        return newStatus; 
    }
    public void setNewStatus(LoanStatus newStatus) { 
        this.newStatus = newStatus; 
    }
    
    public String getReason() { 
        return reason; 
    }
    public void setReason(String reason) { 
        this.reason = reason; 
    }
    
    public String getNotes() { 
        return notes; 
    }
    public void setNotes(String notes) { 
        this.notes = notes; 
    }
    
    public String getInternalNotes() { 
        return internalNotes; 
    }
    public void setInternalNotes(String internalNotes) { 
        this.internalNotes = internalNotes; 
    }
}