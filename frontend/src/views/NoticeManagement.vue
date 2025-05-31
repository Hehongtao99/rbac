<template>
  <div class="notice-management">
    <div class="page-header">
      <h2>通知管理</h2>
      <div class="header-actions">
        <el-button 
          v-if="hasPermission('notice:add')"
          type="primary" 
          @click="handleAdd"
        >
          <el-icon><Plus /></el-icon>
          新增通知
        </el-button>
      </div>
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
        <el-form-item label="公告状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已下线" :value="2" />
          </el-select>
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
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="公告标题" min-width="200" />
        <el-table-column prop="publisherName" label="公告人" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.publishTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button 
              v-if="hasPermission('notice:edit')"
              type="success" 
              size="small" 
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button 
              v-if="hasPermission('notice:publish') && row.status === 0" 
              type="warning" 
              size="small" 
              @click="handlePublish(row)"
            >
              发布
            </el-button>
            <el-button 
              v-if="hasPermission('notice:offline') && row.status === 1" 
              type="info" 
              size="small" 
              @click="handleOffline(row)"
            >
              下线
            </el-button>
            <el-button 
              v-if="hasPermission('notice:delete')"
              type="danger" 
              size="small" 
              @click="handleDelete(row)"
            >
              删除
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑通知' : '新增通知'" width="800px">
              <el-form :model="noticeForm" :rules="noticeRules" ref="noticeFormRef" label-width="100px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="noticeForm.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="noticeForm.content"
            type="textarea"
            :rows="10"
            placeholder="请输入公告内容"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="公告状态" prop="status">
          <el-select v-model="noticeForm.status" placeholder="请选择状态">
            <el-option label="草稿" :value="0" />
            <el-option label="发布" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布时间" v-if="noticeForm.status === 1">
          <el-date-picker
            v-model="noticeForm.publishTime"
            type="datetime"
            placeholder="选择发布时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="confirmNotice" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="通知详情" width="800px">
      <div class="notice-detail">
        <h3>{{ viewNotice.title }}</h3>
        <div class="notice-meta">
          <span>发布人：{{ viewNotice.publisherName }}</span>
          <span>发布时间：{{ formatDate(viewNotice.publishTime) }}</span>
          <span>状态：{{ viewNotice.statusName }}</span>
        </div>
        <div class="notice-content" v-html="viewNotice.content"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getNoticeList, addNotice, updateNotice, deleteNotice, publishNotice, offlineNotice, getNoticeById } from '../api/notice'
import { usePermissions } from '../stores/permission'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const noticeList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const isEdit = ref(false)
const dateRange = ref([])

// 查询表单
const queryForm = reactive({
  title: '',
  status: null,
  publisherName: '',
  startTime: '',
  endTime: '',
  pageNum: 1,
  pageSize: 10
})

// 通知表单
const noticeForm = reactive({
  id: null,
  title: '',
  content: '',
  status: 0,
  publishTime: ''
})

// 查看通知详情
const viewNotice = ref({})

// 表单验证规则
const noticeRules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择公告状态', trigger: 'change' }
  ]
}

// 富文本编辑器
let editor = null

// 权限控制
const { hasPermission } = usePermissions()

onMounted(() => {
  handleSearch()
})

// 获取状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    0: 'info',    // 草稿
    1: 'success', // 已发布
    2: 'warning'  // 已下线
  }
  return typeMap[status] || 'info'
}

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
    status: null,
    publisherName: '',
    startTime: '',
    endTime: '',
    pageNum: 1,
    pageSize: 10
  })
  dateRange.value = []
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(noticeForm, {
    id: row.id,
    title: row.title,
    content: row.content,
    status: row.status,
    publishTime: row.publishTime
  })
  dialogVisible.value = true
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

// 发布
const handlePublish = async (row) => {
  try {
    await ElMessageBox.confirm('确认发布此通知？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await publishNotice(row.id)
    if (response.code === 200) {
      ElMessage.success('发布成功')
      handleSearch()
    } else {
      ElMessage.error(response.message || '发布失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发布失败')
    }
  }
}

// 下线
const handleOffline = async (row) => {
  try {
    await ElMessageBox.confirm('确认下线此通知？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await offlineNotice(row.id)
    if (response.code === 200) {
      ElMessage.success('下线成功')
      handleSearch()
    } else {
      ElMessage.error(response.message || '下线失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('下线失败')
    }
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除此通知？删除后无法恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await deleteNotice(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      handleSearch()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 确认提交
const noticeFormRef = ref(null)
const confirmNotice = async () => {
  if (!noticeFormRef.value) return
  
  try {
    await noticeFormRef.value.validate()
    
    submitLoading.value = true
    
    // 处理提交数据，确保日期格式正确
    const submitData = { ...noticeForm }
    
    // 如果没有设置发布时间但状态是发布，则设置为当前时间
    if (submitData.status === 1 && !submitData.publishTime) {
      const now = new Date()
      submitData.publishTime = now.getFullYear() + '-' + 
        String(now.getMonth() + 1).padStart(2, '0') + '-' + 
        String(now.getDate()).padStart(2, '0') + ' ' + 
        String(now.getHours()).padStart(2, '0') + ':' + 
        String(now.getMinutes()).padStart(2, '0') + ':' + 
        String(now.getSeconds()).padStart(2, '0')
    }
    
    // 如果状态不是发布，清空发布时间
    if (submitData.status !== 1) {
      submitData.publishTime = null
    }
    
    const response = isEdit.value 
      ? await updateNotice(submitData)
      : await addNotice(submitData)
      
    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      closeDialog()
      handleSearch()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    console.error('提交错误:', error)
    ElMessage.error('操作失败: ' + (error.message || '未知错误'))
  } finally {
    submitLoading.value = false
  }
}

// 关闭对话框
const closeDialog = () => {
  dialogVisible.value = false
  resetForm()
}

// 重置表单
const resetForm = () => {
  Object.assign(noticeForm, {
    id: null,
    title: '',
    content: '',
    status: 0,
    publishTime: ''
  })
}

// 初始化富文本编辑器（这里使用简单的div，实际项目中可以集成Quill或TinyMCE）
const initEditor = () => {
  // 这里简化处理，实际应该集成富文本编辑器
  const editorEl = document.getElementById('editor')
  if (editorEl) {
    editorEl.innerHTML = '<div contenteditable="true" style="min-height: 200px; border: 1px solid #ddd; padding: 10px;">请输入内容...</div>'
    editor = {
      root: editorEl.firstChild,
      setContents: (content) => {
        editorEl.firstChild.innerHTML = content
      }
    }
  }
}
</script>

<style scoped>
.notice-management {
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