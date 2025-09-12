import React, { createContext, useReducer, useEffect } from 'react'

// Export the context immediately  
export const LoanContext = createContext()

// Initial state
const initialState = {
  loans: [],
  selectedLoan: null,
  loading: false,
  error: null,
  filters: {
    status: '',
    search: ''
  }
}

// Reducer
function loanReducer(state, action) {
  switch (action.type) {
    case 'SET_LOADING':
      return { ...state, loading: action.payload }
    case 'SET_LOANS':
      return { ...state, loans: action.payload, loading: false }
    case 'SET_SELECTED_LOAN':
      return { ...state, selectedLoan: action.payload }
    case 'ADD_LOAN':
      return { ...state, loans: [action.payload, ...state.loans] }
    case 'UPDATE_LOAN':
      return {
        ...state,
        loans: state.loans.map(loan =>
          loan.loanId === action.payload.loanId ? action.payload : loan
        )
      }
    case 'SET_FILTERS':
      return { ...state, filters: { ...state.filters, ...action.payload } }
    case 'SET_ERROR':
      return { ...state, error: action.payload, loading: false }
    default:
      return state
  }
}

// Provider component
export function LoanProvider({ children }) {
  const [state, dispatch] = useReducer(loanReducer, initialState)

  // Mock data
  useEffect(() => {
    const mockLoans = [
      {
        loanId: 'LN001',
        borrowerName: 'John Smith',
        propertyAddress: '123 Main St, Kansas City, MO',
        loanAmount: 350000,
        loanType: 'CONVENTIONAL',
        status: 'PRE_UW',
        interestRate: 6.75,
        ltv: 80,
        dti: 28,
        processor: { id: 2, firstName: 'Sarah', lastName: 'Johnson' },
        underwriter: { id: 1, firstName: 'Mike', lastName: 'Chen' },
        loanOfficer: { id: 3, firstName: 'Lisa', lastName: 'Davis' },
        createdAt: '2024-09-01T10:00:00Z',
        conditions: [
          {
            id: 1,
            conditionType: 'Appraisal',
            status: 'PENDING',
            priority: 'HIGH',
            assignedTo: 'processor',
            comments: 'Order appraisal for property valuation'
          }
        ]
      },
      {
        loanId: 'LN002',
        borrowerName: 'Maria Garcia',
        propertyAddress: '456 Oak Ave, Overland Park, KS',
        loanAmount: 275000,
        loanType: 'FHA',
        status: 'APPROVED_CONDITIONS',
        interestRate: 6.25,
        ltv: 85,
        dti: 31,
        processor: { id: 4, firstName: 'Tom', lastName: 'Wilson' },
        underwriter: { id: 1, firstName: 'Mike', lastName: 'Chen' },
        loanOfficer: { id: 5, firstName: 'Robert', lastName: 'Taylor' },
        createdAt: '2024-08-28T14:30:00Z',
        conditions: [
          {
            id: 3,
            conditionType: 'Insurance (HOI)',
            status: 'PENDING',
            priority: 'HIGH',
            assignedTo: 'borrower',
            comments: 'Need homeowners insurance quote'
          }
        ]
      }
    ]
    
    dispatch({ type: 'SET_LOANS', payload: mockLoans })
  }, [])

  // Functions
  const selectLoan = (loan) => {
    dispatch({ type: 'SET_SELECTED_LOAN', payload: loan })
  }

  const setFilters = (filters) => {
    dispatch({ type: 'SET_FILTERS', payload: filters })
  }

  const getFilteredLoans = () => {
    let filtered = state.loans

    if (state.filters.status) {
      filtered = filtered.filter(loan => loan.status === state.filters.status)
    }

    if (state.filters.search) {
      const searchTerm = state.filters.search.toLowerCase()
      filtered = filtered.filter(loan =>
        loan.borrowerName.toLowerCase().includes(searchTerm) ||
        loan.loanId.toLowerCase().includes(searchTerm) ||
        loan.propertyAddress.toLowerCase().includes(searchTerm)
      )
    }

    return filtered
  }

  const getLoanCountsByStatus = () => {
    const statusList = ['CREATING', 'PRE_UW', 'PRE_APPROVED', 'APPROVED_CONDITIONS', 'CLEAR_TO_CLOSE', 'CLOSING', 'CLOSED']
    const counts = {}
    statusList.forEach(status => {
      counts[status] = state.loans.filter(loan => loan.status === status).length
    })
    return counts
  }

  const createLoan = async (loanData) => {
    const newLoan = {
      ...loanData,
      loanId: `LN${String(state.loans.length + 1).padStart(3, '0')}`,
      status: 'CREATING',
      createdAt: new Date().toISOString(),
      conditions: []
    }
    
    dispatch({ type: 'ADD_LOAN', payload: newLoan })
    return { success: true, loan: newLoan }
  }

  const updateLoan = async (loanId, updates) => {
    const existingLoan = state.loans.find(loan => loan.loanId === loanId)
    if (existingLoan) {
      const updatedLoan = { ...existingLoan, ...updates }
      dispatch({ type: 'UPDATE_LOAN', payload: updatedLoan })
      return { success: true, loan: updatedLoan }
    }
    return { success: false, error: 'Loan not found' }
  }

  const value = {
    ...state,
    selectLoan,
    setFilters,
    getFilteredLoans,
    getLoanCountsByStatus,
    createLoan,
    updateLoan
  }

  return (
    <LoanContext.Provider value={value}>
      {children}
    </LoanContext.Provider>
  )
}