package com.lender.mortgage.repository;

import com.lender.mortgage.entity.Loan;
import com.lender.mortgage.entity.User;
import com.lender.mortgage.entity.enums.LoanStatus;
import com.lender.mortgage.entity.enums.LoanType;
import com.lender.mortgage.entity.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class LoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRepository loanRepository;

    private User testUser;
    private Loan testLoan;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("password");
        testUser.setRole(UserRole.LOAN_OFFICER);
        testUser.setActive(true);
        entityManager.persistAndFlush(testUser);

        testLoan = new Loan();
        testLoan.setLoanNumber("LN2409150001");
        testLoan.setStatus(LoanStatus.APPLICATION_STARTED);
        testLoan.setLoanType(LoanType.CONVENTIONAL);
        testLoan.setLoanAmount(new BigDecimal("350000.00"));
        testLoan.setBorrowerFirstName("Jane");
        testLoan.setBorrowerLastName("Smith");
        testLoan.setBorrowerEmail("jane.smith@example.com");
        testLoan.setPropertyAddress("123 Main St");
        testLoan.setLoanOfficer(testUser);
        testLoan.setCreatedBy("test@example.com");
        testLoan.setApplicationDate(LocalDateTime.now());
        entityManager.persistAndFlush(testLoan);
    }

    @Test
    void findByLoanNumber_ShouldReturnLoan_WhenExists() {
        // Act
        Optional<Loan> result = loanRepository.findByLoanNumber("LN2409150001");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getBorrowerLastName()).isEqualTo("Smith");
    }

    @Test
    void findByStatus_ShouldReturnLoansWithStatus() {
        // Act
        List<Loan> result = loanRepository.findByStatus(LoanStatus.APPLICATION_STARTED);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(LoanStatus.APPLICATION_STARTED);
    }

    @Test
    void findByLoanOfficer_ShouldReturnLoansForOfficer() {
        // Act
        Page<Loan> result = loanRepository.findByLoanOfficer(testUser, PageRequest.of(0, 10));

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLoanOfficer().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void countActiveLoans_ShouldReturnCorrectCount() {
        // Act
        long result = loanRepository.countActiveLoans();

        // Assert
        assertThat(result).isEqualTo(1);
    }

    @Test
    void existsByLoanNumber_ShouldReturnTrue_WhenExists() {
        // Act
        boolean result = loanRepository.existsByLoanNumber("LN2409150001");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void existsByLoanNumber_ShouldReturnFalse_WhenNotExists() {
        // Act
        boolean result = loanRepository.existsByLoanNumber("NONEXISTENT");

        // Assert
        assertThat(result).isFalse();
    }
}