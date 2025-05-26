<template>
  <div class="notice-view">
    <div class="page-header">
      <h2>通知公告</h2>
    </div>

    <!-- 查询条件 -->
    <div class="search-container">
      <el-form :model="queryForm" :inline="true" class="search-form">
        <el-form-item label="公告标题">
          <el-input 
            v-model="queryForm.title" 
            placeholder="请输入公告标题" 
            clearable 
          />
        </el-form-item>
        <el-form-item label="公告人">
          <el-input 
            v-model="queryForm.publisherName" 
            placeholder="请输入公告人姓名" 
            clearable 
          />
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD HH:mm:ss"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格 -->
    <div class="table-container">
      <el-table :data="noticeList" v-loading="loading" stripe>
        <el-table-column prop="title" label="公告标题" min-width="250" />
        <el-table-column prop="publisherName" label="发布人" width="120" />
        <el-table-column prop="publishTime" label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.publishTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </div>
    </div>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="通知详情" width="800px">
      <div class="notice-detail">
        <h3>{{ viewNotice.title }}</h3>
        <div class="notice-meta">
          <span>发布人：{{ viewNotice.publisherName }}</span>
          <span>发布时间：{{ formatDate(viewNotice.publishTime) }}</span>
        </div>
        <div class="notice-content" v-html="viewNotice.content"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getNoticeList, getNoticeById } from '../api/notice'

// 响应式数据
const loading = ref(false)
const noticeList = ref([])
const total = ref(0)
const viewDialogVisible = ref(false)
const dateRange = ref([])

// 查询表单 - 只查询已发布的通知
const queryForm = reactive({
  title: '',
  status: 1, // 固定查询已发布状态
  publisherName: '',
  startTime: '',
  endTime: '',
  pageNum: 1,
  pageSize: 10
})

// 查看通知详情
const viewNotice = ref({})

onMounted(() => {
  handleSearch()
})

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 日期范围改变
const handleDateChange = (dates) => {
  if (dates && dates.length === 2) {
    queryForm.startTime = dates[0] + ' 00:00:00'
    queryForm.endTime = dates[1] + ' 23:59:59'
  } else {
    queryForm.startTime = ''
    queryForm.endTime = ''
  }
}

// 查询
const handleSearch = async () => {
  loading.value = true
  try {
    const response = await getNoticeList(queryForm)
    if (response.code === 200) {
      noticeList.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      ElMessage.error(response.message || '查询失败')
    }
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 重置
const handleReset = () => {
  Object.assign(queryForm, {
    title: '',
    status: 1, // 保持查询已发布状态
    publisherName: '',
    startTime: '',
    endTime: '',
    pageNum: 1,
    pageSize: 10
  })
  dateRange.value = []
  handleSearch()
}

// 查看
const handleView = async (row) => {
  try {
    const response = await getNoticeById(row.id)
    if (response.code === 200) {
      viewNotice.value = response.data
      viewDialogVisible.value = true
    } else {
      ElMessage.error(response.message || '获取详情失败')
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}
</script>

<style scoped>
.notice-view {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.search-container {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-form {
  margin: 0;
}

.table-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.notice-detail h3 {
  color: #303133;
  margin-bottom: 15px;
}

.notice-meta {
  margin-bottom: 20px;
  color: #909399;
  font-size: 14px;
}

.notice-meta span {
  margin-right: 20px;
}

.notice-content {
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
  line-height: 1.6;
}
</style> 