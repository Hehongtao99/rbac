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

      <!-- 组织信息卡片 -->
      <el-card class="organization-info-card" v-if="userInfo && userInfo.organizations && userInfo.organizations.length > 0">
        <template #header>
          <div class="card-header">
            <span>组织信息</span>
          </div>
        </template>
        
        <div class="organization-info">
          <div 
            v-for="(org, index) in userInfo.organizations" 
            :key="org.id" 
            class="org-item"
            :class="{ 'org-divider': index > 0 }"
          >
            <div class="org-header">
              <el-tag :type="getOrgType(org.orgType)" size="small">
                {{ getOrgTypeName(org.orgType) }}
              </el-tag>
              <span class="org-name">{{ org.orgName }}</span>
            </div>
            
            <div class="org-path" v-if="org.collegeName || org.majorName || org.className">
              <div class="path-item" v-if="org.collegeName">
                <span class="path-label">学院：</span>
                <span class="path-value">{{ org.collegeName }}</span>
              </div>
              <div class="path-item" v-if="org.majorName">
                <span class="path-label">专业：</span>
                <span class="path-value">{{ org.majorName }}</span>
              </div>
              <div class="path-item" v-if="org.className">
                <span class="path-label">班级：</span>
                <span class="path-value">{{ org.className }}</span>
              </div>
            </div>
            
            <div class="org-details">
              <div class="detail-item">
                <span class="detail-label">组织编码：</span>
                <span class="detail-value">{{ org.orgCode }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">组织层级：</span>
                <span class="detail-value">第{{ org.orgLevel }}级</span>
              </div>
            </div>
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
        case '学生':
          return 'primary'
        case '教师':
          return 'success'
        default:
          return 'info'
      }
    }

    // 获取组织类型
    const getOrgType = (orgType) => {
      switch (orgType) {
        case 'COLLEGE':
          return 'danger'
        case 'MAJOR':
          return 'warning'
        case 'CLASS':
          return 'success'
        default:
          return 'info'
      }
    }

    // 获取组织类型名称
    const getOrgTypeName = (orgType) => {
      switch (orgType) {
        case 'COLLEGE':
          return '学院'
        case 'MAJOR':
          return '专业'
        case 'CLASS':
          return '班级'
        default:
          return '未知'
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
      getOrgType,
      getOrgTypeName,
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

.organization-info-card {
  margin-bottom: 20px;
}

.organization-info {
  padding: 10px 0;
}

.org-item {
  padding: 15px 0;
}

.org-divider {
  border-top: 1px solid #f0f0f0;
}

.org-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.org-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-left: 10px;
}

.org-path {
  margin-bottom: 10px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.path-item {
  display: flex;
  margin-bottom: 5px;
}

.path-item:last-child {
  margin-bottom: 0;
}

.path-label {
  font-weight: bold;
  color: #666;
  min-width: 60px;
}

.path-value {
  color: #333;
  margin-left: 5px;
}

.org-details {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.detail-item {
  display: flex;
  align-items: center;
}

.detail-label {
  font-weight: bold;
  color: #666;
  margin-right: 5px;
}

.detail-value {
  color: #333;
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