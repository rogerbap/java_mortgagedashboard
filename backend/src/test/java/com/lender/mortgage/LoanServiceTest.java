package com.lender.mortgage.service;

import com.lender.mortgage.dto.request.CreateLoanRequest;
import com.lender.mortgage.dto.request.UpdateLoanStatusRequest;
import com.lender.mortgage.dto.response.LoanResponse;
import com.lender.mortgage.entity.Loan;
import com.lender.mortgage.entity.User;
import com.lender.mortgage.entity.enums.LoanStatus;
import com.lender.mortgage.entity.enums.LoanType;
import com.lender.mortgage.entity.enums.UserRole;
import com.lender.mortgage.exception.LoanProcessingException;
import com.lender.mortgage.exception.ResourceNotFoundException;
import com.lender.mortgage.repository.LoanRepository;
import com.lender.mortgage.repository.LoanStatusHistoryRepository;
import com.lender.mortgage.service.impl.LoanServiceImpl;
import com.lender.mortgage.utils.LoanNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanStatusHistoryRepository statusHistoryRepository;

    @Mock
    private UserService userService;

    @Mock
    private LoanNumberGenerator loanNumberGenerator;

    @InjectMocks
    private LoanServiceImpl loanService;

    private Loan testLoan;
    private User testUser;
    private CreateLoanRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.LOAN_OFFICER);

        testLoan = new Loan();
        testLoan.setId(1L);
        testLoan.setLoanNumber("LN2409150001");
        testLoan.setStatus(LoanStatus.APPLICATION_STARTED);
        testLoan.setLoanType(LoanType.CONVENTIONAL);
        testLoan.setLoanAmount(new BigDecimal("350000.00"));
        testLoan.setBorrowerFirstName("John");
        testLoan.setBorrowerLastName("Doe");
        testLoan.setBorrowerEmail("john.doe@example.com");
        testLoan.setPropertyAddress("123 Main St");
        testLoan.setCreatedBy("test@example.com");

        createRequest = new CreateLoanRequest();
        createRequest.setLoanType(LoanType.CONVENTIONAL);
        createRequest.setLoanAmount(new BigDecimal("350000.00"));
        createRequest.setBorrowerFirstName("John");
        createRequest.setBorrowerLastName("Doe");
        createRequest.setBorrowerEmail("john.doe@example.com");
        createRequest.setPropertyAddress("123 Main St");
    }

    @Test
    void createLoan_ShouldReturnLoanResponse_WhenValidRequest() {
        // Arrange
        String loanNumber = "LN2409150001";
        when(loanNumberGenerator.generate()).thenReturn(loanNumber);
        when(loanRepository.save(any(Loan.class))).thenReturn(testLoan);

        // Act
        LoanResponse result = loanService.createLoan(createRequest, "test@example.com");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getLoanNumber()).isEqualTo(loanNumber);
        assertThat(result.getStatus()).isEqualTo(LoanStatus.APPLICATION_STARTED);
        assertThat(result.getBorrowerFullName()).isEqualTo("John Doe");
        
        verify(loanRepository).save(any(Loan.class));
        verify(statusHistoryRepository).save(any());
    }

    @Test
    void updateLoanStatus_ShouldUpdateStatus_WhenValidTransition() {
        // Arrange
        Long loanId = 1L;
        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setNewStatus(LoanStatus.PRE_UNDERWRITING);
        request.setReason("Ready for underwriting");

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(testLoan));
        when(userService.getUserEntityByEmail("test@example.com")).thenReturn(testUser);
        when(loanRepository.save(any(Loan.class))).thenReturn(testLoan);

        // Act
        LoanResponse result = loanService.updateLoanStatus(loanId, request, "test@example.com");

        // Assert
        assertThat(result).isNotNull();
        verify(loanRepository).save(any(Loan.class));
        verify(statusHistoryRepository).save(any());
    }

    @Test
    void updateLoanStatus_ShouldThrowException_WhenInvalidTransition() {
        // Arrange
        Long loanId = 1L;
        testLoan.setStatus(LoanStatus.CLOSED);
        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setNewStatus(LoanStatus.PRE_UNDERWRITING);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(testLoan));

        // Act & Assert
        assertThatThrownBy(() -> loanService.updateLoanStatus(loanId, request, "test@example.com"))
                .isInstanceOf(LoanProcessingException.class)
                .hasMessageContaining("Cannot transition from CLOSED to PRE_UNDERWRITING");
    }

    @Test
    void getLoanById_ShouldThrowException_WhenLoanNotFound() {
        // Arrange
        Long loanId = 999L;
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> loanService.getLoanById(loanId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Loan not found with id: 999");
    }

    @Test
    void calculateLoanMetrics_ShouldCalculateLTVAndDTI() {
        // Arrange
        testLoan.setLoanAmount(new BigDecimal("400000"));
        testLoan.setPropertyValue(new BigDecimal("500000"));
        testLoan.setBorrowerAnnualIncome(new BigDecimal("100000"));
        testLoan.setInterestRate(new BigDecimal("6.5"));
        testLoan.setLoanTermMonths(360);

        // Act
        loanService.calculateLoanMetrics(testLoan);

        // Assert
        assertThat(testLoan.getLoanToValueRatio()).isEqualByComparingTo(new BigDecimal("80.0000"));
        assertThat(testLoan.getMonthlyPayment()).isNotNull();
        assertThat(testLoan.getDebtToIncomeRatio()).isNotNull();
    }
}