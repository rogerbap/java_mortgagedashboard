package com.lender.mortgage.entity;

import com.lender.mortgage.entity.enums.ConditionStatus;
import com.lender.mortgage.entity.enums.ConditionType;
import com.lender.mortgage.entity.enums.Priority;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entity representing a condition that must be satisfied for loan approval
 */
@Entity
@Table(name = "loan_conditions", indexes = {
    @Index(name = "idx_condition_loan", columnList = "loan_id"),
    @Index(name = "idx_condition_status", columnList = "status"),
    @Index(name = "idx_condition_priority", columnList = "priority"),
    @Index(name = "idx_condition_assigned_to", columnList = "assigned_to_id"),
    @Index(name = "idx_condition_due_date", columnList = "dueDate")
})
@EntityListeners(AuditingEntityListener.class)
public class LoanCondition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "condition_seq")
    @SequenceGenerator(name = "condition_seq", sequenceName = "condition_sequence", allocationSize = 1)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConditionType type;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConditionStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;
    
    private LocalDateTime dueDate;
    
    private LocalDateTime completedDate;
    
    @Column(columnDefinition = "TEXT")
    private String comments;
    
    @Column(columnDefinition = "TEXT")
    private String internalNotes;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(nullable = false, updatable = false)
    private String createdBy;
    
    private String lastModifiedBy;
    
    // Constructors
    public LoanCondition() {}
    
    public LoanCondition(Loan loan, ConditionType type, String title, Priority priority) {
        this.loan = loan;
        this.type = type;
        this.title = title;
        this.priority = priority;
        this.status = ConditionStatus.PENDING;
    }
    
    // Business methods
    public boolean isOverdue() {
        return dueDate != null && LocalDateTime.now().isAfter(dueDate) && status.isActive();
    }
    
    public boolean canBeCompleted() {
        return status == ConditionStatus.PENDING || status == ConditionStatus.IN_PROGRESS;
    }
    
    public void markCompleted(String completedBy) {
        this.status = ConditionStatus.COMPLETED;
        this.completedDate = LocalDateTime.now();
        this.lastModifiedBy = completedBy;
    }
    
    public void markWaived(String waivedBy, String reason) {
        this.status = ConditionStatus.WAIVED;
        this.completedDate = LocalDateTime.now();
        this.lastModifiedBy = waivedBy;
        this.comments = (this.comments != null ? this.comments + "\n" : "") + "WAIVED: " + reason;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Loan getLoan() { return loan; }
    public void setLoan(Loan loan) { this.loan = loan; }
    
    public ConditionType getType() { return type; }
    public void setType(ConditionType type) { this.type = type; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ConditionStatus getStatus() { return status; }
    public void setStatus(ConditionStatus status) { this.status = status; }
    
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    
    public User getAssignedTo() { return assignedTo; }
    public void setAssignedTo(User assignedTo) { this.assignedTo = assignedTo; }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    
    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }
    
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    
    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
}