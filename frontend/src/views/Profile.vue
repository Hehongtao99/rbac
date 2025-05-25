<template>
  <div class="profile">
    <div class="profile-header">
      <h2>个人中心</h2>
    </div>

    <div class="profile-content">
      <el-card class="user-info-card">
        <template #header>
          <div class="card-header">
            <span>个人信息</span>
            <el-button 
              v-if="hasPermission('change-password')"
              type="primary" 
              size="small"
              @click="showChangePassword = true"
            >
              修改密码
            </el-button>
          </div>
        </template>
        
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
          <div class="info-item">
            <span class="label">角色：</span>
            <span class="value">
              <el-tag 
                v-for="role in userInfo.roles" 
                :key="role" 
                :type="getRoleType(role)"
                style="margin-right: 8px;"
              >
                {{ role }}
              </el-tag>
            </span>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="showChangePassword" title="修改密码" width="400px">
      <el-form :model="passwordForm" label-width="100px">
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showChangePassword = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo } from '../api/auth'

export default {
  name: 'Profile',
  setup() {
    const userInfo = ref(null)
    const showChangePassword = ref(false)
    
    const passwordForm = reactive({
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })

    // 检查按钮权限
    const hasPermission = (permission) => {
      return window.userButtons && window.userButtons.includes(permission)
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

    // 获取用户信息
    const fetchUserInfo = async () => {
      try {
        const response = await getUserInfo()
        userInfo.value = response.data
      } catch (error) {
        console.error('获取用户信息失败:', error)
        ElMessage.error('获取用户信息失败')
      }
    }

    // 修改密码
    const handleChangePassword = () => {
      if (!passwordForm.oldPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
        ElMessage.warning('请填写完整信息')
        return
      }
      
      if (passwordForm.newPassword !== passwordForm.confirmPassword) {
        ElMessage.warning('两次输入密码不一致')
        return
      }
      
      ElMessage.info('修改密码功能待实现')
      showChangePassword.value = false
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
      showChangePassword,
      passwordForm,
      hasPermission,
      getRoleType,
      handleChangePassword
    }
  }
}
</script>

<style scoped>
.profile {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.profile-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e6e6e6;
}

.profile-header h2 {
  margin: 0;
  color: #333;
}

.profile-content {
  max-width: 600px;
}

.user-info-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  padding: 10px 0;
}

.info-item {
  display: flex;
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
  min-width: 100px;
}

.value {
  color: #333;
  font-weight: 500;
}
</style> 