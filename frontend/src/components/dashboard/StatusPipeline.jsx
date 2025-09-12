import React from 'react'
import { ChevronRight } from 'lucide-react'
import { LOAN_STATUS_CONFIG, STATUS_FLOW } from '../../utils/constants'
import clsx from 'clsx'

const StatusPipeline = ({ statusCounts, onStatusClick, activeStatus }) => {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200">
      <div className="p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-6">
          Loan Status Pipeline
        </h2>
        
        <div className="grid grid-cols-1 lg:grid-cols-7 gap-4">
          {STATUS_FLOW.map((status, index) => {
            const config = LOAN_STATUS_CONFIG[status]
            const count = statusCounts[status] || 0
            const isActive = activeStatus === status
            
            return (
              <div key={status} className="relative">
                {/* Status Card */}
                <button
                  onClick={() => onStatusClick(status)}
                  className={clsx(
                    'w-full text-center p-4 rounded-lg border-2 transition-all duration-200',
                    'hover:shadow-md focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2',
                    isActive
                      ? 'border-primary-500 bg-primary-50 shadow-md'
                      : 'border-gray-200 bg-white hover:border-gray-300'
                  )}
                >
                  {/* Count Circle */}
                  <div className={clsx(
                    'w-12 h-12 rounded-full flex items-center justify-center text-white text-lg font-bold mb-3 mx-auto',
                    config.color
                  )}>
                    {count}
                  </div>
                  
                  {/* Status Label */}
                  <h3 className={clsx(
                    'font-medium text-sm mb-1',
                    isActive ? 'text-primary-900' : 'text-gray-900'
                  )}>
                    {config.label}
                  </h3>
                  
                  {/* Count Label */}
                  <p className={clsx(
                    'text-xs',
                    isActive ? 'text-primary-600' : 'text-gray-500'
                  )}>
                    {count} {count === 1 ? 'loan' : 'loans'}
                  </p>
                </button>

                {/* Arrow (except for last item) */}
                {index < STATUS_FLOW.length - 1 && (
                  <div className="hidden lg:block absolute top-1/2 -right-2 transform -translate-y-1/2 z-10">
                    <div className="bg-white rounded-full p-1 shadow-sm border border-gray-200">
                      <ChevronRight className="w-4 h-4 text-gray-400" />
                    </div>
                  </div>
                )}
              </div>
            )
          })}
        </div>

        {/* Pipeline Summary */}
        <div className="mt-6 pt-6 border-t border-gray-200">
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 text-center">
            <div>
              <div className="text-2xl font-bold text-gray-900">
                {Object.values(statusCounts).reduce((sum, count) => sum + count, 0)}
              </div>
              <div className="text-sm text-gray-500">Total Loans</div>
            </div>
            
            <div>
              <div className="text-2xl font-bold text-yellow-600">
                {(statusCounts.PRE_UW || 0) + (statusCounts.PRE_APPROVED || 0)}
              </div>
              <div className="text-sm text-gray-500">In Process</div>
            </div>
            
            <div>
              <div className="text-2xl font-bold text-orange-600">
                {statusCounts.APPROVED_CONDITIONS || 0}
              </div>
              <div className="text-sm text-gray-500">Pending Conditions</div>
            </div>
            
            <div>
              <div className="text-2xl font-bold text-green-600">
                {(statusCounts.CLEAR_TO_CLOSE || 0) + (statusCounts.CLOSING || 0) + (statusCounts.CLOSED || 0)}
              </div>
              <div className="text-sm text-gray-500">Ready/Closing/Closed</div>
            </div>
          </div>
        </div>

        {/* Active Filter Indicator */}
        {activeStatus && (
          <div className="mt-4 p-3 bg-primary-50 rounded-lg border border-primary-200">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <div className={clsx('w-3 h-3 rounded-full', LOAN_STATUS_CONFIG[activeStatus].color)} />
                <span className="text-sm font-medium text-primary-900">
                  Filtered by: {LOAN_STATUS_CONFIG[activeStatus].label}
                </span>
              </div>
              <button
                onClick={() => onStatusClick(activeStatus)}
                className="text-primary-600 hover:text-primary-800 text-sm font-medium"
              >
                Clear Filter
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default StatusPipeline