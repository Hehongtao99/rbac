<template>
  <div class="my-applications">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>我的申请</span>
          <el-button type="primary" @click="goToApply">申请开课</el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="filter-container">
        <el-form :inline="true" :model="filterForm" class="demo-form-inline">
          <el-form-item label="状态">
            <el-select v-model="filterForm.status" placeholder="全部状态" clearable @change="loadApplications">
              <el-option label="全部" value=""></el-option>
              <el-option label="待审核" value="0"></el-option>
              <el-option label="通过" value="1"></el-option>
              <el-option label="拒绝" value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="学年">
            <el-select v-model="filterForm.academicYear" placeholder="选择学年" clearable @change="loadApplications">
              <el-option label="2024-2025" value="2024-2025"></el-option>
              <el-option label="2023-2024" value="2023-2024"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="课程名称">
            <el-input 
              v-model="filterForm.keyword" 
              placeholder="搜索课程名称" 
              clearable 
              @keyup.enter="loadApplications"
              style="width: 200px;">
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadApplications">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 申请列表 -->
      <el-table :data="applicationList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="courseName" label="课程名称" min-width="150">
          <template #default="{ row }">
            <div class="course-info">
              <div class="course-name">{{ row.courseName }}</div>
              <div class="course-desc">{{ row.reason }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="academicYear" label="学年" width="120" />
        <el-table-column prop="semester" label="学期" width="100">
          <template #default="{ row }">
            <el-tag :type="row.semester === '第一学期' ? 'success' : 'warning'">
              {{ row.semester === '第一学期' ? '上半期' : '下半期' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="courseHours" label="学时" width="80">
          <template #default="{ row }">
            {{ row.courseHours }}节
          </template>
        </el-table-column>
        <el-table-column prop="maxStudents" label="计划人数" width="100">
          <template #default="{ row }">
            {{ row.maxStudents }}人
          </template>
        </el-table-column>
        <el-table-column prop="status" label="申请状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.applyTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="reviewTime" label="审核时间" width="180">
          <template #default="{ row }">
            {{ row.reviewTime ? formatDate(row.reviewTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="info" @click="viewApplication(row)">详情</el-button>
            <el-button 
              v-if="row.status === 0" 
              size="small" 
              @click="editApplication(row)">
              编辑
            </el-button>
            <el-button 
              v-if="row.status === 0" 
              size="small" 
              type="danger" 
              @click="deleteApplication(row.id)">
              删除
            </el-button>
            <el-button 
              v-if="row.status === 1" 
              size="small" 
              type="success" 
              @click="goToSchedule">
              安排课表
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

    <!-- 查看申请详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="申请详情"
      width="700px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请ID">{{ applicationDetail.id }}</el-descriptions-item>
        <el-descriptions-item label="课程名称">{{ applicationDetail.courseName }}</el-descriptions-item>
        <el-descriptions-item label="教师姓名">{{ applicationDetail.teacherName }}</el-descriptions-item>
        <el-descriptions-item label="学年">{{ applicationDetail.academicYear }}</el-descriptions-item>
        <el-descriptions-item label="学期">{{ applicationDetail.semester }}</el-descriptions-item>
        <el-descriptions-item label="计划人数">{{ applicationDetail.maxStudents }}人</el-descriptions-item>
        <el-descriptions-item label="课程学时">{{ applicationDetail.courseHours }}节</el-descriptions-item>
        <el-descriptions-item label="申请状态">
          <el-tag :type="getStatusType(applicationDetail.status)">
            {{ getStatusText(applicationDetail.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ formatDate(applicationDetail.applyTime) }}</el-descriptions-item>
        <el-descriptions-item label="审核时间" v-if="applicationDetail.reviewTime">
          {{ formatDate(applicationDetail.reviewTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="审核人" v-if="applicationDetail.reviewerName">
          {{ applicationDetail.reviewerName }}
        </el-descriptions-item>
      </el-descriptions>
      
      <el-descriptions :column="1" border style="margin-top: 20px">
        <el-descriptions-item label="申请理由">
          <div class="reason-text">{{ applicationDetail.reason }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="审核意见" v-if="applicationDetail.reviewComment">
          <div class="review-comment" :class="applicationDetail.status === 1 ? 'success' : 'danger'">
            {{ applicationDetail.reviewComment }}
          </div>
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="viewDialogVisible = false">关闭</el-button>
          <el-button 
            v-if="applicationDetail.status === 0" 
            type="primary" 
            @click="editFromDetail">
            编辑申请
          </el-button>
          <el-button 
            v-if="applicationDetail.status === 1" 
            type="success" 
            @click="goToSchedule">
            安排课表
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑申请对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑申请"
      width="600px"
    >
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-form-item label="课程名称">
          <el-input v-model="editForm.courseName" readonly />
        </el-form-item>
        <el-form-item label="学年">
          <el-input v-model="editForm.academicYear" readonly />
        </el-form-item>
        <el-form-item label="学期">
          <el-input v-model="editForm.semester" readonly />
        </el-form-item>
        <el-form-item label="课程学时" prop="courseHours">
          <el-input 
            v-model="editForm.courseHours" 
            readonly
            placeholder="课程学时">
            <template #suffix>节</template>
          </el-input>
        </el-form-item>
        <el-form-item label="计划人数" prop="maxStudents">
          <el-input 
            v-model="editForm.maxStudents" 
            readonly
            placeholder="计划人数">
            <template #suffix>人</template>
          </el-input>
        </el-form-item>
        <el-form-item label="申请理由" prop="reason">
          <el-input 
            v-model="editForm.reason" 
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
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="updateApplication" :loading="editLoading">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourseApplicationList, updateCourseApplication, deleteCourseApplication } from '../../api/courseApplication'

export default {
  name: 'MyApplications',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const editLoading = ref(false)
    const viewDialogVisible = ref(false)
    const editDialogVisible = ref(false)
    const editFormRef = ref()

    const filterForm = reactive({
      status: '',
      academicYear: '',
      keyword: ''
    })

    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })

    const applicationList = ref([])
    const applicationDetail = ref({})
    
    const editForm = reactive({
      id: null,
      courseName: '',
      academicYear: '',
      semester: '',
      courseHours: null,
      maxStudents: null,
      reason: ''
    })

    const editRules = {
      reason: [
        { required: true, message: '请输入申请理由', trigger: 'blur' },
        { min: 10, max: 500, message: '申请理由长度在10-500个字符', trigger: 'blur' }
      ]
    }

    // 获取申请列表
    const loadApplications = async () => {
      try {
        loading.value = true
        const params = {
          page: pagination.currentPage,
          size: pagination.pageSize,
          ...filterForm
        }
        
        console.log('=== 前端发送请求参数 ===', params)
        const response = await getCourseApplicationList(params)
        console.log('=== 前端收到完整响应 ===', response)
        console.log('=== 响应数据结构 ===', response.data)
        console.log('=== records数组 ===', response.data?.records)
        
        if (response.code === 200) {
          applicationList.value = response.data.records || []
          pagination.total = response.data.total || 0
          console.log('=== 设置到页面的数据 ===', applicationList.value)
          console.log('=== 总数 ===', pagination.total)
        } else {
          console.error('响应码不是200:', response.code)
        }
      } catch (error) {
        console.error('获取申请列表失败:', error)
        ElMessage.error('获取申请列表失败')
      } finally {
        loading.value = false
      }
    }

    // 状态类型
    const getStatusType = (status) => {
      const statusMap = {
        0: 'warning', // 待审核
        1: 'success', // 通过
        2: 'danger'   // 拒绝
      }
      return statusMap[status] || 'info'
    }

    // 状态文本
    const getStatusText = (status) => {
      const statusMap = {
        0: '待审核',
        1: '已通过',
        2: '已拒绝'
      }
      return statusMap[status] || '未知'
    }

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '-'
      return new Date(dateString).toLocaleString('zh-CN')
    }

    // 查看申请详情
    const viewApplication = (application) => {
      applicationDetail.value = { ...application }
      viewDialogVisible.value = true
    }

    // 编辑申请
    const editApplication = (application) => {
      Object.assign(editForm, {
        id: application.id,
        courseName: application.courseName,
        academicYear: application.academicYear,
        semester: application.semester,
        courseHours: application.courseHours,
        maxStudents: application.maxStudents,
        reason: application.reason
      })
      editDialogVisible.value = true
    }

    // 从详情页编辑
    const editFromDetail = () => {
      viewDialogVisible.value = false
      editApplication(applicationDetail.value)
    }

    // 更新申请
    const updateApplication = async () => {
      try {
        await editFormRef.value.validate()
        editLoading.value = true

        const response = await updateCourseApplication(editForm.id, editForm)
        
        if (response.code === 200) {
          ElMessage.success('更新成功')
          editDialogVisible.value = false
          loadApplications()
        }
      } catch (error) {
        console.error('更新申请失败:', error)
        ElMessage.error(error.response?.data?.message || '更新申请失败')
      } finally {
        editLoading.value = false
      }
    }

    // 删除申请
    const deleteApplication = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除这个申请吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const response = await deleteCourseApplication(id)
        
        if (response.code === 200) {
          ElMessage.success('删除成功')
          loadApplications()
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除申请失败:', error)
          ElMessage.error('删除失败')
        }
      }
    }

    // 跳转到申请开课页面
    const goToApply = () => {
      router.push('/course/application')
    }

    // 跳转到课程表管理
    const goToSchedule = () => {
      router.push('/course/schedule')
    }

    // 重置筛选
    const resetFilter = () => {
      Object.assign(filterForm, {
        status: '',
        academicYear: '',
        keyword: ''
      })
      pagination.currentPage = 1
      loadApplications()
    }

    // 分页处理
    const handleSizeChange = (size) => {
      pagination.pageSize = size
      pagination.currentPage = 1
      loadApplications()
    }

    const handleCurrentChange = (page) => {
      pagination.currentPage = page
      loadApplications()
    }

    onMounted(() => {
      loadApplications()
    })

    return {
      loading,
      editLoading,
      filterForm,
      pagination,
      applicationList,
      applicationDetail,
      editForm,
      editRules,
      editFormRef,
      viewDialogVisible,
      editDialogVisible,
      loadApplications,
      getStatusType,
      getStatusText,
      formatDate,
      viewApplication,
      editApplication,
      editFromDetail,
      updateApplication,
      deleteApplication,
      goToApply,
      goToSchedule,
      resetFilter,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.my-applications {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-container {
  margin-bottom: 20px;
}

.course-info {
  display: flex;
  flex-direction: column;
}

.course-name {
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.course-desc {
  font-size: 12px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
}

.reason-text {
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.review-comment {
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
  padding: 10px;
  border-radius: 4px;
  border-left: 4px solid;
}

.review-comment.success {
  background-color: #f0f9ff;
  border-color: #67c23a;
  color: #67c23a;
}

.review-comment.danger {
  background-color: #fef0f0;
  border-color: #f56c6c;
  color: #f56c6c;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .my-applications {
    padding: 10px;
  }
  
  .filter-container :deep(.el-form--inline .el-form-item) {
    display: block;
    margin-bottom: 15px;
  }
  
  .course-desc {
    max-width: 150px;
  }
}
</style> 