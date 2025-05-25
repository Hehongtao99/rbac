import { ref, computed } from 'vue'

// 全局权限状态
const userButtons = ref([])
const userMenus = ref([])
const userInfo = ref(null)

// 设置用户权限
export const setUserPermissions = (buttons, menus, info) => {
  userButtons.value = buttons || []
  userMenus.value = menus || []
  userInfo.value = info || null
  
  // 同时更新全局变量，保持兼容性
  window.userButtons = userButtons.value
}

// 检查按钮权限
export const hasPermission = (permission) => {
  if (!permission) return true
  return userButtons.value.includes(permission)
}

// 检查是否有任意一个权限
export const hasAnyPermission = (permissions) => {
  if (!permissions || permissions.length === 0) return true
  return permissions.some(permission => userButtons.value.includes(permission))
}

// 检查是否有所有权限
export const hasAllPermissions = (permissions) => {
  if (!permissions || permissions.length === 0) return true
  return permissions.every(permission => userButtons.value.includes(permission))
}

// 获取当前用户的所有权限
export const getUserPermissions = () => {
  return userButtons.value
}

// 获取用户菜单
export const getUserMenus = () => {
  return userMenus.value
}

// 获取用户信息
export const getUserInfo = () => {
  return userInfo.value
}

// 清空权限
export const clearPermissions = () => {
  userButtons.value = []
  userMenus.value = []
  userInfo.value = null
  window.userButtons = []
}

// 刷新权限的回调函数
let refreshCallback = null

// 设置刷新回调
export const setRefreshCallback = (callback) => {
  refreshCallback = callback
  window.refreshUserPermissions = callback
}

// 刷新用户权限
export const refreshPermissions = async () => {
  if (refreshCallback) {
    await refreshCallback()
    return true
  }
  return false
}

// 导出响应式权限状态供组件使用
export const usePermissions = () => {
  return {
    userButtons: computed(() => userButtons.value),
    userMenus: computed(() => userMenus.value),
    userInfo: computed(() => userInfo.value),
    hasPermission,
    hasAnyPermission,
    hasAllPermissions,
    getUserPermissions,
    getUserMenus,
    getUserInfo,
    refreshPermissions
  }
} 