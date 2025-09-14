package com.lender.mortgage.service;

import com.lender.mortgage.dto.request.CreateLoanRequest;
import com.lender.mortgage.dto.request.UpdateLoanRequest;
import com.lender.mortgage.dto.request.UpdateLoanStatusRequest;
import com.lender.mortgage.dto.response.LoanResponse;
import com.lender.mortgage.dto.response.LoanSummaryResponse;
import com.lender.mortgage.entity.Loan;
import com.lender.mortgage.entity.enums.LoanStatus;
import com.lender.mortgage.entity.enums.LoanType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface LoanService {
    
    /**
     * Create a new loan application
     */
    LoanResponse createLoan(CreateLoanRequest request, String createdByEmail);
    
    /**
     * Update loan details
     */
    LoanResponse updateLoan(Long loanId, UpdateLoanRequest request, String updatedByEmail);
    
    /**
     * Update loan status
     */
    LoanResponse updateLoanStatus(Long loanId, UpdateLoanStatusRequest request, String updatedByEmail);
    
    /**
     * Get loan by ID
     */
    LoanResponse getLoanById(Long loanId);
    
    /**
     * Get loan by loan number
     */
    LoanResponse getLoanByNumber(String loanNumber);
    
    /**
     * Get all loans with pagination
     */
    Page<LoanSummaryResponse> getAllLoans(Pageable pageable);
    
    /**
     * Search loans by multiple criteria
     */
    Page<LoanSummaryResponse> searchLoans(String searchTerm, Pageable pageable);
    
    /**
     * Get loans by status
     */
    Page<LoanSummaryResponse> getLoansByStatus(LoanStatus status, Pageable pageable);
    
    /**
     * Get loans by multiple statuses
     */
    Page<LoanSummaryResponse> getLoansByStatuses(List<LoanStatus> statuses, Pageable pageable);
    
    /**
     * Get loans by loan officer
     */
    Page<LoanSummaryResponse> getLoansByOfficer(Long officerId, Pageable pageable);
    
    /**
     * Get loans by processor
     */
    Page<LoanSummaryResponse> getLoansByProcessor(Long processorId, Pageable pageable);
    
    /**
     * Get loans by underwriter
     */
    Page<LoanSummaryResponse> getLoansByUnderwriter(Long underwriterId, Pageable pageable);
    
    /**
     * Get loans by borrower email
     */
    Page<LoanSummaryResponse> getLoansByBorrower(String borrowerEmail, Pageable pageable);
    
    /**
     * Get loans by loan type
     */
    Page<LoanSummaryResponse> getLoansByType(List<LoanType> types, Pageable pageable);
    
    /**
     * Get loans by amount range
     */
    Page<LoanSummaryResponse> getLoansByAmountRange(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable);
    
    /**
     * Get loans by date range
     */
    Page<LoanSummaryResponse> getLoansByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Assign loan officer
     */
    LoanResponse assignLoanOfficer(Long loanId, Long officerId, String assignedByEmail);
    
    /**
     * Assign processor
     */
    LoanResponse assignProcessor(Long loanId, Long processorId, String assignedByEmail);
    
    /**
     * Assign underwriter
     */
    LoanResponse assignUnderwriter(Long loanId, Long underwriterId, String assignedByEmail);
    
    /**
     * Get overdue loans
     */
    List<LoanSummaryResponse> getOverdueLoans();
    
    /**
     * Get loans closing soon
     */
    List<LoanSummaryResponse> getLoansClosingSoon(int daysAhead);
    
    /**
     * Get loans ready to clear to close
     */
    List<LoanSummaryResponse> getLoansReadyToClearToClose();
    
    /**
     * Generate unique loan number
     */
    String generateLoanNumber();
    
    /**
     * Check if loan number exists
     */
    boolean existsByLoanNumber(String loanNumber);
    
    /**
     * Get loan entity by ID (for internal use)
     */
    Loan getLoanEntity(Long loanId);
    
    /**
     * Calculate loan metrics (DTI, LTV, etc.)
     */
    void calculateLoanMetrics(Loan loan);
}