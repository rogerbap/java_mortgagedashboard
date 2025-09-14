package com.lender.mortgage.repository;

import com.lender.mortgage.entity.Document;
import com.lender.mortgage.entity.Loan;
import com.lender.mortgage.entity.User;
import com.lender.mortgage.entity.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    List<Document> findByLoan(Loan loan);
    
    List<Document> findByLoanAndActiveTrue(Loan loan);
    
    Page<Document> findByLoanAndActiveTrue(Loan loan, Pageable pageable);
    
    List<Document> findByDocumentType(DocumentType documentType);
    
    List<Document> findByLoanAndDocumentType(Loan loan, DocumentType documentType);
    
    List<Document> findByUploadedBy(User uploadedBy);
    
    Page<Document> findByUploadedBy(User uploadedBy, Pageable pageable);
    
    @Query("SELECT d FROM Document d WHERE d.active = true")
    Page<Document> findAllActiveDocuments(Pageable pageable);
    
    @Query("SELECT d FROM Document d WHERE d.loan = :loan AND d.documentType = :type AND d.active = true")
    List<Document> findActiveLoanDocumentsByType(@Param("loan") Loan loan, @Param("type") DocumentType type);
    
    @Query("SELECT d FROM Document d WHERE d.uploadedAt BETWEEN :startDate AND :endDate AND d.active = true")
    List<Document> findDocumentsUploadedBetween(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT d FROM Document d WHERE LOWER(d.originalFileName) LIKE LOWER(CONCAT('%', :search, '%')) AND d.active = true")
    Page<Document> findByFileNameContaining(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT COUNT(d) FROM Document d WHERE d.loan = :loan AND d.active = true")
    long countActiveDocumentsByLoan(@Param("loan") Loan loan);
    
    @Query("SELECT d.documentType, COUNT(d) FROM Document d WHERE d.active = true GROUP BY d.documentType")
    List<Object[]> countDocumentsByType();
    
    @Query("SELECT SUM(d.fileSize) FROM Document d WHERE d.active = true")
    Long getTotalFileSize();
    
    @Query("SELECT d FROM Document d WHERE d.checksum = :checksum AND d.active = true")
    List<Document> findByChecksum(@Param("checksum") String checksum);
}