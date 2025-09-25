package com.lender.mortgage.entity.enums;

public enum UserRole {
    ADMIN("Admin", "System administrator"),
    LOAN_OFFICER("Loan Officer", "Processes loan applications"),
    UNDERWRITER("Underwriter", "Reviews and approves loans"),
    PROCESSOR("Processor", "Handles loan documentation"),
    CUSTOMER("Customer", "Loan applicant");

    private final String displayName;
    private final String description;

    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}