package com.lender.mortgage.repository;

import com.lender.mortgage.entity.Loan;
import com.lender.mortgage.entity.User;
import com.lender.mortgage.entity.enums.LoanStatus;
import com.lender.mortgage.entity.enums.LoanType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    
    Optional<Loan> findByLoanNumber(String loanNumber);
    
    List<Loan> findByStatus(LoanStatus status);
    
    Page<Loan> findByStatus(LoanStatus status, Pageable pageable);
    
    List<Loan> findByLoanOfficer(User loanOfficer);
    
    List<Loan> findByProcessor(User processor);
    
    List<Loan> findByUnderwriter(User underwriter);
    
    Page<Loan> findByLoanOfficer(User loanOfficer, Pageable pageable);
    
    Page<Loan> findByProcessor(User processor, Pageable pageable);
    
    Page<Loan> findByUnderwriter(User underwriter, Pageable pageable);
    
    @Query("SELECT l FROM Loan l WHERE l.borrowerEmail = :email")
    List<Loan> findByBorrowerEmail(@Param("email") String email);
    
    @Query("SELECT l FROM Loan l WHERE l.borrowerEmail = :email")
    Page<Loan> findByBorrowerEmail(@Param("email") String email, Pageable pageable);
    
    @Query("SELECT l FROM Loan l WHERE " +
           "LOWER(l.borrowerFirstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(l.borrowerLastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(l.loanNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(l.propertyAddress) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Loan> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT l FROM Loan l WHERE l.status IN :statuses")
    Page<Loan> findByStatusIn(@Param("statuses") List<LoanStatus> statuses, Pageable pageable);
    
    @Query("SELECT l FROM Loan l WHERE l.loanType IN :types")
    Page<Loan> findByLoanTypeIn(@Param("types") List<LoanType> types, Pageable pageable);
    
    @Query("SELECT l FROM Loan l WHERE l.loanAmount BETWEEN :minAmount AND :maxAmount")
    Page<Loan> findByLoanAmountBetween(@Param("minAmount") BigDecimal minAmount, 
                                      @Param("maxAmount") BigDecimal maxAmount, 
                                      Pageable pageable);
    
    @Query("SELECT l FROM Loan l WHERE l.createdAt BETWEEN :startDate AND :endDate")
    Page<Loan> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate, 
                                     Pageable pageable);
    
    // Dashboard statistics queries
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = :status")
    long countByStatus(@Param("status") LoanStatus status);
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status NOT IN ('CLOSED', 'DENIED', 'WITHDRAWN', 'CANCELLED')")
    long countActiveLoans();
    
    @Query("SELECT SUM(l.loanAmount) FROM Loan l WHERE l.status = 'CLOSED' AND l.closingDate BETWEEN :startDate AND :endDate")
    BigDecimal sumClosedLoanAmountByDateRange(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT AVG(l.loanAmount) FROM Loan l WHERE l.status = 'CLOSED'")
    Double averageClosedLoanAmount();
    
    @Query("SELECT l.loanType, COUNT(l) FROM Loan l GROUP BY l.loanType")
    List<Object[]> countLoansByType();
    
    @Query("SELECT l.status, COUNT(l) FROM Loan l GROUP BY l.status")
    List<Object[]> countLoansByStatus();
    
    @Query("SELECT l FROM Loan l WHERE l.expectedClosingDate < :date AND l.status NOT IN ('CLOSED', 'DENIED', 'WITHDRAWN', 'CANCELLED')")
    List<Loan> findOverdueLoans(@Param("date") LocalDateTime date);
    
    @Query("SELECT l FROM Loan l WHERE l.expectedClosingDate BETWEEN :startDate AND :endDate AND l.status NOT IN ('CLOSED', 'DENIED', 'WITHDRAWN', 'CANCELLED')")
    List<Loan> findLoansClosingSoon(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT l FROM Loan l WHERE l.status = 'APPROVED_WITH_CONDITIONS' AND " +
           "SIZE(l.conditions) = (SELECT COUNT(c) FROM LoanCondition c WHERE c.loan = l AND c.status = 'COMPLETED')")
    List<Loan> findLoansReadyToClearToClose();
    
    boolean existsByLoanNumber(String loanNumber);
}
