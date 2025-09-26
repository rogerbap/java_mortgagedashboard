package com.lender.mortgage.entity.enums;

public enum ConditionType {
    INCOME_VERIFICATION("Income Verification", "Verify borrower's income documentation"),
    EMPLOYMENT_VERIFICATION("Employment Verification", "Verify borrower's employment status"),
    ASSET_VERIFICATION("Asset Verification", "Verify borrower's assets and bank statements"),
    CREDIT_EXPLANATION("Credit Explanation", "Explanation required for credit issues"),
    PROPERTY_APPRAISAL("Property Appraisal", "Property appraisal required"),
    INSURANCE("Insurance", "Property insurance documentation needed"),
    TITLE_WORK("Title Work", "Title search and title insurance required"),
    HOA_DOCUMENTS("HOA Documents", "Homeowners association documentation"),
    SURVEY("Survey", "Property survey required"),
    REPAIRS("Repairs", "Property repairs required before closing"),
    LEGAL_REVIEW("Legal Review", "Legal documentation review required"),
    OTHER("Other", "Other condition type");

    private final String displayName;
    private final String description;

    ConditionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}