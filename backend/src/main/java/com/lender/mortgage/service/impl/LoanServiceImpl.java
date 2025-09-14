package com.lender.mortgage.service.impl;

import com.lender.mortgage.dto.request.CreateLoanRequest;
import com.lender.mortgage.dto.request.UpdateLoanRequest;
import com.lender.mortgage.dto.request.UpdateLoanStatusRequest;
import com.lender.mortgage.dto.response.LoanResponse;
import com.lender.mortgage.dto.response.LoanSummaryResponse;
import com.lender.mortgage.entity.Loan;
import com.lender.mortgage.entity.LoanStatusHistory;
import com.lender.mortgage.entity.User;
import com.lender.mortgage.entity.enums.LoanStatus;
import com.lender.mortgage.entity.enums.LoanType;
import com.lender.mortgage.exception.BadRequestException;
import com.lender.mortgage.exception.LoanProcessingException;
import com.lender.mortgage.exception.ResourceNotFoundException;
import com.lender.mortgage.repository.LoanRepository;
import com.lender.mortgage.repository.LoanStatusHistoryRepository;
import com.lender.mortgage.service.LoanService;
import com.lender.mortgage.service.UserService;
import com.lender.mortgage.util.LoanNumberGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {
    
    private static final Logger logger = LoggerFactory.getLogger(LoanServiceImpl.class);
    
    @Autowired
    private LoanRepository loanRepository;
    
    @Autowired
    private LoanStatusHistoryRepository statusHistoryRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private LoanNumberGenerator loanNumberGenerator;
    
    @Override
    public LoanResponse createLoan(CreateLoanRequest request, String createdByEmail) {
        // Generate unique loan number
        String loanNumber = generateLoanNumber();
        
        // Create loan entity
        Loan loan = new Loan();
        loan.setLoanNumber(loanNumber);
        loan.setLoanType(request.getLoanType());
        loan.setLoanAmount(request.getLoanAmount());
        loan.setInterestRate(request.getInterestRate());
        loan.setLoanTermMonths(request.getLoanTermMonths());
        loan.setStatus(LoanStatus.APPLICATION_STARTED);
        loan.setApplicationDate(LocalDateTime.now());
        loan.setCreatedBy(createdByEmail);
        
        // Borrower information
        loan.setBorrowerFirstName(request.getBorrowerFirstName());
        loan.setBorrowerLastName(request.getBorrowerLastName());
        loan.setBorrowerEmail(request.getBorrowerEmail());
        loan.setBorrowerPhone(request.getBorrowerPhone());
        loan.setBorrowerDateOfBirth(request.getBorrowerDateOfBirth());
        loan.setBorrowerAnnualIncome(request.getBorrowerAnnualIncome());
        loan.setCreditScore(request.getCreditScore());
        loan.setEmployerName(request.getEmployerName());
        loan.setJobTitle(request.getJobTitle());
        loan.setEmploymentYears(request.getEmploymentYears());
        loan.setEmploymentMonths(request.getEmploymentMonths());
        
        // Co-borrower information
        loan.setCoBorrowerFirstName(request.getCoBorrowerFirstName());
        loan.setCoBorrowerLastName(request.getCoBorrowerLastName());
        loan.setCoBorrowerEmail(request.getCoBorrowerEmail());
        loan.setCoBorrowerPhone(request.getCoBorrowerPhone());
        loan.setCoBorrowerAnnualIncome(request.getCoBorrowerAnnualIncome());
        
        // Property information
        loan.setPropertyAddress(request.getPropertyAddress());
        loan.setPropertyCity(request.getPropertyCity());
        loan.setPropertyState(request.getPropertyState());
        loan.setPropertyZip(request.getPropertyZip());
        loan.setPropertyValue(request.getPropertyValue());
        loan.setPurchasePrice(request.getPurchasePrice());
        loan.setDownPayment(request.getDownPayment());
        loan.setPropertyType(request.getPropertyType());
        loan.setOwnerOccupied(request.getOwnerOccupied());
        loan.setPropertyYear(request.getPropertyYear());
        loan.setSquareFootage(request.getSquareFootage());
        loan.setBedrooms(request.getBedrooms());
        loan.setBathrooms(request.getBathrooms());
        
        // Staff assignments
        if (request.getLoanOfficerId() != null) {
            User loanOfficer = userService.getUserEntity(request.getLoanOfficerId());
            loan.setLoanOfficer(loanOfficer);
        }
        if (request.getProcessorId() != null) {
            User processor = userService.getUserEntity(request.getProcessorId());
            loan.setProcessor(processor);
        }
        if (request.getUnderwriterId() != null) {
            User underwriter = userService.getUserEntity(request.getUnderwriterId());
            loan.setUnderwriter(underwriter);
        }
        
        // Other fields
        loan.setExpectedClosingDate(request.getExpectedClosingDate());
        loan.setNotes(request.getNotes());
        
        // Calculate loan metrics
        calculateLoanMetrics(loan);
        
        // Save loan
        Loan savedLoan = loanRepository.save(loan);
        
        // Create initial status history record
        LoanStatusHistory statusHistory = new LoanStatusHistory(
            savedLoan, null, LoanStatus.APPLICATION_STARTED, 
            userService.getUserEntityByEmail(createdByEmail), 
            "Loan application created"
        );
        statusHistoryRepository.save(statusHistory);
        
        logger.info("Created new loan: {} for borrower: {}", 
                   savedLoan.getLoanNumber(), savedLoan.getBorrowerFullName());
        
        return new LoanResponse(savedLoan);
    }
    
    @Override
    public LoanResponse updateLoan(Long loanId, UpdateLoanRequest request, String updatedByEmail) {
        Loan loan = getLoanEntity(loanId);
        
        // Update loan fields based on request
        // Implementation similar to create but with updates
        // ... (updating fields based on request)
        
        loan.setLastModifiedBy(updatedByEmail);
        
        // Recalculate metrics if financial data changed
        calculateLoanMetrics(loan);
        
        Loan savedLoan = loanRepository.save(loan);
        
        logger.info("Updated loan: {}", savedLoan.getLoanNumber());
        
        return new LoanResponse(savedLoan);
    }
    
    @Override
    public LoanResponse updateLoanStatus(Long loanId, UpdateLoanStatusRequest request, String updatedByEmail) {
        Loan loan = getLoanEntity(loanId);
        LoanStatus oldStatus = loan.getStatus();
        LoanStatus newStatus = request.getNewStatus();
        
        // Validate status transition
        if (!oldStatus.canTransitionTo(newStatus)) {
            throw new LoanProcessingException(
                String.format("Cannot transition from %s to %s", oldStatus, newStatus)
            );
        }
        
        // Update loan status
        loan.setStatus(newStatus);
        loan.setLastModifiedBy(updatedByEmail);
        
        // Update specific date fields based on new status
        LocalDateTime now = LocalDateTime.now();
        switch (newStatus) {
            case PRE_APPROVED -> loan.setPreApprovalDate(now);
            case APPROVED_WITH_CONDITIONS -> loan.setApprovalDate(now);
            case CLEAR_TO_CLOSE -> loan.setClearToCloseDate(now);
            case CLOSING, CLOSED -> loan.setClosingDate(now);
        }
        
        Loan savedLoan = loanRepository.save(loan);
        
        // Create status history record
        LoanStatusHistory statusHistory = new LoanStatusHistory(
            savedLoan, oldStatus, newStatus,
            userService.getUserEntityByEmail(updatedByEmail),
            request.getReason()
        );
        statusHistory.setNotes(request.getNotes());
        statusHistoryRepository.save(statusHistory);
        
        logger.info("Updated loan {} status from {} to {}", 
                   savedLoan.getLoanNumber(), oldStatus, newStatus);
        
        return new LoanResponse(savedLoan);
    }
    
    @Override
    @Transactional(readOnly = true)
    public LoanResponse getLoanById(Long loanId) {
        Loan loan = getLoanEntity(loanId);
        return new LoanResponse(loan);
    }
    
    @Override
    @Transactional(readOnly = true)
    public LoanResponse getLoanByNumber(String loanNumber) {
        Loan loan = loanRepository.findByLoanNumber(loanNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with number: " + loanNumber));
        return new LoanResponse(loan);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LoanSummaryResponse> getAllLoans(Pageable pageable) {
        return loanRepository.findAll(pageable)
                .map(LoanSummaryResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LoanSummaryResponse> searchLoans(String searchTerm, Pageable pageable) {
        return loanRepository.findBySearchTerm(searchTerm, pageable)
                .map(LoanSummaryResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LoanSummaryResponse> getLoansByStatus(LoanStatus status, Pageable pageable) {
        return loanRepository.findByStatus(status, pageable)
                .map(LoanSummaryResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LoanSummaryResponse> getLoansByStatuses(List<LoanStatus> statuses, Pageable pageable) {
        return loanRepository.findByStatusIn(statuses, pageable)
                .map(LoanSummaryResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LoanSummaryResponse> getLoansByOfficer(Long officerId, Pageable pageable) {
        User officer = userService.getUserEntity(officerId);
        return loanRepository.findByLoanOfficer(officer, pageable)
                .map(LoanSummaryResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LoanSummaryResponse> getLoansByProcessor(Long processorId, Pageable pageable) {
        User processor = userService.getUserEntity(processorId);
        return loanRepository.findByProcessor(processor, pageable)
                .map(LoanSummaryResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LoanSummaryResponse> getLoansByUnderwriter(Long underwriterId, Pageable pageable) {
        User underwriter = userService.getUserEntity(underwriterId);
        return loanRepository.findByUnderwriter(underwriter, pageable)
                .map(LoanSummaryResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LoanSummaryResponse> getLoansByBorrower(String borrowerEmail, Pageable pageable) {
        return loanRepository.findByBorrowerEmail(borrowerEmail, pageable)
                .map(LoanSummaryResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LoanSummaryResponse> getLoansByType(List<LoanType> types, Pageable pageable) {
        return loanRepository.findByLoanTypeIn(types, pageable)
                .map(LoanSummaryResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LoanSummaryResponse> getLoansByAmountRange(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable) {
        return loanRepository.findByLoanAmountBetween(minAmount, maxAmount, pageable)
                .map(LoanSummaryResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LoanSummaryResponse> getLoansByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return loanRepository.findByCreatedAtBetween(startDate, endDate, pageable)
                .map(LoanSummaryResponse::new);
    }
    
    @Override
    public LoanResponse assignLoanOfficer(Long loanId, Long officerId, String assignedByEmail) {
        Loan loan = getLoanEntity(loanId);
        User officer = userService.getUserEntity(officerId);
        
        loan.setLoanOfficer(officer);
        loan.setLastModifiedBy(assignedByEmail);
        
        Loan savedLoan = loanRepository.save(loan);
        
        logger.info("Assigned loan officer {} to loan {}", officer.getFullName(), loan.getLoanNumber());
        
        return new LoanResponse(savedLoan);
    }
    
    @Override
    public LoanResponse assignProcessor(Long loanId, Long processorId, String assignedByEmail) {
        Loan loan = getLoanEntity(loanId);
        User processor = userService.getUserEntity(processorId);
        
        loan.setProcessor(processor);
        loan.setLastModifiedBy(assignedByEmail);
        
        Loan savedLoan = loanRepository.save(loan);
        
        logger.info("Assigned processor {} to loan {}", processor.getFullName(), loan.getLoanNumber());
        
        return new LoanResponse(savedLoan);
    }
    
    @Override
    public LoanResponse assignUnderwriter(Long loanId, Long underwriterId, String assignedByEmail) {
        Loan loan = getLoanEntity(loanId);
        User underwriter = userService.getUserEntity(underwriterId);
        
        loan.setUnderwriter(underwriter);
        loan.setLastModifiedBy(assignedByEmail);
        
        Loan savedLoan = loanRepository.save(loan);
        
        logger.info("Assigned underwriter {} to loan {}", underwriter.getFullName(), loan.getLoanNumber());
        
        return new LoanResponse(savedLoan);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LoanSummaryResponse> getOverdueLoans() {
        return loanRepository.findOverdueLoans(LocalDateTime.now())
                .stream()
                .map(LoanSummaryResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LoanSummaryResponse> getLoansClosingSoon(int daysAhead) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(daysAhead);
        
        return loanRepository.findLoansClosingSoon(startDate, endDate)
                .stream()
                .map(LoanSummaryResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LoanSummaryResponse> getLoansReadyToClearToClose() {
        return loanRepository.findLoansReadyToClearToClose()
                .stream()
                .map(LoanSummaryResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public String generateLoanNumber() {
        return loanNumberGenerator.generate();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByLoanNumber(String loanNumber) {
        return loanRepository.existsByLoanNumber(loanNumber);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Loan getLoanEntity(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + loanId));
    }
    
    @Override
    public void calculateLoanMetrics(Loan loan) {
        // Calculate LTV (Loan to Value Ratio)
        if (loan.getLoanAmount() != null && loan.getPropertyValue() != null && 
            loan.getPropertyValue().compareTo(BigDecimal.ZERO) > 0) {
            
            BigDecimal ltv = loan.getLoanAmount()
                    .divide(loan.getPropertyValue(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            loan.setLoanToValueRatio(ltv);
        }
        
        // Calculate DTI (Debt to Income Ratio)
        if (loan.getLoanAmount() != null && loan.getBorrowerAnnualIncome() != null &&
            loan.getBorrowerAnnualIncome().compareTo(BigDecimal.ZERO) > 0) {
            
            BigDecimal totalIncome = loan.getTotalBorrowerIncome();
            
            // Estimate monthly payment (simplified calculation)
            if (loan.getInterestRate() != null && loan.getLoanTermMonths() != null) {
                BigDecimal monthlyRate = loan.getInterestRate().divide(new BigDecimal("1200"), 6, RoundingMode.HALF_UP);
                BigDecimal payment = calculateMonthlyPayment(loan.getLoanAmount(), monthlyRate, loan.getLoanTermMonths());
                loan.setMonthlyPayment(payment);
                
                // Calculate DTI
                BigDecimal monthlyIncome = totalIncome.divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
                BigDecimal dti = payment.divide(monthlyIncome, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
                loan.setDebtToIncomeRatio(dti);
            }
        }
        
        // Set monthly income
        if (loan.getBorrowerAnnualIncome() != null) {
            loan.setBorrowerMonthlyIncome(
                loan.getBorrowerAnnualIncome().divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP)
            );
        }
    }
    
    private BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal monthlyRate, Integer termMonths) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(new BigDecimal(termMonths), 2, RoundingMode.HALF_UP);
        }
        
        // PMT = P * [r(1+r)^n] / [(1+r)^n - 1]
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal power = onePlusRate.pow(termMonths);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(power);
        BigDecimal denominator = power.subtract(BigDecimal.ONE);
        
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}