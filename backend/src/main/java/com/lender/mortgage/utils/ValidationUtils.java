package com.lender.mortgage.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationUtils {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[\\+]?[1-9]?[0-9]{7,15}$"
    );
    
    private static final Pattern SSN_PATTERN = Pattern.compile(
        "^\\d{3}-\\d{2}-\\d{4}$"
    );
    
    private static final Pattern ZIP_PATTERN = Pattern.compile(
        "^\\d{5}(-\\d{4})?$"
    );
    
    private ValidationUtils() {
        // Utility class - private constructor
    }
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate phone number format
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.replaceAll("[\\s\\-\\(\\)]", "")).matches();
    }
    
    /**
     * Validate SSN format (XXX-XX-XXXX)
     */
    public static boolean isValidSSN(String ssn) {
        return ssn != null && SSN_PATTERN.matcher(ssn).matches();
    }
    
    /**
     * Validate ZIP code format
     */
    public static boolean isValidZipCode(String zipCode) {
        return zipCode != null && ZIP_PATTERN.matcher(zipCode).matches();
    }
    
    /**
     * Validate credit score range
     */
    public static boolean isValidCreditScore(Integer creditScore) {
        return creditScore != null && creditScore >= 300 && creditScore <= 850;
    }
    
    /**
     * Validate loan amount range
     */
    public static boolean isValidLoanAmount(BigDecimal loanAmount) {
        if (loanAmount == null) {
            return false;
        }
        
        BigDecimal minAmount = new BigDecimal("1000");
        BigDecimal maxAmount = new BigDecimal("10000000");
        
        return loanAmount.compareTo(minAmount) >= 0 && loanAmount.compareTo(maxAmount) <= 0;
    }
    
    /**
     * Validate interest rate range
     */
    public static boolean isValidInterestRate(BigDecimal interestRate) {
        if (interestRate == null) {
            return false;
        }
        
        BigDecimal minRate = new BigDecimal("0.01");
        BigDecimal maxRate = new BigDecimal("50.00");
        
        return interestRate.compareTo(minRate) >= 0 && interestRate.compareTo(maxRate) <= 0;
    }
    
    /**
     * Validate LTV ratio
     */
    public static boolean isValidLTV(BigDecimal ltv) {
        if (ltv == null) {
            return true; // LTV can be null
        }
        
        BigDecimal minLTV = BigDecimal.ZERO;
        BigDecimal maxLTV = new BigDecimal("100");
        
        return ltv.compareTo(minLTV) >= 0 && ltv.compareTo(maxLTV) <= 0;
    }
    
    /**
     * Validate DTI ratio
     */
    public static boolean isValidDTI(BigDecimal dti) {
        if (dti == null) {
            return true; // DTI can be null
        }
        
        BigDecimal minDTI = BigDecimal.ZERO;
        BigDecimal maxDTI = new BigDecimal("100");
        
        return dti.compareTo(minDTI) >= 0 && dti.compareTo(maxDTI) <= 0;
    }
    
    /**
     * Sanitize string input
     */
    public static String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        
        return input.trim().replaceAll("[<>\"'&]", "");
    }
    
    /**
     * Format SSN for display (mask middle digits)
     */
    public static String formatSSNForDisplay(String ssn) {
        if (ssn == null || !isValidSSN(ssn)) {
            return ssn;
        }
        
        return ssn.substring(0, 3) + "-XX-" + ssn.substring(6);
    }
    
    /**
     * Format phone number for display
     */
    public static String formatPhoneForDisplay(String phone) {
        if (phone == null) {
            return null;
        }
        
        String cleanPhone = phone.replaceAll("[^\\d]", "");
        
        if (cleanPhone.length() == 10) {
            return String.format("(%s) %s-%s", 
                cleanPhone.substring(0, 3),
                cleanPhone.substring(3, 6),
                cleanPhone.substring(6)
            );
        }
        
        return phone;
    }
}