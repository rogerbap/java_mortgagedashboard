import { apiHelpers } from './api'
import { API_ENDPOINTS } from '../utils/constants'

class LoanService {
  // Get all loans
  async getLoans(filters = {}) {
    try {
      const params = new URLSearchParams(filters).toString()
      const url = params ? `${API_ENDPOINTS.LOANS.BASE}?${params}` : API_ENDPOINTS.LOANS.BASE
      const response = await apiHelpers.get(url)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Get loan by ID
  async getLoanById(loanId) {
    try {
      const response = await apiHelpers.get(API_ENDPOINTS.LOANS.BY_ID(loanId))
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Create new loan
  async createLoan(loanData) {
    try {
      const response = await apiHelpers.post(API_ENDPOINTS.LOANS.BASE, loanData)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Update loan
  async updateLoan(loanId, updates) {
    try {
      const response = await apiHelpers.put(API_ENDPOINTS.LOANS.BY_ID(loanId), updates)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Update loan status
  async updateLoanStatus(loanId, status) {
    try {
      const response = await apiHelpers.put(API_ENDPOINTS.LOANS.STATUS(loanId), { status })
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Update interest rate
  async updateInterestRate(loanId, interestRate) {
    try {
      const response = await apiHelpers.put(API_ENDPOINTS.LOANS.INTEREST_RATE(loanId), { interestRate })
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Delete loan
  async deleteLoan(loanId) {
    try {
      const response = await apiHelpers.delete(API_ENDPOINTS.LOANS.BY_ID(loanId))
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Get loan statistics
  async getLoanStatistics() {
    try {
      const response = await apiHelpers.get(`${API_ENDPOINTS.LOANS.BASE}/statistics`)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Search loans
  async searchLoans(searchTerm) {
    try {
      const response = await apiHelpers.get(`${API_ENDPOINTS.LOANS.BASE}/search?q=${encodeURIComponent(searchTerm)}`)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Get loans by status
  async getLoansByStatus(status) {
    try {
      const response = await apiHelpers.get(`${API_ENDPOINTS.LOANS.BASE}?status=${status}`)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Get loans assigned to user
  async getMyLoans(role) {
    try {
      const response = await apiHelpers.get(`${API_ENDPOINTS.LOANS.BASE}/my-loans?role=${role}`)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Assign loan to team members
  async assignLoan(loanId, assignments) {
    try {
      const response = await apiHelpers.put(`${API_ENDPOINTS.LOANS.BY_ID(loanId)}/assign`, assignments)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Send loan to next stage
  async sendLoanToStage(loanId, targetStage, comments = '') {
    try {
      const response = await apiHelpers.post(`${API_ENDPOINTS.LOANS.BY_ID(loanId)}/send-to-stage`, {
        targetStage,
        comments
      })
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Get loan history/audit trail
  async getLoanHistory(loanId) {
    try {
      const response = await apiHelpers.get(`${API_ENDPOINTS.LOANS.BY_ID(loanId)}/history`)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Export loans
  async exportLoans(filters = {}, format = 'csv') {
    try {
      const params = new URLSearchParams({ ...filters, format }).toString()
      const response = await apiHelpers.get(`${API_ENDPOINTS.LOANS.BASE}/export?${params}`, {
        responseType: 'blob'
      })
      
      // Create download link
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', `loans-export-${new Date().toISOString().split('T')[0]}.${format}`)
      document.body.appendChild(link)
      link.click()
      link.remove()
      window.URL.revokeObjectURL(url)
      
      return { success: true }
    } catch (error) {
      throw error
    }
  }

  // Calculate loan metrics
  calculateLTV(loanAmount, propertyValue) {
    if (!loanAmount || !propertyValue || propertyValue <= 0) return 0
    return Math.round((loanAmount / propertyValue) * 100)
  }

  // Format loan amount for display
  formatLoanAmount(amount) {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(amount)
  }

  // Calculate monthly payment (basic calculation)
  calculateMonthlyPayment(loanAmount, interestRate, termYears = 30) {
    if (!loanAmount || !interestRate) return 0
    
    const monthlyRate = interestRate / 100 / 12
    const numberOfPayments = termYears * 12
    
    if (monthlyRate === 0) {
      return loanAmount / numberOfPayments
    }
    
    const monthlyPayment = loanAmount * (
      (monthlyRate * Math.pow(1 + monthlyRate, numberOfPayments)) /
      (Math.pow(1 + monthlyRate, numberOfPayments) - 1)
    )
    
    return Math.round(monthlyPayment * 100) / 100
  }

  // Validate loan data
  validateLoanData(loanData) {
    const errors = {}
    
    if (!loanData.borrowerName?.trim()) {
      errors.borrowerName = 'Borrower name is required'
    }
    
    if (!loanData.propertyAddress?.trim()) {
      errors.propertyAddress = 'Property address is required'
    }
    
    if (!loanData.loanAmount || loanData.loanAmount <= 0) {
      errors.loanAmount = 'Valid loan amount is required'
    }
    
    if (!loanData.interestRate || loanData.interestRate <= 0) {
      errors.interestRate = 'Valid interest rate is required'
    }
    
    return {
      isValid: Object.keys(errors).length === 0,
      errors
    }
  }
}

// Create and export singleton instance
export const loanService = new LoanService()
export default loanService