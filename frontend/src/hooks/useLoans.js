import { useContext } from 'react'
import { LoanContext } from '../context/LoanContext'

export function useLoans() {
  const context = useContext(LoanContext)
  
  if (!context) {
    throw new Error('useLoans must be used within a LoanProvider')
  }
  
  return context
}

export default useLoans