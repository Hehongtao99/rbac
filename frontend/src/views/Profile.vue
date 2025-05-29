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
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password" 
            show-password 
            placeholder="请输入原密码"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            show-password 
            placeholder="请输入新密码"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            show-password 
            placeholder="请再次输入新密码"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closePasswordDialog">取消</el-button>
        <el-button type="primary" :loading="passwordLoading" @click="handleChangePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo, changePassword } from '../api/auth'
import { usePermissions } from '../stores/permission'

export default {
  name: 'Profile',
  setup() {
    const userInfo = ref(null)
    const showChangePassword = ref(false)
    const passwordLoading = ref(false)
    const passwordFormRef = ref()
    
    const passwordForm = reactive({
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })

    // 使用响应式权限检查
    const { hasPermission } = usePermissions()

    // 密码表单验证规则
    const passwordRules = {
      oldPassword: [
        { required: true, message: '请输入原密码', trigger: 'blur' }
      ],
      newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请确认新密码', trigger: 'blur' },
        { 
          validator: (rule, value, callback) => {
            if (value !== passwordForm.newPassword) {
              callback(new Error('两次输入密码不一致'))
            } else {
              callback()
            }
          }, 
          trigger: 'blur' 
        }
      ]
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

    // 关闭密码对话框
    const closePasswordDialog = () => {
      showChangePassword.value = false
      resetPasswordForm()
    }

    // 重置密码表单
    const resetPasswordForm = () => {
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
      
      if (passwordFormRef.value) {
        passwordFormRef.value.clearValidate()
      }
    }

    // 修改密码
    const handleChangePassword = async () => {
      try {
        await passwordFormRef.value.validate()
        
        passwordLoading.value = true
        
        await changePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword,
          confirmPassword: passwordForm.confirmPassword
        })
        
        ElMessage.success('密码修改成功')
        closePasswordDialog()
      } catch (error) {
        console.error('修改密码失败:', error)
      } finally {
        passwordLoading.value = false
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
      showChangePassword,
      passwordForm,
      passwordRules,
      passwordLoading,
      passwordFormRef,
      hasPermission,
      getRoleType,
      handleChangePassword,
      closePasswordDialog
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