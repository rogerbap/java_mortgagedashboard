package com.lender.mortgage.entity.enums;

import java.util.Set;

public enum LoanStatus {
    // Application Phase
    DRAFT("Draft", "Loan application in draft state"),
    APPLICATION_STARTED("Application Started", "Borrower has started the loan application"),
    SUBMITTED("Submitted", "Loan application submitted for review"),
    DOCUMENT_COLLECTION("Document Collection", "Collecting required documentation"),
    
    // Review Phase
    UNDER_REVIEW("Under Review", "Loan application under review"),
    PRE_UNDERWRITING("Pre-Underwriting", "Initial underwriting review"),
    
    // Approval Phase
    PRE_APPROVED("Pre-Approved", "Loan has been pre-approved"),
    APPROVED("Approved", "Loan application approved"),
    APPROVED_WITH_CONDITIONS("Approved with Conditions", "Loan approved but conditions must be met"),
    
    // Closing Phase
    CLEAR_TO_CLOSE("Clear to Close", "All conditions met, ready for closing"),
    CLOSING("Closing", "Loan is in closing process"),
    FUNDED("Funded", "Loan has been funded"),
    CLOSED("Closed", "Loan has been successfully closed"),
    
    // Terminal States
    DENIED("Denied", "Loan application denied"),
    WITHDRAWN("Withdrawn", "Loan application withdrawn by borrower"),
    CANCELLED("Cancelled", "Loan application cancelled");

    private final String displayName;
    private final String description;

    LoanStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { 
        return displayName; 
    }
    
    public String getDescription() { 
        return description; 
    }

    // Helper method to check if status is terminal (no further transitions)
    public boolean isTerminal() {
        return this == CLOSED || this == DENIED || this == WITHDRAWN || this == CANCELLED || this == FUNDED;
    }

    // Helper method to check valid transitions
    public boolean canTransitionTo(LoanStatus newStatus) {
        if (isTerminal()) {
            return false; // Terminal states cannot transition
        }

        switch (this) {
            case DRAFT:
                return newStatus == APPLICATION_STARTED || newStatus == WITHDRAWN;
            
            case APPLICATION_STARTED:
                return newStatus == SUBMITTED || newStatus == WITHDRAWN;
            
            case SUBMITTED:
                return newStatus == DOCUMENT_COLLECTION || newStatus == UNDER_REVIEW || 
                       newStatus == DENIED || newStatus == WITHDRAWN;
            
            case DOCUMENT_COLLECTION:
                return newStatus == UNDER_REVIEW || newStatus == PRE_UNDERWRITING || 
                       newStatus == DENIED || newStatus == WITHDRAWN;
            
            case UNDER_REVIEW:
                return newStatus == PRE_UNDERWRITING || newStatus == DENIED || newStatus == WITHDRAWN;
            
            case PRE_UNDERWRITING:
                return newStatus == PRE_APPROVED || newStatus == DENIED || newStatus == WITHDRAWN;
            
            case PRE_APPROVED:
                return newStatus == APPROVED || newStatus == APPROVED_WITH_CONDITIONS || 
                       newStatus == DENIED || newStatus == WITHDRAWN;
            
            case APPROVED:
                return newStatus == CLEAR_TO_CLOSE || newStatus == APPROVED_WITH_CONDITIONS || 
                       newStatus == DENIED || newStatus == WITHDRAWN;
            
            case APPROVED_WITH_CONDITIONS:
                return newStatus == CLEAR_TO_CLOSE || newStatus == DENIED || newStatus == WITHDRAWN;
            
            case CLEAR_TO_CLOSE:
                return newStatus == CLOSING || newStatus == CANCELLED;
            
            case CLOSING:
                return newStatus == FUNDED || newStatus == CLOSED || newStatus == CANCELLED;
            
            default:
                return false;
        }
    }

    // Get all possible next statuses
    public Set<LoanStatus> getPossibleNextStatuses() {
        return Set.of(LoanStatus.values())
                .stream()
                .filter(this::canTransitionTo)
                .collect(java.util.stream.Collectors.toSet());
    }
}