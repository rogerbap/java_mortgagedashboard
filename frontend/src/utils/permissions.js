// User roles
export const USER_ROLES = {
  BORROWER: 'BORROWER',
  PROCESSOR: 'PROCESSOR',
  LOAN_OFFICER: 'LOAN_OFFICER',
  UNDERWRITER: 'UNDERWRITER',
  MANAGER: 'MANAGER'
}

// Permission actions
export const PERMISSIONS = {
  // General permissions
  VIEW_LOANS: 'view_loans',
  VIEW_OWN_LOANS: 'view_own_loans',
  CREATE_LOAN: 'create_loan',
  EDIT_LOAN: 'edit_loan',
  DELETE_LOAN: 'delete_loan',
  
  // Status management
  UPDATE_STATUS: 'update_status',
  CLEAR_TO_CLOSE: 'clear_to_close',
  SEND_TO_UNDERWRITER: 'send_to_underwriter',
  SEND_TO_PROCESSOR: 'send_to_processor',
  
  // Conditions
  ADD_CONDITIONS: 'add_conditions',
  UPDATE_CONDITIONS: 'update_conditions',
  COMPLETE_CONDITIONS: 'complete_conditions',
  WAIVE_CONDITIONS: 'waive_conditions',
  
  // Documents
  UPLOAD_DOCS: 'upload_docs',
  VIEW_DOCS: 'view_docs',
  DELETE_DOCS: 'delete_docs',
  
  // Financial
  CHANGE_INTEREST_RATE: 'change_interest_rate',
  VIEW_FINANCIAL_DETAILS: 'view_financial_details',
  
  // Team management
  ASSIGN_LOANS: 'assign_loans',
  ASSIGN_TEAM_MEMBERS: 'assign_team_members',
  
  // User management
  USER_MANAGEMENT: 'user_management',
  
  // Reports and analytics
  VIEW_REPORTS: 'view_reports',
  EXPORT_DATA: 'export_data',
  
  // Administrative
  ALL_ACCESS: 'all_access'
}

// Role-based permission mapping
export const ROLE_PERMISSIONS = {
  [USER_ROLES.BORROWER]: [
    PERMISSIONS.VIEW_OWN_LOANS,
    PERMISSIONS.UPLOAD_DOCS,
    PERMISSIONS.VIEW_DOCS
  ],
  
  [USER_ROLES.PROCESSOR]: [
    PERMISSIONS.VIEW_LOANS,
    PERMISSIONS.UPDATE_CONDITIONS,
    PERMISSIONS.COMPLETE_CONDITIONS,
    PERMISSIONS.WAIVE_CONDITIONS,
    PERMISSIONS.UPLOAD_DOCS,
    PERMISSIONS.VIEW_DOCS,
    PERMISSIONS.SEND_TO_UNDERWRITER,
    PERMISSIONS.VIEW_FINANCIAL_DETAILS
  ],
  
  [USER_ROLES.LOAN_OFFICER]: [
    PERMISSIONS.VIEW_LOANS,
    PERMISSIONS.VIEW_DOCS,
    PERMISSIONS.VIEW_FINANCIAL_DETAILS,
    PERMISSIONS.VIEW_REPORTS
  ],
  
  [USER_ROLES.UNDERWRITER]: [
    PERMISSIONS.VIEW_LOANS,
    PERMISSIONS.UPDATE_STATUS,
    PERMISSIONS.ADD_CONDITIONS,
    PERMISSIONS.UPDATE_CONDITIONS,
    PERMISSIONS.COMPLETE_CONDITIONS,
    PERMISSIONS.WAIVE_CONDITIONS,
    PERMISSIONS.CLEAR_TO_CLOSE,
    PERMISSIONS.SEND_TO_PROCESSOR,
    PERMISSIONS.UPLOAD_DOCS,
    PERMISSIONS.VIEW_DOCS,
    PERMISSIONS.CHANGE_INTEREST_RATE,
    PERMISSIONS.VIEW_FINANCIAL_DETAILS,
    PERMISSIONS.VIEW_REPORTS
  ],
  
  [USER_ROLES.MANAGER]: [
    PERMISSIONS.ALL_ACCESS
  ]
}

// Utility functions for permission checking
export const permissionUtils = {
  // Check if user has specific permission
  hasPermission: (userRole, permission) => {
    if (!userRole || !permission) return false
    
    const userPermissions = ROLE_PERMISSIONS[userRole] || []
    return userPermissions.includes(permission) || userPermissions.includes(PERMISSIONS.ALL_ACCESS)
  },

  // Check if user has any of the specified permissions
  hasAnyPermission: (userRole, permissions) => {
    if (!userRole || !permissions || !Array.isArray(permissions)) return false
    
    return permissions.some(permission => 
      permissionUtils.hasPermission(userRole, permission)
    )
  },

  // Check if user has all of the specified permissions
  hasAllPermissions: (userRole, permissions) => {
    if (!userRole || !permissions || !Array.isArray(permissions)) return false
    
    return permissions.every(permission => 
      permissionUtils.hasPermission(userRole, permission)
    )
  },

  // Get all permissions for a user role
  getUserPermissions: (userRole) => {
    return ROLE_PERMISSIONS[userRole] || []
  },

  // Check if user can edit loan based on status and role
  canEditLoan: (userRole, loanStatus) => {
    if (permissionUtils.hasPermission(userRole, PERMISSIONS.ALL_ACCESS)) {
      return true
    }

    // Business logic for loan editing based on status
    const editableStatuses = {
      [USER_ROLES.PROCESSOR]: ['CREATING', 'PRE_UW', 'APPROVED_CONDITIONS'],
      [USER_ROLES.UNDERWRITER]: ['PRE_UW', 'PRE_APPROVED', 'APPROVED_CONDITIONS', 'CLEAR_TO_CLOSE'],
      [USER_ROLES.LOAN_OFFICER]: [], // Read-only access
      [USER_ROLES.BORROWER]: [] // Read-only access
    }

    const allowedStatuses = editableStatuses[userRole] || []
    return allowedStatuses.includes(loanStatus)
  },

  // Check if user can change loan status
  canChangeStatus: (userRole, fromStatus, toStatus) => {
    if (permissionUtils.hasPermission(userRole, PERMISSIONS.ALL_ACCESS)) {
      return true
    }

    // Status transition rules
    const statusTransitions = {
      [USER_ROLES.PROCESSOR]: {
        'CREATING': ['PRE_UW'],
        'APPROVED_CONDITIONS': [], // Can work on conditions but not change status
        'CLEAR_TO_CLOSE': ['CLOSING']
      },
      [USER_ROLES.UNDERWRITER]: {
        'PRE_UW': ['PRE_APPROVED', 'APPROVED_CONDITIONS'],
        'PRE_APPROVED': ['APPROVED_CONDITIONS'],
        'APPROVED_CONDITIONS': ['CLEAR_TO_CLOSE', 'PRE_UW'], // Can send back
        'CLEAR_TO_CLOSE': ['CLOSING', 'APPROVED_CONDITIONS'] // Can send back
      }
    }

    const allowedTransitions = statusTransitions[userRole]?.[fromStatus] || []
    return allowedTransitions.includes(toStatus)
  },

  // Check if user can assign team members
  canAssignTeamMember: (userRole, teamMemberRole) => {
    if (permissionUtils.hasPermission(userRole, PERMISSIONS.ALL_ACCESS)) {
      return true
    }

    // Assignment rules
    const assignmentRules = {
      [USER_ROLES.MANAGER]: [USER_ROLES.PROCESSOR, USER_ROLES.UNDERWRITER, USER_ROLES.LOAN_OFFICER],
      [USER_ROLES.UNDERWRITER]: [USER_ROLES.PROCESSOR] // Can assign processor
    }

    const canAssign = assignmentRules[userRole] || []
    return canAssign.includes(teamMemberRole)
  }
}

export default permissionUtils