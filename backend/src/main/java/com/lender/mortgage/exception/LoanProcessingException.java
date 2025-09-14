package com.lender.mortgage.exception;

public class LoanProcessingException extends RuntimeException {
    
    public LoanProcessingException(String message) {
        super(message);
    }
    
    public LoanProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}