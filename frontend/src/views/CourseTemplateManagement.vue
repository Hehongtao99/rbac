<template>
  <div class="course-template-management">
    <div class="header">
      <h2>课程模板管理</h2>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        新增模板
      </el-button>
    </div>

    <el-table :data="templateList" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="templateName" label="模板名称" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="courseHours" label="学时" width="80">
        <template #default="scope">
          {{ scope.row.courseHours }}节
        </template>
      </el-table-column>
      <el-table-column prop="academicYear" label="学年" width="120" />
      <el-table-column prop="semester" label="学期" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.semester === '第一学期' ? 'success' : 'warning'">
            {{ scope.row.semester === '第一学期' ? '上半期' : '下半期' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="maxStudents" label="计划人数" width="100">
        <template #default="scope">
          {{ scope.row.maxStudents }}人
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="280">
        <template #default="scope">
          <el-button size="small" @click="editTemplate(scope.row)">编辑</el-button>
          <el-button 
            size="small" 
            :type="scope.row.status === 1 ? 'warning' : 'success'"
            @click="toggleStatus(scope.row)"
          >
            {{ scope.row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button size="small" type="danger" @click="deleteTemplate(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑模板' : '新增模板'"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="form.templateName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入课程描述" />
        </el-form-item>
        <el-form-item label="课程学时" prop="courseHours">
          <el-select v-model="form.courseHours" placeholder="请选择课程学时">
            <el-option label="16节" :value="16" />
            <el-option label="20节" :value="20" />
            <el-option label="24节" :value="24" />
            <el-option label="28节" :value="28" />
            <el-option label="32节" :value="32" />
            <el-option label="36节" :value="36" />
            <el-option label="40节" :value="40" />
            <el-option label="48节" :value="48" />
          </el-select>
        </el-form-item>
        <el-form-item label="学年" prop="academicYear">
          <el-select v-model="form.academicYear" placeholder="请选择学年">
            <el-option label="2023-2024学年" value="2023-2024" />
            <el-option label="2024-2025学年" value="2024-2025" />
            <el-option label="2025-2026学年" value="2025-2026" />
            <el-option label="2026-2027学年" value="2026-2027" />
          </el-select>
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-select v-model="form.semester" placeholder="请选择学期">
            <el-option label="第一学期（上半期）" value="第一学期" />
            <el-option label="第二学期（下半期）" value="第二学期" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划人数" prop="maxStudents">
          <el-input-number v-model="form.maxStudents" :min="1" :max="200" placeholder="请输入计划人数" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

export default {
  name: 'CourseTemplateManagement',
  setup() {
    const templateList = ref([])
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const formRef = ref(null)

    const form = reactive({
      id: null,
      templateName: '',
      description: '',
      courseHours: 16,
      academicYear: '',
      semester: '',
      maxStudents: 50,
      status: 1
    })

    const rules = {
      templateName: [
        { required: true, message: '请输入模板名称', trigger: 'blur' }
      ],
      courseHours: [
        { required: true, message: '请选择课程学时', trigger: 'change' }
      ],
      academicYear: [
        { required: true, message: '请选择学年', trigger: 'change' }
      ],
      semester: [
        { required: true, message: '请选择学期', trigger: 'change' }
      ],
      maxStudents: [
        { required: true, message: '请输入计划人数', trigger: 'blur' }
      ],
      status: [
        { required: true, message: '请选择状态', trigger: 'change' }
      ]
    }

    const loadTemplates = async () => {
      try {
        const response = await axios.get('/api/course-templates', {
          params: {
            page: currentPage.value,
            size: pageSize.value
          }
        })
        if (response.data.code === 200) {
          templateList.value = response.data.data.records || []
          total.value = response.data.data.total || 0
        }
      } catch (error) {
        console.error('加载课程模板失败:', error)
        ElMessage.error('加载课程模板失败')
      }
    }

    const showAddDialog = () => {
      isEdit.value = false
      resetForm()
      dialogVisible.value = true
    }

    const editTemplate = (row) => {
      isEdit.value = true
      Object.assign(form, row)
      dialogVisible.value = true
    }

    const toggleStatus = async (row) => {
      try {
        const newStatus = row.status === 1 ? 0 : 1
        const statusText = newStatus === 1 ? '启用' : '禁用'
        
        await ElMessageBox.confirm(`确定要${statusText}这个课程模板吗？`, '确认操作', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const response = await axios.put(`/api/course-templates/${row.id}`, {
          ...row,
          status: newStatus
        })
        
        if (response.data.code === 200) {
          ElMessage.success(`${statusText}成功`)
          loadTemplates()
        } else {
          ElMessage.error(response.data.message)
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('状态切换失败:', error)
          ElMessage.error('操作失败')
        }
      }
    }

    const resetForm = () => {
      Object.assign(form, {
        id: null,
        templateName: '',
        description: '',
        courseHours: 16,
        academicYear: '',
        semester: '',
        maxStudents: 50,
        status: 1
      })
      if (formRef.value) {
        formRef.value.resetFields()
      }
    }

    const submitForm = async () => {
      if (!formRef.value) return
      
      try {
        await formRef.value.validate()
        
        const url = isEdit.value ? `/api/course-templates/${form.id}` : '/api/course-templates'
        const method = isEdit.value ? 'put' : 'post'
        
        const response = await axios[method](url, form)
        
        if (response.data.code === 200) {
          ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
          dialogVisible.value = false
          loadTemplates()
        } else {
          ElMessage.error(response.data.message)
        }
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('操作失败')
      }
    }

    const deleteTemplate = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除这个课程模板吗？', '确认删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const response = await axios.delete(`/api/course-templates/${id}`)
        
        if (response.data.code === 200) {
          ElMessage.success('删除成功')
          loadTemplates()
        } else {
          ElMessage.error(response.data.message)
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
          ElMessage.error('删除失败')
        }
      }
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      loadTemplates()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      loadTemplates()
    }

    onMounted(() => {
      loadTemplates()
    })

    return {
      templateList,
      currentPage,
      pageSize,
      total,
      dialogVisible,
      isEdit,
      form,
      rules,
      formRef,
      showAddDialog,
      editTemplate,
      toggleStatus,
      submitForm,
      deleteTemplate,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.course-template-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}
</style> 