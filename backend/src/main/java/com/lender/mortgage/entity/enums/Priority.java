package com.lender.mortgage.entity.enums;

public enum Priority {
    LOW("Low", "Low priority - can be completed later"),
    MEDIUM("Medium", "Medium priority - should be completed soon"),
    HIGH("High", "High priority - needs immediate attention"),
    CRITICAL("Critical", "Critical priority - blocking loan progress");

    private final String displayName;
    private final String description;

    Priority(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}