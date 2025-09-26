package com.lender.mortgage.dto.request;

import com.lender.mortgage.entity.enums.LoanType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreateLoanRequest {
    
    @NotNull(message = "Loan type is required")
    private LoanType loanType;
    
    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "1000.00", message = "Loan amount must be at least $1,000")
    private BigDecimal loanAmount;
    
    @DecimalMin(value = "0.01", message = "Interest rate must be greater than 0")
    @DecimalMax(value = "50.00", message = "Interest rate cannot exceed 50%")
    private BigDecimal interestRate;
    
    @Min(value = 1, message = "Loan term must be at least 1 month")
    @Max(value = 480, message = "Loan term cannot exceed 480 months")
    private Integer loanTermMonths;
    
    // Borrower Information
    @NotBlank(message = "Borrower first name is required")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String borrowerFirstName;
    
    @NotBlank(message = "Borrower last name is required")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String borrowerLastName;
    
    @NotBlank(message = "Borrower email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String borrowerEmail;
    
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{10,20}$", message = "Invalid phone number format")
    private String borrowerPhone;
    
    @NotNull(message = "Borrower date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate borrowerDateOfBirth;
    
    @NotNull(message = "Borrower annual income is required")
    @DecimalMin(value = "0.01", message = "Annual income must be greater than 0")
    private BigDecimal borrowerAnnualIncome;
    
    @Min(value = 300, message = "Credit score must be at least 300")
    @Max(value = 850, message = "Credit score cannot exceed 850")
    private Integer creditScore;
    
    @Size(max = 255, message = "Employer name cannot exceed 255 characters")
    private String employerName;
    
    @Size(max = 100, message = "Job title cannot exceed 100 characters")
    private String jobTitle;
    
    @Min(value = 0, message = "Employment years cannot be negative")
    private Integer employmentYears;
    
    @Min(value = 0, message = "Employment months cannot be negative")
    @Max(value = 11, message = "Employment months cannot exceed 11")
    private Integer employmentMonths;
    
    // Co-borrower Information (Optional)
    @Size(max = 100, message = "Co-borrower first name cannot exceed 100 characters")
    private String coBorrowerFirstName;
    
    @Size(max = 100, message = "Co-borrower last name cannot exceed 100 characters")
    private String coBorrowerLastName;
    
    @Email(message = "Invalid co-borrower email format")
    @Size(max = 255, message = "Co-borrower email cannot exceed 255 characters")
    private String coBorrowerEmail;
    
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{10,20}$", message = "Invalid co-borrower phone number format")
    private String coBorrowerPhone;
    
    @DecimalMin(value = "0.01", message = "Co-borrower annual income must be greater than 0")
    private BigDecimal coBorrowerAnnualIncome;
    
    // Property Information
    @NotBlank(message = "Property address is required")
    @Size(max = 500, message = "Property address cannot exceed 500 characters")
    private String propertyAddress;
    
    @NotBlank(message = "Property city is required")
    @Size(max = 100, message = "Property city cannot exceed 100 characters")
    private String propertyCity;
    
    @NotBlank(message = "Property state is required")
    @Size(max = 50, message = "Property state cannot exceed 50 characters")
    private String propertyState;
    
    @NotBlank(message = "Property ZIP code is required")
    @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Invalid ZIP code format")
    private String propertyZip;
    
    @NotNull(message = "Purchase price is required")
    @DecimalMin(value = "1000.00", message = "Purchase price must be at least $1,000")
    private BigDecimal purchasePrice;
    
    @NotNull(message = "Down payment is required")
    @DecimalMin(value = "0.00", message = "Down payment cannot be negative")
    private BigDecimal downPayment;
    
    @NotBlank(message = "Property type is required")
    @Size(max = 50, message = "Property type cannot exceed 50 characters")
    private String propertyType;
    
    @NotNull(message = "Owner occupied status is required")
    private Boolean ownerOccupied;
    
    @Min(value = 1800, message = "Property year must be at least 1800")
    private Integer propertyYear;
    
    @Min(value = 1, message = "Square footage must be at least 1")
    private Integer squareFootage;
    
    @Min(value = 0, message = "Number of bedrooms cannot be negative")
    private Integer bedrooms;
    
    @DecimalMin(value = "0.0", message = "Number of bathrooms cannot be negative")
    private BigDecimal bathrooms;
    
    // Staff Assignment IDs (Optional)
    private Long loanOfficerId;
    private Long processorId;
    private Long underwriterId;
    
    // Additional Information
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
    
    // Default constructor
    public CreateLoanRequest() {}
    
    // All getter methods
    public LoanType getLoanType() { return loanType; }
    public void setLoanType(LoanType loanType) { this.loanType = loanType; }
    
    public BigDecimal getLoanAmount() { return loanAmount; }
    public void setLoanAmount(BigDecimal loanAmount) { this.loanAmount = loanAmount; }
    
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    
    public Integer getLoanTermMonths() { return loanTermMonths; }
    public void setLoanTermMonths(Integer loanTermMonths) { this.loanTermMonths = loanTermMonths; }
    
    // Borrower getters
    public String getBorrowerFirstName() { return borrowerFirstName; }
    public void setBorrowerFirstName(String borrowerFirstName) { this.borrowerFirstName = borrowerFirstName; }
    
    public String getBorrowerLastName() { return borrowerLastName; }
    public void setBorrowerLastName(String borrowerLastName) { this.borrowerLastName = borrowerLastName; }
    
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
    
    // Co-borrower getters
    public String getCoBorrowerFirstName() { return coBorrowerFirstName; }
    public void setCoBorrowerFirstName(String coBorrowerFirstName) { this.coBorrowerFirstName = coBorrowerFirstName; }
    
    public String getCoBorrowerLastName() { return coBorrowerLastName; }
    public void setCoBorrowerLastName(String coBorrowerLastName) { this.coBorrowerLastName = coBorrowerLastName; }
    
    public String getCoBorrowerEmail() { return coBorrowerEmail; }
    public void setCoBorrowerEmail(String coBorrowerEmail) { this.coBorrowerEmail = coBorrowerEmail; }
    
    public String getCoBorrowerPhone() { return coBorrowerPhone; }
    public void setCoBorrowerPhone(String coBorrowerPhone) { this.coBorrowerPhone = coBorrowerPhone; }
    
    public BigDecimal getCoBorrowerAnnualIncome() { return coBorrowerAnnualIncome; }
    public void setCoBorrowerAnnualIncome(BigDecimal coBorrowerAnnualIncome) { this.coBorrowerAnnualIncome = coBorrowerAnnualIncome; }
    
    // Property getters
    public String getPropertyAddress() { return propertyAddress; }
    public void setPropertyAddress(String propertyAddress) { this.propertyAddress = propertyAddress; }
    
    public String getPropertyCity() { return propertyCity; }
    public void setPropertyCity(String propertyCity) { this.propertyCity = propertyCity; }
    
    public String getPropertyState() { return propertyState; }
    public void setPropertyState(String propertyState) { this.propertyState = propertyState; }
    
    public String getPropertyZip() { return propertyZip; }
    public void setPropertyZip(String propertyZip) { this.propertyZip = propertyZip; }
    
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    
    public BigDecimal getDownPayment() { return downPayment; }
    public void setDownPayment(BigDecimal downPayment) { this.downPayment = downPayment; }

    public BigDecimal getPropertyValue() { 
    return getPurchasePrice(); // Typically same as purchase price initially
}

public LocalDateTime getExpectedClosingDate() {
    // Calculate expected closing date (typically 30-45 days from application)
    return LocalDateTime.now().plusDays(45);
}
    
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
    
    // Staff assignment getters
    public Long getLoanOfficerId() { return loanOfficerId; }
    public void setLoanOfficerId(Long loanOfficerId) { this.loanOfficerId = loanOfficerId; }
    
    public Long getProcessorId() { return processorId; }
    public void setProcessorId(Long processorId) { this.processorId = processorId; }
    
    public Long getUnderwriterId() { return underwriterId; }
    public void setUnderwriterId(Long underwriterId) { this.underwriterId = underwriterId; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}