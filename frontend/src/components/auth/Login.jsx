import React, { useState, useEffect } from 'react'
import { Navigate } from 'react-router-dom'
import { Home, Eye, EyeOff, User, Lock } from 'lucide-react'
import { useAuth } from '../../hooks/useAuth'
import Button from '../common/Button'
import toast from 'react-hot-toast'

const Login = () => {
  const { login, user, loading, error } = useAuth()
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  })
  const [showPassword, setShowPassword] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)

  // Redirect if already authenticated
  if (user) {
    return <Navigate to="/dashboard" replace />
  }

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    
    if (!formData.username || !formData.password) {
      toast.error('Please enter both username and password')
      return
    }

    setIsSubmitting(true)
    
    try {
      const result = await login(formData)
      
      if (result.success) {
        toast.success('Login successful!')
        // Navigation will be handled by the route protection
      } else {
        toast.error(result.error || 'Login failed')
      }
    } catch (error) {
      toast.error('An unexpected error occurred')
    } finally {
      setIsSubmitting(false)
    }
  }

  // Sample credentials for demo
  const sampleCredentials = [
    { role: 'Underwriter', username: 'mchen', password: 'password123' },
    { role: 'Processor', username: 'sjohnson', password: 'password123' },
    { role: 'Manager', username: 'admin', password: 'password123' },
    { role: 'Loan Officer', username: 'ldavis', password: 'password123' }
  ]

  const fillSampleCredentials = (username, password) => {
    setFormData({ username, password })
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary-50 to-primary-100">
      <div className="max-w-md w-full space-y-8 p-8">
        {/* Header */}
        <div className="text-center">
          <div className="flex justify-center">
            <div className="flex items-center justify-center w-16 h-16 bg-primary-600 rounded-full">
              <Home className="w-8 h-8 text-white" />
            </div>
          </div>
          <h2 className="mt-6 text-3xl font-bold text-gray-900">
            Mortgage Loan Dashboard
          </h2>
          <p className="mt-2 text-sm text-gray-600">
            Sign in to manage your loan pipeline
          </p>
        </div>

        {/* Login Form */}
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          <div className="card">
            <div className="card-body space-y-4">
              {/* Username Field */}
              <div>
                <label htmlFor="username" className="form-label">
                  Username
                </label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <User className="h-5 w-5 text-gray-400" />
                  </div>
                  <input
                    id="username"
                    name="username"
                    type="text"
                    required
                    className="form-input pl-10"
                    placeholder="Enter your username"
                    value={formData.username}
                    onChange={handleInputChange}
                    disabled={isSubmitting}
                  />
                </div>
              </div>

              {/* Password Field */}
              <div>
                <label htmlFor="password" className="form-label">
                  Password
                </label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <Lock className="h-5 w-5 text-gray-400" />
                  </div>
                  <input
                    id="password"
                    name="password"
                    type={showPassword ? 'text' : 'password'}
                    required
                    className="form-input pl-10 pr-10"
                    placeholder="Enter your password"
                    value={formData.password}
                    onChange={handleInputChange}
                    disabled={isSubmitting}
                  />
                  <button
                    type="button"
                    className="absolute inset-y-0 right-0 pr-3 flex items-center"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? (
                      <EyeOff className="h-5 w-5 text-gray-400" />
                    ) : (
                      <Eye className="h-5 w-5 text-gray-400" />
                    )}
                  </button>
                </div>
              </div>

              {/* Error Message */}
              {error && (
                <div className="rounded-md bg-danger-50 p-4">
                  <div className="text-sm text-danger-800">
                    {error}
                  </div>
                </div>
              )}

              {/* Submit Button */}
              <Button
                type="submit"
                fullWidth
                loading={isSubmitting}
                disabled={isSubmitting}
                className="mt-6"
              >
                Sign In
              </Button>
            </div>
          </div>
        </form>

        {/* Demo Credentials */}
        <div className="card">
          <div className="card-body">
            <h3 className="text-sm font-medium text-gray-900 mb-3">
              Demo Credentials (Click to auto-fill)
            </h3>
            <div className="space-y-2">
              {sampleCredentials.map((cred, index) => (
                <button
                  key={index}
                  type="button"
                  onClick={() => fillSampleCredentials(cred.username, cred.password)}
                  className="w-full text-left px-3 py-2 text-sm bg-gray-50 hover:bg-gray-100 rounded-md transition-colors"
                  disabled={isSubmitting}
                >
                  <div className="font-medium text-gray-900">{cred.role}</div>
                  <div className="text-gray-500">{cred.username}</div>
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="text-center text-xs text-gray-500">
          <p>© 2024 Mortgage Loan Dashboard. All rights reserved.</p>
        </div>
      </div>
    </div>
  )
}

export default Login