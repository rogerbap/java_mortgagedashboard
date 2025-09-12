import React, { useState } from 'react'
import { useLoans } from '../../hooks/useLoans'
import { LOAN_TYPE, VALIDATION_RULES } from '../../utils/constants'
import { ModalBody, ModalFooter } from '../common/Modal'
import Button from '../common/Button'
import toast from 'react-hot-toast'

const LoanForm = ({ onClose, loan = null }) => {
  const { createLoan, updateLoan } = useLoans()
  const isEditing = !!loan

  const [formData, setFormData] = useState({
    borrowerName: loan?.borrowerName || '',
    propertyAddress: loan?.propertyAddress || '',
    loanAmount: loan?.loanAmount || '',
    loanType: loan?.loanType || 'CONVENTIONAL',
    interestRate: loan?.interestRate || 6.5,
    ltv: loan?.ltv || 80,
    dti: loan?.dti || 30,
    processorId: loan?.processor?.id || '',
    underwriterId: loan?.underwriter?.id || '',
    loanOfficerId: loan?.loanOfficer?.id || ''
  })

  const [errors, setErrors] = useState({})
  const [isSubmitting, setIsSubmitting] = useState(false)

  // Mock team members (would come from API in real implementation)
  const teamMembers = {
    processors: [
      { id: 2, firstName: 'Sarah', lastName: 'Johnson' },
      { id: 4, firstName: 'Tom', lastName: 'Wilson' },
      { id: 6, firstName: 'Emily', lastName: 'Chen' }
    ],
    underwriters: [
      { id: 1, firstName: 'Mike', lastName: 'Chen' },
      { id: 7, firstName: 'Jennifer', lastName: 'Martinez' }
    ],
    loanOfficers: [
      { id: 3, firstName: 'Lisa', lastName: 'Davis' },
      { id: 5, firstName: 'Robert', lastName: 'Taylor' },
      { id: 8, firstName: 'Amanda', lastName: 'Wilson' }
    ]
  }

  const handleInputChange = (e) => {
    const { name, value, type } = e.target
    
    setFormData(prev => ({
      ...prev,
      [name]: type === 'number' ? parseFloat(value) || '' : value
    }))

    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }))
    }
  }

  const validateForm = () => {
    const newErrors = {}

    // Required fields
    if (!formData.borrowerName.trim()) {
      newErrors.borrowerName = 'Borrower name is required'
    }

    if (!formData.propertyAddress.trim()) {
      newErrors.propertyAddress = 'Property address is required'
    }

    if (!formData.loanAmount) {
      newErrors.loanAmount = 'Loan amount is required'
    } else if (formData.loanAmount < VALIDATION_RULES.LOAN_AMOUNT.MIN) {
      newErrors.loanAmount = `Loan amount must be at least $${VALIDATION_RULES.LOAN_AMOUNT.MIN.toLocaleString()}`
    } else if (formData.loanAmount > VALIDATION_RULES.LOAN_AMOUNT.MAX) {
      newErrors.loanAmount = `Loan amount must be less than $${VALIDATION_RULES.LOAN_AMOUNT.MAX.toLocaleString()}`
    }

    if (!formData.interestRate) {
      newErrors.interestRate = 'Interest rate is required'
    } else if (formData.interestRate < VALIDATION_RULES.INTEREST_RATE.MIN) {
      newErrors.interestRate = `Interest rate must be at least ${VALIDATION_RULES.INTEREST_RATE.MIN}%`
    } else if (formData.interestRate > VALIDATION_RULES.INTEREST_RATE.MAX) {
      newErrors.interestRate = `Interest rate must be less than ${VALIDATION_RULES.INTEREST_RATE.MAX}%`
    }

    if (formData.ltv && (formData.ltv < VALIDATION_RULES.LTV.MIN || formData.ltv > VALIDATION_RULES.LTV.MAX)) {
      newErrors.ltv = `LTV must be between ${VALIDATION_RULES.LTV.MIN}% and ${VALIDATION_RULES.LTV.MAX}%`
    }

    if (formData.dti && (formData.dti < VALIDATION_RULES.DTI.MIN || formData.dti > VALIDATION_RULES.DTI.MAX)) {
      newErrors.dti = `DTI must be between ${VALIDATION_RULES.DTI.MIN}% and ${VALIDATION_RULES.DTI.MAX}%`
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    
    if (!validateForm()) {
      toast.error('Please fix the errors below')
      return
    }

    setIsSubmitting(true)

    try {
      let result
      if (isEditing) {
        result = await updateLoan(loan.loanId, formData)
      } else {
        result = await createLoan(formData)
      }

      if (result.success) {
        toast.success(`Loan ${isEditing ? 'updated' : 'created'} successfully!`)
        onClose()
      } else {
        toast.error(result.error || `Failed to ${isEditing ? 'update' : 'create'} loan`)
      }
    } catch (error) {
      toast.error('An unexpected error occurred')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <ModalBody>
        {/* Borrower Information */}
        <div className="space-y-4">
          <div>
            <label htmlFor="borrowerName" className="form-label">
              Borrower Name *
            </label>
            <input
              type="text"
              id="borrowerName"
              name="borrowerName"
              value={formData.borrowerName}
              onChange={handleInputChange}
              className={`form-input ${errors.borrowerName ? 'border-danger-500' : ''}`}
              placeholder="Enter borrower's full name"
              disabled={isSubmitting}
            />
            {errors.borrowerName && (
              <p className="mt-1 text-sm text-danger-600">{errors.borrowerName}</p>
            )}
          </div>

          <div>
            <label htmlFor="propertyAddress" className="form-label">
              Property Address *
            </label>
            <textarea
              id="propertyAddress"
              name="propertyAddress"
              rows={2}
              value={formData.propertyAddress}
              onChange={handleInputChange}
              className={`form-input ${errors.propertyAddress ? 'border-danger-500' : ''}`}
              placeholder="Enter complete property address"
              disabled={isSubmitting}
            />
            {errors.propertyAddress && (
              <p className="mt-1 text-sm text-danger-600">{errors.propertyAddress}</p>
            )}
          </div>

          {/* Loan Details */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label htmlFor="loanAmount" className="form-label">
                Loan Amount * ($)
              </label>
              <input
                type="number"
                id="loanAmount"
                name="loanAmount"
                min={VALIDATION_RULES.LOAN_AMOUNT.MIN}
                max={VALIDATION_RULES.LOAN_AMOUNT.MAX}
                value={formData.loanAmount}
                onChange={handleInputChange}
                className={`form-input ${errors.loanAmount ? 'border-danger-500' : ''}`}
                placeholder="350000"
                disabled={isSubmitting}
              />
              {errors.loanAmount && (
                <p className="mt-1 text-sm text-danger-600">{errors.loanAmount}</p>
              )}
            </div>

            <div>
              <label htmlFor="loanType" className="form-label">
                Loan Type *
              </label>
              <select
                id="loanType"
                name="loanType"
                value={formData.loanType}
                onChange={handleInputChange}
                className="form-input"
                disabled={isSubmitting}
              >
                {Object.values(LOAN_TYPE).map(type => (
                  <option key={type} value={type}>
                    {type}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Financial Details */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div>
              <label htmlFor="interestRate" className="form-label">
                Interest Rate * (%)
              </label>
              <input
                type="number"
                id="interestRate"
                name="interestRate"
                step={VALIDATION_RULES.INTEREST_RATE.STEP}
                min={VALIDATION_RULES.INTEREST_RATE.MIN}
                max={VALIDATION_RULES.INTEREST_RATE.MAX}
                value={formData.interestRate}
                onChange={handleInputChange}
                className={`form-input ${errors.interestRate ? 'border-danger-500' : ''}`}
                placeholder="6.75"
                disabled={isSubmitting}
              />
              {errors.interestRate && (
                <p className="mt-1 text-sm text-danger-600">{errors.interestRate}</p>
              )}
            </div>

            <div>
              <label htmlFor="ltv" className="form-label">
                LTV (%)
              </label>
              <input
                type="number"
                id="ltv"
                name="ltv"
                min={VALIDATION_RULES.LTV.MIN}
                max={VALIDATION_RULES.LTV.MAX}
                value={formData.ltv}
                onChange={handleInputChange}
                className={`form-input ${errors.ltv ? 'border-danger-500' : ''}`}
                placeholder="80"
                disabled={isSubmitting}
              />
              {errors.ltv && (
                <p className="mt-1 text-sm text-danger-600">{errors.ltv}</p>
              )}
            </div>

            <div>
              <label htmlFor="dti" className="form-label">
                DTI (%)
              </label>
              <input
                type="number"
                id="dti"
                name="dti"
                min={VALIDATION_RULES.DTI.MIN}
                max={VALIDATION_RULES.DTI.MAX}
                value={formData.dti}
                onChange={handleInputChange}
                className={`form-input ${errors.dti ? 'border-danger-500' : ''}`}
                placeholder="30"
                disabled={isSubmitting}
              />
              {errors.dti && (
                <p className="mt-1 text-sm text-danger-600">{errors.dti}</p>
              )}
            </div>
          </div>

          {/* Team Assignment */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div>
              <label htmlFor="processorId" className="form-label">
                Processor
              </label>
              <select
                id="processorId"
                name="processorId"
                value={formData.processorId}
                onChange={handleInputChange}
                className="form-input"
                disabled={isSubmitting}
              >
                <option value="">Select Processor</option>
                {teamMembers.processors.map(member => (
                  <option key={member.id} value={member.id}>
                    {member.firstName} {member.lastName}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label htmlFor="underwriterId" className="form-label">
                Underwriter
              </label>
              <select
                id="underwriterId"
                name="underwriterId"
                value={formData.underwriterId}
                onChange={handleInputChange}
                className="form-input"
                disabled={isSubmitting}
              >
                <option value="">Select Underwriter</option>
                {teamMembers.underwriters.map(member => (
                  <option key={member.id} value={member.id}>
                    {member.firstName} {member.lastName}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label htmlFor="loanOfficerId" className="form-label">
                Loan Officer
              </label>
              <select
                id="loanOfficerId"
                name="loanOfficerId"
                value={formData.loanOfficerId}
                onChange={handleInputChange}
                className="form-input"
                disabled={isSubmitting}
              >
                <option value="">Select Loan Officer</option>
                {teamMembers.loanOfficers.map(member => (
                  <option key={member.id} value={member.id}>
                    {member.firstName} {member.lastName}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>
      </ModalBody>

      <ModalFooter>
        <Button
          type="button"
          variant="secondary"
          onClick={onClose}
          disabled={isSubmitting}
        >
          Cancel
        </Button>
        <Button
          type="submit"
          loading={isSubmitting}
          disabled={isSubmitting}
        >
          {isEditing ? 'Update' : 'Create'} Loan
        </Button>
      </ModalFooter>
    </form>
  )
}

export default LoanForm