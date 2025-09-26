package com.lender.mortgage.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    
    private DateUtils() {
        // Utility class - private constructor
    }
    
    /**
     * Format LocalDateTime to default string format
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DEFAULT_FORMATTER) : null;
    }
    
    /**
     * Format LocalDateTime with custom formatter
     */
    public static String format(LocalDateTime dateTime, DateTimeFormatter formatter) {
        return dateTime != null ? dateTime.format(formatter) : null;
    }
    
    /**
     * Get days between two dates
     */
    public static long daysBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
    }
    
    /**
     * Check if date is in the past
     */
    public static boolean isPast(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isBefore(LocalDateTime.now());
    }
    
    /**
     * Check if date is in the future
     */
    public static boolean isFuture(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(LocalDateTime.now());
    }
    
    /**
     * Get start of day
     */
    public static LocalDateTime startOfDay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate().atStartOfDay() : null;
    }
    
    /**
     * Get end of day
     */
    public static LocalDateTime endOfDay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate().atTime(23, 59, 59, 999999999) : null;
    }
    
    /**
     * Add business days to a date (excluding weekends)
     */
    public static LocalDateTime addBusinessDays(LocalDateTime dateTime, int days) {
        if (dateTime == null) {
            return null;
        }
        
        LocalDateTime result = dateTime;
        int addedDays = 0;
        
        while (addedDays < days) {
            result = result.plusDays(1);
            
            // Skip weekends (Saturday = 6, Sunday = 7)
            if (result.getDayOfWeek().getValue() < 6) {
                addedDays++;
            }
        }
        
        return result;
    }
}
