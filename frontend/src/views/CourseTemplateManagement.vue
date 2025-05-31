<template>
  <div class="course-template-management">
    <div class="header">
      <h2>课程模板管理</h2>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        新增模板
      </el-button>
    </div>

    <!-- 查询条件 -->
    <div class="search-container">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="模板名称">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="请输入模板名称" 
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="学年">
          <el-select 
            v-model="searchForm.academicYear" 
            placeholder="请选择学年" 
            clearable
            style="width: 150px"
          >
            <el-option label="2023-2024学年" value="2023-2024" />
            <el-option label="2024-2025学年" value="2024-2025" />
            <el-option label="2025-2026学年" value="2025-2026" />
            <el-option label="2026-2027学年" value="2026-2027" />
          </el-select>
        </el-form-item>
        <el-form-item label="学期">
          <el-select 
            v-model="searchForm.semester" 
            placeholder="请选择学期" 
            clearable
            style="width: 120px"
          >
            <el-option label="第一学期" value="第一学期" />
            <el-option label="第二学期" value="第二学期" />
          </el-select>
        </el-form-item>
        <el-form-item label="学院">
          <el-select 
            v-model="searchForm.collegeId" 
            placeholder="请选择学院" 
            clearable
            style="width: 150px"
            @change="handleSearchCollegeChange"
          >
            <el-option 
              v-for="college in colleges" 
              :key="college.id" 
              :label="college.orgName" 
              :value="college.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="专业">
          <el-select 
            v-model="searchForm.majorId" 
            placeholder="请选择专业" 
            clearable
            style="width: 150px"
            :disabled="!searchForm.collegeId"
          >
            <el-option 
              v-for="major in searchMajors" 
              :key="major.id" 
              :label="major.orgName" 
              :value="major.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select 
            v-model="searchForm.status" 
            placeholder="请选择状态" 
            clearable
            style="width: 100px"
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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

    <el-table :data="templateList" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="templateName" label="模板名称" min-width="150" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip min-width="200" />
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
      <el-table-column label="适用学院" width="120">
        <template #default="scope">
          <span v-if="scope.row.collegeName">{{ scope.row.collegeName }}</span>
          <el-tag v-else type="info" size="small">不限制</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="适用专业" width="150">
        <template #default="scope">
          <span v-if="scope.row.majorName">{{ scope.row.majorName }}</span>
          <el-tag v-else type="info" size="small">不限制</el-tag>
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
        
        <!-- 学院专业选择 -->
        <el-divider content-position="left">权限设置</el-divider>
        <el-form-item>
          <el-alert 
            title="权限说明：如果不选择学院和专业，则所有教师都可以申请此课程模板；如果选择了学院和专业，则只有对应学院专业的教师才能申请。" 
            type="info" 
            :closable="false"
            style="margin-bottom: 15px"
          />
        </el-form-item>
        <el-form-item label="适用学院">
          <el-select 
            v-model="form.collegeId" 
            placeholder="请选择学院（可不选）" 
            clearable
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
        <el-form-item label="适用专业">
          <el-select 
            v-model="form.majorId" 
            placeholder="请选择专业（可不选）" 
            clearable
            style="width: 100%"
            :disabled="!form.collegeId"
          >
            <el-option 
              v-for="major in majors" 
              :key="major.id" 
              :label="major.orgName" 
              :value="major.id" 
            />
          </el-select>
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
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'CourseTemplateManagement',
  components: {
    Search,
    Refresh,
    Plus
  },
  setup() {
    const templateList = ref([])
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const formRef = ref(null)
    
    // 组织数据
    const colleges = ref([])
    const majors = ref([])
    const searchMajors = ref([])

    const form = reactive({
      id: null,
      templateName: '',
      description: '',
      courseHours: 16,
      academicYear: '',
      semester: '',
      maxStudents: 50,
      collegeId: null,
      majorId: null,
      status: 1
    })

    const searchForm = reactive({
      keyword: '',
      academicYear: '',
      semester: '',
      collegeId: null,
      majorId: null,
      status: null
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

    // 获取学院列表
    const fetchColleges = async () => {
      try {
        const response = await axios.get('/api/admin/organizations-by-level/1')
        if (response.data.code === 200) {
          colleges.value = response.data.data || []
        }
      } catch (error) {
        console.error('获取学院列表失败:', error)
      }
    }

    // 根据学院获取专业列表
    const fetchMajorsByCollege = async (collegeId) => {
      try {
        const response = await axios.get(`/api/admin/organizations-by-parent/${collegeId}`)
        if (response.data.code === 200) {
          return response.data.data || []
        }
        return []
      } catch (error) {
        console.error('获取专业列表失败:', error)
        return []
      }
    }

    // 处理学院改变（表单中）
    const handleCollegeChange = async (collegeId) => {
      form.majorId = null
      majors.value = []
      
      if (collegeId) {
        majors.value = await fetchMajorsByCollege(collegeId)
      }
    }

    // 处理搜索条件中的学院改变
    const handleSearchCollegeChange = async (collegeId) => {
      searchForm.majorId = null
      searchMajors.value = []
      
      if (collegeId) {
        searchMajors.value = await fetchMajorsByCollege(collegeId)
      }
    }

    const loadTemplates = async () => {
      try {
        const response = await axios.post('/api/course-templates/list', {
          page: currentPage.value,
          size: pageSize.value,
          keyword: searchForm.keyword || '',
          academicYear: searchForm.academicYear || '',
          semester: searchForm.semester || '',
          collegeId: searchForm.collegeId || null,
          majorId: searchForm.majorId || null,
          status: searchForm.status,
          enabledOnly: false
        })
        
        if (response.data.code === 200) {
          const data = response.data.data
          templateList.value = data.records || []
          total.value = data.total || 0
        } else {
          ElMessage.error('加载课程模板失败: ' + response.data.message)
        }
      } catch (error) {
        console.error('加载课程模板失败:', error)
        ElMessage.error('加载课程模板失败: ' + (error.message || '网络错误'))
      }
    }

    const handleSearch = () => {
      currentPage.value = 1
      loadTemplates()
    }

    const handleReset = () => {
      searchForm.keyword = ''
      searchForm.academicYear = ''
      searchForm.semester = ''
      searchForm.collegeId = null
      searchForm.majorId = null
      searchForm.status = null
      searchMajors.value = []
      currentPage.value = 1
      loadTemplates()
    }

    const showAddDialog = async () => {
      isEdit.value = false
      resetForm()
      await fetchColleges()
      dialogVisible.value = true
    }

    const editTemplate = async (row) => {
      isEdit.value = true
      await fetchColleges()
      
      // 设置表单数据
      Object.assign(form, {
        id: row.id,
        templateName: row.templateName,
        description: row.description,
        courseHours: row.courseHours,
        academicYear: row.academicYear,
        semester: row.semester,
        maxStudents: row.maxStudents,
        collegeId: row.collegeId,
        majorId: row.majorId,
        status: row.status
      })
      
      // 如果有学院ID，加载对应的专业列表
      if (row.collegeId) {
        majors.value = await fetchMajorsByCollege(row.collegeId)
      }
      
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
        collegeId: null,
        majorId: null,
        status: 1
      })
      majors.value = []
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

    onMounted(async () => {
      await fetchColleges()
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
      searchForm,
      rules,
      formRef,
      colleges,
      majors,
      searchMajors,
      showAddDialog,
      editTemplate,
      toggleStatus,
      submitForm,
      deleteTemplate,
      handleSizeChange,
      handleCurrentChange,
      handleSearch,
      handleReset,
      handleCollegeChange,
      handleSearchCollegeChange
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

.search-container {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 4px;
}

.search-form {
  margin-bottom: 0;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}

.el-divider {
  margin: 20px 0 15px 0;
}
</style> 