package com.lender.mortgage.entity.enums;

public enum Priority {
    LOW("Low", 1, "#10B981"),
    MEDIUM("Medium", 2, "#F59E0B"),
    HIGH("High", 3, "#EF4444"),
    URGENT("Urgent", 4, "#DC2626");

    private final String displayName;
    private final int level;
    private final String colorCode;

    Priority(String displayName, int level, String colorCode) {
        this.displayName = displayName;
        this.level = level;
        this.colorCode = colorCode;
    }

    public String getDisplayName() { return displayName; }
    public int getLevel() { return level; }
    public String getColorCode() { return colorCode; }
    
    public boolean isHigherThan(Priority other) {
        return this.level > other.level;
    }
}