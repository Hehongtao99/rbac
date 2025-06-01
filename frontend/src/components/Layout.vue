<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="250px" class="sidebar">
      <div class="logo">
        <h2>考试系统</h2>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        router
        unique-opened
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <span>仪表板</span>
        </el-menu-item>
        
        <el-sub-menu index="question">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>题库管理</span>
          </template>
          <el-menu-item index="/question-bank-manage">题库管理</el-menu-item>
          <el-menu-item index="/question-manage">题目管理</el-menu-item>
          <el-menu-item index="/ai-question-generate">AI题目生成</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="exam">
          <template #title>
            <el-icon><Monitor /></el-icon>
            <span>考试管理</span>
          </template>
          <el-menu-item index="/exam-start">开始考试</el-menu-item>
          <el-menu-item index="/exam-list">考试记录</el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/wrong-question-book">
          <el-icon><Collection /></el-icon>
          <span>错题本</span>
        </el-menu-item>
        
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/user">用户管理</el-menu-item>
          <el-menu-item index="/system/role">角色管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    
    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">{{ currentPageTitle }}</span>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              <el-icon><User /></el-icon>
              <span>{{ userInfo.nickname || '用户' }}</span>
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
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
      
      <!-- 主内容 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  House, 
  Document, 
  User, 
  Setting, 
  Monitor,
  Collection,
  ArrowDown
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 用户信息
const userInfo = ref({
  nickname: localStorage.getItem('nickname') || '用户'
})

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

// 当前页面标题
const currentPageTitle = computed(() => {
  return route.meta?.title || '考试系统'
})

// 处理下拉菜单命令
const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm(
          '确定要退出登录吗？',
          '确认退出',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        // 清除本地存储
        localStorage.removeItem('token')
        localStorage.removeItem('nickname')
        
        ElMessage.success('退出登录成功')
        router.push('/login')
      } catch (error) {
        // 用户取消，不做任何操作
      }
      break
  }
}

// 初始化
onMounted(() => {
  // 可以在这里获取用户信息
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background: #304156;
  color: white;
}

.logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #434a50;
}

.logo h2 {
  margin: 0;
  color: white;
  font-size: 18px;
}

.sidebar-menu {
  border: none;
  background: #304156;
}

.sidebar-menu :deep(.el-menu-item) {
  color: #bfcbd9;
  border-bottom: 1px solid #434a50;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: #434a50;
  color: white;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: #409eff;
  color: white;
}

.sidebar-menu :deep(.el-sub-menu__title) {
  color: #bfcbd9;
  border-bottom: 1px solid #434a50;
}

.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background: #434a50;
  color: white;
}

.header {
  background: white;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-dropdown:hover {
  background: #f5f7fa;
}

.main-content {
  background: #f5f7fa;
  padding: 20px;
}
</style> 