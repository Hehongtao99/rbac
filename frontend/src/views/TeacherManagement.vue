<template>
  <div class="teacher-management">
    <div class="page-header">
      <h2>教师管理</h2>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入教师编号、姓名、邮箱或手机号"
          style="width: 300px; margin-right: 15px"
          clearable
          @keyup.enter="searchTeachers"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="searchTeachers">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
      </div>
    </div>

    <!-- 教师列表 -->
    <el-table 
      :data="teacherList" 
      border 
      stripe 
      v-loading="loading"
      style="width: 100%"
    >
      <el-table-column prop="teacherNo" label="教师编号" width="120" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="gender" label="性别" width="80" />
      <el-table-column prop="phone" label="手机号码" width="130" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column prop="college" label="学院" width="130" />
      <el-table-column prop="major" label="专业" width="130" />
      <el-table-column label="班级" width="200">
        <template #default="scope">
          {{ formatClasses(scope.row.classes) }}
        </template>
      </el-table-column>
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

    <!-- 教师详情对话框 -->
    <el-dialog
      title="教师详情"
      v-model="dialogVisible"
      width="500px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="教师编号">{{ userDetail.teacherNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ userDetail.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ userDetail.gender }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userDetail.email }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ userDetail.phone }}</el-descriptions-item>
        <el-descriptions-item label="学院">{{ userDetail.college }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ userDetail.major }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ formatClasses(userDetail.classes) }}</el-descriptions-item>
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
import { getTeacherList } from '../api/teacher'

export default {
  name: 'TeacherManagement',
  setup() {
    const loading = ref(false)
    const searchKeyword = ref('')
    const teacherList = ref([])
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    
    const dialogVisible = ref(false)
    const userDetail = reactive({
      teacherNo: '',
      name: '',
      gender: '',
      email: '',
      phone: '',
      college: '',
      major: '',
      classes: [],
      status: 1,
      createTime: ''
    })
    
    // 获取教师列表
    const fetchTeacherList = async () => {
      loading.value = true
      try {
        const response = await getTeacherList({
          page: currentPage.value,
          size: pageSize.value,
          keyword: searchKeyword.value
        })
        
        if (response.code === 200) {
          teacherList.value = response.data.records
          total.value = response.data.total
          console.log('教师列表数据:', teacherList.value)
        }
      } catch (error) {
        console.error('获取教师列表失败:', error)
        ElMessage.error('获取教师列表失败')
      } finally {
        loading.value = false
      }
    }
    
    // 搜索教师
    const searchTeachers = () => {
      currentPage.value = 1
      fetchTeacherList()
    }
    
    // 分页处理
    const handleSizeChange = (val) => {
      pageSize.value = val
      fetchTeacherList()
    }
    
    const handleCurrentChange = (val) => {
      currentPage.value = val
      fetchTeacherList()
    }
    
    // 查看教师详情
    const viewUser = (teacher) => {
      Object.assign(userDetail, teacher)
      dialogVisible.value = true
    }
    
    // 格式化日期
    const formatDate = (dateTime) => {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      return date.toLocaleString()
    }
    
    // 格式化班级
    const formatClasses = (classes) => {
      if (!classes || !Array.isArray(classes) || classes.length === 0) {
        return '未分配'
      }
      return classes.map(c => c.name).join(', ')
    }
    
    onMounted(() => {
      fetchTeacherList()
    })
    
    return {
      loading,
      searchKeyword,
      teacherList,
      currentPage,
      pageSize,
      total,
      dialogVisible,
      userDetail,
      fetchTeacherList,
      searchTeachers,
      handleSizeChange,
      handleCurrentChange,
      viewUser,
      formatDate,
      formatClasses
    }
  }
}
</script>

<style scoped>
.teacher-management {
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