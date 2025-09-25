package com.lender.mortgage.dto.request;

import com.lender.mortgage.entity.enums.ConditionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreateConditionRequest {
    @NotNull(message = "Loan ID is required")
    private Long loanId;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Status is required")
    private ConditionStatus status;

    private LocalDate dueDate;
    private String category;
    private String notes;

    public CreateConditionRequest() {}

    // Getters and Setters
    public Long getLoanId() { return loanId; }
    public void setLoanId(Long loanId) { this.loanId = loanId; }

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