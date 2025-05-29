<template>
  <div class="layout-container">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="250px" class="sidebar">
        <div class="logo">
          <h2>户外广告系统</h2>
        </div>
        
        <el-menu
          :default-active="$route.path"
          class="sidebar-menu"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <menu-item
            v-for="menu in userMenus"
            :key="menu.id"
            :menu="menu"
          />
        </el-menu>
      </el-aside>
      
      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="header">
          <div class="header-left">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-icon><Avatar /></el-icon>
                {{ userInfo?.nickname || userInfo?.username }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <!-- 内容区域 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserInfo } from '../api/auth'
import MenuItem from '../components/MenuItem.vue'
import { setUserPermissions, clearPermissions, usePermissions, setRefreshCallback } from '../stores/permission'

export default {
  name: 'Layout',
  components: {
    MenuItem
  },
  setup() {
    const router = useRouter()
    const { userInfo, userMenus, userButtons } = usePermissions()

    // 获取用户信息和权限
    const fetchUserInfo = async () => {
      try {
        const response = await getUserInfo()
        
        // 使用响应式权限状态
        setUserPermissions(
          response.data.buttons || [],
          response.data.menus || [],
          response.data
        )
        
        // 更新本地存储，确保权限信息是最新的
        localStorage.setItem('userInfo', JSON.stringify(response.data))
      } catch (error) {
        console.error('获取用户信息失败:', error)
        ElMessage.error('获取用户信息失败')
      }
    }

    // 处理下拉菜单命令
    const handleCommand = async (command) => {
      switch (command) {
        case 'profile':
          router.push('/profile')
          break
        case 'logout':
          try {
            await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            })
            
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
            clearPermissions()
            ElMessage.success('退出登录成功')
            router.push('/login')
          } catch (error) {
            // 用户取消操作
          }
          break
      }
    }

    // 刷新用户权限（供其他组件调用）
    const refreshUserPermissions = async () => {
      await fetchUserInfo()
    }

    onMounted(async () => {
      // 先从本地存储获取用户信息作为初始显示
      const localUserInfo = localStorage.getItem('userInfo')
      if (localUserInfo) {
        try {
          const parsed = JSON.parse(localUserInfo)
          setUserPermissions(
            parsed.buttons || [],
            parsed.menus || [],
            parsed
          )
        } catch (error) {
          console.error('解析本地用户信息失败:', error)
        }
      }
      
      // 立即从服务器获取最新权限信息
      await fetchUserInfo()
      
      // 设置刷新回调
      setRefreshCallback(refreshUserPermissions)
    })

    return {
      userInfo,
      userMenus,
      userButtons,
      handleCommand
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4b;
  color: white;
  border-bottom: 1px solid #1f2d3d;
}

.logo h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.sidebar-menu {
  border: none;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

.header {
  background: white;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.user-info .el-icon {
  margin: 0 4px;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}

:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
}

:deep(.el-submenu .el-submenu__title) {
  height: 50px;
  line-height: 50px;
}
</style> 