import React from 'react'
import { Loader2 } from 'lucide-react'
import clsx from 'clsx'

const LoadingSpinner = ({ 
  size = 'md', 
  color = 'primary', 
  className = '',
  text = null 
}) => {
  const sizeClasses = {
    sm: 'w-4 h-4',
    md: 'w-6 h-6',
    lg: 'w-8 h-8',
    xl: 'w-12 h-12'
  }

  const colorClasses = {
    primary: 'text-primary-600',
    white: 'text-white',
    gray: 'text-gray-600',
    success: 'text-success-600',
    warning: 'text-warning-600',
    danger: 'text-danger-600'
  }

  const spinnerClass = clsx(
    'animate-spin',
    sizeClasses[size],
    colorClasses[color],
    className
  )

  return (
    <div className="flex items-center justify-center space-x-2">
      <Loader2 className={spinnerClass} />
      {text && (
        <span className={clsx(
          'text-sm font-medium',
          colorClasses[color]
        )}>
          {text}
        </span>
      )}
    </div>
  )
}

export default LoadingSpinner