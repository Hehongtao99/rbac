<template>
  <div class="course-application">
    <div class="header">
      <h2>申请开课</h2>
      <div class="tabs">
        <el-button 
          :type="activeTab === 'templates' ? 'primary' : ''"
          @click="activeTab = 'templates'"
        >
          课程模板
        </el-button>
        <el-button 
          :type="activeTab === 'applications' ? 'primary' : ''"
          @click="activeTab = 'applications'"
        >
          我的申请
        </el-button>
      </div>
    </div>

    <!-- 课程模板列表 -->
    <div v-if="activeTab === 'templates'">
      <div class="search-box" style="margin-bottom: 20px;">
        <el-input
          v-model="templateSearchKeyword"
          placeholder="搜索课程模板"
          style="width: 300px; margin-right: 10px"
          clearable
          @keyup.enter="searchTemplates"
        />
        <el-button type="primary" @click="searchTemplates">搜索</el-button>
      </div>

      <el-table :data="templateList" border style="width: 100%">
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
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button size="small" type="primary" @click="applyForTemplate(scope.row)">
              申请开课
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="templateCurrentPage"
        v-model:page-size="templatePageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="templateTotal"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleTemplatePageSizeChange"
        @current-change="handleTemplatePageChange"
        style="margin-top: 20px; text-align: right;"
      />
    </div>

    <!-- 我的申请列表 -->
    <div v-if="activeTab === 'applications'">
      <el-table :data="applicationList" border style="width: 100%">
        <el-table-column prop="courseName" label="课程名称" />
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
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button v-if="scope.row.status === 0" size="small" @click="editApplication(scope.row)">编辑</el-button>
            <el-button v-if="scope.row.status === 0" size="small" type="danger" @click="deleteApplication(scope.row.id)">删除</el-button>
            <el-button size="small" type="info" @click="viewApplication(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; text-align: right;"
      />
    </div>

    <!-- 申请开课对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑申请' : '申请开课'"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="课程名称">
          <el-input :value="selectedTemplate?.templateName" readonly />
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input :value="selectedTemplate?.description" type="textarea" rows="2" readonly />
        </el-form-item>
        <el-form-item label="学年">
          <el-input :value="selectedTemplate?.academicYear" readonly />
        </el-form-item>
        <el-form-item label="学期">
          <el-input :value="selectedTemplate?.semester === '第一学期' ? '第一学期（上半期）' : '第二学期（下半期）'" readonly />
        </el-form-item>
        <el-form-item label="课程学时">
          <el-input :value="selectedTemplate?.courseHours + '节'" readonly />
        </el-form-item>
        <el-form-item label="计划人数">
          <el-input :value="selectedTemplate?.maxStudents + '人'" readonly />
        </el-form-item>
        <el-form-item label="申请理由" prop="reason">
          <el-input v-model="form.reason" type="textarea" rows="3" placeholder="请输入申请理由" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看申请详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="申请详情"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请ID">{{ applicationDetail.id }}</el-descriptions-item>
        <el-descriptions-item label="课程名称">{{ applicationDetail.courseName }}</el-descriptions-item>
        <el-descriptions-item label="学年">{{ applicationDetail.academicYear }}</el-descriptions-item>
        <el-descriptions-item label="学期">{{ applicationDetail.semester }}</el-descriptions-item>
        <el-descriptions-item label="计划人数">{{ applicationDetail.maxStudents }}人</el-descriptions-item>
        <el-descriptions-item label="课程学时">{{ applicationDetail.courseHours }}节</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(applicationDetail.status)">
            {{ getStatusText(applicationDetail.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ applicationDetail.applyTime }}</el-descriptions-item>
        <el-descriptions-item label="审核时间" v-if="applicationDetail.reviewTime">
          {{ applicationDetail.reviewTime }}
        </el-descriptions-item>
      </el-descriptions>
      <el-descriptions :column="1" border style="margin-top: 20px">
        <el-descriptions-item label="申请理由">{{ applicationDetail.reason }}</el-descriptions-item>
        <el-descriptions-item label="审核意见" v-if="applicationDetail.reviewComment">
          {{ applicationDetail.reviewComment }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

export default {
  name: 'CourseApplication',
  setup() {
    const activeTab = ref('templates')
    const applicationList = ref([])
    const templateList = ref([])
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const templateCurrentPage = ref(1)
    const templatePageSize = ref(10)
    const templateTotal = ref(0)
    const templateSearchKeyword = ref('')
    const dialogVisible = ref(false)
    const viewDialogVisible = ref(false)
    const isEdit = ref(false)
    const formRef = ref(null)
    const selectedTemplate = ref(null)

    const form = reactive({
      id: null,
      templateId: null,
      reason: ''
    })

    const applicationDetail = reactive({})

    const rules = {
      reason: [
        { required: true, message: '请输入申请理由', trigger: 'blur' }
      ]
    }

    const loadApplications = async () => {
      try {
        const response = await axios.get('/api/course-applications', {
          params: {
            page: currentPage.value,
            size: pageSize.value
          }
        })
        if (response.data.code === 200) {
          applicationList.value = response.data.data.records || []
          total.value = response.data.data.total || 0
        }
      } catch (error) {
        console.error('加载申请列表失败:', error)
        ElMessage.error('加载申请列表失败')
      }
    }

    const loadTemplates = async () => {
      try {
        const response = await axios.get('/api/course-templates/enabled', {
          params: {
            keyword: templateSearchKeyword.value
          }
        })
        if (response.data.code === 200) {
          templateList.value = response.data.data || []
          templateTotal.value = response.data.data.length || 0
        }
      } catch (error) {
        console.error('加载课程模板失败:', error)
        ElMessage.error('加载课程模板失败')
      }
    }

    const searchTemplates = () => {
      loadTemplates()
    }

    const applyForTemplate = (template) => {
      selectedTemplate.value = template
      isEdit.value = false
      resetForm()
      form.templateId = template.id
      dialogVisible.value = true
    }

    const editApplication = (row) => {
      isEdit.value = true
      Object.assign(form, {
        id: row.id,
        templateId: row.templateId,
        reason: row.reason
      })
      // 查找对应的模板信息
      selectedTemplate.value = templateList.value.find(t => t.id === row.templateId) || {
        templateName: row.courseName,
        description: '',
        courseHours: row.courseHours,
        academicYear: row.academicYear,
        semester: row.semester,
        maxStudents: row.maxStudents
      }
      dialogVisible.value = true
    }

    const viewApplication = (row) => {
      Object.assign(applicationDetail, row)
      viewDialogVisible.value = true
    }

    const resetForm = () => {
      Object.assign(form, {
        id: null,
        templateId: null,
        reason: ''
      })
      if (formRef.value) {
        formRef.value.resetFields()
      }
    }

    const submitForm = async () => {
      if (!formRef.value) return
      
      try {
        await formRef.value.validate()
        
        const url = isEdit.value ? `/api/course-applications/${form.id}` : '/api/course-applications'
        const method = isEdit.value ? 'put' : 'post'
        
        const response = await axios[method](url, form)
        
        if (response.data.code === 200) {
          ElMessage.success(isEdit.value ? '更新成功' : '申请提交成功')
          dialogVisible.value = false
          loadApplications()
          // 切换到我的申请页面
          activeTab.value = 'applications'
        } else {
          ElMessage.error(response.data.message)
        }
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('操作失败')
      }
    }

    const getStatusType = (status) => {
      const statusMap = {
        0: 'warning', // 待审核
        1: 'success', // 已通过
        2: 'danger'   // 已拒绝
      }
      return statusMap[status] || 'info'
    }

    const getStatusText = (status) => {
      const statusMap = {
        0: '待审核',
        1: '已通过',
        2: '已拒绝'
      }
      return statusMap[status] || '未知'
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      loadApplications()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      loadApplications()
    }

    const handleTemplatePageSizeChange = (val) => {
      templatePageSize.value = val
      loadTemplates()
    }

    const handleTemplatePageChange = (val) => {
      templateCurrentPage.value = val
      loadTemplates()
    }

    const deleteApplication = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除这个申请吗？', '确认删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const response = await axios.delete(`/api/course-applications/${id}`)
        
        if (response.data.code === 200) {
          ElMessage.success('删除成功')
          loadApplications()
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

    onMounted(() => {
      loadTemplates()
      loadApplications()
    })

    return {
      activeTab,
      applicationList,
      templateList,
      currentPage,
      pageSize,
      total,
      templateCurrentPage,
      templatePageSize,
      templateTotal,
      templateSearchKeyword,
      dialogVisible,
      viewDialogVisible,
      isEdit,
      form,
      applicationDetail,
      rules,
      formRef,
      selectedTemplate,
      loadTemplates,
      searchTemplates,
      applyForTemplate,
      editApplication,
      viewApplication,
      deleteApplication,
      submitForm,
      getStatusType,
      getStatusText,
      handleSizeChange,
      handleCurrentChange,
      handleTemplatePageSizeChange,
      handleTemplatePageChange
    }
  }
}
</script>

<style scoped>
.course-application {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.tabs {
  display: flex;
  gap: 10px;
}

.search-box {
  display: flex;
  align-items: center;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}
</style> 