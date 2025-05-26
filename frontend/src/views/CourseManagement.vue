<template>
  <div class="course-management">
    <div class="page-header">
      <h2>课程管理</h2>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入课程名称、教师或学年"
          style="width: 300px; margin-right: 15px"
          clearable
          @keyup.enter="searchCourses"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="searchCourses">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button type="success" @click="openCreateDialog">
          <el-icon><Plus /></el-icon>
          新增课程
        </el-button>
      </div>
    </div>

    <!-- 课程列表 -->
    <el-table 
      :data="courseList" 
      border 
      stripe 
      v-loading="loading"
      style="width: 100%"
    >
      <el-table-column prop="courseName" label="课程名称" width="180" />
      <el-table-column prop="courseDescription" label="课程描述" width="200" show-overflow-tooltip />
      <el-table-column prop="teacherName" label="指定教师" width="120" />
      <el-table-column prop="maxStudents" label="课程人数" width="100" align="center" />
      <el-table-column prop="currentStudents" label="当前人数" width="100" align="center" />
      <el-table-column prop="courseHours" label="课程学时" width="100" align="center">
        <template #default="scope">
          {{ scope.row.courseHours }}节
        </template>
      </el-table-column>
      <el-table-column prop="academicYear" label="学年" width="150" />
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
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button type="primary" size="small" @click="viewCourse(scope.row)">查看</el-button>
          <el-button type="warning" size="small" @click="editCourse(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deleteCourseConfirm(scope.row)">删除</el-button>
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

    <!-- 课程详情对话框 -->
    <el-dialog
      title="课程详情"
      v-model="detailDialogVisible"
      width="600px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="课程名称">{{ courseDetail.courseName }}</el-descriptions-item>
        <el-descriptions-item label="课程描述">{{ courseDetail.courseDescription }}</el-descriptions-item>
        <el-descriptions-item label="指定教师">{{ courseDetail.teacherName }}</el-descriptions-item>
        <el-descriptions-item label="课程人数">{{ courseDetail.maxStudents }}人</el-descriptions-item>
        <el-descriptions-item label="当前人数">{{ courseDetail.currentStudents }}人</el-descriptions-item>
        <el-descriptions-item label="课程学时">{{ courseDetail.courseHours }}节</el-descriptions-item>
        <el-descriptions-item label="学年">{{ courseDetail.academicYear }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="courseDetail.status === 1 ? 'success' : 'danger'">
            {{ courseDetail.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(courseDetail.createTime) }}</el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加/编辑课程对话框 -->
    <el-dialog
      :title="isEdit ? '编辑课程' : '新增课程'"
      v-model="formDialogVisible"
      width="600px"
    >
      <el-form :model="courseForm" label-width="100px" ref="courseFormRef">
        <el-form-item label="课程名称" required>
          <el-input v-model="courseForm.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程描述" required>
          <el-input 
            v-model="courseForm.courseDescription" 
            type="textarea" 
            :rows="3"
            placeholder="请输入课程描述" 
          />
        </el-form-item>
        <el-form-item label="指定教师" required>
          <el-select v-model="courseForm.teacherId" placeholder="请选择教师" style="width: 100%">
            <el-option 
              v-for="teacher in teacherList" 
              :key="teacher.id" 
              :label="teacher.name" 
              :value="teacher.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="课程人数" required>
          <el-input-number 
            v-model="courseForm.maxStudents" 
            :min="1" 
            :max="500" 
            placeholder="请输入课程人数" 
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="课程学时" required>
          <el-select v-model="courseForm.courseHours" placeholder="请选择课程学时" style="width: 100%">
            <el-option label="16节" :value="16" />
            <el-option label="20节" :value="20" />
            <el-option label="24节" :value="24" />
            <el-option label="28节" :value="28" />
            <el-option label="32节" :value="32" />
            <el-option label="36节" :value="36" />
            <el-option label="40节" :value="40" />
            <el-option label="48节" :value="48" />
          </el-select>
        </el-form-item>
        <el-form-item label="学年" required>
          <el-select v-model="courseForm.academicYear" placeholder="请选择学年" style="width: 100%">
            <el-option label="2022-2023上期" value="2022-2023上期" />
            <el-option label="2022-2023下期" value="2022-2023下期" />
            <el-option label="2023-2024上期" value="2023-2024上期" />
            <el-option label="2023-2024下期" value="2023-2024下期" />
            <el-option label="2024-2025上期" value="2024-2025上期" />
            <el-option label="2024-2025下期" value="2024-2025下期" />
            <el-option label="2025-2026上期" value="2025-2026上期" />
            <el-option label="2025-2026下期" value="2025-2026下期" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="courseForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="formDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">
            {{ isEdit ? '更新' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourseList, createCourse, updateCourse, deleteCourse, getCourseDetail, getTeacherList } from '../api/course'

export default {
  name: 'CourseManagement',
  setup() {
    const loading = ref(false)
    const searchKeyword = ref('')
    const courseList = ref([])
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    
    const detailDialogVisible = ref(false)
    const courseDetail = reactive({})
    
    const formDialogVisible = ref(false)
    const isEdit = ref(false)
    const submitLoading = ref(false)
    const courseForm = reactive({
      courseName: '',
      courseDescription: '',
      teacherId: null,
      maxStudents: null,
      courseHours: null,
      academicYear: '',
      status: 1
    })
    
    const teacherList = ref([])
    
    // 获取课程列表
    const fetchCourseList = async () => {
      loading.value = true
      try {
        const response = await getCourseList({
          page: currentPage.value,
          size: pageSize.value,
          keyword: searchKeyword.value
        })
        
        if (response.code === 200) {
          courseList.value = response.data.records
          total.value = response.data.total
        }
      } catch (error) {
        console.error('获取课程列表失败:', error)
        ElMessage.error('获取课程列表失败')
      } finally {
        loading.value = false
      }
    }
    
    // 获取教师列表
    const fetchTeacherList = async () => {
      try {
        const response = await getTeacherList()
        if (response.code === 200) {
          teacherList.value = response.data
        }
      } catch (error) {
        console.error('获取教师列表失败:', error)
        ElMessage.error('获取教师列表失败')
      }
    }
    
    // 搜索课程
    const searchCourses = () => {
      currentPage.value = 1
      fetchCourseList()
    }
    
    // 分页处理
    const handleSizeChange = (val) => {
      pageSize.value = val
      fetchCourseList()
    }
    
    const handleCurrentChange = (val) => {
      currentPage.value = val
      fetchCourseList()
    }
    
    // 查看课程详情
    const viewCourse = (course) => {
      Object.assign(courseDetail, course)
      detailDialogVisible.value = true
    }
    
    // 打开创建对话框
    const openCreateDialog = () => {
      isEdit.value = false
      resetForm()
      formDialogVisible.value = true
    }
    
    // 编辑课程
    const editCourse = (course) => {
      isEdit.value = true
      Object.assign(courseForm, course)
      formDialogVisible.value = true
    }
    
    // 重置表单
    const resetForm = () => {
      courseForm.courseName = ''
      courseForm.courseDescription = ''
      courseForm.teacherId = null
      courseForm.maxStudents = null
      courseForm.courseHours = null
      courseForm.academicYear = ''
      courseForm.status = 1
    }
    
    // 提交表单
    const submitForm = async () => {
      if (!courseForm.courseName || !courseForm.courseDescription || 
          !courseForm.teacherId || !courseForm.maxStudents || 
          !courseForm.courseHours || !courseForm.academicYear) {
        ElMessage.error('请填写完整信息')
        return
      }
      
      submitLoading.value = true
      try {
        let response
        if (isEdit.value) {
          response = await updateCourse(courseForm.id, courseForm)
        } else {
          response = await createCourse(courseForm)
        }
        
        if (response.code === 200) {
          ElMessage.success(isEdit.value ? '课程更新成功' : '课程创建成功')
          formDialogVisible.value = false
          fetchCourseList()
        } else {
          ElMessage.error(response.message || '操作失败')
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
      } finally {
        submitLoading.value = false
      }
    }
    
    // 删除课程确认
    const deleteCourseConfirm = (course) => {
      ElMessageBox.confirm(
        `确定要删除课程"${course.courseName}"吗？`,
        '确认删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        deleteCourseHandler(course.id)
      })
    }
    
    // 删除课程
    const deleteCourseHandler = async (id) => {
      try {
        const response = await deleteCourse(id)
        if (response.code === 200) {
          ElMessage.success('课程删除成功')
          fetchCourseList()
        } else {
          ElMessage.error(response.message || '删除失败')
        }
      } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
      }
    }
    
    // 格式化日期
    const formatDate = (dateTime) => {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      return date.toLocaleString()
    }
    
    onMounted(() => {
      fetchCourseList()
      fetchTeacherList()
    })
    
    return {
      loading,
      searchKeyword,
      courseList,
      currentPage,
      pageSize,
      total,
      detailDialogVisible,
      courseDetail,
      formDialogVisible,
      isEdit,
      submitLoading,
      courseForm,
      teacherList,
      fetchCourseList,
      searchCourses,
      handleSizeChange,
      handleCurrentChange,
      viewCourse,
      openCreateDialog,
      editCourse,
      submitForm,
      deleteCourseConfirm,
      formatDate
    }
  }
}
</script>

<style scoped>
.course-management {
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
  gap: 15px;
}

.dialog-footer {
  text-align: right;
}
</style> 