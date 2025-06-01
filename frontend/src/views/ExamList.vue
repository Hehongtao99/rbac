<template>
  <div class="exam-list">
    <div class="header">
      <h2>我的考试记录</h2>
      <el-button type="primary" @click="goToExamStart">
        开始新考试
      </el-button>
    </div>

    <div class="content">
      <el-card>
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="title" label="考试标题" min-width="200">
            <template #default="scope">
              <div class="exam-title">
                <span>{{ scope.row.title }}</span>
                <el-tag 
                  :type="getStatusTagType(scope.row.status)" 
                  size="small"
                  style="margin-left: 10px;"
                >
                  {{ scope.row.statusName }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="bankName" label="题库" width="150" />
          
          <el-table-column label="得分" width="120">
            <template #default="scope">
              <div class="score-info">
                <span :style="{ color: getScoreColor(scope.row.userScore, scope.row.totalScore) }">
                  {{ scope.row.userScore || 0 }}
                </span>
                <span class="total-score">/ {{ scope.row.totalScore }}</span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="得分率" width="100">
            <template #default="scope">
              <span :style="{ color: getScoreColor(scope.row.userScore, scope.row.totalScore) }">
                {{ getScoreRate(scope.row.userScore, scope.row.totalScore) }}%
              </span>
            </template>
          </el-table-column>
          
          <el-table-column prop="startTime" label="开始时间" width="180">
            <template #default="scope">
              {{ formatDateTime(scope.row.startTime) }}
            </template>
          </el-table-column>
          
          <el-table-column prop="endTime" label="结束时间" width="180">
            <template #default="scope">
              {{ scope.row.endTime ? formatDateTime(scope.row.endTime) : '-' }}
            </template>
          </el-table-column>
          
          <el-table-column label="用时" width="100">
            <template #default="scope">
              {{ getExamDuration(scope.row) }}分钟
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button 
                v-if="scope.row.status === 0"
                type="primary" 
                size="small" 
                @click="continueExam(scope.row)"
              >
                继续考试
              </el-button>
              <el-button 
                v-if="scope.row.status === 1"
                type="success" 
                size="small" 
                @click="viewResult(scope.row)"
              >
                查看结果
              </el-button>
              <el-button 
                v-if="scope.row.status === 1"
                type="info" 
                size="small" 
                @click="retakeExam(scope.row)"
              >
                重新考试
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getUserExamList } from '@/api/exam'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const tableData = ref([])

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 获取考试列表
const fetchData = async () => {
  try {
    loading.value = true
    const response = await getUserExamList({
      current: pagination.current,
      size: pagination.size
    })
    
    tableData.value = response.data.records || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取考试记录失败')
  } finally {
    loading.value = false
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  fetchData()
}

// 当前页变化
const handleCurrentChange = (current) => {
  pagination.current = current
  fetchData()
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const statusMap = {
    0: 'warning',  // 进行中
    1: 'success',  // 已完成
    2: 'danger'    // 已超时
  }
  return statusMap[status] || ''
}

// 获取得分颜色
const getScoreColor = (userScore, totalScore) => {
  if (!userScore || !totalScore) return '#909399'
  const rate = (userScore / totalScore) * 100
  if (rate >= 90) return '#67c23a'
  if (rate >= 80) return '#e6a23c'
  if (rate >= 60) return '#f56c6c'
  return '#909399'
}

// 获取得分率
const getScoreRate = (userScore, totalScore) => {
  if (!userScore || !totalScore) return 0
  return Math.round((userScore / totalScore) * 100)
}

// 获取考试用时
const getExamDuration = (exam) => {
  if (!exam.startTime || !exam.endTime) {
    if (exam.status === 0) return '-' // 进行中
    return 0
  }
  const start = new Date(exam.startTime).getTime()
  const end = new Date(exam.endTime).getTime()
  return Math.round((end - start) / (1000 * 60))
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 继续考试
const continueExam = (exam) => {
  if (!exam.id) {
    ElMessage.error('考试ID无效')
    return
  }
  console.log('继续考试，examId:', exam.id)
  router.push(`/exam/${exam.id}`)
}

// 查看结果
const viewResult = (exam) => {
  if (!exam.id) {
    ElMessage.error('考试ID无效')
    return
  }
  console.log('查看结果，examId:', exam.id)
  router.push(`/exam-result/${exam.id}`)
}

// 重新考试
const retakeExam = (exam) => {
  router.push('/exam-start')
}

// 开始新考试
const goToExamStart = () => {
  router.push('/exam-start')
}

// 初始化
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.exam-list {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.content {
  background: white;
  border-radius: 8px;
}

.exam-title {
  display: flex;
  align-items: center;
}

.score-info {
  font-weight: bold;
}

.total-score {
  color: #909399;
  font-weight: normal;
  margin-left: 2px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style> 