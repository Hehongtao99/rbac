<template>
  <div class="student-management">
    <div class="page-header">
      <h2>学生管理</h2>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入学生姓名、学号、专业或班级"
          style="width: 300px; margin-right: 15px"
          clearable
          @keyup.enter="searchStudents"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="searchStudents">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
      </div>
    </div>

    <!-- 学生列表 -->
    <el-table 
      :data="studentList" 
      border 
      stripe 
      v-loading="loading"
      style="width: 100%"
    >
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="nickname" label="姓名" width="120" />
      <el-table-column prop="phone" label="手机号码" width="130" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button type="primary" size="small" @click="viewUser(scope.row)">查看</el-button>
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
      style="margin-top: 20px; text-align: right"
    />

    <!-- 用户详情对话框 -->
    <el-dialog
      title="用户详情"
      v-model="dialogVisible"
      width="500px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ userDetail.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ userDetail.nickname }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userDetail.email }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ userDetail.phone }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="userDetail.status === 1 ? 'success' : 'danger'">
            {{ userDetail.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(userDetail.createTime) }}</el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudentList } from '../api/student'

export default {
  name: 'StudentManagement',
  setup() {
    const loading = ref(false)
    const searchKeyword = ref('')
    const studentList = ref([])
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    
    const dialogVisible = ref(false)
    const userDetail = reactive({
      username: '',
      nickname: '',
      email: '',
      phone: '',
      status: 1,
      createTime: ''
    })
    
    // 获取学生列表
    const fetchStudentList = async () => {
      loading.value = true
      try {
        const response = await getStudentList({
          page: currentPage.value,
          size: pageSize.value,
          keyword: searchKeyword.value
        })
        
        if (response.code === 200) {
          studentList.value = response.data.records
          total.value = response.data.total
        }
      } catch (error) {
        console.error('获取学生列表失败:', error)
        ElMessage.error('获取学生列表失败')
      } finally {
        loading.value = false
      }
    }
    
    // 搜索学生
    const searchStudents = () => {
      currentPage.value = 1
      fetchStudentList()
    }
    
    // 分页处理
    const handleSizeChange = (val) => {
      pageSize.value = val
      fetchStudentList()
    }
    
    const handleCurrentChange = (val) => {
      currentPage.value = val
      fetchStudentList()
    }
    
    // 查看用户详情
    const viewUser = (user) => {
      Object.assign(userDetail, user)
      dialogVisible.value = true
    }
    
    // 格式化日期
    const formatDate = (dateTime) => {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      return date.toLocaleString()
    }
    
    onMounted(() => {
      fetchStudentList()
    })
    
    return {
      loading,
      searchKeyword,
      studentList,
      currentPage,
      pageSize,
      total,
      dialogVisible,
      userDetail,
      fetchStudentList,
      searchStudents,
      handleSizeChange,
      handleCurrentChange,
      viewUser,
      formatDate
    }
  }
}
</script>

<style scoped>
.student-management {
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
  align-items: center;
}

.dialog-footer {
  text-align: right;
}
</style> 