package com.lender.mortgage.dto.response;

import com.lender.mortgage.entity.Loan;
import com.lender.mortgage.entity.enums.LoanStatus;
import com.lender.mortgage.entity.enums.LoanType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanSummaryResponse {
    
    private Long id;
    private String loanNumber;
    private LoanType loanType;
    private BigDecimal loanAmount;
    private LoanStatus status;
    private LocalDateTime applicationDate;
    
    // Borrower summary info
    private String borrowerFullName;
    private String borrowerEmail;
    
    // Property summary info
    private String propertyCity;
    private String propertyState;
    private BigDecimal purchasePrice;
    
    // Staff summary
    private String loanOfficerName;
    private String processorName;
    private String underwriterName;
    
    // Key dates
    private LocalDateTime preApprovalDate;
    private LocalDateTime approvalDate;
    private LocalDateTime closingDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Calculated fields
    private BigDecimal loanToValueRatio;
    private BigDecimal debtToIncomeRatio;
    private Integer daysSinceApplication;
    
    // Default no-argument constructor
    public LoanSummaryResponse() {}
    
    // Constructor that takes Loan entity - THIS WAS MISSING!
    public LoanSummaryResponse(Loan loan) {
        this.id = loan.getId();
        this.loanNumber = loan.getLoanNumber();
        this.loanType = loan.getLoanType();
        this.loanAmount = loan.getLoanAmount();
        this.status = loan.getStatus();
        this.applicationDate = loan.getApplicationDate();
        
        // Borrower summary info
        this.borrowerFullName = loan.getBorrowerFullName();
        this.borrowerEmail = loan.getBorrowerEmail();
        
        // Property summary info
        this.propertyCity = loan.getPropertyCity();
        this.propertyState = loan.getPropertyState();
        this.purchasePrice = loan.getPurchasePrice();
        
        // Staff summary - safely get names
        if (loan.getLoanOfficer() != null) {
            this.loanOfficerName = loan.getLoanOfficer().getFullName();
        }
        if (loan.getProcessor() != null) {
            this.processorName = loan.getProcessor().getFullName();
        }
        if (loan.getUnderwriter() != null) {
            this.underwriterName = loan.getUnderwriter().getFullName();
        }
        
        // Key dates
        this.preApprovalDate = loan.getPreApprovalDate();
        this.approvalDate = loan.getApprovalDate();
        this.closingDate = loan.getClosingDate();
        this.createdAt = loan.getCreatedAt();
        this.updatedAt = loan.getUpdatedAt();
        
        // Calculated fields
        this.loanToValueRatio = loan.getLoanToValueRatio();
        this.debtToIncomeRatio = loan.getDebtToIncomeRatio();
        
        // Calculate days since application
        if (loan.getApplicationDate() != null) {
            this.daysSinceApplication = (int) java.time.temporal.ChronoUnit.DAYS
                .between(loan.getApplicationDate().toLocalDate(), java.time.LocalDate.now());
        }
    }
    
    // All getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getLoanNumber() { return loanNumber; }
    public void setLoanNumber(String loanNumber) { this.loanNumber = loanNumber; }
    
    public LoanType getLoanType() { return loanType; }
    public void setLoanType(LoanType loanType) { this.loanType = loanType; }
    
    public BigDecimal getLoanAmount() { return loanAmount; }
    public void setLoanAmount(BigDecimal loanAmount) { this.loanAmount = loanAmount; }
    
    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }
    
    public LocalDateTime getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDateTime applicationDate) { this.applicationDate = applicationDate; }
    
    public String getBorrowerFullName() { return borrowerFullName; }
    public void setBorrowerFullName(String borrowerFullName) { this.borrowerFullName = borrowerFullName; }
    
    public String getBorrowerEmail() { return borrowerEmail; }
    public void setBorrowerEmail(String borrowerEmail) { this.borrowerEmail = borrowerEmail; }
    
    public String getPropertyCity() { return propertyCity; }
    public void setPropertyCity(String propertyCity) { this.propertyCity = propertyCity; }
    
    public String getPropertyState() { return propertyState; }
    public void setPropertyState(String propertyState) { this.propertyState = propertyState; }
    
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    
    public String getLoanOfficerName() { return loanOfficerName; }
    public void setLoanOfficerName(String loanOfficerName) { this.loanOfficerName = loanOfficerName; }
    
    public String getProcessorName() { return processorName; }
    public void setProcessorName(String processorName) { this.processorName = processorName; }
    
    public String getUnderwriterName() { return underwriterName; }
    public void setUnderwriterName(String underwriterName) { this.underwriterName = underwriterName; }
    
    public LocalDateTime getPreApprovalDate() { return preApprovalDate; }
    public void setPreApprovalDate(LocalDateTime preApprovalDate) { this.preApprovalDate = preApprovalDate; }
    
    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }
    
    public LocalDateTime getClosingDate() { return closingDate; }
    public void setClosingDate(LocalDateTime closingDate) { this.closingDate = closingDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public BigDecimal getLoanToValueRatio() { return loanToValueRatio; }
    public void setLoanToValueRatio(BigDecimal loanToValueRatio) { this.loanToValueRatio = loanToValueRatio; }
    
    public BigDecimal getDebtToIncomeRatio() { return debtToIncomeRatio; }
    public void setDebtToIncomeRatio(BigDecimal debtToIncomeRatio) { this.debtToIncomeRatio = debtToIncomeRatio; }
    
    public Integer getDaysSinceApplication() { return daysSinceApplication; }
    public void setDaysSinceApplication(Integer daysSinceApplication) { this.daysSinceApplication = daysSinceApplication; }
}