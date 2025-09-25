package com.lender.mortgage.dto.request;

import com.lender.mortgage.entity.enums.LoanStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateLoanStatusRequest {
    @NotNull(message = "Status is required")
    private LoanStatus status;

    private String reason;
    private String notes;

    public UpdateLoanStatusRequest() {}

    public UpdateLoanStatusRequest(LoanStatus status, String reason, String notes) {
        this.status = status;
        this.reason = reason;
        this.notes = notes;
    }

    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}