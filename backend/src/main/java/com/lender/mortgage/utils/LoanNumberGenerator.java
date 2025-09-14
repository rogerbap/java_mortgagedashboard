package com.lender.mortgage.util;

import com.lender.mortgage.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class LoanNumberGenerator {
    
    private static final String PREFIX = "LN";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyMMdd");
    private static final Random random = new Random();
    
    @Autowired
    private LoanRepository loanRepository;
    
    /**
     * Generate unique loan number in format: LN{YYMMDD}{4-digit-sequence}
     * Example: LN2409150001
     */
    public String generate() {
        String dateStr = LocalDateTime.now().format(DATE_FORMAT);
        String loanNumber;
        int attempts = 0;
        
        do {
            int sequence = random.nextInt(9999) + 1; // 1-9999
            loanNumber = String.format("%s%s%04d", PREFIX, dateStr, sequence);
            attempts++;
            
            // Safety check to avoid infinite loop
            if (attempts > 100) {
                // Fall back to timestamp-based generation
                long timestamp = System.currentTimeMillis() % 10000;
                loanNumber = String.format("%s%s%04d", PREFIX, dateStr, timestamp);
                break;
            }
            
        } while (loanRepository.existsByLoanNumber(loanNumber));
        
        return loanNumber;
    }
    
    /**
     * Validate loan number format
     */
    public boolean isValidFormat(String loanNumber) {
        if (loanNumber == null || loanNumber.length() != 12) {
            return false;
        }
        
        return loanNumber.startsWith(PREFIX) && 
               loanNumber.substring(2, 8).matches("\\d{6}") &&
               loanNumber.substring(8, 12).matches("\\d{4}");
    }
}