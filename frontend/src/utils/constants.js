// Loan Status Constants
export const LOAN_STATUS = {
  CREATING: 'CREATING',
  PRE_UW: 'PRE_UW',
  PRE_APPROVED: 'PRE_APPROVED',
  APPROVED_CONDITIONS: 'APPROVED_CONDITIONS',
  CLEAR_TO_CLOSE: 'CLEAR_TO_CLOSE',
  CLOSING: 'CLOSING',
  CLOSED: 'CLOSED'
}

// Loan Status Display Names and Colors
export const LOAN_STATUS_CONFIG = {
  [LOAN_STATUS.CREATING]: {
    label: 'Creating Loan',
    color: 'bg-gray-500',
    textColor: 'text-gray-800',
    bgColor: 'bg-gray-100'
  },
  [LOAN_STATUS.PRE_UW]: {
    label: 'Pre Underwriting',
    color: 'bg-blue-500',
    textColor: 'text-blue-800',
    bgColor: 'bg-blue-100'
  },
  [LOAN_STATUS.PRE_APPROVED]: {
    label: 'Pre Approved',
    color: 'bg-yellow-500',
    textColor: 'text-yellow-800',
    bgColor: 'bg-yellow-100'
  },
  [LOAN_STATUS.APPROVED_CONDITIONS]: {
    label: 'Approved w/ Conditions',
    color: 'bg-orange-500',
    textColor: 'text-orange-800',
    bgColor: 'bg-orange-100'
  },
  [LOAN_STATUS.CLEAR_TO_CLOSE]: {
    label: 'Clear to Close',
    color: 'bg-green-500',
    textColor: 'text-green-800',
    bgColor: 'bg-green-100'
  },
  [LOAN_STATUS.CLOSING]: {
    label: 'Closing',
    color: 'bg-purple-500',
    textColor: 'text-purple-800',
    bgColor: 'bg-purple-100'
  },
  [LOAN_STATUS.CLOSED]: {
    label: 'Closed',
    color: 'bg-emerald-600',
    textColor: 'text-emerald-800',
    bgColor: 'bg-emerald-100'
  }
}

// Loan Types
export const LOAN_TYPE = {
  CONVENTIONAL: 'CONVENTIONAL',
  FHA: 'FHA',
  VA: 'VA',
  USDA: 'USDA',
  JUMBO: 'JUMBO'
}

export const LOAN_TYPE_CONFIG = {
  [LOAN_TYPE.CONVENTIONAL]: {
    label: 'Conventional',
    description: 'Standard conventional mortgage loan'
  },
  [LOAN_TYPE.FHA]: {
    label: 'FHA',
    description: 'Federal Housing Administration loan'
  },
  [LOAN_TYPE.VA]: {
    label: 'VA',
    description: 'Veterans Affairs loan'
  },
  [LOAN_TYPE.USDA]: {
    label: 'USDA',
    description: 'United States Department of Agriculture loan'
  },
  [LOAN_TYPE.JUMBO]: {
    label: 'Jumbo',
    description: 'High-balance mortgage loan'
  }
}

// Condition Status
export const CONDITION_STATUS = {
  PENDING: 'PENDING',
  IN_PROGRESS: 'IN_PROGRESS',
  COMPLETED: 'COMPLETED',
  WAIVED: 'WAIVED'
}

export const CONDITION_STATUS_CONFIG = {
  [CONDITION_STATUS.PENDING]: {
    label: 'Pending',
    color: 'bg-gray-500',
    textColor: 'text-gray-800',
    bgColor: 'bg-gray-100'
  },
  [CONDITION_STATUS.IN_PROGRESS]: {
    label: 'In Progress',
    color: 'bg-blue-500',
    textColor: 'text-blue-800',
    bgColor: 'bg-blue-100'
  },
  [CONDITION_STATUS.COMPLETED]: {
    label: 'Completed',
    color: 'bg-green-500',
    textColor: 'text-green-800',
    bgColor: 'bg-green-100'
  },
  [CONDITION_STATUS.WAIVED]: {
    label: 'Waived',
    color: 'bg-yellow-500',
    textColor: 'text-yellow-800',
    bgColor: 'bg-yellow-100'
  }
}

// Priority Levels
export const PRIORITY = {
  LOW: 'LOW',
  MEDIUM: 'MEDIUM',
  HIGH: 'HIGH'
}

export const PRIORITY_CONFIG = {
  [PRIORITY.LOW]: {
    label: 'Low',
    color: 'bg-green-500',
    textColor: 'text-green-800',
    bgColor: 'bg-green-100'
  },
  [PRIORITY.MEDIUM]: {
    label: 'Medium',
    color: 'bg-yellow-500',
    textColor: 'text-yellow-800',
    bgColor: 'bg-yellow-100'
  },
  [PRIORITY.HIGH]: {
    label: 'High',
    color: 'bg-red-500',
    textColor: 'text-red-800',
    bgColor: 'bg-red-100'
  }
}

// User Roles
export const USER_ROLE = {
  BORROWER: 'BORROWER',
  PROCESSOR: 'PROCESSOR',
  LOAN_OFFICER: 'LOAN_OFFICER',
  UNDERWRITER: 'UNDERWRITER',
  MANAGER: 'MANAGER'
}

export const USER_ROLE_CONFIG = {
  [USER_ROLE.BORROWER]: {
    label: 'Borrower',
    description: 'Loan applicant'
  },
  [USER_ROLE.PROCESSOR]: {
    label: 'Processor',
    description: 'Loan processor'
  },
  [USER_ROLE.LOAN_OFFICER]: {
    label: 'Loan Officer',
    description: 'Loan origination officer'
  },
  [USER_ROLE.UNDERWRITER]: {
    label: 'Underwriter',
    description: 'Loan underwriter'
  },
  [USER_ROLE.MANAGER]: {
    label: 'Manager',
    description: 'Department manager'
  }
}

// Preset Conditions
export const PRESET_CONDITIONS = [
  'Appraisal',
  'Title (Commitment, Schedule A and B, Record of Deed, Survey, Title Insurance, Prelim CD)',
  'Inspection',
  'Insurance (HOI)',
  'Mortgage Insurance Quote',
  'Bank Statements',
  'Paystubs',
  'W2',
  '1099',
  'Tax Returns',
  'Flood Insurance',
  'UCC-1',
  'Tax Transcripts',
  'VOI',
  'VOE',
  'LOE'
]

// Assignment Options
export const ASSIGNMENT_OPTIONS = [
  { value: 'processor', label: 'Processor' },
  { value: 'borrower', label: 'Borrower' },
  { value: 'underwriter', label: 'Underwriter' }
]

// Document Types
export const DOCUMENT_TYPES = [
  'Application',
  'Credit Report',
  'Income Documentation',
  'Bank Statements',
  'Tax Returns',
  'Property Documentation',
  'Insurance Documentation',
  'Title Documentation',
  'Appraisal',
  'Other'
]

// Status Flow for Pipeline
export const STATUS_FLOW = [
  LOAN_STATUS.CREATING,
  LOAN_STATUS.PRE_UW,
  LOAN_STATUS.PRE_APPROVED,
  LOAN_STATUS.APPROVED_CONDITIONS,
  LOAN_STATUS.CLEAR_TO_CLOSE,
  LOAN_STATUS.CLOSING,
  LOAN_STATUS.CLOSED
]

// API Endpoints
export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/auth/login',
    LOGOUT: '/auth/logout',
    ME: '/auth/me',
    REFRESH: '/auth/refresh'
  },
  LOANS: {
    BASE: '/loans',
    BY_ID: (id) => `/loans/${id}`,
    STATUS: (id) => `/loans/${id}/status`,
    INTEREST_RATE: (id) => `/loans/${id}/interest-rate`
  },
  CONDITIONS: {
    BASE: '/conditions',
    BY_ID: (id) => `/conditions/${id}`,
    BY_LOAN: (loanId) => `/conditions/loan/${loanId}`,
    STATUS: (id) => `/conditions/${id}/status`
  },
  DOCUMENTS: {
    BASE: '/documents',
    BY_ID: (id) => `/documents/${id}`,
    BY_LOAN: (loanId) => `/documents/loan/${loanId}`,
    UPLOAD: '/documents/upload'
  },
  USERS: {
    BASE: '/users',
    BY_ID: (id) => `/users/${id}`,
    BY_ROLE: (role) => `/users/role/${role}`
  }
}

// Validation Rules
export const VALIDATION_RULES = {
  LOAN_AMOUNT: {
    MIN: 1000,
    MAX: 10000000
  },
  INTEREST_RATE: {
    MIN: 0.1,
    MAX: 20.0,
    STEP: 0.125
  },
  LTV: {
    MIN: 1,
    MAX: 100
  },
  DTI: {
    MIN: 1,
    MAX: 100
  }
}

// Date Formats
export const DATE_FORMATS = {
  DISPLAY: 'MMM dd, yyyy',
  INPUT: 'yyyy-MM-dd',
  DATETIME: 'MMM dd, yyyy HH:mm',
  TIME: 'HH:mm'
}

// Local Storage Keys
export const STORAGE_KEYS = {
  AUTH_TOKEN: 'authToken',
  USER_PREFERENCES: 'userPreferences',
  DASHBOARD_FILTERS: 'dashboardFilters'
}

export default {
  LOAN_STATUS,
  LOAN_STATUS_CONFIG,
  LOAN_TYPE,
  LOAN_TYPE_CONFIG,
  CONDITION_STATUS,
  CONDITION_STATUS_CONFIG,
  PRIORITY,
  PRIORITY_CONFIG,
  USER_ROLE,
  USER_ROLE_CONFIG,
  PRESET_CONDITIONS,
  ASSIGNMENT_OPTIONS,
  DOCUMENT_TYPES,
  STATUS_FLOW,
  API_ENDPOINTS,
  VALIDATION_RULES,
  DATE_FORMATS,
  STORAGE_KEYS
}