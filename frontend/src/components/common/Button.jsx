import React from 'react'
import clsx from 'clsx'
import LoadingSpinner from './LoadingSpinner'

const Button = ({
  children,
  variant = 'primary',
  size = 'md',
  disabled = false,
  loading = false,
  icon = null,
  iconPosition = 'left',
  fullWidth = false,
  className = '',
  onClick,
  type = 'button',
  ...props
}) => {
  const baseClasses = 'inline-flex items-center justify-center font-medium rounded-lg border transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed'

  const variantClasses = {
    primary: 'bg-primary-600 text-white border-primary-600 hover:bg-primary-700 focus:ring-primary-500',
    secondary: 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50 focus:ring-primary-500',
    success: 'bg-success-600 text-white border-success-600 hover:bg-success-700 focus:ring-success-500',
    warning: 'bg-warning-600 text-white border-warning-600 hover:bg-warning-700 focus:ring-warning-500',
    danger: 'bg-danger-600 text-white border-danger-600 hover:bg-danger-700 focus:ring-danger-500',
    ghost: 'bg-transparent text-gray-700 border-transparent hover:bg-gray-100 focus:ring-primary-500',
    outline: 'bg-transparent text-primary-600 border-primary-600 hover:bg-primary-50 focus:ring-primary-500'
  }

  const sizeClasses = {
    sm: 'px-3 py-2 text-sm',
    md: 'px-4 py-2 text-sm',
    lg: 'px-6 py-3 text-base',
    xl: 'px-8 py-4 text-lg'
  }

  const iconSizeClasses = {
    sm: 'w-4 h-4',
    md: 'w-4 h-4',
    lg: 'w-5 h-5',
    xl: 'w-6 h-6'
  }

  const buttonClasses = clsx(
    baseClasses,
    variantClasses[variant],
    sizeClasses[size],
    {
      'w-full': fullWidth,
      'cursor-not-allowed': disabled || loading
    },
    className
  )

  const handleClick = (event) => {
    if (disabled || loading) {
      event.preventDefault()
      return
    }
    if (onClick) {
      onClick(event)
    }
  }

  const renderIcon = () => {
    if (loading) {
      return <LoadingSpinner size="sm" color="white" />
    }
    if (icon) {
      return React.cloneElement(icon, {
        className: clsx(iconSizeClasses[size], icon.props.className)
      })
    }
    return null
  }

  const renderContent = () => {
    const iconElement = renderIcon()
    
    if (loading) {
      return (
        <>
          {iconElement}
          <span className="ml-2">Loading...</span>
        </>
      )
    }

    if (!icon) {
      return children
    }

    if (iconPosition === 'right') {
      return (
        <>
          <span>{children}</span>
          <span className="ml-2">{iconElement}</span>
        </>
      )
    }

    return (
      <>
        <span className="mr-2">{iconElement}</span>
        <span>{children}</span>
      </>
    )
  }

  return (
    <button
      type={type}
      className={buttonClasses}
      disabled={disabled || loading}
      onClick={handleClick}
      {...props}
    >
      {renderContent()}
    </button>
  )
}

export default Button