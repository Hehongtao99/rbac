<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <div class="header-content">
        <h1 class="animate__animated animate__fadeInDown">欢迎来到RBAC权限管理系统</h1>
        <div class="user-actions">
          <el-button type="primary" @click="refreshUserInfo" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新信息
          </el-button>
        </div>
      </div>
    </div>

    <div class="dashboard-content">
      <div class="user-card animate__animated animate__fadeInUp">
        <div class="card-header">
          <el-icon class="user-icon"><Avatar /></el-icon>
          <h2>用户信息</h2>
        </div>
        
        <div class="user-info" v-if="userInfo">
          <div class="info-item">
            <span class="label">用户ID：</span>
            <span class="value">{{ userInfo.id }}</span>
          </div>
          <div class="info-item">
            <span class="label">用户名：</span>
            <span class="value">{{ userInfo.username }}</span>
          </div>
          <div class="info-item">
            <span class="label">昵称：</span>
            <span class="value">{{ userInfo.nickname }}</span>
          </div>
          <div class="info-item">
            <span class="label">邮箱：</span>
            <span class="value">{{ userInfo.email }}</span>
          </div>
          <div class="info-item">
            <span class="label">手机号：</span>
            <span class="value">{{ userInfo.phone }}</span>
          </div>
        </div>
      </div>

      <div class="role-card animate__animated animate__fadeInUp animate__delay-1s">
        <div class="card-header">
          <el-icon class="role-icon"><UserFilled /></el-icon>
          <h2>角色权限</h2>
        </div>
        
        <div class="role-info" v-if="userInfo && userInfo.roles">
          <div class="role-list">
            <el-tag 
              v-for="role in userInfo.roles" 
              :key="role" 
              :type="getRoleType(role)"
              size="large"
              class="role-tag"
            >
              <el-icon><Crown /></el-icon>
              {{ role }}
            </el-tag>
          </div>
          
          <div class="role-description">
            <h3>当前角色说明：</h3>
            <ul>
              <li v-for="role in userInfo.roles" :key="role">
                <strong>{{ role }}：</strong>{{ getRoleDescription(role) }}
              </li>
            </ul>
          </div>
        </div>
      </div>

      <div class="stats-card animate__animated animate__fadeInUp animate__delay-2s">
        <div class="card-header">
          <el-icon class="stats-icon"><DataAnalysis /></el-icon>
          <h2>系统统计</h2>
        </div>
        
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-number">1</div>
            <div class="stat-label">在线用户</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ userInfo?.roles?.length || 0 }}</div>
            <div class="stat-label">拥有角色</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">2</div>
            <div class="stat-label">系统角色</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">100%</div>
            <div class="stat-label">系统可用性</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserInfo } from '../api/auth'

export default {
  name: 'Dashboard',
  setup() {
    const router = useRouter()
    const userInfo = ref(null)
    const loading = ref(false)

    // 获取用户信息
    const fetchUserInfo = async () => {
      try {
        loading.value = true
        const response = await getUserInfo()
        userInfo.value = response.data
      } catch (error) {
        console.error('获取用户信息失败:', error)
        ElMessage.error('获取用户信息失败')
      } finally {
        loading.value = false
      }
    }

    // 刷新用户信息
    const refreshUserInfo = () => {
      fetchUserInfo()
    }

    // 获取角色类型
    const getRoleType = (role) => {
      switch (role) {
        case '管理员':
          return 'danger'
        case '用户':
          return 'primary'
        default:
          return 'info'
      }
    }

    // 获取角色描述
    const getRoleDescription = (role) => {
      switch (role) {
        case '管理员':
          return '拥有系统的完全访问权限，可以管理用户、角色和系统设置'
        case '用户':
          return '普通用户权限，可以访问基本功能和个人信息管理'
        default:
          return '未知角色'
      }
    }

    // 退出登录
    const handleLogout = async () => {
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        ElMessage.success('退出登录成功')
        router.push('/login')
      } catch (error) {
        // 用户取消操作
      }
    }

    onMounted(() => {
      // 先从本地存储获取用户信息
      const localUserInfo = localStorage.getItem('userInfo')
      if (localUserInfo) {
        userInfo.value = JSON.parse(localUserInfo)
      }
      
      // 然后从服务器获取最新信息
      fetchUserInfo()
    })

    return {
      userInfo,
      loading,
      refreshUserInfo,
      getRoleType,
      getRoleDescription,
      handleLogout
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  min-height: calc(100vh - 120px);
  padding: 20px;
}

.dashboard-header {
  margin-bottom: 30px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px 30px;
  border-radius: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content h1 {
  color: #333;
  font-size: 28px;
  font-weight: 600;
  margin: 0;
}

.user-actions {
  display: flex;
  gap: 15px;
}

.dashboard-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.user-card,
.role-card {
  grid-column: span 1;
}

.stats-card {
  grid-column: span 2;
}

.user-card,
.role-card,
.stats-card {
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.card-header h2 {
  margin: 0;
  color: #333;
  font-size: 22px;
  font-weight: 600;
}

.user-icon,
.role-icon,
.stats-icon {
  font-size: 24px;
  margin-right: 12px;
  color: #667eea;
}

.user-info {
  space-y: 15px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  font-weight: 600;
  color: #666;
  min-width: 80px;
}

.value {
  color: #333;
  font-weight: 500;
}

.role-list {
  margin-bottom: 25px;
}

.role-tag {
  margin-right: 15px;
  margin-bottom: 10px;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 600;
}

.role-description h3 {
  color: #333;
  margin-bottom: 15px;
  font-size: 16px;
}

.role-description ul {
  list-style: none;
  padding: 0;
}

.role-description li {
  padding: 8px 0;
  color: #666;
  line-height: 1.6;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

@media (max-width: 768px) {
  .dashboard-content {
    grid-template-columns: 1fr;
  }
  
  .user-card,
  .role-card,
  .stats-card {
    grid-column: span 1;
  }
  
  .header-content {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style> 