package com.lender.mortgage.entity.enums;

public enum DocumentType {
    APPLICATION("Application", "Loan application form"),
    INCOME_DOCS("Income Documents", "Pay stubs, W2s, tax returns"),
    BANK_STATEMENTS("Bank Statements", "Bank account statements"),
    CREDIT_REPORT("Credit Report", "Credit report and score"),
    APPRAISAL_REPORT("Appraisal Report", "Property appraisal report"),
    TITLE_DOCS("Title Documents", "Title search and insurance"),
    INSURANCE_DOCS("Insurance Documents", "Property insurance documentation"),
    EMPLOYMENT_VERIFICATION("Employment Verification", "Employment verification letter"),
    GIFT_LETTER("Gift Letter", "Gift fund documentation and verification"),
    CLOSING_DOCS("Closing Documents", "Final closing documentation"),
    OTHER("Other", "Miscellaneous documents");

    private final String displayName;
    private final String description;

    DocumentType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}