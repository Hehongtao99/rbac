<template>
  <div class="user-management">
    <div class="page-header">
      <h2>用户管理</h2>
      <div class="header-actions">
        <el-button 
          v-if="hasPermission('user:add')"
          type="primary" 
          @click="handleAdd"
        >
          <el-icon><Plus /></el-icon>
          新增用户
        </el-button>
      </div>
    </div>

    <div class="table-container">
      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300">
          <template #default="{ row }">
            <el-button 
              v-if="hasPermission('user:edit')"
              type="primary" 
              size="small" 
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button 
              v-if="hasPermission('user:assign-role')"
              type="warning" 
              size="small" 
              @click="handleAssignRole(row)"
            >
              分配角色
            </el-button>
            <el-button 
              v-if="hasPermission('user:delete')"
              type="danger" 
              size="small" 
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑用户对话框 -->
    <el-dialog v-model="userDialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px">
      <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
          <el-input 
            v-model="userForm.password" 
            type="password" 
            :placeholder="isEdit ? '留空则不修改密码' : '请输入密码'" 
            show-password 
          />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="userForm.status" placeholder="请选择状态">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeUserDialog">取消</el-button>
        <el-button type="primary" @click="confirmUser" :loading="userLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog v-model="assignRoleVisible" title="分配角色" width="400px">
      <el-form :model="assignRoleForm" label-width="80px">
        <el-form-item label="用户">
          <el-input v-model="assignRoleForm.username" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="assignRoleForm.roleId" placeholder="请选择角色">
            <el-option 
              v-for="role in roles" 
              :key="role.id" 
              :label="role.roleName" 
              :value="role.id" 
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignRoleVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssignRole" :loading="assignLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'
import { usePermissions } from '../stores/permission'

export default {
  name: 'UserManagement',
  setup() {
    const users = ref([])
    const roles = ref([])
    const loading = ref(false)
    const userDialogVisible = ref(false)
    const userLoading = ref(false)
    const isEdit = ref(false)
    const assignRoleVisible = ref(false)
    const assignLoading = ref(false)
    const userFormRef = ref()
    
    const userForm = reactive({
      id: null,
      username: '',
      password: '',
      nickname: '',
      email: '',
      phone: '',
      status: 1
    })
    
    const assignRoleForm = reactive({
      userId: null,
      username: '',
      roleId: null
    })

    // 表单验证规则
    const userRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
      ],
      nickname: [
        { required: true, message: '请输入昵称', trigger: 'blur' }
      ],
      email: [
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
      ],
      status: [
        { required: true, message: '请选择状态', trigger: 'change' }
      ]
    }

    // 使用响应式权限检查
    const { hasPermission } = usePermissions()

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return ''
      return new Date(dateString).toLocaleString('zh-CN')
    }

    // 获取用户列表
    const fetchUsers = async () => {
      try {
        loading.value = true
        const response = await request.get('/admin/users')
        users.value = response.data
      } catch (error) {
        console.error('获取用户列表失败:', error)
        ElMessage.error('获取用户列表失败')
      } finally {
        loading.value = false
      }
    }

    // 获取角色列表
    const fetchRoles = async () => {
      try {
        const response = await request.get('/admin/roles')
        roles.value = response.data
      } catch (error) {
        console.error('获取角色列表失败:', error)
      }
    }

    // 重置用户表单
    const resetUserForm = () => {
      userForm.id = null
      userForm.username = ''
      userForm.password = ''
      userForm.nickname = ''
      userForm.email = ''
      userForm.phone = ''
      userForm.status = 1
      
      if (userFormRef.value) {
        userFormRef.value.clearValidate()
      }
    }

    // 关闭用户对话框
    const closeUserDialog = () => {
      userDialogVisible.value = false
      resetUserForm()
    }

    // 新增用户
    const handleAdd = () => {
      isEdit.value = false
      resetUserForm()
      userDialogVisible.value = true
    }

    // 编辑用户
    const handleEdit = async (row) => {
      try {
        isEdit.value = true
        const response = await request.get(`/admin/users/${row.id}`)
        const userData = response.data
        
        userForm.id = userData.id
        userForm.username = userData.username
        userForm.password = '' // 编辑时密码为空
        userForm.nickname = userData.nickname
        userForm.email = userData.email
        userForm.phone = userData.phone
        userForm.status = userData.status
        
        userDialogVisible.value = true
      } catch (error) {
        console.error('获取用户信息失败:', error)
        ElMessage.error('获取用户信息失败')
      }
    }

    // 确认新增/编辑用户
    const confirmUser = async () => {
      if (!userFormRef.value) return
      
      try {
        await userFormRef.value.validate()
        userLoading.value = true
        
        if (isEdit.value) {
          // 编辑用户
          await request.put(`/admin/users/${userForm.id}`, userForm)
          ElMessage.success('编辑用户成功')
        } else {
          // 新增用户
          await request.post('/admin/users', userForm)
          ElMessage.success('新增用户成功')
        }
        
        closeUserDialog()
        fetchUsers()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error(error.response?.data?.message || '操作失败')
      } finally {
        userLoading.value = false
      }
    }

    // 分配角色
    const handleAssignRole = (row) => {
      assignRoleForm.userId = row.id
      assignRoleForm.username = row.username
      assignRoleForm.roleId = null
      assignRoleVisible.value = true
    }

    // 确认分配角色
    const confirmAssignRole = async () => {
      if (!assignRoleForm.roleId) {
        ElMessage.warning('请选择角色')
        return
      }

      try {
        assignLoading.value = true
        await request.post('/admin/assign-role', null, {
          params: {
            userId: assignRoleForm.userId,
            roleId: assignRoleForm.roleId
          }
        })
        
        ElMessage.success('分配角色成功')
        assignRoleVisible.value = false
        fetchUsers()
        
        // 刷新权限信息，让所有组件立即更新
        const { refreshPermissions } = usePermissions()
        await refreshPermissions()
        ElMessage.info('权限已更新，请查看最新菜单和按钮')
      } catch (error) {
        console.error('分配角色失败:', error)
      } finally {
        assignLoading.value = false
      }
    }

    // 删除用户
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm(`确定要删除用户 ${row.username} 吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await request.delete(`/admin/users/${row.id}`)
        ElMessage.success('删除用户成功')
        fetchUsers()
      } catch (error) {
        if (error.response) {
          ElMessage.error(error.response.data?.message || '删除用户失败')
        }
        // 用户取消操作不显示错误
      }
    }

    onMounted(() => {
      fetchUsers()
      fetchRoles()
    })

    return {
      users,
      roles,
      loading,
      userDialogVisible,
      userLoading,
      isEdit,
      userForm,
      userRules,
      userFormRef,
      assignRoleVisible,
      assignLoading,
      assignRoleForm,
      hasPermission,
      formatDate,
      handleAdd,
      handleEdit,
      confirmUser,
      closeUserDialog,
      handleAssignRole,
      confirmAssignRole,
      handleDelete
    }
  }
}
</script>

<style scoped>
.user-management {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e6e6e6;
}

.page-header h2 {
  margin: 0;
  color: #333;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.table-container {
  margin-top: 20px;
}
</style> 