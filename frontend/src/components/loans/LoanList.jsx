import React from 'react'
import LoanCard from './LoanCard'
import LoadingSpinner from '../common/LoadingSpinner'

const LoanList = ({ loans, loading }) => {
  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <LoadingSpinner size="lg" text="Loading loans..." />
      </div>
    )
  }

  if (!loans || loans.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-12">
        <div className="text-center">
          <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg 
              className="w-8 h-8 text-gray-400" 
              fill="none" 
              stroke="currentColor" 
              viewBox="0 0 24 24"
            >
              <path 
                strokeLinecap="round" 
                strokeLinejoin="round" 
                strokeWidth={2} 
                d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" 
              />
            </svg>
          </div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">No loans found</h3>
          <p className="text-gray-500 mb-6">
            There are no loans matching your current filters.
          </p>
          <div className="flex flex-col sm:flex-row items-center justify-center space-y-2 sm:space-y-0 sm:space-x-4">
            <button className="btn-primary">
              Create New Loan
            </button>
            <button className="btn-secondary">
              Clear Filters
            </button>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* Grid Layout */}
      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
        {loans.map((loan) => (
          <LoanCard key={loan.loanId} loan={loan} />
        ))}
      </div>

      {/* Load More (if needed for pagination) */}
      {/* This would be implemented when backend pagination is added */}
      {loans.length > 9 && (
        <div className="text-center pt-8">
          <button className="btn-secondary">
            Load More Loans
          </button>
        </div>
      )}
    </div>
  )
}

export default LoanList