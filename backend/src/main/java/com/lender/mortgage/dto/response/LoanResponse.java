package com.lender.mortgage.dto.response;

import com.lender.mortgage.entity.Loan;
import com.lender.mortgage.entity.enums.LoanStatus;
import com.lender.mortgage.entity.enums.LoanType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanResponse {
    
    private Long id;
    private String loanNumber;
    private LoanType loanType;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private Integer loanTermMonths;
    private LoanStatus status;
    private LocalDateTime applicationDate;
    
    // Borrower Information
    private String borrowerFirstName;
    private String borrowerLastName;
    private String borrowerFullName;
    private String borrowerEmail;
    private String borrowerPhone;
    private LocalDate borrowerDateOfBirth;
    private BigDecimal borrowerAnnualIncome;
    private Integer creditScore;
    private String employerName;
    private String jobTitle;
    private Integer employmentYears;
    private Integer employmentMonths;
    
    // Co-borrower Information
    private String coBorrowerFirstName;
    private String coBorrowerLastName;
    private String coBorrowerFullName;
    private String coBorrowerEmail;
    private String coBorrowerPhone;
    private BigDecimal coBorrowerAnnualIncome;
    
    // Property Information
    private String propertyAddress;
    private String propertyCity;
    private String propertyState;
    private String propertyZip;
    private String fullPropertyAddress;
    private BigDecimal propertyValue;
    private BigDecimal purchasePrice;
    private BigDecimal downPayment;
    private String propertyType;
    private Boolean ownerOccupied;
    private Integer propertyYear;
    private Integer squareFootage;
    private Integer bedrooms;
    private BigDecimal bathrooms;
    
    // Staff Information
    private UserResponse loanOfficer;
    private UserResponse processor;
    private UserResponse underwriter;
    private UserResponse createdBy;
    
    // Important Dates
    private LocalDateTime preApprovalDate;
    private LocalDateTime approvalDate;
    private LocalDateTime clearToCloseDate;
    private LocalDateTime closingDate;
    private LocalDateTime fundedDate;
    
    // Calculated Fields
    private BigDecimal loanToValueRatio;
    private BigDecimal debtToIncomeRatio;
    
    // Audit Fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String lastModifiedBy;
    private String notes;
    private String internalNotes;
    
    // Default no-argument constructor
    public LoanResponse() {}
    
    // Constructor that takes Loan entity - THIS WAS MISSING!
    public LoanResponse(Loan loan) {
        this.id = loan.getId();
        this.loanNumber = loan.getLoanNumber();
        this.loanType = loan.getLoanType();
        this.loanAmount = loan.getLoanAmount();
        this.interestRate = loan.getInterestRate();
        this.loanTermMonths = loan.getLoanTermMonths();
        this.status = loan.getStatus();
        this.applicationDate = loan.getApplicationDate();
        
        // Borrower Information
        this.borrowerFirstName = loan.getBorrowerFirstName();
        this.borrowerLastName = loan.getBorrowerLastName();
        this.borrowerFullName = loan.getBorrowerFullName();
        this.borrowerEmail = loan.getBorrowerEmail();
        this.borrowerPhone = loan.getBorrowerPhone();
        this.borrowerDateOfBirth = loan.getBorrowerDateOfBirth();
        this.borrowerAnnualIncome = loan.getBorrowerAnnualIncome();
        this.creditScore = loan.getCreditScore();
        this.employerName = loan.getEmployerName();
        this.jobTitle = loan.getJobTitle();
        this.employmentYears = loan.getEmploymentYears();
        this.employmentMonths = loan.getEmploymentMonths();
        
        // Co-borrower Information
        this.coBorrowerFirstName = loan.getCoBorrowerFirstName();
        this.coBorrowerLastName = loan.getCoBorrowerLastName();
        this.coBorrowerFullName = loan.getCoBorrowerFullName();
        this.coBorrowerEmail = loan.getCoBorrowerEmail();
        this.coBorrowerPhone = loan.getCoBorrowerPhone();
        this.coBorrowerAnnualIncome = loan.getCoBorrowerAnnualIncome();
        
        // Property Information
        this.propertyAddress = loan.getPropertyAddress();
        this.propertyCity = loan.getPropertyCity();
        this.propertyState = loan.getPropertyState();
        this.propertyZip = loan.getPropertyZip();
        this.fullPropertyAddress = loan.getFullPropertyAddress();
        this.propertyValue = loan.getPropertyValue();
        this.purchasePrice = loan.getPurchasePrice();
        this.downPayment = loan.getDownPayment();
        this.propertyType = loan.getPropertyType();
        this.ownerOccupied = loan.getOwnerOccupied();
        this.propertyYear = loan.getPropertyYear();
        this.squareFootage = loan.getSquareFootage();
        this.bedrooms = loan.getBedrooms();
        this.bathrooms = loan.getBathrooms();
        
        // Staff Information - Convert entities to DTOs
        if (loan.getLoanOfficer() != null) {
            this.loanOfficer = new UserResponse(loan.getLoanOfficer());
        }
        if (loan.getProcessor() != null) {
            this.processor = new UserResponse(loan.getProcessor());
        }
        if (loan.getUnderwriter() != null) {
            this.underwriter = new UserResponse(loan.getUnderwriter());
        }
        if (loan.getCreatedBy() != null) {
            this.createdBy = new UserResponse(loan.getCreatedBy());
        }
        
        // Important Dates
        this.preApprovalDate = loan.getPreApprovalDate();
        this.approvalDate = loan.getApprovalDate();
        this.clearToCloseDate = loan.getClearToCloseDate();
        this.closingDate = loan.getClosingDate();
        this.fundedDate = loan.getFundedDate();
        
        // Calculated Fields
        this.loanToValueRatio = loan.getLoanToValueRatio();
        this.debtToIncomeRatio = loan.getDebtToIncomeRatio();
        
        // Audit Fields
        this.createdAt = loan.getCreatedAt();
        this.updatedAt = loan.getUpdatedAt();
        this.lastModifiedBy = loan.getLastModifiedBy();
        this.notes = loan.getNotes();
        this.internalNotes = loan.getInternalNotes();
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
    
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    
    public Integer getLoanTermMonths() { return loanTermMonths; }
    public void setLoanTermMonths(Integer loanTermMonths) { this.loanTermMonths = loanTermMonths; }
    
    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }
    
    public LocalDateTime getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDateTime applicationDate) { this.applicationDate = applicationDate; }
    
    // Borrower getters/setters
    public String getBorrowerFirstName() { return borrowerFirstName; }
    public void setBorrowerFirstName(String borrowerFirstName) { this.borrowerFirstName = borrowerFirstName; }
    
    public String getBorrowerLastName() { return borrowerLastName; }
    public void setBorrowerLastName(String borrowerLastName) { this.borrowerLastName = borrowerLastName; }
    
    public String getBorrowerFullName() { return borrowerFullName; }
    public void setBorrowerFullName(String borrowerFullName) { this.borrowerFullName = borrowerFullName; }
    
    public String getBorrowerEmail() { return borrowerEmail; }
    public void setBorrowerEmail(String borrowerEmail) { this.borrowerEmail = borrowerEmail; }
    
    public String getBorrowerPhone() { return borrowerPhone; }
    public void setBorrowerPhone(String borrowerPhone) { this.borrowerPhone = borrowerPhone; }
    
    public LocalDate getBorrowerDateOfBirth() { return borrowerDateOfBirth; }
    public void setBorrowerDateOfBirth(LocalDate borrowerDateOfBirth) { this.borrowerDateOfBirth = borrowerDateOfBirth; }
    
    public BigDecimal getBorrowerAnnualIncome() { return borrowerAnnualIncome; }
    public void setBorrowerAnnualIncome(BigDecimal borrowerAnnualIncome) { this.borrowerAnnualIncome = borrowerAnnualIncome; }
    
    public Integer getCreditScore() { return creditScore; }
    public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }
    
    public String getEmployerName() { return employerName; }
    public void setEmployerName(String employerName) { this.employerName = employerName; }
    
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    
    public Integer getEmploymentYears() { return employmentYears; }
    public void setEmploymentYears(Integer employmentYears) { this.employmentYears = employmentYears; }
    
    public Integer getEmploymentMonths() { return employmentMonths; }
    public void setEmploymentMonths(Integer employmentMonths) { this.employmentMonths = employmentMonths; }
    
    // Co-borrower getters/setters
    public String getCoBorrowerFirstName() { return coBorrowerFirstName; }
    public void setCoBorrowerFirstName(String coBorrowerFirstName) { this.coBorrowerFirstName = coBorrowerFirstName; }
    
    public String getCoBorrowerLastName() { return coBorrowerLastName; }
    public void setCoBorrowerLastName(String coBorrowerLastName) { this.coBorrowerLastName = coBorrowerLastName; }
    
    public String getCoBorrowerFullName() { return coBorrowerFullName; }
    public void setCoBorrowerFullName(String coBorrowerFullName) { this.coBorrowerFullName = coBorrowerFullName; }
    
    public String getCoBorrowerEmail() { return coBorrowerEmail; }
    public void setCoBorrowerEmail(String coBorrowerEmail) { this.coBorrowerEmail = coBorrowerEmail; }
    
    public String getCoBorrowerPhone() { return coBorrowerPhone; }
    public void setCoBorrowerPhone(String coBorrowerPhone) { this.coBorrowerPhone = coBorrowerPhone; }
    
    public BigDecimal getCoBorrowerAnnualIncome() { return coBorrowerAnnualIncome; }
    public void setCoBorrowerAnnualIncome(BigDecimal coBorrowerAnnualIncome) { this.coBorrowerAnnualIncome = coBorrowerAnnualIncome; }
    
    // Property getters/setters
    public String getPropertyAddress() { return propertyAddress; }
    public void setPropertyAddress(String propertyAddress) { this.propertyAddress = propertyAddress; }
    
    public String getPropertyCity() { return propertyCity; }
    public void setPropertyCity(String propertyCity) { this.propertyCity = propertyCity; }
    
    public String getPropertyState() { return propertyState; }
    public void setPropertyState(String propertyState) { this.propertyState = propertyState; }
    
    public String getPropertyZip() { return propertyZip; }
    public void setPropertyZip(String propertyZip) { this.propertyZip = propertyZip; }
    
    public String getFullPropertyAddress() { return fullPropertyAddress; }
    public void setFullPropertyAddress(String fullPropertyAddress) { this.fullPropertyAddress = fullPropertyAddress; }
    
    public BigDecimal getPropertyValue() { return propertyValue; }
    public void setPropertyValue(BigDecimal propertyValue) { this.propertyValue = propertyValue; }
    
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    
    public BigDecimal getDownPayment() { return downPayment; }
    public void setDownPayment(BigDecimal downPayment) { this.downPayment = downPayment; }
    
    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }
    
    public Boolean getOwnerOccupied() { return ownerOccupied; }
    public void setOwnerOccupied(Boolean ownerOccupied) { this.ownerOccupied = ownerOccupied; }
    
    public Integer getPropertyYear() { return propertyYear; }
    public void setPropertyYear(Integer propertyYear) { this.propertyYear = propertyYear; }
    
    public Integer getSquareFootage() { return squareFootage; }
    public void setSquareFootage(Integer squareFootage) { this.squareFootage = squareFootage; }
    
    public Integer getBedrooms() { return bedrooms; }
    public void setBedrooms(Integer bedrooms) { this.bedrooms = bedrooms; }
    
    public BigDecimal getBathrooms() { return bathrooms; }
    public void setBathrooms(BigDecimal bathrooms) { this.bathrooms = bathrooms; }
    
    // Staff getters/setters
    public UserResponse getLoanOfficer() { return loanOfficer; }
    public void setLoanOfficer(UserResponse loanOfficer) { this.loanOfficer = loanOfficer; }
    
    public UserResponse getProcessor() { return processor; }
    public void setProcessor(UserResponse processor) { this.processor = processor; }
    
    public UserResponse getUnderwriter() { return underwriter; }
    public void setUnderwriter(UserResponse underwriter) { this.underwriter = underwriter; }
    
    public UserResponse getCreatedBy() { return createdBy; }
    public void setCreatedBy(UserResponse createdBy) { this.createdBy = createdBy; }
    
    // Date getters/setters
    public LocalDateTime getPreApprovalDate() { return preApprovalDate; }
    public void setPreApprovalDate(LocalDateTime preApprovalDate) { this.preApprovalDate = preApprovalDate; }
    
    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }
    
    public LocalDateTime getClearToCloseDate() { return clearToCloseDate; }
    public void setClearToCloseDate(LocalDateTime clearToCloseDate) { this.clearToCloseDate = clearToCloseDate; }
    
    public LocalDateTime getClosingDate() { return closingDate; }
    public void setClosingDate(LocalDateTime closingDate) { this.closingDate = closingDate; }
    
    public LocalDateTime getFundedDate() { return fundedDate; }
    public void setFundedDate(LocalDateTime fundedDate) { this.fundedDate = fundedDate; }
    
    // Calculated getters/setters
    public BigDecimal getLoanToValueRatio() { return loanToValueRatio; }
    public void setLoanToValueRatio(BigDecimal loanToValueRatio) { this.loanToValueRatio = loanToValueRatio; }
    
    public BigDecimal getDebtToIncomeRatio() { return debtToIncomeRatio; }
    public void setDebtToIncomeRatio(BigDecimal debtToIncomeRatio) { this.debtToIncomeRatio = debtToIncomeRatio; }
    
    // Audit getters/setters
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }
}