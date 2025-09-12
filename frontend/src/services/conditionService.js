import { apiHelpers } from './api'
import { API_ENDPOINTS } from '../utils/constants'

class ConditionService {
  // Get all conditions for a loan
  async getConditionsByLoan(loanId) {
    try {
      const response = await apiHelpers.get(API_ENDPOINTS.CONDITIONS.BY_LOAN(loanId))
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Get condition by ID
  async getConditionById(conditionId) {
    try {
      const response = await apiHelpers.get(API_ENDPOINTS.CONDITIONS.BY_ID(conditionId))
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Create new condition
  async createCondition(conditionData) {
    try {
      const response = await apiHelpers.post(API_ENDPOINTS.CONDITIONS.BASE, conditionData)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Update condition
  async updateCondition(conditionId, updates) {
    try {
      const response = await apiHelpers.put(API_ENDPOINTS.CONDITIONS.BY_ID(conditionId), updates)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Update condition status
  async updateConditionStatus(conditionId, status, comments = '') {
    try {
      const response = await apiHelpers.put(API_ENDPOINTS.CONDITIONS.STATUS(conditionId), {
        status,
        comments
      })
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Delete condition
  async deleteCondition(conditionId) {
    try {
      const response = await apiHelpers.delete(API_ENDPOINTS.CONDITIONS.BY_ID(conditionId))
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Complete condition
  async completeCondition(conditionId, comments = '') {
    try {
      return await this.updateConditionStatus(conditionId, 'COMPLETED', comments)
    } catch (error) {
      throw error
    }
  }

  // Waive condition
  async waiveCondition(conditionId, reason = '') {
    try {
      return await this.updateConditionStatus(conditionId, 'WAIVED', reason)
    } catch (error) {
      throw error
    }
  }

  // Add comment to condition
  async addConditionComment(conditionId, comment) {
    try {
      const response = await apiHelpers.post(`${API_ENDPOINTS.CONDITIONS.BY_ID(conditionId)}/comments`, {
        comment
      })
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Get condition comments
  async getConditionComments(conditionId) {
    try {
      const response = await apiHelpers.get(`${API_ENDPOINTS.CONDITIONS.BY_ID(conditionId)}/comments`)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Get conditions assigned to current user
  async getMyConditions(status = null) {
    try {
      const params = status ? `?status=${status}` : ''
      const response = await apiHelpers.get(`${API_ENDPOINTS.CONDITIONS.BASE}/my-conditions${params}`)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Bulk update conditions
  async bulkUpdateConditions(conditionIds, updates) {
    try {
      const response = await apiHelpers.put(`${API_ENDPOINTS.CONDITIONS.BASE}/bulk-update`, {
        conditionIds,
        updates
      })
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Get condition templates/presets
  async getConditionTemplates() {
    try {
      const response = await apiHelpers.get(`${API_ENDPOINTS.CONDITIONS.BASE}/templates`)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Create condition from template
  async createConditionFromTemplate(loanId, templateId, customizations = {}) {
    try {
      const response = await apiHelpers.post(`${API_ENDPOINTS.CONDITIONS.BASE}/from-template`, {
        loanId,
        templateId,
        customizations
      })
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Get condition statistics
  async getConditionStatistics(loanId = null) {
    try {
      const params = loanId ? `?loanId=${loanId}` : ''
      const response = await apiHelpers.get(`${API_ENDPOINTS.CONDITIONS.BASE}/statistics${params}`)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Upload document for condition
  async uploadConditionDocument(conditionId, file, documentType = 'Other') {
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('conditionId', conditionId)
      formData.append('documentType', documentType)

      const response = await apiHelpers.upload(
        `${API_ENDPOINTS.CONDITIONS.BY_ID(conditionId)}/documents`,
        formData
      )
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Send condition reminder
  async sendConditionReminder(conditionId, message = '') {
    try {
      const response = await apiHelpers.post(`${API_ENDPOINTS.CONDITIONS.BY_ID(conditionId)}/remind`, {
        message
      })
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Reassign condition
  async reassignCondition(conditionId, newAssignee, reason = '') {
    try {
      const response = await apiHelpers.put(`${API_ENDPOINTS.CONDITIONS.BY_ID(conditionId)}/reassign`, {
        assignedTo: newAssignee,
        reason
      })
      return response.data
    } catch (error) {
      throw error
    }
  }

  // Utility methods
  
  // Check if all loan conditions are satisfied
  checkAllConditionsSatisfied(conditions) {
    if (!conditions || conditions.length === 0) return true
    
    return conditions.every(condition => 
      condition.status === 'COMPLETED' || condition.status === 'WAIVED'
    )
  }

  // Get pending conditions count
  getPendingConditionsCount(conditions) {
    if (!conditions) return 0
    
    return conditions.filter(condition => 
      condition.status === 'PENDING' || condition.status === 'IN_PROGRESS'
    ).length
  }

  // Group conditions by status
  groupConditionsByStatus(conditions) {
    if (!conditions) return {}
    
    return conditions.reduce((groups, condition) => {
      const status = condition.status
      if (!groups[status]) {
        groups[status] = []
      }
      groups[status].push(condition)
      return groups
    }, {})
  }

  // Group conditions by assignee
  groupConditionsByAssignee(conditions) {
    if (!conditions) return {}
    
    return conditions.reduce((groups, condition) => {
      const assignee = condition.assignedTo || 'Unassigned'
      if (!groups[assignee]) {
        groups[assignee] = []
      }
      groups[assignee].push(condition)
      return groups
    }, {})
  }

  // Get high priority conditions
  getHighPriorityConditions(conditions) {
    if (!conditions) return []
    
    return conditions.filter(condition => 
      condition.priority === 'HIGH' && 
      (condition.status === 'PENDING' || condition.status === 'IN_PROGRESS')
    )
  }

  // Calculate condition completion rate
  calculateCompletionRate(conditions) {
    if (!conditions || conditions.length === 0) return 100
    
    const completed = conditions.filter(condition => 
      condition.status === 'COMPLETED' || condition.status === 'WAIVED'
    ).length
    
    return Math.round((completed / conditions.length) * 100)
  }

  // Validate condition data
  validateConditionData(conditionData) {
    const errors = {}
    
    if (!conditionData.conditionType?.trim()) {
      errors.conditionType = 'Condition type is required'
    }
    
    if (!conditionData.assignedTo?.trim()) {
      errors.assignedTo = 'Assignee is required'
    }
    
    if (!conditionData.priority) {
      errors.priority = 'Priority is required'
    }
    
    return {
      isValid: Object.keys(errors).length === 0,
      errors
    }
  }

  // Format condition for display
  formatConditionForDisplay(condition) {
    return {
      ...condition,
      formattedCreatedAt: new Date(condition.createdAt).toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric'
      }),
      formattedUpdatedAt: new Date(condition.updatedAt).toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric'
      }),
      isOverdue: this.isConditionOverdue(condition),
      daysSinceCreated: Math.floor(
        (new Date() - new Date(condition.createdAt)) / (1000 * 60 * 60 * 24)
      )
    }
  }

  // Check if condition is overdue (basic implementation)
  isConditionOverdue(condition) {
    if (condition.status === 'COMPLETED' || condition.status === 'WAIVED') {
      return false
    }
    
    const daysSinceCreated = Math.floor(
      (new Date() - new Date(condition.createdAt)) / (1000 * 60 * 60 * 24)
    )
    
    // Simple business rule: high priority conditions are overdue after 3 days,
    // medium after 7 days, low after 14 days
    const overdueThresholds = {
      HIGH: 3,
      MEDIUM: 7,
      LOW: 14
    }
    
    return daysSinceCreated > (overdueThresholds[condition.priority] || 7)
  }
}

// Create and export singleton instance
export const conditionService = new ConditionService()
export default conditionService