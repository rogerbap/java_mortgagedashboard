package com.lender.mortgage.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardStatsResponse {
    
    // Overall statistics
    private Long totalLoans;
    private Long activeLoans;
    private Long closedLoans;
    private Long deniedLoans;
    
    // Financial statistics
    private BigDecimal totalLoanAmount;
    private BigDecimal avgLoanAmount;
    private BigDecimal totalClosedAmount;
    private BigDecimal pipelineValue; // Active loans value
    
    // Performance metrics
    private Long loansClosedThisMonth;
    private Long loansClosedThisWeek;
    private BigDecimal closedAmountThisMonth;
    private Double avgDaysToClose;
    private Double closingRatePercent;
    
    // Current pipeline status
    private Long applicationStarted;
    private Long preUnderwriting;
    private Long preApproved;
    private Long approvedWithConditions;
    private Long clearToClose;
    private Long closing;
    
    // Conditions and documents
    private Long totalActiveConditions;
    private Long overdueConditions;
    private Long highPriorityConditions;
    private Long totalDocuments;
    private Long documentsUploadedToday;
    
    // Alerts and notifications
    private Long overdueLoans;
    private Long loansClosingSoon; // Next 7 days
    private Long loansReadyToClearToClose;
    
    // Distribution charts data
    private Map<String, Long> loansByType;
    private Map<String, Long> loansByStatus;
    private Map<String, Long> conditionsByType;
    private Map<String, Long> conditionsByStatus;
    
    // Team performance
    private List<UserStatsResponse> topLoanOfficers;
    private List<UserStatsResponse> topProcessors;
    private List<UserStatsResponse> topUnderwriters;
    
    // Recent activity
    private List<LoanSummaryResponse> recentLoans;
    private List<ConditionResponse> recentConditions;
    
    public DashboardStatsResponse() {}
    
    // Getters and setters
    public Long getTotalLoans() { return totalLoans; }
    public void setTotalLoans(Long totalLoans) { this.totalLoans = totalLoans; }
    
    public Long getActiveLoans() { return activeLoans; }
    public void setActiveLoans(Long activeLoans) { this.activeLoans = activeLoans; }
    
    public Long getClosedLoans() { return closedLoans; }
    public void setClosedLoans(Long closedLoans) { this.closedLoans = closedLoans; }
    
    public Long getDeniedLoans() { return deniedLoans; }
    public void setDeniedLoans(Long deniedLoans) { this.deniedLoans = deniedLoans; }
    
    public BigDecimal getTotalLoanAmount() { return totalLoanAmount; }
    public void setTotalLoanAmount(BigDecimal totalLoanAmount) { this.totalLoanAmount = totalLoanAmount; }
    
    public BigDecimal getAvgLoanAmount() { return avgLoanAmount; }
    public void setAvgLoanAmount(BigDecimal avgLoanAmount) { this.avgLoanAmount = avgLoanAmount; }
    
    public BigDecimal getTotalClosedAmount() { return totalClosedAmount; }
    public void setTotalClosedAmount(BigDecimal totalClosedAmount) { this.totalClosedAmount = totalClosedAmount; }
    
    public BigDecimal getPipelineValue() { return pipelineValue; }
    public void setPipelineValue(BigDecimal pipelineValue) { this.pipelineValue = pipelineValue; }
    
    public Long getLoansClosedThisMonth() { return loansClosedThisMonth; }
    public void setLoansClosedThisMonth(Long loansClosedThisMonth) { this.loansClosedThisMonth = loansClosedThisMonth; }
    
    public Long getLoansClosedThisWeek() { return loansClosedThisWeek; }
    public void setLoansClosedThisWeek(Long loansClosedThisWeek) { this.loansClosedThisWeek = loansClosedThisWeek; }
    
    public BigDecimal getClosedAmountThisMonth() { return closedAmountThisMonth; }
    public void setClosedAmountThisMonth(BigDecimal closedAmountThisMonth) { this.closedAmountThisMonth = closedAmountThisMonth; }
    
    public Double getAvgDaysToClose() { return avgDaysToClose; }
    public void setAvgDaysToClose(Double avgDaysToClose) { this.avgDaysToClose = avgDaysToClose; }
    
    public Double getClosingRatePercent() { return closingRatePercent; }
    public void setClosingRatePercent(Double closingRatePercent) { this.closingRatePercent = closingRatePercent; }
    
    public Long getApplicationStarted() { return applicationStarted; }
    public void setApplicationStarted(Long applicationStarted) { this.applicationStarted = applicationStarted; }
    
    public Long getPreUnderwriting() { return preUnderwriting; }
    public void setPreUnderwriting(Long preUnderwriting) { this.preUnderwriting = preUnderwriting; }
    
    public Long getPreApproved() { return preApproved; }
    public void setPreApproved(Long preApproved) { this.preApproved = preApproved; }
    
    public Long getApprovedWithConditions() { return approvedWithConditions; }
    public void setApprovedWithConditions(Long approvedWithConditions) { this.approvedWithConditions = approvedWithConditions; }
    
    public Long getClearToClose() { return clearToClose; }
    public void setClearToClose(Long clearToClose) { this.clearToClose = clearToClose; }
    
    public Long getClosing() { return closing; }
    public void setClosing(Long closing) { this.closing = closing; }
    
    public Long getTotalActiveConditions() { return totalActiveConditions; }
    public void setTotalActiveConditions(Long totalActiveConditions) { this.totalActiveConditions = totalActiveConditions; }
    
    public Long getOverdueConditions() { return overdueConditions; }
    public void setOverdueConditions(Long overdueConditions) { this.overdueConditions = overdueConditions; }
    
    public Long getHighPriorityConditions() { return highPriorityConditions; }
    public void setHighPriorityConditions(Long highPriorityConditions) { this.highPriorityConditions = highPriorityConditions; }
    
    public Long getTotalDocuments() { return totalDocuments; }
    public void setTotalDocuments(Long totalDocuments) { this.totalDocuments = totalDocuments; }
    
    public Long getDocumentsUploadedToday() { return documentsUploadedToday; }
    public void setDocumentsUploadedToday(Long documentsUploadedToday) { this.documentsUploadedToday = documentsUploadedToday; }
    
    public Long getOverdueLoans() { return overdueLoans; }
    public void setOverdueLoans(Long overdueLoans) { this.overdueLoans = overdueLoans; }
    
    public Long getLoansClosingSoon() { return loansClosingSoon; }
    public void setLoansClosingSoon(Long loansClosingSoon) { this.loansClosingSoon = loansClosingSoon; }
    
    public Long getLoansReadyToClearToClose() { return loansReadyToClearToClose; }
    public void setLoansReadyToClearToClose(Long loansReadyToClearToClose) { this.loansReadyToClearToClose = loansReadyToClearToClose; }
    
    public Map<String, Long> getLoansByType() { return loansByType; }
    public void setLoansByType(Map<String, Long> loansByType) { this.loansByType = loansByType; }
    
    public Map<String, Long> getLoansByStatus() { return loansByStatus; }
    public void setLoansByStatus(Map<String, Long> loansByStatus) { this.loansByStatus = loansByStatus; }
    
    public Map<String, Long> getConditionsByType() { return conditionsByType; }
    public void setConditionsByType(Map<String, Long> conditionsByType) { this.conditionsByType = conditionsByType; }
    
    public Map<String, Long> getConditionsByStatus() { return conditionsByStatus; }
    public void setConditionsByStatus(Map<String, Long> conditionsByStatus) { this.conditionsByStatus = conditionsByStatus; }
    
    public List<UserStatsResponse> getTopLoanOfficers() { return topLoanOfficers; }
    public void setTopLoanOfficers(List<UserStatsResponse> topLoanOfficers) { this.topLoanOfficers = topLoanOfficers; }
    
    public List<UserStatsResponse> getTopProcessors() { return topProcessors; }
    public void setTopProcessors(List<UserStatsResponse> topProcessors) { this.topProcessors = topProcessors; }
    
    public List<UserStatsResponse> getTopUnderwriters() { return topUnderwriters; }
    public void setTopUnderwriters(List<UserStatsResponse> topUnderwriters) { this.topUnderwriters = topUnderwriters; }
    
    public List<LoanSummaryResponse> getRecentLoans() { return recentLoans; }
    public void setRecentLoans(List<LoanSummaryResponse> recentLoans) { this.recentLoans = recentLoans; }
    
    public List<ConditionResponse> getRecentConditions() { return recentConditions; }
    public void setRecentConditions(List<ConditionResponse> recentConditions) { this.recentConditions = recentConditions; }
}