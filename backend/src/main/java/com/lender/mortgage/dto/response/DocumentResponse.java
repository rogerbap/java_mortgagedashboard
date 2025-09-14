package com.lender.mortgage.dto.response;

import com.lender.mortgage.entity.Document;
import com.lender.mortgage.entity.enums.DocumentType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentResponse {
    
    private Long id;
    private Long loanId;
    private String loanNumber;
    private DocumentType documentType;
    private String fileName;
    private String originalFileName;
    private String mimeType;
    private Long fileSize;
    private String formattedFileSize;
    private String fileExtension;
    private String description;
    private UserResponse uploadedBy;
    private Boolean active;
    private String downloadUrl;
    private LocalDateTime uploadedAt;
    
    // File type indicators
    private Boolean isPdf;
    private Boolean isImage;
    
    public DocumentResponse() {}
    
    public DocumentResponse(Document document) {
        this.id = document.getId();
        this.loanId = document.getLoan().getId();
        this.loanNumber = document.getLoan().getLoanNumber();
        this.documentType = document.getDocumentType();
        this.fileName = document.getFileName();
        this.originalFileName = document.getOriginalFileName();
        this.mimeType = document.getMimeType();
        this.fileSize = document.getFileSize();
        this.formattedFileSize = document.getFormattedFileSize();
        this.fileExtension = document.getFileExtension();
        this.description = document.getDescription();
        
        if (document.getUploadedBy() != null) {
            this.uploadedBy = new UserResponse(document.getUploadedBy());
        }
        
        this.active = document.getActive();
        this.uploadedAt = document.getUploadedAt();
        
        // File type checks
        this.isPdf = document.isPdf();
        this.isImage = document.isImage();
        
        // Download URL will be set by the service
        this.downloadUrl = "/api/documents/" + document.getId() + "/download";
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getLoanId() { return loanId; }
    public void setLoanId(Long loanId) { this.loanId = loanId; }
    
    public String getLoanNumber() { return loanNumber; }
    public void setLoanNumber(String loanNumber) { this.loanNumber = loanNumber; }
    
    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getFormattedFileSize() { return formattedFileSize; }
    public void setFormattedFileSize(String formattedFileSize) { this.formattedFileSize = formattedFileSize; }
    
    public String getFileExtension() { return fileExtension; }
    public void setFileExtension(String fileExtension) { this.fileExtension = fileExtension; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public UserResponse getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(UserResponse uploadedBy) { this.uploadedBy = uploadedBy; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    
    public Boolean getIsPdf() { return isPdf; }
    public void setIsPdf(Boolean isPdf) { this.isPdf = isPdf; }
    
    public Boolean getIsImage() { return isImage; }
    public void setIsImage(Boolean isImage) { this.isImage = isImage; }
}