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