import React, { createContext, useReducer, useEffect } from 'react'

// Export the context immediately
export const AuthContext = createContext()

// Initial state
const initialState = {
  user: null,
  token: localStorage.getItem('authToken'),
  loading: true,
  error: null
}

// Reducer
function authReducer(state, action) {
  switch (action.type) {
    case 'SET_LOADING':
      return { ...state, loading: action.payload }
    case 'LOGIN_SUCCESS':
      return {
        ...state,
        user: action.payload.user,
        token: action.payload.token,
        loading: false,
        error: null
      }
    case 'LOGIN_FAILURE':
      return {
        ...state,
        user: null,
        token: null,
        loading: false,
        error: action.payload
      }
    case 'LOGOUT':
      return {
        ...state,
        user: null,
        token: null,
        loading: false,
        error: null
      }
    case 'SET_USER':
      return { ...state, user: action.payload, loading: false }
    default:
      return state
  }
}

// Provider component
export function AuthProvider({ children }) {
  const [state, dispatch] = useReducer(authReducer, initialState)

  // Initialize auth state
  useEffect(() => {
    const token = localStorage.getItem('authToken')
    if (token) {
      // Mock user data based on token
      const mockUser = {
        id: 1,
        firstName: 'Demo',
        lastName: 'User',
        email: 'demo@lender.com',
        role: 'UNDERWRITER'
      }
      dispatch({ type: 'SET_USER', payload: mockUser })
    } else {
      dispatch({ type: 'SET_LOADING', payload: false })
    }
  }, [])

  // Mock login function
  const login = async (credentials) => {
    dispatch({ type: 'SET_LOADING', payload: true })
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const mockUsers = {
      'mchen': { id: 1, firstName: 'Mike', lastName: 'Chen', email: 'mchen@lender.com', role: 'UNDERWRITER' },
      'sjohnson': { id: 2, firstName: 'Sarah', lastName: 'Johnson', email: 'sjohnson@lender.com', role: 'PROCESSOR' },
      'admin': { id: 3, firstName: 'Admin', lastName: 'User', email: 'admin@lender.com', role: 'MANAGER' },
      'ldavis': { id: 4, firstName: 'Lisa', lastName: 'Davis', email: 'ldavis@lender.com', role: 'LOAN_OFFICER' }
    }
    
    const user = mockUsers[credentials.username]
    if (user && credentials.password === 'password123') {
      const token = 'mock-token-' + Date.now()
      localStorage.setItem('authToken', token)
      dispatch({ type: 'LOGIN_SUCCESS', payload: { user, token } })
      return { success: true }
    } else {
      dispatch({ type: 'LOGIN_FAILURE', payload: 'Invalid credentials' })
      return { success: false, error: 'Invalid credentials' }
    }
  }

  // Logout function
  const logout = () => {
    localStorage.removeItem('authToken')
    dispatch({ type: 'LOGOUT' })
  }

  // Permission functions
  const hasRole = (role) => state.user?.role === role
  const hasAnyRole = (roles) => roles.includes(state.user?.role)
  const canPerform = (action) => {
    if (!state.user) return false
    const permissions = {
      BORROWER: ['view_own_loans', 'upload_docs'],
      PROCESSOR: ['process_loans', 'update_conditions', 'upload_docs'],
      LOAN_OFFICER: ['view_loans'],
      UNDERWRITER: ['approve_loans', 'add_conditions', 'update_status', 'change_interest_rate'],
      MANAGER: ['all_access']
    }
    const userPermissions = permissions[state.user.role] || []
    return userPermissions.includes(action) || userPermissions.includes('all_access')
  }

  const value = {
    user: state.user,
    token: state.token,
    loading: state.loading,
    error: state.error,
    login,
    logout,
    hasRole,
    hasAnyRole,
    canPerform,
    isAuthenticated: !!state.user
  }

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}