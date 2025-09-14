package com.lender.mortgage.controller;

import com.lender.mortgage.dto.request.CreateLoanRequest;
import com.lender.mortgage.dto.request.UpdateLoanRequest;
import com.lender.mortgage.dto.request.UpdateLoanStatusRequest;
import com.lender.mortgage.dto.response.ApiResponse;
import com.lender.mortgage.dto.response.LoanResponse;
import com.lender.mortgage.dto.response.LoanSummaryResponse;
import com.lender.mortgage.entity.enums.LoanStatus;
import com.lender.mortgage.entity.enums.LoanType;
import com.lender.mortgage.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Loan Management", description = "Loan management endpoints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LoanController {
    
    @Autowired
    private LoanService loanService;
    
    @PostMapping
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Create loan", description = "Create a new loan application")
    public ResponseEntity<ApiResponse<LoanResponse>> createLoan(
            @Valid @RequestBody CreateLoanRequest request,
            Authentication authentication) {
        LoanResponse loan = loanService.createLoan(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Loan created successfully", loan));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get all loans", description = "Get all loans with pagination")
    public ResponseEntity<ApiResponse<Page<LoanSummaryResponse>>> getAllLoans(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<LoanSummaryResponse> loans = loanService.getAllLoans(pageable);
        return ResponseEntity.ok(ApiResponse.success("Loans retrieved successfully", loans));
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Search loans", description = "Search loans by borrower name, loan number, or property address")
    public ResponseEntity<ApiResponse<Page<LoanSummaryResponse>>> searchLoans(
            @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<LoanSummaryResponse> loans = loanService.searchLoans(q, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search results", loans));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER') or hasRole('BORROWER')")
    @Operation(summary = "Get loan by ID", description = "Get loan details by ID")
    public ResponseEntity<ApiResponse<LoanResponse>> getLoanById(
            @PathVariable @Parameter(description = "Loan ID") Long id) {
        LoanResponse loan = loanService.getLoanById(id);
        return ResponseEntity.ok(ApiResponse.success("Loan retrieved successfully", loan));
    }
    
    @GetMapping("/number/{loanNumber}")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get loan by number", description = "Get loan details by loan number")
    public ResponseEntity<ApiResponse<LoanResponse>> getLoanByNumber(
            @PathVariable @Parameter(description = "Loan number") String loanNumber) {
        LoanResponse loan = loanService.getLoanByNumber(loanNumber);
        return ResponseEntity.ok(ApiResponse.success("Loan retrieved successfully", loan));
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get loans by status", description = "Get loans with specific status")
    public ResponseEntity<ApiResponse<Page<LoanSummaryResponse>>> getLoansByStatus(
            @PathVariable @Parameter(description = "Loan status") LoanStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<LoanSummaryResponse> loans = loanService.getLoansByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Loans retrieved successfully", loans));
    }
    
    @GetMapping("/officer/{officerId}")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get loans by officer", description = "Get loans assigned to specific loan officer")
    public ResponseEntity<ApiResponse<Page<LoanSummaryResponse>>> getLoansByOfficer(
            @PathVariable @Parameter(description = "Officer ID") Long officerId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<LoanSummaryResponse> loans = loanService.getLoansByOfficer(officerId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Loans retrieved successfully", loans));
    }
    
    @GetMapping("/processor/{processorId}")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get loans by processor", description = "Get loans assigned to specific processor")
    public ResponseEntity<ApiResponse<Page<LoanSummaryResponse>>> getLoansByProcessor(
            @PathVariable @Parameter(description = "Processor ID") Long processorId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<LoanSummaryResponse> loans = loanService.getLoansByProcessor(processorId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Loans retrieved successfully", loans));
    }
    
    @GetMapping("/underwriter/{underwriterId}")
    @PreAuthorize("hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get loans by underwriter", description = "Get loans assigned to specific underwriter")
    public ResponseEntity<ApiResponse<Page<LoanSummaryResponse>>> getLoansByUnderwriter(
            @PathVariable @Parameter(description = "Underwriter ID") Long underwriterId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<LoanSummaryResponse> loans = loanService.getLoansByUnderwriter(underwriterId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Loans retrieved successfully", loans));
    }
    
    @GetMapping("/borrower")
    @PreAuthorize("hasRole('BORROWER')")
    @Operation(summary = "Get borrower's loans", description = "Get loans for current borrower")
    public ResponseEntity<ApiResponse<Page<LoanSummaryResponse>>> getBorrowerLoans(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<LoanSummaryResponse> loans = loanService.getLoansByBorrower(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Loans retrieved successfully", loans));
    }
    
    @GetMapping("/filter")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Filter loans", description = "Filter loans by various criteria")
    public ResponseEntity<ApiResponse<Page<LoanSummaryResponse>>> filterLoans(
            @RequestParam(required = false) List<LoanStatus> statuses,
            @RequestParam(required = false) List<LoanType> types,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<LoanSummaryResponse> loans;
        
        if (statuses != null && !statuses.isEmpty()) {
            loans = loanService.getLoansByStatuses(statuses, pageable);
        } else if (types != null && !types.isEmpty()) {
            loans = loanService.getLoansByType(types, pageable);
        } else if (minAmount != null && maxAmount != null) {
            loans = loanService.getLoansByAmountRange(minAmount, maxAmount, pageable);
        } else if (startDate != null && endDate != null) {
            loans = loanService.getLoansByDateRange(startDate, endDate, pageable);
        } else {
            loans = loanService.getAllLoans(pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.success("Filtered loans retrieved", loans));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Update loan", description = "Update loan details")
    public ResponseEntity<ApiResponse<LoanResponse>> updateLoan(
            @PathVariable @Parameter(description = "Loan ID") Long id,
            @Valid @RequestBody UpdateLoanRequest request,
            Authentication authentication) {
        LoanResponse loan = loanService.updateLoan(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Loan updated successfully", loan));
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Update loan status", description = "Update loan status in pipeline")
    public ResponseEntity<ApiResponse<LoanResponse>> updateLoanStatus(
            @PathVariable @Parameter(description = "Loan ID") Long id,
            @Valid @RequestBody UpdateLoanStatusRequest request,
            Authentication authentication) {
        LoanResponse loan = loanService.updateLoanStatus(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Loan status updated successfully", loan));
    }
    
    @PutMapping("/{id}/assign-officer")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Assign loan officer", description = "Assign loan officer to loan")
    public ResponseEntity<ApiResponse<LoanResponse>> assignLoanOfficer(
            @PathVariable @Parameter(description = "Loan ID") Long id,
            @RequestParam @Parameter(description = "Officer ID") Long officerId,
            Authentication authentication) {
        LoanResponse loan = loanService.assignLoanOfficer(id, officerId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Loan officer assigned successfully", loan));
    }
    
    @PutMapping("/{id}/assign-processor")
    @PreAuthorize("hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Assign processor", description = "Assign processor to loan")
    public ResponseEntity<ApiResponse<LoanResponse>> assignProcessor(
            @PathVariable @Parameter(description = "Loan ID") Long id,
            @RequestParam @Parameter(description = "Processor ID") Long processorId,
            Authentication authentication) {
        LoanResponse loan = loanService.assignProcessor(id, processorId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Processor assigned successfully", loan));
    }
    
    @PutMapping("/{id}/assign-underwriter")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Assign underwriter", description = "Assign underwriter to loan")
    public ResponseEntity<ApiResponse<LoanResponse>> assignUnderwriter(
            @PathVariable @Parameter(description = "Loan ID") Long id,
            @RequestParam @Parameter(description = "Underwriter ID") Long underwriterId,
            Authentication authentication) {
        LoanResponse loan = loanService.assignUnderwriter(id, underwriterId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Underwriter assigned successfully", loan));
    }
    
    @GetMapping("/overdue")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get overdue loans", description = "Get loans that are past expected closing date")
    public ResponseEntity<ApiResponse<List<LoanSummaryResponse>>> getOverdueLoans() {
        List<LoanSummaryResponse> loans = loanService.getOverdueLoans();
        return ResponseEntity.ok(ApiResponse.success("Overdue loans retrieved", loans));
    }
    
    @GetMapping("/closing-soon")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get loans closing soon", description = "Get loans closing within specified days")
    public ResponseEntity<ApiResponse<List<LoanSummaryResponse>>> getLoansClosingSoon(
            @RequestParam(defaultValue = "7") @Parameter(description = "Days ahead") int daysAhead) {
        List<LoanSummaryResponse> loans = loanService.getLoansClosingSoon(daysAhead);
        return ResponseEntity.ok(ApiResponse.success("Loans closing soon retrieved", loans));
    }
    
    @GetMapping("/ready-to-clear")
    @PreAuthorize("hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get loans ready to clear to close", description = "Get loans with all conditions satisfied")
    public ResponseEntity<ApiResponse<List<LoanSummaryResponse>>> getLoansReadyToClearToClose() {
        List<LoanSummaryResponse> loans = loanService.getLoansReadyToClearToClose();
        return ResponseEntity.ok(ApiResponse.success("Loans ready to clear retrieved", loans));
    }
}