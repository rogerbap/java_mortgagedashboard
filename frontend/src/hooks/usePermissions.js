import { useMemo } from 'react'
import { useAuth } from './useAuth'
import { permissionUtils } from '../utils/permissions'

// Custom hook for permission management
export function usePermissions() {
  const { user } = useAuth()

  const permissions = useMemo(() => {
    if (!user?.role) {
      return {
        hasPermission: () => false,
        hasAnyPermission: () => false,
        hasAllPermissions: () => false,
        canEditLoan: () => false,
        canChangeStatus: () => false,
        canAssignTeamMember: () => false,
        userPermissions: []
      }
    }

    return {
      // Check if user has specific permission
      hasPermission: (permission) => permissionUtils.hasPermission(user.role, permission),

      // Check if user has any of the specified permissions
      hasAnyPermission: (permissions) => permissionUtils.hasAnyPermission(user.role, permissions),

      // Check if user has all of the specified permissions
      hasAllPermissions: (permissions) => permissionUtils.hasAllPermissions(user.role, permissions),

      // Check if user can edit loan based on status
      canEditLoan: (loanStatus) => permissionUtils.canEditLoan(user.role, loanStatus),

      // Check if user can change loan status
      canChangeStatus: (fromStatus, toStatus) => permissionUtils.canChangeStatus(user.role, fromStatus, toStatus),

      // Check if user can assign team member
      canAssignTeamMember: (teamMemberRole) => permissionUtils.canAssignTeamMember(user.role, teamMemberRole),

      // Get all permissions for current user
      userPermissions: permissionUtils.getUserPermissions(user.role)
    }
  }, [user?.role])

  return permissions
}

export default usePermissions