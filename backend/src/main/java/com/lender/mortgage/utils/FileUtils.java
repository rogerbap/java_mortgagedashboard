package com.lender.mortgage.util;

import com.lender.mortgage.exception.BadRequestException;
import org.springframework.stereotype.Component;
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

@Component
public class FileUtils {
    
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif"
    );
    
    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/plain"
    );
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    /**
     * Validate uploaded file
     */
    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds maximum limit of 10MB");
        }
        
        String mimeType = file.getContentType();
        if (mimeType == null) {
            throw new BadRequestException("Unable to determine file type");
        }
        
        if (!isAllowedFileType(mimeType)) {
            throw new BadRequestException("File type not allowed: " + mimeType);
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            throw new BadRequestException("Invalid filename");
        }
        
        if (containsDangerousPatterns(filename)) {
            throw new BadRequestException("Filename contains invalid characters");
        }
    }
    
    /**
     * Check if file type is allowed
     */
    public boolean isAllowedFileType(String mimeType) {
        return ALLOWED_IMAGE_TYPES.contains(mimeType.toLowerCase()) ||
               ALLOWED_DOCUMENT_TYPES.contains(mimeType.toLowerCase());
    }
    
    /**
     * Check if file is an image
     */
    public boolean isImageFile(String mimeType) {
        return mimeType != null && ALLOWED_IMAGE_TYPES.contains(mimeType.toLowerCase());
    }
    
    /**
     * Generate secure filename
     */
    public String generateSecureFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new BadRequestException("Original filename cannot be empty");
        }
        
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        
        return uuid + (extension.isEmpty() ? "" : "." + extension);
    }
    
    /**
     * Get file extension
     */
    public String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
    
    /**
     * Calculate file checksum
     */
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
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    /**
     * Create directories if they don't exist
     */
    public void createDirectories(String path) {
        try {
            Path dirPath = Paths.get(path);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directories: " + path, e);
        }
    }
    
    /**
     * Format file size for display
     */
    public String formatFileSize(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return sizeInBytes + " B";
        }
        
        double size = sizeInBytes;
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.1f %s", size, units[unitIndex]);
    }
    
    /**
     * Check for dangerous patterns in filename
     */
    private boolean containsDangerousPatterns(String filename) {
        String lowerFilename = filename.toLowerCase();
        
        // Check for dangerous extensions
        List<String> dangerousExtensions = Arrays.asList(
            ".exe", ".bat", ".cmd", ".scr", ".pif", ".js", ".jar", ".sh", ".ps1"
        );
        
        for (String ext : dangerousExtensions) {
            if (lowerFilename.endsWith(ext)) {
                return true;
            }
        }
        
        // Check for path traversal patterns
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return true;
        }
        
        // Check for null bytes or control characters
        return filename.chars().anyMatch(c -> c < 32 || c == 127);
    }
}