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
        <el-table-column prop="roleNames" label="角色" width="150">
          <template #default="{ row }">
            <el-tag 
              v-for="roleName in row.roleNames" 
              :key="roleName" 
              :type="getRoleTagType(roleName)"
              size="small"
              style="margin-right: 5px;"
            >
              {{ roleName }}
            </el-tag>
            <span v-if="!row.roleNames || row.roleNames.length === 0" style="color: #999;">
              未分配角色
            </span>
          </template>
        </el-table-column>
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
        <el-table-column label="操作" width="380">
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
              v-if="hasPermission('user:assign-role')"
              type="success" 
              size="small" 
              @click="handleAssignOrganization(row)"
            >
              分配组织
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

    <!-- 分配组织对话框 -->
    <el-dialog v-model="assignOrganizationVisible" title="分配组织" width="600px">
      <el-form :model="assignOrganizationForm" label-width="80px">
        <el-form-item label="用户">
          <el-input v-model="assignOrganizationForm.username" disabled />
        </el-form-item>
        <el-form-item label="用户角色">
          <el-input v-model="assignOrganizationForm.userRole" disabled />
        </el-form-item>
        
        <el-form-item label="学院" required>
          <el-select 
            v-model="assignOrganizationForm.collegeId" 
            placeholder="请选择学院"
            style="width: 100%"
            @change="handleCollegeChange"
          >
            <el-option 
              v-for="college in colleges" 
              :key="college.id" 
              :label="college.orgName" 
              :value="college.id" 
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="专业" required>
          <el-select 
            v-model="assignOrganizationForm.majorId" 
            placeholder="请先选择学院"
            style="width: 100%"
            :disabled="!assignOrganizationForm.collegeId"
            @change="handleMajorChange"
          >
            <el-option 
              v-for="major in majors" 
              :key="major.id" 
              :label="major.orgName" 
              :value="major.id" 
            />
          </el-select>
        </el-form-item>
        
        <el-form-item :label="isTeacherUser ? '班级(可多选)' : '班级'" :required="isStudentUser">
          <el-select 
            v-model="assignOrganizationForm.classIds" 
            :multiple="isTeacherUser"
            placeholder="请先选择专业"
            style="width: 100%"
            :disabled="!assignOrganizationForm.majorId"
          >
            <el-option 
              v-for="classOrg in classes" 
              :key="classOrg.id" 
              :label="classOrg.orgName" 
              :value="classOrg.id" 
            />
          </el-select>
        </el-form-item>
        
        <el-alert 
          v-if="isStudentUser"
          title="学生必须完整分配学院、专业和班级" 
          type="info" 
          :closable="false"
          style="margin-bottom: 15px"
        />
        <el-alert 
          v-else-if="isTeacherUser"
          title="教师至少需要分配学院和专业，可以选择多个班级" 
          type="success" 
          :closable="false"
          style="margin-bottom: 15px"
        />
        <el-alert 
          v-else
          title="请先为用户分配角色" 
          type="warning" 
          :closable="false"
          style="margin-bottom: 15px"
        />
      </el-form>
      <template #footer>
        <el-button @click="assignOrganizationVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssignOrganization" :loading="assignOrgLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, reactive, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'
import { usePermissions } from '../stores/permission'

export default {
  name: 'UserManagement',
  setup() {
    const users = ref([])
    const roles = ref([])
    const colleges = ref([])
    const majors = ref([])
    const classes = ref([])
    const loading = ref(false)
    const userDialogVisible = ref(false)
    const userLoading = ref(false)
    const isEdit = ref(false)
    const assignRoleVisible = ref(false)
    const assignLoading = ref(false)
    const assignOrganizationVisible = ref(false)
    const assignOrgLoading = ref(false)
    const isStudentUser = ref(false)
    const isTeacherUser = ref(false)
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

    const assignOrganizationForm = reactive({
      userId: null,
      username: '',
      userRole: '',
      collegeId: null,
      majorId: null,
      classIds: null
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

    // 获取角色标签类型
    const getRoleTagType = (roleName) => {
      switch (roleName) {
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

    // 获取学院列表
    const fetchColleges = async () => {
      try {
        const response = await request.get('/admin/organizations-by-level/1')
        colleges.value = response.data
      } catch (error) {
        console.error('获取学院列表失败:', error)
      }
    }

    // 根据学院获取专业列表
    const fetchMajorsByCollege = async (collegeId) => {
      try {
        const response = await request.get(`/admin/organizations-by-parent/${collegeId}`)
        majors.value = response.data
      } catch (error) {
        console.error('获取专业列表失败:', error)
      }
    }

    // 根据专业获取班级列表
    const fetchClassesByMajor = async (majorId) => {
      try {
        const response = await request.get(`/admin/organizations-by-parent/${majorId}`)
        classes.value = response.data
      } catch (error) {
        console.error('获取班级列表失败:', error)
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
    const handleAssignRole = async (row) => {
      try {
        assignRoleForm.userId = row.id
        assignRoleForm.username = row.username
        assignRoleForm.roleId = null
        
        // 获取用户当前角色并回显
        if (row.roleIds && row.roleIds.length > 0) {
          assignRoleForm.roleId = row.roleIds[0] // 取第一个角色（系统设计为一个用户一个角色）
        }
        
        assignRoleVisible.value = true
      } catch (error) {
        console.error('获取用户角色失败:', error)
        ElMessage.error('获取用户角色失败')
      }
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

    // 处理学院改变
    const handleCollegeChange = (collegeId) => {
      assignOrganizationForm.majorId = null
      if (isTeacherUser.value) {
        assignOrganizationForm.classIds = [] // 教师用数组
      } else {
        assignOrganizationForm.classIds = null // 其他角色用null
      }
      majors.value = []
      classes.value = []
      
      if (collegeId) {
        fetchMajorsByCollege(collegeId)
      }
    }

    // 处理专业改变
    const handleMajorChange = (majorId) => {
      if (isTeacherUser.value) {
        assignOrganizationForm.classIds = [] // 教师用数组
      } else {
        assignOrganizationForm.classIds = null // 其他角色用null
      }
      classes.value = []
      
      if (majorId) {
        fetchClassesByMajor(majorId)
      }
    }

    // 分配组织
    const handleAssignOrganization = async (row) => {
      try {
        assignOrganizationForm.userId = row.id
        assignOrganizationForm.username = row.username
        assignOrganizationForm.collegeId = null
        assignOrganizationForm.majorId = null
        assignOrganizationForm.classIds = null
        
        // 从用户角色信息中获取角色
        if (row.roleNames && row.roleNames.length > 0) {
          const userRole = row.roleNames[0] // 取第一个角色
          assignOrganizationForm.userRole = userRole
          isStudentUser.value = userRole === '学生'
          isTeacherUser.value = userRole === '教师'
          // 根据角色类型初始化classIds
          if (isTeacherUser.value) {
            assignOrganizationForm.classIds = [] // 教师用数组
          } else {
            assignOrganizationForm.classIds = null // 其他角色用null
          }
        } else {
          assignOrganizationForm.userRole = '未分配角色'
          isStudentUser.value = false
          isTeacherUser.value = false
          assignOrganizationForm.classIds = null
        }
        
        // 清空级联选择器
        majors.value = []
        classes.value = []
        
        // 先加载学院列表，确保数据可用
        await fetchColleges()
        
        // 获取用户当前已分配的组织，并回显到对应的级别
        const orgResponse = await request.get(`/admin/user-organizations/${row.id}`)
        const userOrgIds = orgResponse.data || []
        
        if (userOrgIds.length > 0) {
          // 根据分配的组织ID反向查找学院、专业、班级
          await setOrganizationFromUserOrg(userOrgIds)
          // 确保DOM更新
          await nextTick()
        }
        
        assignOrganizationVisible.value = true
      } catch (error) {
        console.error('获取用户组织信息失败:', error)
        ElMessage.error('获取用户组织信息失败')
      }
    }

    // 根据用户已分配的组织ID回显学院、专业、班级选择
    const setOrganizationFromUserOrg = async (orgIds) => {
      try {
        if (!orgIds || orgIds.length === 0) return
        
        console.log('回显组织信息:', { orgIds, isTeacherUser: isTeacherUser.value, isStudentUser: isStudentUser.value })
        
        // 获取第一个组织的详情用于确定学院和专业
        const orgResponse = await request.get(`/admin/organizations/${orgIds[0]}`)
        const org = orgResponse.data
        
        console.log('组织详情:', org)
        
        if (org.orgLevel === 3) { // 班级
          // 根据用户角色设置班级ID
          if (isTeacherUser.value) {
            assignOrganizationForm.classIds = orgIds // 教师可能有多个班级
          } else {
            assignOrganizationForm.classIds = orgIds[0] // 学生只有一个班级
          }
          assignOrganizationForm.majorId = org.parentId
          
          // 获取专业信息
          const majorResponse = await request.get(`/admin/organizations/${org.parentId}`)
          const major = majorResponse.data
          assignOrganizationForm.collegeId = major.parentId
          
          // 加载对应的专业和班级列表
          await fetchMajorsByCollege(major.parentId)
          await fetchClassesByMajor(org.parentId)
          
          // 等待DOM更新后再设置值
          await nextTick()
          
          console.log('回显完成 - 班级级别:', {
            collegeId: assignOrganizationForm.collegeId,
            majorId: assignOrganizationForm.majorId,
            classIds: assignOrganizationForm.classIds
          })
          
        } else if (org.orgLevel === 2) { // 专业
          assignOrganizationForm.majorId = org.id
          assignOrganizationForm.collegeId = org.parentId
          if (isTeacherUser.value) {
            assignOrganizationForm.classIds = [] // 教师用数组
          } else {
            assignOrganizationForm.classIds = null // 其他角色用null
          }
          
          // 加载对应的专业和班级列表
          await fetchMajorsByCollege(org.parentId)
          await fetchClassesByMajor(org.id)
          
          // 等待DOM更新后再设置值
          await nextTick()
          
          console.log('回显完成 - 专业级别:', {
            collegeId: assignOrganizationForm.collegeId,
            majorId: assignOrganizationForm.majorId,
            classIds: assignOrganizationForm.classIds
          })
          
        } else if (org.orgLevel === 1) { // 学院
          assignOrganizationForm.collegeId = org.id
          assignOrganizationForm.majorId = null
          if (isTeacherUser.value) {
            assignOrganizationForm.classIds = [] // 教师用数组
          } else {
            assignOrganizationForm.classIds = null // 其他角色用null
          }
          
          // 加载对应的专业列表
          await fetchMajorsByCollege(org.id)
          
          // 等待DOM更新后再设置值
          await nextTick()
          
          console.log('回显完成 - 学院级别:', {
            collegeId: assignOrganizationForm.collegeId,
            majorId: assignOrganizationForm.majorId,
            classIds: assignOrganizationForm.classIds
          })
        }
      } catch (error) {
        console.error('回显组织信息失败:', error)
      }
    }

    // 确认分配组织
    const confirmAssignOrganization = async () => {
      try {
        // 验证必填项
        if (!assignOrganizationForm.collegeId || !assignOrganizationForm.majorId) {
          ElMessage.warning('请至少选择学院和专业')
          return
        }
        
        if (isStudentUser.value && (!assignOrganizationForm.classIds || assignOrganizationForm.classIds.length === 0)) {
          ElMessage.warning('学生必须选择班级')
          return
        }
        
        assignOrgLoading.value = true
        
        // 构建请求数据
        const requestData = {
          userId: assignOrganizationForm.userId,
          collegeId: assignOrganizationForm.collegeId,
          majorId: assignOrganizationForm.majorId
        }
        
        // 如果有选择班级，添加班级参数
        if (assignOrganizationForm.classIds) {
          if (Array.isArray(assignOrganizationForm.classIds)) {
            // 教师用户的数组形式
            if (assignOrganizationForm.classIds.length > 0) {
              requestData.classIds = assignOrganizationForm.classIds
            }
          } else {
            // 其他用户的单个值形式
            requestData.classIds = [assignOrganizationForm.classIds]
          }
        }

        await request.post('/admin/assign-organization', requestData)
        
        ElMessage.success('分配组织成功')
        assignOrganizationVisible.value = false
        fetchUsers() // 刷新用户列表
      } catch (error) {
        console.error('分配组织失败:', error)
        ElMessage.error(error.response?.data?.message || '分配组织失败')
      } finally {
        assignOrgLoading.value = false
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
      fetchColleges()
    })

    return {
      users,
      roles,
      colleges,
      majors,
      classes,
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
      assignOrganizationVisible,
      assignOrgLoading,
      assignOrganizationForm,
      isStudentUser,
      isTeacherUser,
      hasPermission,
      getRoleTagType,
      formatDate,
      handleAdd,
      handleEdit,
      confirmUser,
      closeUserDialog,
      handleAssignRole,
      confirmAssignRole,
      handleCollegeChange,
      handleMajorChange,
      handleAssignOrganization,
      confirmAssignOrganization,
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