// import { apiHelpers } from './api'

// class AuthService {
//   // Login user
//   async login(credentials) {
//     try {
//       const response = await apiHelpers.post('/auth/login', credentials)
//       return response.data
//     } catch (error) {
//       throw error
//     }
//   }

//   // Get current user profile
//   async getCurrentUser() {
//     try {
//       const response = await apiHelpers.get('/auth/me')
//       return response.data
//     } catch (error) {
//       throw error
//     }
//   }

//   // Refresh token
//   async refreshToken() {
//     try {
//       const response = await apiHelpers.post('/auth/refresh')
//       return response.data
//     } catch (error) {
//       throw error
//     }
//   }

//   // Change password
//   async changePassword(passwordData) {
//     try {
//       const response = await apiHelpers.put('/auth/change-password', passwordData)
//       return response.data
//     } catch (error) {
//       throw error
//     }
//   }

//   // Update user profile
//   async updateProfile(profileData) {
//     try {
//       const response = await apiHelpers.put('/auth/profile', profileData)
//       return response.data
//     } catch (error) {
//       throw error
//     }
//   }

//   // Logout (if backend logout endpoint exists)
//   async logout() {
//     try {
//       await apiHelpers.post('/auth/logout')
//     } catch (error) {
//       // Continue with logout even if API call fails
//       console.warn('Logout API call failed:', error)
//     } finally {
//       // Always remove token from localStorage
//       localStorage.removeItem('authToken')
//     }
//   }

//   // Check if user is authenticated
//   isAuthenticated() {
//     const token = localStorage.getItem('authToken')
//     if (!token) return false

//     try {
//       // Check if token is expired (basic check)
//       const payload = JSON.parse(atob(token.split('.')[1]))
//       const currentTime = Date.now() / 1000
      
//       if (payload.exp < currentTime) {
//         localStorage.removeItem('authToken')
//         return false
//       }
      
//       return true
//     } catch (error) {
//       // Invalid token
//       localStorage.removeItem('authToken')
//       return false
//     }
//   }

//   // Get token from localStorage
//   getToken() {
//     return localStorage.getItem('authToken')
//   }

//   // Set token in localStorage
//   setToken(token) {
//     localStorage.setItem('authToken', token)
//   }

//   // Remove token from localStorage
//   removeToken() {
//     localStorage.removeItem('authToken')
//   }

//   // Decode JWT token to get user info
//   decodeToken(token = null) {
//     const authToken = token || this.getToken()
//     if (!authToken) return null

//     try {
//       const payload = JSON.parse(atob(authToken.split('.')[1]))
//       return payload
//     } catch (error) {
//       console.error('Failed to decode token:', error)
//       return null
//     }
//   }

//   // Get user role from token
//   getUserRole() {
//     const tokenData = this.decodeToken()
//     return tokenData?.role || null
//   }

//   // Get user ID from token
//   getUserId() {
//     const tokenData = this.decodeToken()
//     return tokenData?.sub || tokenData?.userId || null
//   }

//   // Check if token is about to expire (within 5 minutes)
//   isTokenExpiringSoon() {
//     const tokenData = this.decodeToken()
//     if (!tokenData) return true

//     const currentTime = Date.now() / 1000
//     const expirationTime = tokenData.exp
//     const fiveMinutes = 5 * 60 // 5 minutes in seconds

//     return (expirationTime - currentTime) < fiveMinutes
//   }
// }

// // Create and export singleton instance
// export const authService = new AuthService()
// export default authService

// Simplified auth service for demo
class AuthService {
  // Mock login
  async login(credentials) {
    // Simulate API call
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          user: { 
            id: 1, 
            firstName: 'Demo', 
            lastName: 'User', 
            email: credentials.username + '@lender.com',
            role: 'UNDERWRITER' 
          },
          token: 'mock-token'
        })
      }, 500)
    })
  }

  // Mock get current user
  async getCurrentUser() {
    return {
      id: 1,
      firstName: 'Demo',
      lastName: 'User',
      email: 'demo@lender.com',
      role: 'UNDERWRITER'
    }
  }

  // Get token
  getToken() {
    return localStorage.getItem('authToken')
  }

  // Remove token
  removeToken() {
    localStorage.removeItem('authToken')
  }
}

// Create and export singleton instance
export const authService = new AuthService()
export default authService