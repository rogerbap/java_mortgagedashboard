package com.lender.mortgage.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @Column(name = "loan_id")
    private String loanId;

    @Column(name = "borrower_name", nullable = false)
    private String borrowerName;

    @Column(name = "property_address", nullable = false)
    private String propertyAddress;

    @Column(name = "loan_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal loanAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false)
    private LoanType loanType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status = LoanStatus.CREATING;

    @Column(name = "interest_rate", precision = 5, scale = 3)
    private BigDecimal interestRate;

    @Column
    private Integer ltv;

    @Column
    private Integer dti;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "processor_id")
    private User processor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "underwriter_id")
    private User underwriter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "loan_officer_id")
    private User loanOfficer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LoanCondition> conditions = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public Loan() {}

    public Loan(String loanId, String borrowerName, String propertyAddress, BigDecimal loanAmount, LoanType loanType) {
        this.loanId = loanId;
        this.borrowerName = borrowerName;
        this.propertyAddress = propertyAddress;
        this.loanAmount = loanAmount;
        this.loanType = loanType;
    }

    // Getters and Setters
    public String getLoanId() { return loanId; }
    public void setLoanId(String loanId) { this.loanId = loanId; }

    public String getBorrowerName() { return borrowerName; }
    public void setBorrowerName(String borrowerName) { this.borrowerName = borrowerName; }

    public String getPropertyAddress() { return propertyAddress; }
    public void setPropertyAddress(String propertyAddress) { this.propertyAddress = propertyAddress; }

    public BigDecimal getLoanAmount() { return loanAmount; }
    public void setLoanAmount(BigDecimal loanAmount) { this.loanAmount = loanAmount; }

    public LoanType getLoanType() { return loanType; }
    public void setLoanType(LoanType loanType) { this.loanType = loanType; }

    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public Integer getLtv() { return ltv; }
    public void setLtv(Integer ltv) { this.ltv = ltv; }

    public Integer getDti() { return dti; }
    public void setDti(Integer dti) { this.dti = dti; }

    public User getProcessor() { return processor; }
    public void setProcessor(User processor) { this.processor = processor; }

    public User getUnderwriter() { return underwriter; }
    public void setUnderwriter(User underwriter) { this.underwriter = underwriter; }

    public User getLoanOfficer() { return loanOfficer; }
    public void setLoanOfficer(User loanOfficer) { this.loanOfficer = loanOfficer; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public List<LoanCondition> getConditions() { return conditions; }
    public void setConditions(List<LoanCondition> conditions) { this.conditions = conditions; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

enum LoanType {
    CONVENTIONAL, FHA, VA, USDA, JUMBO
}

enum LoanStatus {
    CREATING, PRE_UW, PRE_APPROVED, APPROVED_CONDITIONS, CLEAR_TO_CLOSE, CLOSING, CLOSED
}
