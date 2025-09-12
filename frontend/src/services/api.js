import axios from 'axios'
import toast from 'react-hot-toast'

// Create axios instance with base configuration
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    const { response } = error

    // Handle different error scenarios
    if (response) {
      switch (response.status) {
        case 401:
          // Unauthorized - redirect to login
          localStorage.removeItem('authToken')
          window.location.href = '/login'
          toast.error('Session expired. Please login again.')
          break
        
        case 403:
          // Forbidden
          toast.error('You do not have permission to perform this action.')
          break
        
        case 404:
          // Not found
          toast.error('The requested resource was not found.')
          break
        
        case 422:
          // Validation error
          const message = response.data?.message || 'Validation error occurred.'
          toast.error(message)
          break
        
        case 500:
          // Server error
          toast.error('An internal server error occurred. Please try again later.')
          break
        
        default:
          // Other errors
          const defaultMessage = response.data?.message || 'An unexpected error occurred.'
          toast.error(defaultMessage)
      }
    } else if (error.request) {
      // Network error
      toast.error('Network error. Please check your connection and try again.')
    } else {
      // Other errors
      toast.error('An unexpected error occurred.')
    }

    return Promise.reject(error)
  }
)

// API helper functions
export const apiHelpers = {
  // GET request
  get: (url, config = {}) => api.get(url, config),
  
  // POST request
  post: (url, data = {}, config = {}) => api.post(url, data, config),
  
  // PUT request
  put: (url, data = {}, config = {}) => api.put(url, data, config),
  
  // PATCH request
  patch: (url, data = {}, config = {}) => api.patch(url, data, config),
  
  // DELETE request
  delete: (url, config = {}) => api.delete(url, config),
  
  // Upload file
  upload: (url, formData, onUploadProgress = null) => {
    return api.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      onUploadProgress,
    })
  },
  
  // Download file
  download: (url, filename = 'download') => {
    return api.get(url, {
      responseType: 'blob',
    }).then(response => {
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', filename)
      document.body.appendChild(link)
      link.click()
      link.remove()
      window.URL.revokeObjectURL(url)
    })
  }
}

export default api