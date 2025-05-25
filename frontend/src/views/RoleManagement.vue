<template>
  <div class="role-management">
    <div class="page-header">
      <h2>角色管理</h2>
      <div class="header-actions">
        <el-button 
          v-if="hasPermission('role:add')"
          type="primary" 
          @click="handleAdd"
        >
          <el-icon><Plus /></el-icon>
          新增角色
        </el-button>
      </div>
    </div>

    <div class="table-container">
      <el-table :data="roles" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="roleCode" label="角色编码" />
        <el-table-column prop="description" label="描述" />
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
              v-if="hasPermission('role:edit')"
              type="primary" 
              size="small" 
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button 
              v-if="hasPermission('role:assign-menu')"
              type="warning" 
              size="small" 
              @click="handleAssignMenu(row)"
            >
              分配权限
            </el-button>
            <el-button 
              v-if="hasPermission('role:delete')"
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

    <!-- 新增/编辑角色对话框 -->
    <el-dialog v-model="roleDialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px">
      <el-form :model="roleForm" :rules="roleRules" ref="roleFormRef" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="roleForm.roleCode" placeholder="请输入角色编码（如：ADMIN、USER）" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input 
            v-model="roleForm.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入角色描述" 
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="roleForm.status" placeholder="请选择状态">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeRoleDialog">取消</el-button>
        <el-button type="primary" @click="confirmRole" :loading="roleLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 分配权限对话框 -->
    <el-dialog v-model="assignMenuVisible" title="分配权限" width="600px" @opened="onDialogOpened">
      <div class="assign-menu-content">
        <div class="role-info">
          <span>角色：{{ assignMenuForm.roleName }}</span>
        </div>
        <el-tree
          ref="menuTreeRef"
          :data="menuTreeData"
          :props="treeProps"
          show-checkbox
          node-key="id"
          class="menu-tree"
        />
      </div>
      <template #footer>
        <el-button @click="closeAssignDialog">取消</el-button>
        <el-button type="primary" @click="confirmAssignMenu" :loading="assignLoading">
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
  name: 'RoleManagement',
  setup() {
    const roles = ref([])
    const loading = ref(false)
    const roleDialogVisible = ref(false)
    const roleLoading = ref(false)
    const isEdit = ref(false)
    const roleFormRef = ref()
    const assignMenuVisible = ref(false)
    const assignLoading = ref(false)
    const menuTreeRef = ref()
    const menuTreeData = ref([])
    const checkedMenuIds = ref([])
    
    const roleForm = reactive({
      id: null,
      roleName: '',
      roleCode: '',
      description: '',
      status: 1
    })
    
    const assignMenuForm = reactive({
      roleId: null,
      roleName: ''
    })

    const treeProps = {
      children: 'children',
      label: 'menuName'
    }

    // 表单验证规则
    const roleRules = {
      roleName: [
        { required: true, message: '请输入角色名称', trigger: 'blur' },
        { min: 2, max: 20, message: '角色名称长度在 2 到 20 个字符', trigger: 'blur' }
      ],
      roleCode: [
        { required: true, message: '请输入角色编码', trigger: 'blur' },
        { min: 2, max: 20, message: '角色编码长度在 2 到 20 个字符', trigger: 'blur' },
        { pattern: /^[A-Z_]+$/, message: '角色编码只能包含大写字母和下划线', trigger: 'blur' }
      ],
      description: [
        { max: 200, message: '描述长度不能超过 200 个字符', trigger: 'blur' }
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

    // 获取角色列表
    const fetchRoles = async () => {
      try {
        loading.value = true
        const response = await request.get('/admin/roles')
        roles.value = response.data
      } catch (error) {
        console.error('获取角色列表失败:', error)
        ElMessage.error('获取角色列表失败')
      } finally {
        loading.value = false
      }
    }

    // 获取菜单树数据
    const fetchMenuTree = async () => {
      try {
        const response = await request.get('/admin/menus')
        menuTreeData.value = response.data
      } catch (error) {
        console.error('获取菜单列表失败:', error)
        ElMessage.error('获取菜单列表失败')
      }
    }

    // 获取角色已分配的菜单
    const fetchRoleMenus = async (roleId) => {
      try {
        const response = await request.get(`/admin/role-menus/${roleId}`)
        checkedMenuIds.value = response.data || []
      } catch (error) {
        console.error('获取角色菜单失败:', error)
        checkedMenuIds.value = []
      }
    }

    // 获取叶子节点（只选中叶子节点，避免父子重复）
    const getLeafNodes = (nodes, selectedIds) => {
      const leafNodes = []
      
      const traverse = (nodeList) => {
        nodeList.forEach(node => {
          if (selectedIds.includes(node.id)) {
            if (!node.children || node.children.length === 0) {
              // 叶子节点，直接添加
              leafNodes.push(node.id)
            } else {
              // 有子节点，递归处理子节点
              traverse(node.children)
            }
          }
        })
      }
      
      traverse(nodes)
      return leafNodes
    }

    // 重置角色表单
    const resetRoleForm = () => {
      roleForm.id = null
      roleForm.roleName = ''
      roleForm.roleCode = ''
      roleForm.description = ''
      roleForm.status = 1
      
      if (roleFormRef.value) {
        roleFormRef.value.clearValidate()
      }
    }

    // 关闭角色对话框
    const closeRoleDialog = () => {
      roleDialogVisible.value = false
      resetRoleForm()
    }

    // 确认新增/编辑角色
    const confirmRole = async () => {
      if (!roleFormRef.value) return
      
      try {
        await roleFormRef.value.validate()
        roleLoading.value = true
        
        if (isEdit.value) {
          // 编辑角色
          await request.put(`/admin/roles/${roleForm.id}`, roleForm)
          ElMessage.success('编辑角色成功')
        } else {
          // 新增角色
          await request.post('/admin/roles', roleForm)
          ElMessage.success('新增角色成功')
        }
        
        closeRoleDialog()
        fetchRoles()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error(error.response?.data?.message || '操作失败')
      } finally {
        roleLoading.value = false
      }
    }

    // 新增角色
    const handleAdd = () => {
      isEdit.value = false
      resetRoleForm()
      roleDialogVisible.value = true
    }

    // 编辑角色
    const handleEdit = async (row) => {
      try {
        isEdit.value = true
        const response = await request.get(`/admin/roles/${row.id}`)
        const roleData = response.data
        
        roleForm.id = roleData.id
        roleForm.roleName = roleData.roleName
        roleForm.roleCode = roleData.roleCode
        roleForm.description = roleData.description
        roleForm.status = roleData.status
        
        roleDialogVisible.value = true
      } catch (error) {
        console.error('获取角色信息失败:', error)
        ElMessage.error('获取角色信息失败')
      }
    }

    // 分配权限
    const handleAssignMenu = async (row) => {
      assignMenuForm.roleId = row.id
      assignMenuForm.roleName = row.roleName
      
      // 获取菜单树和角色已分配的菜单
      await fetchMenuTree()
      await fetchRoleMenus(row.id)
      
      assignMenuVisible.value = true
    }

    // 对话框打开后的回调
    const onDialogOpened = async () => {
      // 等待DOM更新后设置选中的节点
      await nextTick()
      if (menuTreeRef.value) {
        // 先清空所有选中状态
        menuTreeRef.value.setCheckedKeys([])
        
        // 如果有已分配的菜单，则设置选中状态
        if (checkedMenuIds.value.length > 0) {
          // 只选中叶子节点，让Element Plus自动处理父节点状态
          const leafNodeIds = getLeafNodes(menuTreeData.value, checkedMenuIds.value)
          menuTreeRef.value.setCheckedKeys(leafNodeIds)
        }
      }
    }

    // 关闭分配权限对话框
    const closeAssignDialog = () => {
      assignMenuVisible.value = false
      // 清空选中状态
      if (menuTreeRef.value) {
        menuTreeRef.value.setCheckedKeys([])
      }
      checkedMenuIds.value = []
    }

    // 确认分配权限
    const confirmAssignMenu = async () => {
      try {
        assignLoading.value = true
        
        // 获取选中的菜单ID
        const checkedKeys = menuTreeRef.value.getCheckedKeys()
        const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys()
        const allCheckedKeys = [...checkedKeys, ...halfCheckedKeys]
        
        await request.post('/admin/assign-menu', allCheckedKeys, {
          params: {
            roleId: assignMenuForm.roleId
          }
        })
        
        ElMessage.success('分配权限成功')
        closeAssignDialog()
        
        // 刷新权限信息，让所有组件立即更新
        const { refreshPermissions } = usePermissions()
        await refreshPermissions()
        ElMessage.info('权限已更新，请查看最新菜单和按钮')
      } catch (error) {
        console.error('分配权限失败:', error)
        ElMessage.error('分配权限失败')
      } finally {
        assignLoading.value = false
      }
    }

    // 删除角色
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm(`确定要删除角色 ${row.roleName} 吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await request.delete(`/admin/roles/${row.id}`)
        ElMessage.success('删除角色成功')
        fetchRoles()
      } catch (error) {
        if (error.response) {
          ElMessage.error(error.response.data?.message || '删除角色失败')
        }
        // 用户取消操作不显示错误
      }
    }

    onMounted(() => {
      fetchRoles()
    })

    return {
      roles,
      loading,
      roleDialogVisible,
      roleLoading,
      isEdit,
      roleForm,
      roleRules,
      roleFormRef,
      assignMenuVisible,
      assignLoading,
      menuTreeRef,
      menuTreeData,
      checkedMenuIds,
      assignMenuForm,
      treeProps,
      hasPermission,
      formatDate,
      handleAdd,
      handleEdit,
      confirmRole,
      closeRoleDialog,
      handleAssignMenu,
      onDialogOpened,
      closeAssignDialog,
      confirmAssignMenu,
      handleDelete
    }
  }
}
</script>

<style scoped>
.role-management {
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

.assign-menu-content {
  max-height: 400px;
  overflow-y: auto;
}

.role-info {
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 4px;
  font-weight: 600;
}

.menu-tree {
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  padding: 10px;
  max-height: 300px;
  overflow-y: auto;
}
</style> 