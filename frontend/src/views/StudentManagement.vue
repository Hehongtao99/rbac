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
      <el-table-column prop="studentNo" label="学号" width="120" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="gender" label="性别" width="80" />
      <el-table-column prop="collegeName" label="学院" width="130" />
      <el-table-column prop="major" label="专业" width="130" />
      <el-table-column prop="className" label="班级" width="120" />
      <el-table-column prop="phone" label="手机号码" width="130" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column prop="grade" label="年级" width="100" />
      <el-table-column prop="educationSystem" label="学制" width="100" />
      <el-table-column label="当前学期" width="120">
        <template #default="scope">
          {{ formatCurrentSemester(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column prop="currentAcademicYear" label="当前学年学期" width="200" />
      <el-table-column label="预计毕业时间" width="120">
        <template #default="scope">
          {{ calculateGraduationYear(scope.row) }}
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
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="scope">
          <el-button type="primary" size="small" @click="viewUser(scope.row)">查看</el-button>
          <el-button type="warning" size="small" @click="assignGradeEducation(scope.row)">分配</el-button>
          <el-button type="success" size="small" @click="setSemester(scope.row)">学期</el-button>
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

    <!-- 学生详情对话框 -->
    <el-dialog
      title="学生详情"
      v-model="dialogVisible"
      width="500px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="学号">{{ userDetail.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ userDetail.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ userDetail.gender }}</el-descriptions-item>
        <el-descriptions-item label="学院">{{ userDetail.collegeName }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ userDetail.major }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ userDetail.className }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userDetail.email }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ userDetail.phone }}</el-descriptions-item>
        <el-descriptions-item label="年级">{{ userDetail.grade || '未分配' }}</el-descriptions-item>
        <el-descriptions-item label="学制">{{ userDetail.educationSystem || '未分配' }}</el-descriptions-item>
        <el-descriptions-item label="当前学期">{{ formatCurrentSemester(userDetail) }}</el-descriptions-item>
        <el-descriptions-item label="当前学年学期">{{ userDetail.currentAcademicYear || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="预计毕业时间">{{ calculateGraduationYear(userDetail) }}</el-descriptions-item>
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

    <!-- 分配年级和学制对话框 -->
    <el-dialog
      title="分配年级和学制"
      v-model="assignDialogVisible"
      width="400px"
    >
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="学生姓名">
          <el-input v-model="assignForm.studentName" disabled />
        </el-form-item>
        <el-form-item label="年级">
          <el-select v-model="assignForm.grade" placeholder="请选择年级" style="width: 100%">
            <el-option label="2020级" value="2020" />
            <el-option label="2021级" value="2021" />
            <el-option label="2022级" value="2022" />
            <el-option label="2023级" value="2023" />
            <el-option label="2024级" value="2024" />
          </el-select>
        </el-form-item>
        <el-form-item label="学制">
          <el-select v-model="assignForm.educationSystem" placeholder="请选择学制" style="width: 100%">
            <el-option label="3年" value="3年" />
            <el-option label="4年" value="4年" />
            <el-option label="5年" value="5年" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="assignDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAssign" :loading="assignLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 设置学期信息对话框 -->
    <el-dialog
      title="设置学期信息"
      v-model="semesterDialogVisible"
      width="500px"
    >
      <el-form :model="semesterForm" label-width="100px">
        <el-form-item label="学生姓名">
          <el-input v-model="semesterForm.studentName" disabled />
        </el-form-item>
        <el-form-item label="当前年级">
          <el-select v-model="semesterForm.currentYear" placeholder="请选择当前年级" style="width: 100%">
            <el-option label="大一" :value="1" />
            <el-option label="大二" :value="2" />
            <el-option label="大三" :value="3" />
            <el-option label="大四" :value="4" />
            <el-option label="大五" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="当前学期">
          <el-select v-model="semesterForm.currentSemester" placeholder="请选择当前学期" style="width: 100%">
            <el-option label="上学期" :value="1" />
            <el-option label="下学期" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="semesterDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSetSemester" :loading="semesterLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudentList, assignGradeAndEducation, setSemesterInfo } from '../api/student'

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
      studentNo: '',
      name: '',
      gender: '',
      email: '',
      phone: '',
      collegeName: '',
      major: '',
      className: '',
      grade: '',
      educationSystem: '',
      currentYear: null,
      currentSemester: null,
      currentAcademicYear: '',
      status: 1,
      createTime: ''
    })
    
    const assignDialogVisible = ref(false)
    const assignLoading = ref(false)
    const assignForm = reactive({
      studentId: null,
      studentName: '',
      grade: '',
      educationSystem: ''
    })
    
    const semesterDialogVisible = ref(false)
    const semesterLoading = ref(false)
    const semesterForm = reactive({
      studentId: null,
      studentName: '',
      studentGrade: '',
      currentYear: null,
      currentSemester: null
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
          console.log('学生列表数据:', studentList.value)
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
    
    // 分配年级和学制
    const assignGradeEducation = (user) => {
      assignForm.studentId = user.id
      assignForm.studentName = user.name
      assignForm.grade = user.grade || ''
      assignForm.educationSystem = user.educationSystem || ''
      assignDialogVisible.value = true
    }
    
    // 确认分配
    const confirmAssign = async () => {
      if (!assignForm.grade || !assignForm.educationSystem) {
        ElMessage.error('请选择年级和学制')
        return
      }
      
      assignLoading.value = true
      try {
        const response = await assignGradeAndEducation(
          assignForm.studentId, 
          assignForm.grade, 
          assignForm.educationSystem
        )
        
        if (response.code === 200) {
          ElMessage.success('分配成功')
          assignDialogVisible.value = false
          fetchStudentList()
        } else {
          ElMessage.error(response.message || '分配失败')
        }
      } catch (error) {
        console.error('分配失败:', error)
        ElMessage.error('分配失败')
      } finally {
        assignLoading.value = false
      }
    }
    
    // 设置学期信息
    const setSemester = (student) => {
      semesterForm.studentId = student.id
      semesterForm.studentName = student.name
      semesterForm.studentGrade = student.grade || ''
      semesterForm.currentYear = student.currentYear || null
      semesterForm.currentSemester = student.currentSemester || null
      semesterDialogVisible.value = true
    }
    
    // 确认设置学期
    const confirmSetSemester = async () => {
      if (!semesterForm.currentYear || !semesterForm.currentSemester) {
        ElMessage.error('请选择当前年级和学期')
        return
      }
      
      semesterLoading.value = true
      try {
        const response = await setSemesterInfo(
          semesterForm.studentId,
          semesterForm.currentYear,
          semesterForm.currentSemester
        )
        
        if (response.code === 200) {
          ElMessage.success('学期信息设置成功')
          semesterDialogVisible.value = false
          fetchStudentList()
        } else {
          ElMessage.error(response.message || '设置失败')
        }
      } catch (error) {
        console.error('设置学期信息失败:', error)
        ElMessage.error('设置失败')
      } finally {
        semesterLoading.value = false
      }
    }
    
    // 格式化当前学期 - 显示为"大几几期"格式
    const formatCurrentSemester = (student) => {
      // 如果没有设置学期信息，返回未设置
      if (!student.currentYear || !student.currentSemester) {
        return '未设置'
      }
      
      try {
        const currentYear = parseInt(student.currentYear)
        const currentSemester = parseInt(student.currentSemester)
        
        // 转换年级
        const yearNames = ['', '大一', '大二', '大三', '大四', '大五']
        const yearName = yearNames[currentYear] || `大${currentYear}`
        
        // 转换学期
        const semesterName = currentSemester === 1 ? '上期' : '下期'
        
        return `${yearName}${semesterName}`
      } catch (error) {
        return '格式错误'
      }
    }
    
    // 格式化日期
    const formatDate = (dateTime) => {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      return date.toLocaleString()
    }
    
    // 计算预计毕业时间
    const calculateGraduationYear = (student) => {
      if (!student.grade) {
        return '未设置'
      }
      
      try {
        const gradeYear = parseInt(student.grade)
        let educationYears = 4 // 默认4年制
        
        // 根据学制确定学习年限
        if (student.educationSystem === '3年') {
          educationYears = 3
        } else if (student.educationSystem === '5年') {
          educationYears = 5
        }
        
        const graduationYear = gradeYear + educationYears
        return `${graduationYear}年`
      } catch (error) {
        return '格式错误'
      }
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
      assignDialogVisible,
      assignLoading,
      assignForm,
      semesterDialogVisible,
      semesterLoading,
      semesterForm,
      fetchStudentList,
      searchStudents,
      handleSizeChange,
      handleCurrentChange,
      viewUser,
      assignGradeEducation,
      confirmAssign,
      setSemester,
      confirmSetSemester,
      formatCurrentSemester,
      formatDate,
      calculateGraduationYear
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