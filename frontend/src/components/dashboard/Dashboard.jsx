import React, { useState } from 'react'
import { Plus, Filter, Search } from 'lucide-react'
import { useAuth } from '../../hooks/useAuth'
import { useLoans } from '../../hooks/useLoans'
import Header from '../common/Header'
import StatusPipeline from './StatusPipeline'
import LoanList from '../loans/LoanList'
import LoanForm from '../loans/LoanForm'
import Modal from '../common/Modal'
import Button from '../common/Button'
import { PERMISSIONS } from '../../utils/permissions'

const Dashboard = () => {
  const { canPerform } = useAuth()
  const { 
    getFilteredLoans, 
    filters, 
    setFilters, 
    loading,
    getLoanCountsByStatus 
  } = useLoans()
  
  const [showCreateLoan, setShowCreateLoan] = useState(false)
  const [showFilters, setShowFilters] = useState(false)

  const loans = getFilteredLoans()
  const statusCounts = getLoanCountsByStatus()

  const handleSearchChange = (e) => {
    setFilters({ search: e.target.value })
  }

  const handleStatusFilter = (status) => {
    setFilters({ status: filters.status === status ? '' : status })
  }

  const clearFilters = () => {
    setFilters({ status: '', search: '', processor: '', underwriter: '' })
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Page Header */}
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-8">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">
              Loan Pipeline
            </h1>
            <p className="text-gray-600">
              Manage and track mortgage loan applications through the entire process
            </p>
          </div>
          
          <div className="flex items-center space-x-3 mt-4 sm:mt-0">
            {canPerform(PERMISSIONS.CREATE_LOAN) && (
              <Button
                onClick={() => setShowCreateLoan(true)}
                icon={<Plus />}
                className="whitespace-nowrap"
              >
                Create Loan
              </Button>
            )}
            
            <Button
              variant="secondary"
              onClick={() => setShowFilters(!showFilters)}
              icon={<Filter />}
              className="whitespace-nowrap"
            >
              Filters
            </Button>
          </div>
        </div>

        {/* Status Pipeline */}
        <div className="mb-8">
          <StatusPipeline 
            statusCounts={statusCounts}
            onStatusClick={handleStatusFilter}
            activeStatus={filters.status}
          />
        </div>

        {/* Search and Filters */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 mb-6">
          <div className="p-4">
            {/* Search Bar */}
            <div className="flex flex-col sm:flex-row gap-4">
              <div className="flex-1 relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                <input
                  type="text"
                  placeholder="Search loans by borrower name, loan ID, or address..."
                  value={filters.search}
                  onChange={handleSearchChange}
                  className="form-input pl-10 w-full"
                />
              </div>
              
              {(filters.status || filters.search) && (
                <Button
                  variant="ghost"
                  onClick={clearFilters}
                  className="whitespace-nowrap"
                >
                  Clear Filters
                </Button>
              )}
            </div>

            {/* Advanced Filters */}
            {showFilters && (
              <div className="mt-4 pt-4 border-t border-gray-200">
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                  <div>
                    <label className="form-label">Status</label>
                    <select
                      value={filters.status}
                      onChange={(e) => setFilters({ status: e.target.value })}
                      className="form-input"
                    >
                      <option value="">All Statuses</option>
                      <option value="CREATING">Creating Loan</option>
                      <option value="PRE_UW">Pre Underwriting</option>
                      <option value="PRE_APPROVED">Pre Approved</option>
                      <option value="APPROVED_CONDITIONS">Approved w/ Conditions</option>
                      <option value="CLEAR_TO_CLOSE">Clear to Close</option>
                      <option value="CLOSING">Closing</option>
                      <option value="CLOSED">Closed</option>
                    </select>
                  </div>
                  
                  <div>
                    <label className="form-label">Processor</label>
                    <select
                      value={filters.processor}
                      onChange={(e) => setFilters({ processor: e.target.value })}
                      className="form-input"
                    >
                      <option value="">All Processors</option>
                      <option value="Sarah Johnson">Sarah Johnson</option>
                      <option value="Tom Wilson">Tom Wilson</option>
                      <option value="Emily Chen">Emily Chen</option>
                    </select>
                  </div>
                  
                  <div>
                    <label className="form-label">Underwriter</label>
                    <select
                      value={filters.underwriter}
                      onChange={(e) => setFilters({ underwriter: e.target.value })}
                      className="form-input"
                    >
                      <option value="">All Underwriters</option>
                      <option value="Mike Chen">Mike Chen</option>
                      <option value="Jennifer Martinez">Jennifer Martinez</option>
                    </select>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Active Filters Display */}
        {(filters.status || filters.search) && (
          <div className="flex items-center space-x-2 mb-4">
            <span className="text-sm text-gray-600">Active filters:</span>
            {filters.status && (
              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-primary-100 text-primary-800">
                Status: {filters.status}
                <button
                  onClick={() => setFilters({ status: '' })}
                  className="ml-1 text-primary-600 hover:text-primary-800"
                >
                  ×
                </button>
              </span>
            )}
            {filters.search && (
              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-primary-100 text-primary-800">
                Search: "{filters.search}"
                <button
                  onClick={() => setFilters({ search: '' })}
                  className="ml-1 text-primary-600 hover:text-primary-800"
                >
                  ×
                </button>
              </span>
            )}
          </div>
        )}

        {/* Results Summary */}
        <div className="flex items-center justify-between mb-4">
          <p className="text-sm text-gray-600">
            Showing {loans.length} loan{loans.length !== 1 ? 's' : ''}
            {filters.status || filters.search ? ' (filtered)' : ''}
          </p>
        </div>

        {/* Loan List */}
        <LoanList loans={loans} loading={loading} />
      </div>

      {/* Create Loan Modal */}
      {showCreateLoan && (
        <Modal
          isOpen={showCreateLoan}
          onClose={() => setShowCreateLoan(false)}
          title="Create New Loan"
          size="lg"
        >
          <LoanForm onClose={() => setShowCreateLoan(false)} />
        </Modal>
      )}
    </div>
  )
}

export default Dashboard