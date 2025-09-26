package com.lender.mortgage.entity;

import com.lender.mortgage.entity.enums.LoanStatus;
import com.lender.mortgage.entity.enums.LoanType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_number", unique = true, nullable = false, length = 20)
    private String loanNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false)
    private LoanType loanType;

    @Column(name = "loan_amount", precision = 12, scale = 2)
    private BigDecimal loanAmount;

    @Column(name = "interest_rate", precision = 5, scale = 4)
    private BigDecimal interestRate;

    @Column(name = "loan_term_months")
    private Integer loanTermMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status;

    @Column(name = "application_date", nullable = false)
    private LocalDateTime applicationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // Borrower Information
    @Column(name = "borrower_first_name", nullable = false, length = 100)
    private String borrowerFirstName;

    @Column(name = "borrower_last_name", nullable = false, length = 100)
    private String borrowerLastName;

    @Column(name = "borrower_email", nullable = false, length = 255)
    private String borrowerEmail;

    @Column(name = "borrower_phone", length = 20)
    private String borrowerPhone;

    @Column(name = "borrower_date_of_birth")
    private LocalDate borrowerDateOfBirth;

    @Column(name = "borrower_annual_income", precision = 12, scale = 2)
    private BigDecimal borrowerAnnualIncome;

    @Column(name = "credit_score")
    private Integer creditScore;

    @Column(name = "employer_name", length = 255)
    private String employerName;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(name = "employment_years")
    private Integer employmentYears;

    @Column(name = "employment_months")
    private Integer employmentMonths;

    // Co-borrower Information (Optional)
    @Column(name = "co_borrower_first_name", length = 100)
    private String coBorrowerFirstName;

    @Column(name = "co_borrower_last_name", length = 100)
    private String coBorrowerLastName;

    @Column(name = "co_borrower_email", length = 255)
    private String coBorrowerEmail;

    @Column(name = "co_borrower_phone", length = 20)
    private String coBorrowerPhone;

    @Column(name = "co_borrower_annual_income", precision = 12, scale = 2)
    private BigDecimal coBorrowerAnnualIncome;

    // Property Information
    @Column(name = "property_address", length = 500)
    private String propertyAddress;

    @Column(name = "property_city", length = 100)
    private String propertyCity;

    @Column(name = "property_state", length = 50)
    private String propertyState;

    @Column(name = "property_zip", length = 10)
    private String propertyZip;

    @Column(name = "property_value", precision = 12, scale = 2)
    private BigDecimal propertyValue;

    @Column(name = "purchase_price", precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "down_payment", precision = 12, scale = 2)
    private BigDecimal downPayment;

    @Column(name = "property_type", length = 50)
    private String propertyType;

    @Column(name = "owner_occupied")
    private Boolean ownerOccupied;

    @Column(name = "property_year")
    private Integer propertyYear;

    @Column(name = "square_footage")
    private Integer squareFootage;

    @Column(name = "bedrooms")
    private Integer bedrooms;

    @Column(name = "bathrooms")
    private BigDecimal bathrooms;

    // Staff Assignments
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_officer_id")
    private User loanOfficer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processor_id")
    private User processor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "underwriter_id")
    private User underwriter;

    // Important Dates
    @Column(name = "pre_approval_date")
    private LocalDateTime preApprovalDate;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "clear_to_close_date")
    private LocalDateTime clearToCloseDate;

    @Column(name = "closing_date")
    private LocalDateTime closingDate;

    @Column(name = "expected_closing_date")
    private LocalDateTime expectedClosingDate;

    public LocalDateTime getExpectedClosingDate() {
        return expectedClosingDate;
    }

    public void setExpectedClosingDate(LocalDateTime expectedClosingDate) {
        this.expectedClosingDate = expectedClosingDate;
    }

    @Column(name = "funded_date")
    private LocalDateTime fundedDate;

    // Audit Fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    // Notes
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;

    @Column(name = "loan_to_value_ratio", precision = 5, scale = 4)
    private BigDecimal loanToValueRatio;

    @Column(name = "debt_to_income_ratio", precision = 5, scale = 4)
    private BigDecimal debtToIncomeRatio;

    @Column(name = "monthly_payment", precision = 10, scale = 2)
    private BigDecimal monthlyPayment;

    @Column(name = "borrower_monthly_income", precision = 12, scale = 2)
    private BigDecimal borrowerMonthlyIncome;

    // Add these getters/setters:
    public void setLoanToValueRatio(BigDecimal loanToValueRatio) {
        this.loanToValueRatio = loanToValueRatio;
    }

    public BigDecimal getTotalBorrowerIncome() {
        BigDecimal total = borrowerAnnualIncome != null ? borrowerAnnualIncome : BigDecimal.ZERO;
        if (coBorrowerAnnualIncome != null) {
            total = total.add(coBorrowerAnnualIncome);
        }
        return total;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setDebtToIncomeRatio(BigDecimal debtToIncomeRatio) {
        this.debtToIncomeRatio = debtToIncomeRatio;
    }

    public void setBorrowerMonthlyIncome(BigDecimal borrowerMonthlyIncome) {
        this.borrowerMonthlyIncome = borrowerMonthlyIncome;
    }

    public BigDecimal getBorrowerMonthlyIncome() {
        return borrowerMonthlyIncome;
    }

    // Convenience Methods
    public String getBorrowerFullName() {
        return borrowerFirstName + " " + borrowerLastName;
    }

    public String getCoBorrowerFullName() {
        if (coBorrowerFirstName != null && coBorrowerLastName != null) {
            return coBorrowerFirstName + " " + coBorrowerLastName;
        }
        return null;
    }

    public String getFullPropertyAddress() {
        return propertyAddress + ", " + propertyCity + ", " + propertyState + " " + propertyZip;
    }

    public BigDecimal getLoanToValueRatio() {
        if (loanAmount != null && propertyValue != null && propertyValue.compareTo(BigDecimal.ZERO) > 0) {
            return loanAmount.divide(propertyValue, 4, java.math.RoundingMode.HALF_UP);
        }
        return null;
    }

    public BigDecimal getDebtToIncomeRatio() {
        if (loanAmount != null && borrowerAnnualIncome != null && borrowerAnnualIncome.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal monthlyPayment = loanAmount.multiply(BigDecimal.valueOf(0.007)); // Rough estimate
            BigDecimal monthlyIncome = borrowerAnnualIncome.divide(BigDecimal.valueOf(12), 2,
                    java.math.RoundingMode.HALF_UP);
            return monthlyPayment.divide(monthlyIncome, 4, java.math.RoundingMode.HALF_UP);
        }
        return null;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}