package com.lender.mortgage.entity.enums;

public enum LoanType {
    CONVENTIONAL("Conventional", "Standard mortgage loan"),
    FHA("FHA", "Federal Housing Administration loan"),
    VA("VA", "Veterans Affairs loan"),
    USDA("USDA", "United States Department of Agriculture loan"),
    JUMBO("Jumbo", "High-balance mortgage loan"),
    REFINANCE("Refinance", "Refinancing existing mortgage");

    private final String displayName;
    private final String description;

    LoanType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}

// File: ConditionStatus.java
package com.lender.mortgage.entity.enums;

public enum ConditionStatus {
    PENDING("Pending", "Condition needs to be addressed"),
    IN_PROGRESS("In Progress", "Condition is being worked on"),
    COMPLETED("Completed", "Condition has been satisfied"),
    WAIVED("Waived", "Condition has been waived"),
    EXPIRED("Expired", "Condition deadline has passed");

    private final String displayName;
    private final String description;

    ConditionStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    
    public boolean isActive() {
        return this == PENDING || this == IN_PROGRESS;
    }
}