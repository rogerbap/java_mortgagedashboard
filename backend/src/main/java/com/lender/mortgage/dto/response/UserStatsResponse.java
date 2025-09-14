package com.lender.mortgage.dto.response;

import java.math.BigDecimal;

public class UserStatsResponse {
    
    private UserResponse user;
    private Long totalLoans;
    private Long activeLoans;
    private Long closedLoans;
    private BigDecimal totalLoanAmount;
    private BigDecimal closedLoanAmount;
    private Double avgDaysToClose;
    private Double closingRatePercent;
    
    public UserStatsResponse() {}
    
    public UserStatsResponse(UserResponse user) {
        this.user = user;
    }
    
    // Getters and setters
    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }
    
    public Long getTotalLoans() { return totalLoans; }
    public void setTotalLoans(Long totalLoans) { this.totalLoans = totalLoans; }
    
    public Long getActiveLoans() { return activeLoans; }
    public void setActiveLoans(Long activeLoans) { this.activeLoans = activeLoans; }
    
    public Long getClosedLoans() { return closedLoans; }
    public void setClosedLoans(Long closedLoans) { this.closedLoans = closedLoans; }
    
    public BigDecimal getTotalLoanAmount() { return totalLoanAmount; }
    public void setTotalLoanAmount(BigDecimal totalLoanAmount) { this.totalLoanAmount = totalLoanAmount; }
    
    public BigDecimal getClosedLoanAmount() { return closedLoanAmount; }
    public void setClosedLoanAmount(BigDecimal closedLoanAmount) { this.closedLoanAmount = closedLoanAmount; }
    
    public Double getAvgDaysToClose() { return avgDaysToClose; }
    public void setAvgDaysToClose(Double avgDaysToClose) { this.avgDaysToClose = avgDaysToClose; }
    
    public Double getClosingRatePercent() { return closingRatePercent; }
    public void setClosingRatePercent(Double closingRatePercent) { this.closingRatePercent = closingRatePercent; }
}