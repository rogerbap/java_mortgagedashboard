package com.lender.mortgage.entity.enums;

public enum ConditionType {
    INCOME_VERIFICATION("Income Verification", "Verify borrower income documentation"),
    EMPLOYMENT_VERIFICATION("Employment Verification", "Confirm current employment status"),
    APPRAISAL("Appraisal", "Property appraisal required"),
    TITLE_WORK("Title Work", "Title search and insurance"),
    INSURANCE("Insurance", "Property insurance documentation"),
    BANK_STATEMENTS("Bank Statements", "Recent bank statements required"),
    TAX_RETURNS("Tax Returns", "Tax return documentation"),
    CREDIT_EXPLANATION("Credit Explanation", "Explanation for credit items"),
    GIFT_LETTER("Gift Letter", "Gift fund documentation"),
    SURVEY("Survey", "Property survey required"),
    HOA_DOCUMENTS("HOA Documents", "Homeowners association documentation"),
    OTHER("Other", "Custom condition type");

    private final String displayName;
    private final String description;

    ConditionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}