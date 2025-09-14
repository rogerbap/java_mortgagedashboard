package com.lender.mortgage.service;

import com.lender.mortgage.dto.response.DocumentResponse;
import com.lender.mortgage.entity.Document;
import com.lender.mortgage.entity.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    
    /**
     * Upload a document for a loan
     */
    DocumentResponse uploadDocument(Long loanId, DocumentType documentType, MultipartFile file, 
                                  String description, String uploadedByEmail);
    
    /**
     * Get document by ID
     */
    DocumentResponse getDocumentById(Long documentId);
    
    /**
     * Get documents by loan ID
     */
    List<DocumentResponse> getDocumentsByLoan(Long loanId);
    
    /**
     * Get documents by loan and type
     */
    List<DocumentResponse> getDocumentsByLoanAndType(Long loanId, DocumentType documentType);
    
    /**
     * Get documents uploaded by user
     */
    Page<DocumentResponse> getDocumentsByUser(Long userId, Pageable pageable);
    
    /**
     * Search documents by filename
     */
    Page<DocumentResponse> searchDocuments(String searchTerm, Pageable pageable);
    
    /**
     * Download document content
     */
    byte[] downloadDocument(Long documentId);
    
    /**
     * Get document download URL
     */
    String getDocumentDownloadUrl(Long documentId);
    
    /**
     * Delete document (soft delete)
     */
    void deleteDocument(Long documentId, String deletedByEmail);
    
    /**
     * Update document description
     */
    DocumentResponse updateDocumentDescription(Long documentId, String description, String updatedByEmail);
    
    /**
     * Get document entity by ID (for internal use)
     */
    Document getDocumentEntity(Long documentId);
    
    /**
     * Validate file upload
     */
    void validateFileUpload(MultipartFile file);
    
    /**
     * Generate secure filename
     */
    String generateSecureFileName(String originalFileName);
    
    /**
     * Calculate file checksum
     */
    String calculateChecksum(byte[] fileContent);
}
