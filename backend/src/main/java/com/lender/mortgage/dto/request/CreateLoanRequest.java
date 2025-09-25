package com.lender.mortgage.dto.request;

import com.lender.mortgage.entity.enums.LoanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class CreateLoanRequest {
    @NotNull(message = "Loan amount is required")
    @Positive(message = "Loan amount must be positive")
    private BigDecimal loanAmount;

    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    @NotBlank(message = "Property address is required")
    private String propertyAddress;

    @NotNull(message = "Property value is required")
    @Positive(message = "Property value must be positive")
    private BigDecimal propertyValue;

    @NotNull(message = "Borrower ID is required")
    private Long borrowerId;

    private Integer termYears;
    private BigDecimal interestRate;
    private String purpose;

    public CreateLoanRequest() {}

    // Getters and Setters
    public BigDecimal getLoanAmount() { return loanAmount; }
    public void setLoanAmount(BigDecimal loanAmount) { this.loanAmount = loanAmount; }

    public LoanType getLoanType() { return loanType; }
    public void setLoanType(LoanType loanType) { this.loanType = loanType; }

    public String getPropertyAddress() { return propertyAddress; }
    public void setPropertyAddress(String propertyAddress) { this.propertyAddress = propertyAddress; }

    public BigDecimal getPropertyValue() { return propertyValue; }
    public void setPropertyValue(BigDecimal propertyValue) { this.propertyValue = propertyValue; }

    public Long getBorrowerId() { return borrowerId; }
    public void setBorrowerId(Long borrowerId) { this.borrowerId = borrowerId; }

    public Integer getTermYears() { return termYears; }
    public void setTermYears(Integer termYears) { this.termYears = termYears; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}