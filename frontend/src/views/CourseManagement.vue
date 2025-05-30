<template>
  <div class="course-management">
    <div class="page-header">
      <div style="display: flex; align-items: center;">
        <h2>开课实例管理</h2>
        <el-badge 
          v-if="hasActiveFilters" 
          :value="getActiveFilterCount()" 
          class="filter-badge"
          style="margin-left: 15px;"
        >
          <el-tag type="info" size="small">筛选中</el-tag>
        </el-badge>
      </div>
      <div class="header-actions">
        <el-select 
          v-model="courseFilter" 
          placeholder="选择课程" 
          style="width: 180px; margin-right: 15px" 
          clearable
          @change="searchCourses"
        >
          <el-option 
            v-for="course in availableCourses" 
            :key="course.value" 
            :label="course.label" 
            :value="course.value"
          />
        </el-select>
        <el-select 
          v-model="teacherFilter" 
          placeholder="选择教师" 
          style="width: 150px; margin-right: 15px" 
          clearable
          filterable
          @change="searchCourses"
        >
          <el-option 
            v-for="teacher in availableTeachers" 
            :key="teacher.value" 
            :label="teacher.label" 
            :value="teacher.value"
          />
        </el-select>
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
        <el-button @click="resetFilters">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
        <el-button @click="updateFilterOptions" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新选项
        </el-button>
      </div>
    </div>

    <!-- 筛选标签显示 -->
    <div v-if="hasActiveFilters" class="filter-tags" style="margin-bottom: 15px;">
      <span style="margin-right: 10px; color: #606266;">
        <el-icon><Filter /></el-icon>
        当前筛选 ({{ total }}条结果):
      </span>
      <el-tag 
        v-if="courseFilter" 
        closable 
        type="primary"
        @close="courseFilter = ''; searchCourses()"
        style="margin-right: 8px;"
      >
        课程: {{ getCourseLabel(courseFilter) }}
      </el-tag>
      <el-tag 
        v-if="teacherFilter" 
        closable 
        type="success"
        @close="teacherFilter = ''; searchCourses()"
        style="margin-right: 8px;"
      >
        教师: {{ getTeacherLabel(teacherFilter) }}
      </el-tag>
      <el-tag 
        v-if="searchKeyword" 
        closable 
        type="warning"
        @close="searchKeyword = ''; searchCourses()"
        style="margin-right: 8px;"
      >
        关键词: {{ searchKeyword }}
      </el-tag>
    </div>

    <!-- 开课实例列表 -->
    <el-table 
      :data="courseList" 
      border 
      stripe 
      v-loading="loading"
      style="width: 100%"
    >
      <el-table-column prop="courseName" label="课程名称" width="250" />
      <el-table-column prop="courseDescription" label="课程描述" width="200" show-overflow-tooltip />
      <el-table-column prop="teacherName" label="开课教师" width="120">
        <template #default="scope">
          <el-tag type="success">{{ scope.row.teacherName }}</el-tag>
        </template>
      </el-table-column>
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
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button type="primary" size="small" @click="viewCourse(scope.row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="margin-top: 20px; text-align: center">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 查看课程详情对话框 -->
    <el-dialog
      title="开课实例详情"
      v-model="detailDialogVisible"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="课程名称">{{ courseDetail.courseName }}</el-descriptions-item>
        <el-descriptions-item label="开课教师">
          <el-tag type="success">{{ courseDetail.teacherName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="课程人数">{{ courseDetail.maxStudents }}</el-descriptions-item>
        <el-descriptions-item label="当前人数">{{ courseDetail.currentStudents }}</el-descriptions-item>
        <el-descriptions-item label="课程学时">{{ courseDetail.courseHours }}节</el-descriptions-item>
        <el-descriptions-item label="学年">{{ courseDetail.academicYear }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="courseDetail.status === 1 ? 'success' : 'danger'">
            {{ courseDetail.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(courseDetail.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <el-descriptions :column="1" border style="margin-top: 20px">
        <el-descriptions-item label="课程描述">{{ courseDetail.courseDescription }}</el-descriptions-item>
      </el-descriptions>
      
      <div style="margin-top: 20px; text-align: center">
        <el-alert
          title="提示"
          description="开课实例由教师申请并通过管理员审批后自动生成，不可编辑或删除。如需修改，请联系申请教师。"
          type="info"
          :closable="false">
        </el-alert>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getCourseInstanceList } from '../api/course'

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
    
    const courseFilter = ref('')
    const teacherFilter = ref('')
    const availableCourses = ref([])
    const availableTeachers = ref([])
    
    // 计算是否有活跃的筛选条件
    const hasActiveFilters = computed(() => {
      return courseFilter.value || teacherFilter.value || searchKeyword.value
    })
    
    // 获取开课实例列表
    const fetchCourseList = async () => {
      loading.value = true
      try {
        const response = await getCourseInstanceList({
          page: currentPage.value,
          size: pageSize.value,
          keyword: searchKeyword.value,
          courseFilter: courseFilter.value,
          teacherFilter: teacherFilter.value
        })
        
        if (response.code === 200) {
          courseList.value = response.data.records
          total.value = response.data.total
          
          // 如果是首次加载或者没有筛选条件，更新筛选选项
          if (currentPage.value === 1 && !courseFilter.value && !teacherFilter.value && !searchKeyword.value) {
            updateFilterOptions()
          }
        }
      } catch (error) {
        console.error('获取开课实例列表失败:', error)
        ElMessage.error('获取开课实例列表失败')
      } finally {
        loading.value = false
      }
    }
    
    // 更新筛选选项
    const updateFilterOptions = async () => {
      try {
        // 获取所有开课实例（不分页）用于生成筛选选项
        const response = await getCourseInstanceList({
          page: 1,
          size: 1000 // 获取所有开课实例
        })
        
        if (response.code === 200) {
          // 从开课实例中提取唯一的课程名称
          const uniqueCourses = [...new Set(response.data.records.map(course => course.courseName))]
          availableCourses.value = uniqueCourses.map(courseName => ({
            label: courseName,
            value: courseName
          })).sort((a, b) => a.label.localeCompare(b.label))
          
          // 从开课实例中提取唯一的教师名称
          const uniqueTeachers = [...new Set(response.data.records
            .filter(course => course.teacherName) // 过滤掉空的教师名称
            .map(course => course.teacherName))]
          availableTeachers.value = uniqueTeachers.map(teacherName => ({
            label: teacherName,
            value: teacherName
          })).sort((a, b) => a.label.localeCompare(b.label))
        }
      } catch (error) {
        console.error('更新筛选选项失败:', error)
      }
    }
    
    // 获取课程选项（从已有开课实例中提取）
    const fetchCourseOptions = async () => {
      // 这个方法现在由 updateFilterOptions 替代，保留以防其他地方调用
      await updateFilterOptions()
    }
    
    // 获取教师选项（从已有开课实例中提取）
    const fetchTeacherOptions = async () => {
      // 这个方法现在由 updateFilterOptions 替代，保留以防其他地方调用  
      await updateFilterOptions()
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
    
    // 重置过滤器
    const resetFilters = () => {
      courseFilter.value = ''
      teacherFilter.value = ''
      searchKeyword.value = ''
      currentPage.value = 1
      fetchCourseList() // 这会自动更新筛选选项
    }
    
    // 获取课程标签
    const getCourseLabel = (value) => {
      const course = availableCourses.value.find(c => c.value === value)
      return course ? course.label : value
    }
    
    // 获取教师标签
    const getTeacherLabel = (value) => {
      const teacher = availableTeachers.value.find(t => t.value === value)
      return teacher ? teacher.label : value
    }
    
    // 格式化日期
    const formatDate = (dateTime) => {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      return date.toLocaleString()
    }
    
    // 获取活跃筛选条件的数量
    const getActiveFilterCount = () => {
      let count = 0
      if (courseFilter.value) count++
      if (teacherFilter.value) count++
      if (searchKeyword.value) count++
      return count
    }
    
    onMounted(() => {
      fetchCourseList() // 筛选选项会在此方法中自动更新
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
      courseFilter,
      teacherFilter,
      availableCourses,
      availableTeachers,
      hasActiveFilters,
      fetchCourseList,
      searchCourses,
      handleSizeChange,
      handleCurrentChange,
      viewCourse,
      formatDate,
      resetFilters,
      getCourseLabel,
      getTeacherLabel,
      updateFilterOptions,
      getActiveFilterCount
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
}

.header-actions {
  display: flex;
  align-items: center;
}

.filter-tags {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background-color: #f5f7fa;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.filter-tags .el-tag {
  margin-right: 8px;
}

.filter-tags .el-tag:last-child {
  margin-right: 0;
}

.filter-badge {
  display: inline-flex;
  align-items: center;
}

.filter-tags .el-icon {
  margin-right: 5px;
}

.header-actions .el-button + .el-button {
  margin-left: 0;
}
</style> 