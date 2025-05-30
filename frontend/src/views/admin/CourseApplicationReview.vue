<template>
  <div class="course-application-review">
    <div class="header">
      <h2>开课申请审批</h2>
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索课程名称或申请人"
          style="width: 300px; margin-right: 10px"
          clearable
        />
        <el-button type="primary" @click="searchApplications">搜索</el-button>
      </div>
    </div>

    <el-table :data="applicationList" border style="width: 100%">
      <el-table-column prop="id" label="申请ID" width="80" />
      <el-table-column prop="courseName" label="课程名称" />
      <el-table-column prop="teacherName" label="申请教师" />
      <el-table-column prop="academicYear" label="学年" />
      <el-table-column prop="semester" label="学期" />
      <el-table-column prop="maxStudents" label="计划人数" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="applyTime" label="申请时间" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="viewApplication(scope.row)">查看</el-button>
          <el-button 
            v-if="scope.row.status === 0" 
            size="small" 
            type="success" 
            @click="approveApplication(scope.row)"
          >
            通过
          </el-button>
          <el-button 
            v-if="scope.row.status === 0" 
            size="small" 
            type="danger" 
            @click="rejectApplication(scope.row)"
          >
            拒绝
          </el-button>
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

    <!-- 查看申请详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="申请详情"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请ID">{{ applicationDetail.id }}</el-descriptions-item>
        <el-descriptions-item label="课程名称">{{ applicationDetail.courseName }}</el-descriptions-item>
        <el-descriptions-item label="申请教师">{{ applicationDetail.teacherName }}</el-descriptions-item>
        <el-descriptions-item label="学年">{{ applicationDetail.academicYear }}</el-descriptions-item>
        <el-descriptions-item label="学期">{{ applicationDetail.semester }}</el-descriptions-item>
        <el-descriptions-item label="计划人数">{{ applicationDetail.maxStudents }}人</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(applicationDetail.status)">
            {{ getStatusText(applicationDetail.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ applicationDetail.applyTime }}</el-descriptions-item>
        <el-descriptions-item label="审核时间" v-if="applicationDetail.reviewTime">
          {{ applicationDetail.reviewTime }}
        </el-descriptions-item>
        <el-descriptions-item label="审核人" v-if="applicationDetail.reviewerName">
          {{ applicationDetail.reviewerName }}
        </el-descriptions-item>
      </el-descriptions>
      <el-descriptions :column="1" border style="margin-top: 20px">
        <el-descriptions-item label="申请理由">{{ applicationDetail.reason }}</el-descriptions-item>
        <el-descriptions-item label="审核意见" v-if="applicationDetail.reviewComment">
          {{ applicationDetail.reviewComment }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 审批对话框 -->
    <el-dialog
      v-model="reviewDialogVisible"
      :title="reviewType === 'approve' ? '通过申请' : '拒绝申请'"
      width="500px"
    >
      <el-form :model="reviewForm" ref="reviewFormRef" label-width="100px">
        <el-form-item label="课程名称">
          <el-input :value="currentApplication?.courseName" readonly />
        </el-form-item>
        <el-form-item label="申请教师">
          <el-input :value="currentApplication?.teacherName" readonly />
        </el-form-item>
        <el-form-item label="审核意见" :rules="[{ required: true, message: '请输入审核意见', trigger: 'blur' }]" prop="reviewComment">
          <el-input 
            v-model="reviewForm.reviewComment" 
            type="textarea" 
            rows="4" 
            :placeholder="reviewType === 'approve' ? '请输入通过理由' : '请输入拒绝理由'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reviewDialogVisible = false">取消</el-button>
          <el-button 
            :type="reviewType === 'approve' ? 'success' : 'danger'" 
            @click="submitReview"
          >
            {{ reviewType === 'approve' ? '通过' : '拒绝' }}
          </el-button>
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
  name: 'CourseApplicationReview',
  setup() {
    const applicationList = ref([])
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const searchKeyword = ref('')
    const viewDialogVisible = ref(false)
    const reviewDialogVisible = ref(false)
    const reviewType = ref('approve') // 'approve' or 'reject'
    const currentApplication = ref(null)
    const reviewFormRef = ref(null)

    const applicationDetail = reactive({})

    const reviewForm = reactive({
      reviewComment: ''
    })

    const loadApplications = async () => {
      try {
        const response = await axios.get('/api/course-applications/admin', {
          params: {
            page: currentPage.value,
            size: pageSize.value,
            keyword: searchKeyword.value
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

    const searchApplications = () => {
      currentPage.value = 1
      loadApplications()
    }

    const viewApplication = (row) => {
      Object.assign(applicationDetail, row)
      viewDialogVisible.value = true
    }

    const approveApplication = (row) => {
      currentApplication.value = row
      reviewType.value = 'approve'
      reviewForm.reviewComment = ''
      reviewDialogVisible.value = true
    }

    const rejectApplication = (row) => {
      currentApplication.value = row
      reviewType.value = 'reject'
      reviewForm.reviewComment = ''
      reviewDialogVisible.value = true
    }

    const submitReview = async () => {
      if (!reviewFormRef.value) return
      
      try {
        await reviewFormRef.value.validate()
        
        const status = reviewType.value === 'approve' ? 1 : 2
        const response = await axios.put(`/api/course-applications/${currentApplication.value.id}/review`, {
          status,
          reviewComment: reviewForm.reviewComment
        })
        
        if (response.data.code === 200) {
          ElMessage.success(reviewType.value === 'approve' ? '申请已通过' : '申请已拒绝')
          reviewDialogVisible.value = false
          loadApplications()
        } else {
          ElMessage.error(response.data.message)
        }
      } catch (error) {
        console.error('审核失败:', error)
        ElMessage.error('审核失败')
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

    onMounted(() => {
      loadApplications()
    })

    return {
      applicationList,
      currentPage,
      pageSize,
      total,
      searchKeyword,
      viewDialogVisible,
      reviewDialogVisible,
      reviewType,
      currentApplication,
      applicationDetail,
      reviewForm,
      reviewFormRef,
      loadApplications,
      searchApplications,
      viewApplication,
      approveApplication,
      rejectApplication,
      submitReview,
      getStatusType,
      getStatusText,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.course-application-review {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
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