package com.lender.mortgage.service.impl;

import com.lender.mortgage.dto.response.DocumentResponse;
import com.lender.mortgage.entity.Document;
import com.lender.mortgage.entity.Loan;
import com.lender.mortgage.entity.User;
import com.lender.mortgage.entity.enums.DocumentType;
import com.lender.mortgage.exception.BadRequestException;
import com.lender.mortgage.exception.ResourceNotFoundException;
import com.lender.mortgage.repository.DocumentRepository;
import com.lender.mortgage.service.DocumentService;
import com.lender.mortgage.service.LoanService;
import com.lender.mortgage.service.UserService;
import com.lender.mortgage.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
    
    @Value("${app.file-storage.upload-dir:uploads}")
    private String uploadDir;
    
    @Value("${app.file-storage.max-file-size:10485760}") // 10MB default
    private long maxFileSize;
    
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
        "application/pdf",
        "image/jpeg",
        "image/jpg", 
        "image/png",
        "image/gif",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/plain"
    );
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private LoanService loanService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private FileUtils fileUtils;
    
    @Override
    public DocumentResponse uploadDocument(Long loanId, DocumentType documentType, MultipartFile file, 
                                         String description, String uploadedByEmail) {
        
        // Validate file
        validateFileUpload(file);
        
        Loan loan = loanService.getLoanEntity(loanId);
        User uploadedBy = userService.getUserEntityByEmail(uploadedByEmail);
        
        try {
            // Generate secure filename
            String secureFileName = generateSecureFileName(file.getOriginalFilename());
            
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir, "loans", loan.getLoanNumber());
            Files.createDirectories(uploadPath);
            
            // Save file to storage
            Path filePath = uploadPath.resolve(secureFileName);
            file.transferTo(filePath);
            
            // Calculate checksum
            byte[] fileBytes = Files.readAllBytes(filePath);
            String checksum = calculateChecksum(fileBytes);
            
            // Create document entity
            Document document = new Document(
                loan, documentType, secureFileName, file.getOriginalFilename(),
                filePath.toString(), file.getContentType(), file.getSize(), uploadedBy
            );
            document.setDescription(description);
            document.setChecksum(checksum);
            
            Document savedDocument = documentRepository.save(document);
            
            logger.info("Uploaded document {} for loan {}", 
                       savedDocument.getOriginalFileName(), loan.getLoanNumber());
            
            return new DocumentResponse(savedDocument);
            
        } catch (IOException e) {
            logger.error("Failed to upload file: {}", e.getMessage());
            throw new BadRequestException("Failed to upload file: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getDocumentById(Long documentId) {
        Document document = getDocumentEntity(documentId);
        return new DocumentResponse(document);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsByLoan(Long loanId) {
        Loan loan = loanService.getLoanEntity(loanId);
        return documentRepository.findByLoanAndActiveTrue(loan)
                .stream()
                .map(DocumentResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsByLoanAndType(Long loanId, DocumentType documentType) {
        Loan loan = loanService.getLoanEntity(loanId);
        return documentRepository.findActiveLoanDocumentsByType(loan, documentType)
                .stream()
                .map(DocumentResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponse> getDocumentsByUser(Long userId, Pageable pageable) {
        User user = userService.getUserEntity(userId);
        return documentRepository.findByUploadedBy(user, pageable)
                .map(DocumentResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponse> searchDocuments(String searchTerm, Pageable pageable) {
        return documentRepository.findByFileNameContaining(searchTerm, pageable)
                .map(DocumentResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public byte[] downloadDocument(Long documentId) {
        Document document = getDocumentEntity(documentId);
        
        try {
            Path filePath = Paths.get(document.getFilePath());
            if (!Files.exists(filePath)) {
                throw new ResourceNotFoundException("File not found on disk: " + document.getFileName());
            }
            
            byte[] fileContent = Files.readAllBytes(filePath);
            
            // Verify file integrity
            String calculatedChecksum = calculateChecksum(fileContent);
            if (!calculatedChecksum.equals(document.getChecksum())) {
                logger.warn("File integrity check failed for document {}", documentId);
                throw new BadRequestException("File integrity check failed");
            }
            
            logger.info("Downloaded document {} for loan {}", 
                       document.getOriginalFileName(), document.getLoan().getLoanNumber());
            
            return fileContent;
            
        } catch (IOException e) {
            logger.error("Failed to read file: {}", e.getMessage());
            throw new BadRequestException("Failed to read file: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public String getDocumentDownloadUrl(Long documentId) {
        Document document = getDocumentEntity(documentId);
        return "/api/documents/" + document.getId() + "/download";
    }
    
    @Override
    public void deleteDocument(Long documentId, String deletedByEmail) {
        Document document = getDocumentEntity(documentId);
        
        // Soft delete - mark as inactive
        document.setActive(false);
        documentRepository.save(document);
        
        logger.info("Deleted document {} by user {}", 
                   document.getOriginalFileName(), deletedByEmail);
    }
    
    @Override
    public DocumentResponse updateDocumentDescription(Long documentId, String description, String updatedByEmail) {
        Document document = getDocumentEntity(documentId);
        
        document.setDescription(description);
        Document savedDocument = documentRepository.save(document);
        
        logger.info("Updated description for document {}", document.getOriginalFileName());
        
        return new DocumentResponse(savedDocument);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Document getDocumentEntity(Long documentId) {
        return documentRepository.findById(documentId)
                .filter(Document::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
    }
    
    @Override
    public void validateFileUpload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new BadRequestException("File size exceeds maximum allowed size of " + 
                (maxFileSize / 1024 / 1024) + "MB");
        }
        
        String mimeType = file.getContentType();
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType.toLowerCase())) {
            throw new BadRequestException("File type not allowed: " + mimeType);
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            throw new BadRequestException("Invalid filename");
        }
        
        // Check for potentially dangerous file extensions
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        List<String> dangerousExtensions = Arrays.asList("exe", "bat", "cmd", "scr", "pif", "js", "jar");
        if (dangerousExtensions.contains(extension)) {
            throw new BadRequestException("File extension not allowed: " + extension);
        }
    }
    
    @Override
    public String generateSecureFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new BadRequestException("Original filename cannot be empty");
        }
        
        // Extract file extension
        String extension = "";
        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = originalFileName.substring(lastDotIndex);
        }
        
        // Generate UUID-based filename
        String uuid = UUID.randomUUID().toString();
        return uuid + extension;
    }
    
    @Override
    public String calculateChecksum(byte[] fileContent) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(fileContent);
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA-256 algorithm not available", e);
            throw new RuntimeException("Checksum calculation failed", e);
        }
    }
}