package com.lender.mortgage.controller;

import com.lender.mortgage.dto.response.ApiResponse;
import com.lender.mortgage.dto.response.DashboardStatsResponse;
import com.lender.mortgage.service.LoanService;
import com.lender.mortgage.service.ConditionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Dashboard statistics and analytics endpoints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {
    
    @Autowired
    private LoanService loanService;
    
    @Autowired
    private ConditionService conditionService;
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get dashboard statistics", description = "Get comprehensive dashboard statistics")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        
        // In a real implementation, you would create a DashboardService
        // that aggregates data from multiple sources to build the stats response
        // For now, this is a simplified example structure
        
        DashboardStatsResponse stats = new DashboardStatsResponse();
        
        // Example: You would populate these from your services
        // stats.setTotalLoans(loanRepository.count());
        // stats.setActiveLoans(loanRepository.countActiveLoans());
        // stats.setOverdueLoans((long) loanService.getOverdueLoans().size());
        // stats.setOverdueConditions((long) conditionService.getOverdueConditions().size());
        // ... etc
        
        // For demo purposes, setting some mock values
        stats.setTotalLoans(150L);
        stats.setActiveLoans(45L);
        stats.setClosedLoans(95L);
        stats.setDeniedLoans(10L);
        
        return ResponseEntity.ok(ApiResponse.success("Dashboard statistics retrieved", stats));
    }
    
    @GetMapping("/alerts")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get dashboard alerts", description = "Get important alerts and notifications")
    public ResponseEntity<ApiResponse<Object>> getDashboardAlerts() {
        
        // In a real implementation, you would aggregate alerts from various sources
        // such as overdue loans, high priority conditions, etc.
        
        Object alerts = new Object(); // Placeholder
        
        return ResponseEntity.ok(ApiResponse.success("Dashboard alerts retrieved", alerts));
    }
    
    @GetMapping("/recent-activity")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('PROCESSOR') or hasRole('UNDERWRITER') or hasRole('MANAGER')")
    @Operation(summary = "Get recent activity", description = "Get recent loans and condition updates")
    public ResponseEntity<ApiResponse<Object>> getRecentActivity() {
        
        // In a real implementation, you would get recent loans, conditions, documents, etc.
        
        Object recentActivity = new Object(); // Placeholder
        
        return ResponseEntity.ok(ApiResponse.success("Recent activity retrieved", recentActivity));
    }
}