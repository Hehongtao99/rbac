<template>
  <div class="course-application">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>申请开课</span>
          <div class="header-controls">
            <el-button type="success" @click="goToMyApplications">
              <el-icon><Document /></el-icon>
              查看我的申请
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索框 -->
      <div class="search-container">
        <el-form :inline="true" :model="queryForm" class="demo-form-inline">
          <el-form-item label="课程名称">
            <el-input
              v-model="queryForm.keyword"
              placeholder="搜索课程模板"
              clearable
              @keyup.enter="fetchTemplateList"
              style="width: 200px;">
            </el-input>
          </el-form-item>
          <el-form-item label="学年">
            <el-select v-model="queryForm.academicYear" placeholder="选择学年" clearable @change="fetchTemplateList">
              <el-option label="2024-2025" value="2024-2025"></el-option>
              <el-option label="2023-2024" value="2023-2024"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="学期">
            <el-select v-model="queryForm.semester" placeholder="选择学期" clearable @change="fetchTemplateList">
              <el-option label="第一学期" value="第一学期"></el-option>
              <el-option label="第二学期" value="第二学期"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="fetchTemplateList">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 课程模板列表 -->
      <el-table :data="templateList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="templateName" label="模板名称" min-width="200">
          <template #default="{ row }">
            <div class="template-info">
              <div class="template-name">
                {{ row.templateName }}
                <el-tag v-if="isTemplateApplied(row.id)" type="warning" size="small" style="margin-left: 8px;">
                  已申请
                </el-tag>
              </div>
              <div class="template-desc">{{ row.description }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="academicYear" label="学年" width="120" />
        <el-table-column prop="semester" label="学期" width="120">
          <template #default="{ row }">
            <el-tag :type="row.semester === '第一学期' ? 'success' : 'warning'">
              {{ row.semester === '第一学期' ? '上半期' : '下半期' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="courseHours" label="计划学时" width="100">
          <template #default="{ row }">
            {{ row.courseHours }}节
          </template>
        </el-table-column>
        <el-table-column prop="maxStudents" label="计划人数" width="100">
          <template #default="{ row }">
            {{ row.maxStudents }}人
          </template>
        </el-table-column>
        <el-table-column label="适用学院" width="120">
          <template #default="{ row }">
            <span v-if="row.collegeName">{{ row.collegeName }}</span>
            <el-tag v-else type="info" size="small">不限制</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="适用专业" width="150">
          <template #default="{ row }">
            <span v-if="row.majorName">{{ row.majorName }}</span>
            <el-tag v-else type="info" size="small">不限制</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="!isTemplateApplied(row.id)"
              size="small" 
              type="primary" 
              @click="applyForTemplate(row)">
              申请开课
            </el-button>
            <el-button 
              v-else
              size="small" 
              type="info" 
              disabled>
              已申请
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; text-align: right;"
      />
    </el-card>

    <!-- 申请开课对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="申请开课"
      width="600px"
    >
      <el-form :model="applicationForm" :rules="applicationRules" ref="applicationFormRef" label-width="100px">
        <el-form-item label="课程名称">
          <el-input v-model="selectedTemplate.templateName" readonly />
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input v-model="selectedTemplate.description" type="textarea" rows="2" readonly />
        </el-form-item>
        <el-form-item label="学年">
          <el-input v-model="selectedTemplate.academicYear" readonly />
        </el-form-item>
        <el-form-item label="学期">
          <el-input :value="selectedTemplate.semester === '第一学期' ? '第一学期（上半期）' : '第二学期（下半期）'" readonly />
        </el-form-item>
        <el-form-item label="适用学院" v-if="selectedTemplate.collegeName">
          <el-input v-model="selectedTemplate.collegeName" readonly />
        </el-form-item>
        <el-form-item label="适用专业" v-if="selectedTemplate.majorName">
          <el-input v-model="selectedTemplate.majorName" readonly />
        </el-form-item>
        <el-form-item label="计划学时" prop="courseHours">
          <el-input 
            v-model="applicationForm.courseHours" 
            readonly
            placeholder="课程学时">
            <template #suffix>节</template>
          </el-input>
        </el-form-item>
        <el-form-item label="计划人数" prop="maxStudents">
          <el-input 
            v-model="applicationForm.maxStudents" 
            readonly
            placeholder="计划人数">
            <template #suffix>人</template>
          </el-input>
        </el-form-item>
        <el-form-item label="申请理由" prop="reason">
          <el-input 
            v-model="applicationForm.reason" 
            type="textarea" 
            rows="4" 
            placeholder="请输入申请理由"
            maxlength="500"
            show-word-limit>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitApplication" :loading="submitLoading">提交申请</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import { getAvailableTemplatesForTeacher } from '../../api/courseTemplate'
import { createCourseApplication, getAppliedTemplateIds } from '../../api/courseApplication'

export default {
  name: 'CourseApplication',
  components: {
    Document
  },
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const submitLoading = ref(false)
    const dialogVisible = ref(false)
    const applicationFormRef = ref()
    const appliedTemplateIds = ref([])
    const templateList = ref([])
    const selectedTemplate = ref({})
    
    // 查询条件
    const queryForm = reactive({
      keyword: '',
      academicYear: '',
      semester: '',
      page: 1,
      size: 10
    })
    
    // 分页信息
    const pagination = reactive({
      total: 0,
      currentPage: 1,
      pageSize: 10
    })
    
    // 申请表单
    const applicationForm = reactive({
      templateId: null,
      courseHours: null,
      maxStudents: null,
      reason: ''
    })
    
    // 表单验证规则
    const applicationRules = {
      reason: [
        { required: true, message: '请输入申请理由', trigger: 'blur' },
        { min: 10, message: '申请理由至少10个字符', trigger: 'blur' }
      ]
    }
    
    // 学年选项
    const academicYearOptions = [
      '2023-2024',
      '2024-2025',
      '2025-2026'
    ]
    
    // 学期选项
    const semesterOptions = [
      '第一学期',
      '第二学期'
    ]

    // 获取已申请的模板ID列表
    const loadAppliedTemplateIds = async () => {
      try {
        const response = await getAppliedTemplateIds()
        if (response.code === 200) {
          appliedTemplateIds.value = response.data || []
        }
      } catch (error) {
        console.error('获取已申请模板列表失败:', error)
      }
    }

    // 检查模板是否已申请
    const isTemplateApplied = (templateId) => {
      return appliedTemplateIds.value.includes(templateId)
    }

    // 获取课程模板列表
    const fetchTemplateList = async () => {
      try {
        loading.value = true
        const params = {
          page: pagination.currentPage,
          size: pagination.pageSize,
          keyword: queryForm.keyword,
          academicYear: queryForm.academicYear,
          semester: queryForm.semester
        }
        
        const response = await getAvailableTemplatesForTeacher(params)
        if (response.code === 200) {
          templateList.value = response.data.records || []
          pagination.total = response.data.total || 0
        } else {
          ElMessage.error(response.message || '获取课程模板列表失败')
        }
      } catch (error) {
        console.error('获取课程模板列表失败:', error)
        ElMessage.error('获取课程模板列表失败')
      } finally {
        loading.value = false
      }
    }

    // 搜索模板
    const searchTemplates = () => {
      pagination.currentPage = 1
      fetchTemplateList()
    }

    // 重置搜索
    const resetSearch = () => {
      Object.assign(queryForm, {
        keyword: '',
        academicYear: '',
        semester: ''
      })
      pagination.currentPage = 1
      fetchTemplateList()
    }

    // 申请开课
    const applyForTemplate = (template) => {
      // 检查是否已申请
      if (isTemplateApplied(template.id)) {
        ElMessage.warning('您已经申请过该课程模板，不能重复申请')
        return
      }
      
      selectedTemplate.value = { ...template }
      applicationForm.courseHours = template.courseHours
      applicationForm.maxStudents = template.maxStudents
      applicationForm.reason = ''
      dialogVisible.value = true
    }

    // 提交申请
    const submitApplication = async () => {
      try {
        await applicationFormRef.value.validate()
        submitLoading.value = true

        const submitData = {
          templateId: selectedTemplate.value.id,
          courseHours: applicationForm.courseHours,
          maxStudents: applicationForm.maxStudents,
          reason: applicationForm.reason
        }

        const response = await createCourseApplication(submitData)
        
        if (response.code === 200) {
          ElMessage.success('申请提交成功')
          dialogVisible.value = false
          // 重新加载已申请的模板列表
          await loadAppliedTemplateIds()
          // 跳转到我的申请页面
          router.push('/course/my-applications')
        }
      } catch (error) {
        console.error('提交申请失败:', error)
        ElMessage.error(error.response?.data?.message || '提交申请失败')
      } finally {
        submitLoading.value = false
      }
    }

    // 跳转到我的申请页面
    const goToMyApplications = () => {
      router.push('/course/my-applications')
    }

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '-'
      return new Date(dateString).toLocaleString('zh-CN')
    }

    // 分页处理
    const handleSizeChange = (size) => {
      pagination.pageSize = size
      pagination.currentPage = 1
      fetchTemplateList()
    }

    const handleCurrentChange = (page) => {
      pagination.currentPage = page
      fetchTemplateList()
    }

    onMounted(async () => {
      await loadAppliedTemplateIds()
      await fetchTemplateList()
    })

    return {
      loading,
      submitLoading,
      dialogVisible,
      applicationFormRef,
      queryForm,
      pagination,
      templateList,
      selectedTemplate,
      applicationForm,
      applicationRules,
      appliedTemplateIds,
      isTemplateApplied,
      fetchTemplateList,
      searchTemplates,
      resetSearch,
      applyForTemplate,
      submitApplication,
      goToMyApplications,
      formatDate,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.course-application {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-container {
  margin-bottom: 20px;
}

.template-info {
  display: flex;
  flex-direction: column;
}

.template-name {
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.template-desc {
  font-size: 12px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .course-application {
    padding: 10px;
  }
  
  .search-container :deep(.el-form--inline .el-form-item) {
    display: block;
    margin-bottom: 15px;
  }
  
  .template-desc {
    max-width: 150px;
  }
  
  .header-controls {
    flex-direction: column;
    gap: 5px;
  }
}
</style> 