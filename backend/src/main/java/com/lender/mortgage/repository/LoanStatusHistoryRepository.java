package com.lender.mortgage.repository;

import com.lender.mortgage.entity.Loan;
import com.lender.mortgage.entity.LoanStatusHistory;
import com.lender.mortgage.entity.User;
import com.lender.mortgage.entity.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanStatusHistoryRepository extends JpaRepository<LoanStatusHistory, Long> {
    
    List<LoanStatusHistory> findByLoanOrderByChangedAtDesc(Loan loan);
    
    Page<LoanStatusHistory> findByLoanOrderByChangedAtDesc(Loan loan, Pageable pageable);
    
    List<LoanStatusHistory> findByChangedBy(User changedBy);
    
    Page<LoanStatusHistory> findByChangedBy(User changedBy, Pageable pageable);
    
    List<LoanStatusHistory> findByToStatus(LoanStatus toStatus);
    
    @Query("SELECT h FROM LoanStatusHistory h WHERE h.changedAt BETWEEN :startDate AND :endDate ORDER BY h.changedAt DESC")
    List<LoanStatusHistory> findByChangedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT h FROM LoanStatusHistory h WHERE h.changedAt BETWEEN :startDate AND :endDate ORDER BY h.changedAt DESC")
    Page<LoanStatusHistory> findByChangedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate, 
                                                  Pageable pageable);
    
    @Query("SELECT h.toStatus, COUNT(h) FROM LoanStatusHistory h WHERE h.changedAt BETWEEN :startDate AND :endDate GROUP BY h.toStatus")
    List<Object[]> countStatusChangesByDateRange(@Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(h) FROM LoanStatusHistory h WHERE h.loan = :loan")
    long countByLoan(@Param("loan") Loan loan);
}