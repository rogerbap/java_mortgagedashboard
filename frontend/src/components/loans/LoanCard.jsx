import React from 'react'
import { useNavigate } from 'react-router-dom'
import { 
  MapPin, 
  DollarSign, 
  Percent, 
  User, 
  Clock, 
  AlertCircle,
  Eye
} from 'lucide-react'
import { LOAN_STATUS_CONFIG, LOAN_TYPE_CONFIG } from '../../utils/constants'
import { useLoans } from '../../hooks/useLoans'
import clsx from 'clsx'

const LoanCard = ({ loan }) => {
  const navigate = useNavigate()
  const { selectLoan } = useLoans()

  const handleCardClick = () => {
    selectLoan(loan)
    navigate(`/loans/${loan.loanId}`)
  }

  const statusConfig = LOAN_STATUS_CONFIG[loan.status]
  const loanTypeConfig = LOAN_TYPE_CONFIG[loan.loanType]
  
  // Calculate pending conditions
  const pendingConditions = loan.conditions?.filter(
    c => c.status === 'PENDING' || c.status === 'IN_PROGRESS'
  ).length || 0

  // Format currency
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(amount)
  }

  // Format date
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    })
  }

  return (
    <div 
      className="card hover:shadow-lg transition-all duration-200 cursor-pointer group"
      onClick={handleCardClick}
    >
      <div className="card-body">
        {/* Header */}
        <div className="flex justify-between items-start mb-4">
          <div>
            <h3 className="text-lg font-semibold text-gray-900 group-hover:text-primary-600 transition-colors">
              {loan.borrowerName}
            </h3>
            <p className="text-sm text-gray-500">{loan.loanId}</p>
          </div>
          
          {/* Status Badge */}
          <span className={clsx(
            'status-badge',
            statusConfig.textColor,
            statusConfig.bgColor
          )}>
            {statusConfig.label}
          </span>
        </div>

        {/* Property Info */}
        <div className="space-y-3 mb-4">
          <div className="flex items-start">
            <MapPin className="w-4 h-4 text-gray-400 mt-0.5 mr-2 flex-shrink-0" />
            <span className="text-sm text-gray-600 line-clamp-2">
              {loan.propertyAddress}
            </span>
          </div>
          
          <div className="flex items-center">
            <DollarSign className="w-4 h-4 text-gray-400 mr-2" />
            <span className="text-lg font-semibold text-gray-900">
              {formatCurrency(loan.loanAmount)}
            </span>
            <span className="ml-2 text-sm text-gray-500">
              ({loanTypeConfig.label})
            </span>
          </div>
          
          <div className="flex items-center">
            <Percent className="w-4 h-4 text-gray-400 mr-2" />
            <span className="text-sm text-gray-600">
              {loan.interestRate}% | LTV: {loan.ltv}% | DTI: {loan.dti}%
            </span>
          </div>
        </div>

        {/* Team Members */}
        <div className="space-y-2 mb-4">
          {loan.processor && (
            <div className="flex items-center text-sm">
              <User className="w-3 h-3 text-gray-400 mr-2" />
              <span className="text-gray-500 mr-1">Processor:</span>
              <span className="text-gray-900 font-medium">
                {loan.processor.firstName} {loan.processor.lastName}
              </span>
            </div>
          )}
          
          {loan.underwriter && (
            <div className="flex items-center text-sm">
              <User className="w-3 h-3 text-gray-400 mr-2" />
              <span className="text-gray-500 mr-1">Underwriter:</span>
              <span className="text-gray-900 font-medium">
                {loan.underwriter.firstName} {loan.underwriter.lastName}
              </span>
            </div>
          )}
        </div>

        {/* Conditions Alert */}
        {pendingConditions > 0 && (
          <div className="flex items-center p-2 bg-orange-50 border border-orange-200 rounded-md mb-4">
            <AlertCircle className="w-4 h-4 text-orange-600 mr-2" />
            <span className="text-sm text-orange-800">
              {pendingConditions} pending condition{pendingConditions !== 1 ? 's' : ''}
            </span>
          </div>
        )}

        {/* Footer */}
        <div className="flex items-center justify-between pt-4 border-t border-gray-200">
          <div className="flex items-center text-sm text-gray-500">
            <Clock className="w-4 h-4 mr-1" />
            <span>Created {formatDate(loan.createdAt)}</span>
          </div>
          
          <button className="flex items-center text-sm text-primary-600 hover:text-primary-800 transition-colors group-hover:translate-x-1 transform duration-200">
            <Eye className="w-4 h-4 mr-1" />
            View Details
          </button>
        </div>
      </div>
    </div>
  )
}

export default LoanCard