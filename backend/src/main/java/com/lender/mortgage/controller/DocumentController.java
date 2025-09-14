package com.lender.mortgage.controller;

import com.lender.mortgage.dto.response.ApiResponse;
import com.lender.mortgage.dto.response.DocumentResponse;
import com.lender.mortgage.entity.enums.DocumentType;
import com.lender.mortgage.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Document Management", description = "Document management endpoints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DocumentController {
    
    @Autowired
    private DocumentService documentService;
    
    @PostMapping("/upload")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER') or hasRole('BORROWER')")
    @Operation(summary = "Upload document", description = "Upload a document for a loan")
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(
            @RequestParam @Parameter(description = "Loan ID") Long loanId,
            @RequestParam @Parameter(description = "Document type") DocumentType documentType,
            @RequestParam @Parameter(description = "File to upload") MultipartFile file,
            @RequestParam(required = false) @Parameter(description = "Document description") String description,
            Authentication authentication) {
        
        DocumentResponse document = documentService.uploadDocument(
            loanId, documentType, file, description, authentication.getName()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Document uploaded successfully", document));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER') or hasRole('BORROWER')")
    @Operation(summary = "Get document by ID", description = "Get document details by ID")
    public ResponseEntity<ApiResponse<DocumentResponse>> getDocumentById(
            @PathVariable @Parameter(description = "Document ID") Long id) {
        DocumentResponse document = documentService.getDocumentById(id);
        return ResponseEntity.ok(ApiResponse.success("Document retrieved successfully", document));
    }
    
    @GetMapping("/loan/{loanId}")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER') or hasRole('BORROWER')")
    @Operation(summary = "Get documents by loan", description = "Get all documents for a specific loan")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getDocumentsByLoan(
            @PathVariable @Parameter(description = "Loan ID") Long loanId) {
        List<DocumentResponse> documents = documentService.getDocumentsByLoan(loanId);
        return ResponseEntity.ok(ApiResponse.success("Documents retrieved successfully", documents));
    }
    
    @GetMapping("/loan/{loanId}/type/{documentType}")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER') or hasRole('BORROWER')")
    @Operation(summary = "Get documents by loan and type", description = "Get documents for a loan with specific type")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getDocumentsByLoanAndType(
            @PathVariable @Parameter(description = "Loan ID") Long loanId,
            @PathVariable @Parameter(description = "Document type") DocumentType documentType) {
        List<DocumentResponse> documents = documentService.getDocumentsByLoanAndType(loanId, documentType);
        return ResponseEntity.ok(ApiResponse.success("Documents retrieved successfully", documents));
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get documents by user", description = "Get documents uploaded by specific user")
    public ResponseEntity<ApiResponse<Page<DocumentResponse>>> getDocumentsByUser(
            @PathVariable @Parameter(description = "User ID") Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<DocumentResponse> documents = documentService.getDocumentsByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Documents retrieved successfully", documents));
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Search documents", description = "Search documents by filename")
    public ResponseEntity<ApiResponse<Page<DocumentResponse>>> searchDocuments(
            @RequestParam @Parameter(description = "Search term") String q,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<DocumentResponse> documents = documentService.searchDocuments(q, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search results", documents));
    }
    
    @GetMapping("/{id}/download")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER') or hasRole('BORROWER')")
    @Operation(summary = "Download document", description = "Download document file")
    public ResponseEntity<ByteArrayResource> downloadDocument(
            @PathVariable @Parameter(description = "Document ID") Long id) {
        
        DocumentResponse documentInfo = documentService.getDocumentById(id);
        byte[] documentContent = documentService.downloadDocument(id);
        
        ByteArrayResource resource = new ByteArrayResource(documentContent);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                       "attachment; filename=\"" + documentInfo.getOriginalFileName() + "\"")
                .contentType(MediaType.parseMediaType(documentInfo.getMimeType()))
                .contentLength(documentContent.length)
                .body(resource);
    }
    
    @PutMapping("/{id}/description")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Update document description", description = "Update document description")
    public ResponseEntity<ApiResponse<DocumentResponse>> updateDocumentDescription(
            @PathVariable @Parameter(description = "Document ID") Long id,
            @RequestParam @Parameter(description = "New description") String description,
            Authentication authentication) {
        DocumentResponse document = documentService.updateDocumentDescription(id, description, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Document description updated successfully", document));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Delete document", description = "Delete document (soft delete)")
    public ResponseEntity<ApiResponse<String>> deleteDocument(
            @PathVariable @Parameter(description = "Document ID") Long id,
            Authentication authentication) {
        documentService.deleteDocument(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Document deleted successfully", null));
    }
}