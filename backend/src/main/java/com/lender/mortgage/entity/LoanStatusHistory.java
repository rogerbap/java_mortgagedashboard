package com.lender.mortgage.entity;

import com.lender.mortgage.entity.enums.LoanStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entity to track loan status changes for audit trail
 */
@Entity
@Table(name = "loan_status_history", indexes = {
    @Index(name = "idx_status_history_loan", columnList = "loan_id"),
    @Index(name = "idx_status_history_date", columnList = "changedAt"),
    @Index(name = "idx_status_history_user", columnList = "changed_by_id")
})
@EntityListeners(AuditingEntityListener.class)
public class LoanStatusHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "status_history_seq")
    @SequenceGenerator(name = "status_history_seq", sequenceName = "status_history_sequence", allocationSize = 1)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;
    
    @Enumerated(EnumType.STRING)
    private LoanStatus fromStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus toStatus;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id")
    private User changedBy;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt;
    
    // Constructors
    public LoanStatusHistory() {}
    
    public LoanStatusHistory(Loan loan, LoanStatus fromStatus, LoanStatus toStatus, User changedBy, String reason) {
        this.loan = loan;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
        this.reason = reason;
    }
    
    // Business methods
    public boolean isStatusAdvancement() {
        if (fromStatus == null) return true; // Initial status
        
        return switch (fromStatus) {
            case APPLICATION_STARTED -> toStatus == LoanStatus.PRE_UNDERWRITING;
            case PRE_UNDERWRITING -> toStatus == LoanStatus.PRE_APPROVED;
            case PRE_APPROVED -> toStatus == LoanStatus.APPROVED_WITH_CONDITIONS;
            case APPROVED_WITH_CONDITIONS -> toStatus == LoanStatus.CLEAR_TO_CLOSE;
            case CLEAR_TO_CLOSE -> toStatus == LoanStatus.CLOSING;
            case CLOSING -> toStatus == LoanStatus.CLOSED;
            default -> false;
        };
    }
    
    public boolean isStatusRegression() {
        if (fromStatus == null || toStatus.isTerminal()) return false;
        return !isStatusAdvancement() && !fromStatus.equals(toStatus);
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Loan getLoan() { return loan; }
    public void setLoan(Loan loan) { this.loan = loan; }
    
    public LoanStatus getFromStatus() { return fromStatus; }
    public void setFromStatus(LoanStatus fromStatus) { this.fromStatus = fromStatus; }
    
    public LoanStatus getToStatus() { return toStatus; }
    public void setToStatus(LoanStatus toStatus) { this.toStatus = toStatus; }
    
    public User getChangedBy() { return changedBy; }
    public void setChangedBy(User changedBy) { this.changedBy = changedBy; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}