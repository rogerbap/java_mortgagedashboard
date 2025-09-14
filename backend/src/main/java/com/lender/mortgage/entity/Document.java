package com.lender.mortgage.entity;

import com.lender.mortgage.entity.enums.DocumentType;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entity representing a document uploaded for a loan
 */
@Entity
@Table(name = "documents", indexes = {
    @Index(name = "idx_document_loan", columnList = "loan_id"),
    @Index(name = "idx_document_type", columnList = "documentType"),
    @Index(name = "idx_document_uploaded_by", columnList = "uploaded_by_id")
})
@EntityListeners(AuditingEntityListener.class)
public class Document {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_seq")
    @SequenceGenerator(name = "document_seq", sequenceName = "document_sequence", allocationSize = 1)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private String originalFileName;
    
    @Column(nullable = false)
    private String filePath; // Path to actual file storage
    
    @Column(nullable = false)
    private String mimeType;
    
    @Column(nullable = false)
    private Long fileSize; // in bytes
    
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    private String checksum; // For file integrity verification
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;
    
    // Constructors
    public Document() {}
    
    public Document(Loan loan, DocumentType documentType, String fileName, String originalFileName, 
                   String filePath, String mimeType, Long fileSize, User uploadedBy) {
        this.loan = loan;
        this.documentType = documentType;
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.filePath = filePath;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.uploadedBy = uploadedBy;
    }
    
    // Business methods
    public String getFormattedFileSize() {
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        if (fileSize < 1024 * 1024 * 1024) return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        return String.format("%.1f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
    }
    
    public String getFileExtension() {
        int lastDot = originalFileName.lastIndexOf('.');
        return lastDot > 0 ? originalFileName.substring(lastDot + 1).toLowerCase() : "";
    }
    
    public boolean isPdf() {
        return "application/pdf".equals(mimeType) || "pdf".equals(getFileExtension());
    }
    
    public boolean isImage() {
        return mimeType != null && mimeType.startsWith("image/");
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Loan getLoan() { return loan; }
    public void setLoan(Loan loan) { this.loan = loan; }
    
    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public User getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
    
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}