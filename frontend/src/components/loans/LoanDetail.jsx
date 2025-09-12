import React from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft } from 'lucide-react'
import { useLoans } from '../../hooks/useLoans'
import Header from '../common/Header'
import LoadingSpinner from '../common/LoadingSpinner'
import Button from '../common/Button'

const LoanDetail = () => {
  const { loanId } = useParams()
  const navigate = useNavigate()
  const { selectedLoan, loans } = useLoans()

  // Find loan if not in selectedLoan (direct URL access)
  const loan = selectedLoan || loans.find(l => l.loanId === loanId)

  if (!loan) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Header />
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="flex items-center justify-center py-12">
            <div className="text-center">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">
                Loan Not Found
              </h2>
              <p className="text-gray-600 mb-6">
                The loan with ID "{loanId}" could not be found.
              </p>
              <Button onClick={() => navigate('/dashboard')}>
                Back to Dashboard
              </Button>
            </div>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Back Button */}
        <div className="flex items-center mb-6">
          <Button
            variant="ghost"
            onClick={() => navigate('/dashboard')}
            icon={<ArrowLeft />}
            className="mr-4"
          >
            Back to Dashboard
          </Button>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">
              {loan.borrowerName} - {loan.loanId}
            </h1>
            <p className="text-gray-600">Loan Details & Management</p>
          </div>
        </div>

        {/* Loan Detail Content */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
          <div className="text-center py-12">
            <LoadingSpinner size="lg" />
            <h3 className="mt-4 text-lg font-medium text-gray-900">
              Loan Detail View
            </h3>
            <p className="mt-2 text-gray-600">
              This component will show detailed loan information, conditions, documents, and management actions.
            </p>
            <p className="mt-2 text-sm text-gray-500">
              Full implementation coming in the next phase...
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default LoanDetail