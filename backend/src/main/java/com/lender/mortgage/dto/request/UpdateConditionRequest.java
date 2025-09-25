package com.lender.mortgage.dto.request;

import com.lender.mortgage.entity.enums.ConditionStatus;
import java.time.LocalDate;

public class UpdateConditionRequest {
    private String description;
    private ConditionStatus status;
    private LocalDate dueDate;
    private String category;
    private String notes;

    public UpdateConditionRequest() {}

    // Getters and Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ConditionStatus getStatus() { return status; }
    public void setStatus(ConditionStatus status) { this.status = status; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}