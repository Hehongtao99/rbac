<template>
  <div class="organization-management">
    <div class="page-header">
      <h2>组织管理</h2>
      <div class="header-actions">
        <el-button 
          v-if="hasPermission('org:add')"
          type="primary" 
          @click="handleAdd"
        >
          <el-icon><Plus /></el-icon>
          新增组织
        </el-button>
      </div>
    </div>

    <!-- 查询条件 -->
    <div class="search-container">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="组织名称">
          <el-input 
            v-model="searchForm.orgName" 
            placeholder="请输入组织名称" 
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="组织编码">
          <el-input 
            v-model="searchForm.orgCode" 
            placeholder="请输入组织编码" 
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="组织类型">
          <el-select 
            v-model="searchForm.orgType" 
            placeholder="请选择组织类型" 
            clearable
            style="width: 150px"
          >
            <el-option label="学院" value="COLLEGE" />
            <el-option label="专业" value="MAJOR" />
            <el-option label="班级" value="CLASS" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="tree-container">
      <!-- 树形显示 -->
      <el-tree
        v-if="!isSearchMode"
        :data="organizationTree"
        :props="treeProps"
        default-expand-all
        node-key="id"
        class="organization-tree"
      >
        <template #default="{ node, data }">
          <div class="tree-node">
            <div class="node-info">
              <el-tag :type="getOrgTypeColor(data.orgType)" size="small">
                {{ getOrgTypeName(data.orgType) }}
              </el-tag>
              <span class="node-name">{{ data.orgName }}</span>
              <span class="node-code">({{ data.orgCode }})</span>
            </div>
            <div class="node-actions">
              <el-button 
                v-if="hasPermission('org:add') && data.orgLevel < 3"
                type="primary" 
                size="small" 
                @click.stop="handleAddChild(data)"
              >
                添加下级
              </el-button>
              <el-button 
                v-if="hasPermission('org:edit')"
                type="warning" 
                size="small" 
                @click.stop="handleEdit(data)"
              >
                编辑
              </el-button>
              <el-button 
                v-if="hasPermission('org:delete')"
                type="danger" 
                size="small" 
                @click.stop="handleDelete(data)"
              >
                删除
              </el-button>
            </div>
          </div>
        </template>
      </el-tree>

      <!-- 表格显示（搜索结果） -->
      <el-table
        v-else
        :data="organizationTree"
        stripe
        class="organization-table"
      >
        <el-table-column label="组织类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getOrgTypeColor(row.orgType)" size="small">
              {{ getOrgTypeName(row.orgType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orgName" label="组织名称" />
        <el-table-column prop="orgCode" label="组织编码" />
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button 
              v-if="hasPermission('org:edit')"
              type="warning" 
              size="small" 
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button 
              v-if="hasPermission('org:delete')"
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

    <!-- 新增/编辑组织对话框 -->
    <el-dialog v-model="orgDialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="orgForm" :rules="orgRules" ref="orgFormRef" label-width="100px">
        <el-form-item label="父级组织" v-if="parentOrgName">
          <el-input :value="parentOrgName" disabled />
        </el-form-item>
        <el-form-item label="组织类型" v-if="!isEdit">
          <el-input :value="getOrgTypeName(orgForm.orgType)" disabled />
        </el-form-item>
        <el-form-item label="组织名称" prop="orgName">
          <el-input v-model="orgForm.orgName" placeholder="请输入组织名称" />
        </el-form-item>
        <el-form-item label="组织编码" prop="orgCode">
          <el-input v-model="orgForm.orgCode" placeholder="请输入组织编码" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="orgForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="orgForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="orgForm.sort" :min="0" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="orgForm.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入组织描述" 
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="orgForm.status" placeholder="请选择状态">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeOrgDialog">取消</el-button>
        <el-button type="primary" @click="confirmOrg" :loading="orgLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import request from '../utils/request'
import { usePermissions } from '../stores/permission'

export default {
  name: 'OrganizationManagement',
  components: {
    Search,
    Refresh,
    Plus
  },
  setup() {
    const organizationTree = ref([])
    const loading = ref(false)
    const orgDialogVisible = ref(false)
    const orgLoading = ref(false)
    const isEdit = ref(false)
    const parentOrgName = ref('')
    const orgFormRef = ref()
    const isSearchMode = ref(false)
    
    const orgForm = reactive({
      id: null,
      orgName: '',
      orgCode: '',
      parentId: null,
      orgType: '',
      orgLevel: null,
      sort: 0,
      phone: '',
      email: '',
      description: '',
      status: 1
    })

    const searchForm = reactive({
      orgName: '',
      orgCode: '',
      orgType: ''
    })

    const treeProps = {
      children: 'children',
      label: 'orgName'
    }

    // 表单验证规则
    const orgRules = {
      orgName: [
        { required: true, message: '请输入组织名称', trigger: 'blur' },
        { min: 2, max: 50, message: '组织名称长度在 2 到 50 个字符', trigger: 'blur' }
      ],
      orgCode: [
        { required: true, message: '请输入组织编码', trigger: 'blur' },
        { min: 2, max: 20, message: '组织编码长度在 2 到 20 个字符', trigger: 'blur' }
      ],
      phone: [
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
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

    // 对话框标题
    const dialogTitle = computed(() => {
      if (isEdit.value) {
        return '编辑组织'
      }
      const typeMap = {
        'COLLEGE': '新增学院',
        'MAJOR': '新增专业',
        'CLASS': '新增班级'
      }
      return typeMap[orgForm.orgType] || '新增组织'
    })

    // 获取组织类型颜色
    const getOrgTypeColor = (orgType) => {
      const colorMap = {
        'COLLEGE': 'success',
        'MAJOR': 'warning',
        'CLASS': 'info'
      }
      return colorMap[orgType] || 'default'
    }

    // 获取组织类型名称
    const getOrgTypeName = (orgType) => {
      const nameMap = {
        'COLLEGE': '学院',
        'MAJOR': '专业',
        'CLASS': '班级'
      }
      return nameMap[orgType] || '未知'
    }

    // 获取组织树
    const fetchOrganizations = async (searchParams = {}) => {
      try {
        loading.value = true
        const response = await request.post('/admin/organizations/query', searchParams)
        organizationTree.value = response.data
        
        // 判断是否为搜索模式
        isSearchMode.value = Object.values(searchParams).some(value => value && value.trim() !== '')
      } catch (error) {
        console.error('获取组织列表失败:', error)
        ElMessage.error('获取组织列表失败')
      } finally {
        loading.value = false
      }
    }

    // 搜索
    const handleSearch = () => {
      const params = {}
      if (searchForm.orgName) params.orgName = searchForm.orgName
      if (searchForm.orgCode) params.orgCode = searchForm.orgCode
      if (searchForm.orgType) params.orgType = searchForm.orgType
      
      fetchOrganizations(params)
    }

    // 重置搜索
    const handleReset = () => {
      searchForm.orgName = ''
      searchForm.orgCode = ''
      searchForm.orgType = ''
      fetchOrganizations()
    }

    // 重置组织表单
    const resetOrgForm = () => {
      orgForm.id = null
      orgForm.orgName = ''
      orgForm.orgCode = ''
      orgForm.parentId = null
      orgForm.orgType = ''
      orgForm.orgLevel = null
      orgForm.sort = 0
      orgForm.phone = ''
      orgForm.email = ''
      orgForm.description = ''
      orgForm.status = 1
      parentOrgName.value = ''
      
      if (orgFormRef.value) {
        orgFormRef.value.clearValidate()
      }
    }

    // 关闭组织对话框
    const closeOrgDialog = () => {
      orgDialogVisible.value = false
      resetOrgForm()
    }

    // 新增顶级组织（学院）
    const handleAdd = () => {
      isEdit.value = false
      resetOrgForm()
      orgForm.parentId = 0
      orgForm.orgType = 'COLLEGE'
      orgForm.orgLevel = 1
      parentOrgName.value = ''
      orgDialogVisible.value = true
    }

    // 新增子组织
    const handleAddChild = (parentOrg) => {
      isEdit.value = false
      resetOrgForm()
      orgForm.parentId = parentOrg.id
      parentOrgName.value = `${getOrgTypeName(parentOrg.orgType)} - ${parentOrg.orgName}`
      
      // 根据父级确定子级类型
      if (parentOrg.orgLevel === 1) {
        orgForm.orgType = 'MAJOR'
        orgForm.orgLevel = 2
      } else if (parentOrg.orgLevel === 2) {
        orgForm.orgType = 'CLASS'
        orgForm.orgLevel = 3
      }
      
      orgDialogVisible.value = true
    }

    // 编辑组织
    const handleEdit = async (org) => {
      try {
        isEdit.value = true
        const response = await request.get(`/admin/organizations/${org.id}`)
        const orgData = response.data
        
        orgForm.id = orgData.id
        orgForm.orgName = orgData.orgName
        orgForm.orgCode = orgData.orgCode
        orgForm.parentId = orgData.parentId
        orgForm.orgType = orgData.orgType
        orgForm.orgLevel = orgData.orgLevel
        orgForm.sort = orgData.sort
        orgForm.phone = orgData.phone
        orgForm.email = orgData.email
        orgForm.description = orgData.description
        orgForm.status = orgData.status
        
        // 设置父级组织名称
        if (orgData.parentId && orgData.parentId !== 0) {
          // 从树中找父级名称
          const findParent = (nodes, parentId) => {
            for (const node of nodes) {
              if (node.id === parentId) {
                return `${getOrgTypeName(node.orgType)} - ${node.orgName}`
              }
              if (node.children) {
                const found = findParent(node.children, parentId)
                if (found) return found
              }
            }
            return ''
          }
          parentOrgName.value = findParent(organizationTree.value, orgData.parentId)
        } else {
          parentOrgName.value = ''
        }
        
        orgDialogVisible.value = true
      } catch (error) {
        console.error('获取组织信息失败:', error)
        ElMessage.error('获取组织信息失败')
      }
    }

    // 确认新增/编辑组织
    const confirmOrg = async () => {
      if (!orgFormRef.value) return
      
      try {
        await orgFormRef.value.validate()
        orgLoading.value = true
        
        if (isEdit.value) {
          // 编辑组织
          await request.put(`/admin/organizations/${orgForm.id}`, orgForm)
          ElMessage.success('编辑组织成功')
        } else {
          // 新增组织
          await request.post('/admin/organizations', orgForm)
          ElMessage.success('新增组织成功')
        }
        
        closeOrgDialog()
        fetchOrganizations()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error(error.response?.data?.message || '操作失败')
      } finally {
        orgLoading.value = false
      }
    }

    // 删除组织
    const handleDelete = async (org) => {
      try {
        await ElMessageBox.confirm(`确定要删除${getOrgTypeName(org.orgType)} ${org.orgName} 吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await request.delete(`/admin/organizations/${org.id}`)
        ElMessage.success('删除组织成功')
        fetchOrganizations()
      } catch (error) {
        if (error.response) {
          ElMessage.error(error.response.data?.message || '删除组织失败')
        }
        // 用户取消操作不显示错误
      }
    }

    onMounted(() => {
      fetchOrganizations()
    })

    return {
      organizationTree,
      loading,
      orgDialogVisible,
      orgLoading,
      isEdit,
      parentOrgName,
      orgForm,
      orgRules,
      orgFormRef,
      treeProps,
      dialogTitle,
      searchForm,
      isSearchMode,
      hasPermission,
      getOrgTypeColor,
      getOrgTypeName,
      handleAdd,
      handleAddChild,
      handleEdit,
      confirmOrg,
      closeOrgDialog,
      handleDelete,
      handleSearch,
      handleReset
    }
  }
}
</script>

<style scoped>
.organization-management {
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

.search-container {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.search-form {
  margin: 0;
}

.tree-container {
  margin-top: 20px;
}

.organization-tree {
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  padding: 10px;
}

.organization-table {
  border: 1px solid #e6e6e6;
  border-radius: 4px;
}

.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 8px 0;
}

.node-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.node-name {
  font-weight: 600;
  color: #333;
}

.node-code {
  color: #666;
  font-size: 12px;
}

.node-actions {
  display: flex;
  gap: 8px;
}

:deep(.el-tree-node__content) {
  height: auto;
  padding: 4px 0;
}

:deep(.el-tree-node__label) {
  width: 100%;
}
</style> 