<template>
  <div class="course-selection">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>学生选课</span>
          <!-- 调试信息 -->
          <div style="font-size: 12px; color: #999;">
            权限状态: {{ debugPermissions }}
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <!-- 可选课程 -->
        <el-tab-pane label="可选课程" name="available">
          <div class="course-list">
            <el-row :gutter="20">
              <el-col :span="24" v-if="availableCourses.length === 0">
                <el-empty description="暂无可选课程"></el-empty>
              </el-col>
              <el-col :span="12" v-for="course in availableCourses" :key="course.courseApplicationId">
                <el-card class="course-card" shadow="hover">
                  <div class="course-info">
                    <h3 class="course-name">{{ course.courseName }}</h3>
                    <div class="course-details">
                      <p><strong>任课教师：</strong>{{ course.teacherName }}</p>
                      <p><strong>学年学期：</strong>{{ course.academicYear }} {{ course.semester }}</p>
                      <p><strong>课程学时：</strong>{{ course.courseHours }}学时</p>
                      <p><strong>选课人数：</strong>{{ course.currentStudents }}/{{ course.maxStudents }}</p>
                    </div>
                    <div class="course-actions">
                      <el-button 
                        v-if="!course.isSelected" 
                        type="primary" 
                        @click="handleSelectCourse(course)"
                        :disabled="course.currentStudents >= course.maxStudents"
                      >
                        {{ course.currentStudents >= course.maxStudents ? '已满员' : '选课' }}
                      </el-button>
                      <el-button 
                        v-else-if="course.isSelected" 
                        type="success" 
                        disabled
                      >
                        已选择
                      </el-button>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>

        <!-- 已选课程 -->
        <el-tab-pane label="已选课程" name="selected">
          <div class="selected-courses">
            <el-table :data="selectedCourses" style="width: 100%">
              <el-table-column prop="courseName" label="课程名称" width="200"></el-table-column>
              <el-table-column prop="teacherName" label="任课教师" width="120"></el-table-column>
              <el-table-column prop="academicYear" label="学年" width="120"></el-table-column>
              <el-table-column prop="semester" label="学期" width="100"></el-table-column>
              <el-table-column prop="courseHours" label="学时" width="80"></el-table-column>
              <el-table-column prop="selectionTime" label="选课时间" width="180">
                <template #default="scope">
                  {{ formatDateTime(scope.row.selectionTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="scope">
                  <el-button 
                    type="danger" 
                    size="small" 
                    @click="handleDropCourse(scope.row)"
                  >
                    退课
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- 我的课程表 -->
        <el-tab-pane label="我的课程表" name="schedule">
          <div class="my-schedule">
            <el-table :data="scheduleData" style="width: 100%">
              <el-table-column prop="courseName" label="课程名称" width="200"></el-table-column>
              <el-table-column prop="teacherName" label="任课教师" width="120"></el-table-column>
              <el-table-column prop="weekNumber" label="周次" width="80"></el-table-column>
              <el-table-column prop="dayOfWeek" label="星期" width="80">
                <template #default="scope">
                  {{ getDayOfWeekText(scope.row.dayOfWeek) }}
                </template>
              </el-table-column>
              <el-table-column prop="timeRange" label="时间" width="150"></el-table-column>
              <el-table-column prop="academicYear" label="学年" width="120"></el-table-column>
              <el-table-column prop="semester" label="学期" width="100"></el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { getAvailableCourses, getSelectedCourses, selectCourse, dropCourse } from '@/api/courseSelection'
import { getStudentSchedule } from '@/api/schedule'
import { usePermissions } from '@/stores/permission'

export default {
  name: 'CourseSelection',
  setup() {
    const { hasPermission, getUserPermissions } = usePermissions()
    
    return {
      hasPermission,
      getUserPermissions
    }
  },
  data() {
    return {
      activeTab: 'available',
      availableCourses: [],
      selectedCourses: [],
      scheduleData: [],
      currentStudentId: null
    }
  },
  computed: {
    debugPermissions() {
      const permissions = this.getUserPermissions()
      return `权限数量: ${permissions.length}, 包含选课权限: ${permissions.includes('course-selection:view-available')}`
    }
  },
  mounted() {
    this.getCurrentStudentId()
    this.loadAvailableCourses()
    
    // 调试权限信息
    console.log('当前用户权限:', this.getUserPermissions())
    console.log('是否有查看可选课程权限:', this.hasPermission('course-selection:view-available'))
  },
  methods: {
    getCurrentStudentId() {
      // 从用户信息中获取学生ID，这里需要根据实际的用户管理逻辑来实现
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      this.currentStudentId = userInfo.studentId || 1 // 临时使用固定值
    },
    
    handleTabClick(tab) {
      if (tab.name === 'available') {
        this.loadAvailableCourses()
      } else if (tab.name === 'selected') {
        this.loadSelectedCourses()
      } else if (tab.name === 'schedule') {
        this.loadSchedule()
      }
    },
    
    async loadAvailableCourses() {
      try {
        const response = await getAvailableCourses(this.currentStudentId)
        this.availableCourses = response.data || []
        console.log('加载可选课程成功:', this.availableCourses.length)
      } catch (error) {
        this.$message.error('加载可选课程失败')
        console.error('加载可选课程失败:', error)
      }
    },
    
    async loadSelectedCourses() {
      try {
        const response = await getSelectedCourses(this.currentStudentId)
        this.selectedCourses = response.data || []
        console.log('加载已选课程成功:', this.selectedCourses.length)
      } catch (error) {
        this.$message.error('加载已选课程失败')
        console.error('加载已选课程失败:', error)
      }
    },
    
    async loadSchedule() {
      try {
        const response = await getStudentSchedule(this.currentStudentId)
        this.scheduleData = response.data || []
        console.log('加载课程表成功:', this.scheduleData.length)
      } catch (error) {
        this.$message.error('加载课程表失败')
        console.error('加载课程表失败:', error)
      }
    },
    
    async handleSelectCourse(course) {
      try {
        await this.$confirm('确认选择该课程吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await selectCourse({
          studentId: this.currentStudentId,
          courseApplicationId: course.courseApplicationId
        })
        
        this.$message.success('选课成功')
        this.loadAvailableCourses()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '选课失败')
        }
      }
    },
    
    async handleDropCourse(course) {
      try {
        await this.$confirm('确认退选该课程吗？退课后将从课程表中移除。', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await dropCourse(this.currentStudentId, course.courseApplicationId)
        
        this.$message.success('退课成功')
        this.loadSelectedCourses()
        this.loadAvailableCourses()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '退课失败')
        }
      }
    },
    
    formatDateTime(dateTime) {
      if (!dateTime) return ''
      return new Date(dateTime).toLocaleString('zh-CN')
    },
    
    getDayOfWeekText(dayOfWeek) {
      const days = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日']
      return days[dayOfWeek] || ''
    }
  }
}
</script>

// ... existing code ...