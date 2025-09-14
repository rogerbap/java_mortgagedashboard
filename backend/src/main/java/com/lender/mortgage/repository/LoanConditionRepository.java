package com.lender.mortgage.repository;

import com.lender.mortgage.entity.Loan;
import com.lender.mortgage.entity.LoanCondition;
import com.lender.mortgage.entity.User;
import com.lender.mortgage.entity.enums.ConditionStatus;
import com.lender.mortgage.entity.enums.ConditionType;
import com.lender.mortgage.entity.enums.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanConditionRepository extends JpaRepository<LoanCondition, Long> {
    
    List<LoanCondition> findByLoan(Loan loan);
    
    List<LoanCondition> findByLoanAndStatus(Loan loan, ConditionStatus status);
    
    List<LoanCondition> findByAssignedTo(User assignedTo);
    
    Page<LoanCondition> findByAssignedTo(User assignedTo, Pageable pageable);
    
    List<LoanCondition> findByStatus(ConditionStatus status);
    
    Page<LoanCondition> findByStatus(ConditionStatus status, Pageable pageable);
    
    List<LoanCondition> findByType(ConditionType type);
    
    List<LoanCondition> findByPriority(Priority priority);
    
    @Query("SELECT c FROM LoanCondition c WHERE c.status IN ('PENDING', 'IN_PROGRESS')")
    List<LoanCondition> findActiveConditions();
    
    @Query("SELECT c FROM LoanCondition c WHERE c.status IN ('PENDING', 'IN_PROGRESS')")
    Page<LoanCondition> findActiveConditions(Pageable pageable);
    
    @Query("SELECT c FROM LoanCondition c WHERE c.assignedTo = :user AND c.status IN ('PENDING', 'IN_PROGRESS')")
    List<LoanCondition> findActiveConditionsByUser(@Param("user") User user);
    
    @Query("SELECT c FROM LoanCondition c WHERE c.assignedTo = :user AND c.status IN ('PENDING', 'IN_PROGRESS')")
    Page<LoanCondition> findActiveConditionsByUser(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT c FROM LoanCondition c WHERE c.dueDate < :date AND c.status IN ('PENDING', 'IN_PROGRESS')")
    List<LoanCondition> findOverdueConditions(@Param("date") LocalDateTime date);
    
    @Query("SELECT c FROM LoanCondition c WHERE c.dueDate BETWEEN :startDate AND :endDate AND c.status IN ('PENDING', 'IN_PROGRESS')")
    List<LoanCondition> findConditionsDueSoon(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT c FROM LoanCondition c WHERE c.priority IN ('HIGH', 'URGENT') AND c.status IN ('PENDING', 'IN_PROGRESS')")
    List<LoanCondition> findHighPriorityActiveConditions();
    
    @Query("SELECT COUNT(c) FROM LoanCondition c WHERE c.loan = :loan AND c.status IN ('PENDING', 'IN_PROGRESS')")
    long countActiveConditionsByLoan(@Param("loan") Loan loan);
    
    @Query("SELECT COUNT(c) FROM LoanCondition c WHERE c.loan = :loan AND c.status = 'COMPLETED'")
    long countCompletedConditionsByLoan(@Param("loan") Loan loan);
    
    @Query("SELECT c.status, COUNT(c) FROM LoanCondition c GROUP BY c.status")
    List<Object[]> countConditionsByStatus();
    
    @Query("SELECT c.type, COUNT(c) FROM LoanCondition c WHERE c.status IN ('PENDING', 'IN_PROGRESS') GROUP BY c.type")
    List<Object[]> countActiveConditionsByType();
}