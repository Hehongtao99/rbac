<template>
  <div class="schedule-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>课程表管理</span>
          <div class="header-controls">
            <el-select v-model="filterForm.academicYear" placeholder="选择学年" @change="loadSchedule" style="width: 150px; margin-right: 10px;">
              <el-option label="2024-2025" value="2024-2025"></el-option>
              <el-option label="2023-2024" value="2023-2024"></el-option>
            </el-select>
            <el-select v-model="filterForm.weekNumber" placeholder="选择周次" @change="loadWeeklySchedule" style="width: 120px; margin-right: 10px;">
              <el-option 
                v-for="week in 20" 
                :key="week" 
                :label="`第${week}周`" 
                :value="week">
              </el-option>
            </el-select>
            <el-button type="primary" @click="showQuickAdd = true">快速添加</el-button>
          </div>
        </div>
      </template>

      <!-- 网格状课程表 -->
      <div class="schedule-grid-container" v-if="filterForm.weekNumber">
        <div class="schedule-grid">
          <!-- 表头 -->
          <div class="grid-header">
            <div class="time-header">时间</div>
            <div class="day-header" v-for="day in weekDays" :key="day.value">
              {{ day.label }}
            </div>
          </div>

          <!-- 时间行 -->
          <div 
            v-for="timeSlot in timeSlots" 
            :key="timeSlot.timeSlot" 
            class="grid-row">
            
            <!-- 时间列 -->
            <div class="time-cell">
              <div class="time-slot-name">{{ timeSlot.slotName }}</div>
              <div class="time-range">{{ timeSlot.startTime }}-{{ timeSlot.endTime }}</div>
              <div class="time-period">{{ timeSlot.period }}</div>
            </div>

            <!-- 课程格子 -->
            <div 
              v-for="day in weekDays" 
              :key="`${timeSlot.timeSlot}-${day.value}`"
              class="course-cell"
              :class="{ 
                'has-course': getCellCourse(timeSlot.timeSlot, day.value)
              }"
              @click="handleCellClick(timeSlot.timeSlot, day.value)">
              
              <!-- 有课程时显示 -->
              <div v-if="getCellCourse(timeSlot.timeSlot, day.value)" class="course-content">
                <div class="course-name">{{ getCellCourse(timeSlot.timeSlot, day.value).courseName }}</div>
                <div class="course-time">{{ getDetailedTimeDisplay(timeSlot.timeSlot) }}</div>
                <div class="course-teacher">{{ getCellCourse(timeSlot.timeSlot, day.value).teacherName }}</div>
                <div class="course-actions">
                  <el-button type="text" size="small" @click.stop="editCourse(getCellCourse(timeSlot.timeSlot, day.value))">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                  <el-button type="text" size="small" @click.stop="removeCourse(getCellCourse(timeSlot.timeSlot, day.value))">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>

              <!-- 无课程时显示 -->
              <div v-else class="empty-content">
                <div class="add-hint">点击添加课程</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 提示信息 -->
      <div class="no-week-selected" v-else>
        <el-empty description="请选择周次查看课程表">
          <el-button type="primary" @click="filterForm.weekNumber = 1">查看第1周</el-button>
        </el-empty>
      </div>
    </el-card>

    <!-- 添加课程对话框 -->
    <el-dialog v-model="showAddDialog" title="添加课程" width="450px">
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="80px">
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="addForm.courseId" placeholder="请选择课程" style="width: 100%">
            <el-option 
              v-for="course in availableCourses" 
              :key="course.value"
              :label="course.label" 
              :value="course.value">
            </el-option>
          </el-select>
          <div style="margin-top: 5px; font-size: 12px; color: #999;">
            共找到 {{ availableCourses.length }} 门可选课程
          </div>
        </el-form-item>
        <el-form-item label="星期">
          <el-input :value="getDayName(addForm.dayOfWeek)" readonly />
        </el-form-item>
        <el-form-item label="时间段">
          <el-input :value="getTimeSlotName(addForm.timeSlot)" readonly />
        </el-form-item>
        <el-form-item label="周次">
          <el-input :value="`第${filterForm.weekNumber}周`" readonly />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="addSingleCourse" :loading="addLoading">添加课程</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 快速添加对话框 -->
    <el-dialog v-model="showQuickAdd" title="快速添加课程" width="500px">
      <el-form :model="quickAddForm" :rules="quickAddRules" ref="quickAddFormRef" label-width="80px">
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="quickAddForm.courseId" placeholder="请选择课程" style="width: 100%">
            <el-option 
              v-for="course in availableCourses" 
              :key="course.value"
              :label="course.label" 
              :value="course.value">
            </el-option>
          </el-select>
          <div style="margin-top: 5px; font-size: 12px; color: #999;">
            共找到 {{ availableCourses.length }} 门可选课程
          </div>
        </el-form-item>
        <el-form-item label="周次" prop="weekNumbers">
          <el-select v-model="quickAddForm.weekNumbers" multiple placeholder="请选择周次" style="width: 100%">
            <el-option 
              v-for="week in 20" 
              :key="week" 
              :label="`第${week}周`" 
              :value="week">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="星期" prop="dayOfWeek">
          <el-select v-model="quickAddForm.dayOfWeek" placeholder="请选择星期" style="width: 100%">
            <el-option 
              v-for="day in weekDays" 
              :key="day.value"
              :label="day.label" 
              :value="day.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="时间段" prop="timeSlot">
          <el-select v-model="quickAddForm.timeSlot" placeholder="请选择时间段" style="width: 100%">
            <el-option 
              v-for="slot in timeSlots" 
              :key="slot.timeSlot"
              :label="`${slot.slotName} (${slot.startTime}-${slot.endTime})`" 
              :value="slot.timeSlot">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showQuickAdd = false">取消</el-button>
          <el-button type="primary" @click="quickAddCourse" :loading="quickAddLoading">批量添加</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑课程对话框 -->
    <el-dialog v-model="showEditDialog" title="课程详情" width="400px">
      <el-form :model="editCourseForm" label-width="80px">
        <el-form-item label="课程名称">
          <el-input v-model="editCourseForm.courseName" readonly></el-input>
        </el-form-item>
        <el-form-item label="时间段">
          <el-input :value="getTimeSlotDisplay(editCourseForm.timeSlot)" readonly></el-input>
        </el-form-item>
        <el-form-item label="教师">
          <el-input v-model="editCourseForm.teacherName" readonly></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditDialog = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import scheduleApi from '../../api/schedule'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'ScheduleManagement',
  data() {
    return {
      showQuickAdd: false,
      showEditDialog: false,
      showAddDialog: false,
      quickAddLoading: false,
      addLoading: false,
      filterForm: {
        academicYear: '2024-2025',
        weekNumber: null
      },
      quickAddForm: {
        courseId: null,
        weekNumbers: [],
        dayOfWeek: null,
        timeSlot: null
      },
      editCourseForm: {
        id: null,
        courseName: '',
        timeSlot: null,
        teacherName: ''
      },
      addForm: {
        courseId: null,
        dayOfWeek: null,
        timeSlot: null
      },
      quickAddRules: {
        courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
        weekNumbers: [{ required: true, message: '请选择周次', trigger: 'change' }],
        dayOfWeek: [{ required: true, message: '请选择星期', trigger: 'change' }],
        timeSlot: [{ required: true, message: '请选择时间段', trigger: 'change' }]
      },
      addRules: {
        courseId: [{ required: true, message: '请选择课程', trigger: 'change' }]
      },
      weekDays: [
        { label: '周一', value: 1 },
        { label: '周二', value: 2 },
        { label: '周三', value: 3 },
        { label: '周四', value: 4 },
        { label: '周五', value: 5 },
        { label: '周六', value: 6 },
        { label: '周日', value: 7 }
      ],
      timeSlots: [],
      weeklyScheduleData: {},
      availableCourses: []
    }
  },
  computed: {
    currentUser() {
      return JSON.parse(localStorage.getItem('user') || '{}')
    }
  },
  created() {
    // 添加调试信息
    console.log('=== ScheduleManagement 页面加载 ===')
    const token = localStorage.getItem('token')
    const userInfo = localStorage.getItem('userInfo')
    console.log('localStorage中的token:', token ? `${token.substring(0, 20)}...` : 'null')
    console.log('localStorage中的userInfo:', userInfo)
    
    this.loadTimeSlots()
    this.loadAvailableCourses()
    // 默认选择第1周
    this.filterForm.weekNumber = 1
    this.loadWeeklySchedule()
  },
  methods: {
    async loadTimeSlots() {
      try {
        console.log('=== loadTimeSlots 开始 ===')
        const response = await scheduleApi.getAllTimeSlots()
        console.log('=== loadTimeSlots 响应 ===')
        console.log('完整响应:', response)
        if (response.code === 200) {
          this.timeSlots = response.data || []
          console.log('设置 timeSlots:', this.timeSlots)
        } else {
          console.log('时间段响应code不是200:', response.code, response.message)
        }
      } catch (error) {
        console.error('=== loadTimeSlots 异常 ===')
        console.error('异常详情:', error)
        console.error('异常响应:', error.response)
      }
    },

    async loadAvailableCourses() {
      try {
        console.log('=== loadAvailableCourses 开始 ===')
        console.log('参数 academicYear:', this.filterForm.academicYear)
        const response = await scheduleApi.getAvailableCourses(this.filterForm.academicYear)
        console.log('=== loadAvailableCourses 响应 ===')
        console.log('完整响应:', response)
        if (response.code === 200) {
          this.availableCourses = response.data || []
          console.log('设置 availableCourses:', this.availableCourses)
        } else {
          console.log('响应code不是200:', response.code, response.message)
        }
      } catch (error) {
        console.error('=== loadAvailableCourses 异常 ===')
        console.error('异常详情:', error)
        console.error('异常响应:', error.response)
      }
    },

    async loadWeeklySchedule() {
      if (!this.filterForm.weekNumber) return
      
      try {
        console.log('=== loadWeeklySchedule 开始 ===')
        const response = await scheduleApi.getWeeklySchedule(
          this.filterForm.academicYear, 
          this.filterForm.weekNumber
        )
        console.log('=== loadWeeklySchedule 响应 ===')
        console.log('完整响应:', response)
        if (response.code === 200) {
          this.weeklyScheduleData = response.data || {}
          console.log('设置 weeklyScheduleData:', this.weeklyScheduleData)
        } else {
          console.log('周课程表响应code不是200:', response.code, response.message)
        }
      } catch (error) {
        console.error('=== loadWeeklySchedule 异常 ===')
        console.error('异常详情:', error)
        console.error('异常响应:', error.response)
      }
    },

    loadSchedule() {
      this.loadAvailableCourses()
      this.loadWeeklySchedule()
    },

    getCellCourse(timeSlot, dayOfWeek) {
      const dayName = this.weekDays.find(d => d.value === dayOfWeek)?.label
      const daySchedules = this.weeklyScheduleData[dayName] || []
      return daySchedules.find(s => s.timeSlot === timeSlot)
    },

    handleCellClick(timeSlot, dayOfWeek) {
      // 如果这个格子没有课程，弹出添加对话框
      if (!this.getCellCourse(timeSlot, dayOfWeek)) {
        this.addForm = {
          courseId: null,
          dayOfWeek: dayOfWeek,
          timeSlot: timeSlot
        }
        this.showAddDialog = true
      }
    },

    async addSingleCourse() {
      try {
        await this.$refs.addFormRef.validate()
        this.addLoading = true

        const scheduleData = {
          courseId: this.addForm.courseId,
          weekNumber: this.filterForm.weekNumber,
          dayOfWeek: this.addForm.dayOfWeek,
          timeSlot: this.addForm.timeSlot,
          academicYear: this.filterForm.academicYear
        }

        // 检查时间冲突
        const conflictResponse = await scheduleApi.checkTimeConflict(
          this.filterForm.academicYear,
          this.filterForm.weekNumber,
          this.addForm.dayOfWeek,
          this.addForm.timeSlot
        )
        
        if (conflictResponse.data) {
          ElMessage.warning('该时间段已有课程安排')
          return
        }

        const response = await scheduleApi.addCourseToSchedule(scheduleData)
        if (response.code === 200) {
          ElMessage.success('添加成功')
          this.showAddDialog = false
          this.loadWeeklySchedule()
          this.loadAvailableCourses()
        }
      } catch (error) {
        console.error('添加课程失败:', error)
        ElMessage.error(error.response?.data?.msg || '添加失败')
      } finally {
        this.addLoading = false
      }
    },

    async quickAddCourse() {
      try {
        await this.$refs.quickAddFormRef.validate()
        this.quickAddLoading = true

        // 批量添加到多个周次
        const promises = this.quickAddForm.weekNumbers.map(weekNumber => {
          const scheduleData = {
            courseId: this.quickAddForm.courseId,
            weekNumber: weekNumber,
            dayOfWeek: this.quickAddForm.dayOfWeek,
            timeSlot: this.quickAddForm.timeSlot,
            academicYear: this.filterForm.academicYear
          }
          return scheduleApi.addCourseToSchedule(scheduleData)
        })

        await Promise.all(promises)
        ElMessage.success(`成功添加${this.quickAddForm.weekNumbers.length}个周次的课程`)
        this.showQuickAdd = false
        this.resetQuickAddForm()
        this.loadWeeklySchedule()
        this.loadAvailableCourses()
      } catch (error) {
        console.error('批量添加课程失败:', error)
        ElMessage.error('批量添加失败')
      } finally {
        this.quickAddLoading = false
      }
    },

    editCourse(schedule) {
      this.editCourseForm = {
        id: schedule.id,
        courseName: schedule.courseName,
        timeSlot: schedule.timeSlot,
        teacherName: schedule.teacherName
      }
      this.showEditDialog = true
    },

    async removeCourse(schedule) {
      try {
        await ElMessageBox.confirm('确定要删除这门课程安排吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const response = await scheduleApi.removeCourseFromSchedule(schedule.id)
        if (response.code === 200) {
          ElMessage.success('删除成功')
          this.loadWeeklySchedule()
          this.loadAvailableCourses()
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除课程失败:', error)
          ElMessage.error('删除失败')
        }
      }
    },

    resetQuickAddForm() {
      this.quickAddForm = {
        courseId: null,
        weekNumbers: [],
        dayOfWeek: null,
        timeSlot: null
      }
    },

    getTimeSlotDisplay(timeSlot) {
      const timeSlotObj = this.timeSlots.find(s => s.timeSlot === timeSlot)
      return timeSlotObj ? `${timeSlotObj.slotName} (${timeSlotObj.startTime}-${timeSlotObj.endTime})` : ''
    },

    getDetailedTimeDisplay(timeSlot) {
      const timeSlotObj = this.timeSlots.find(s => s.timeSlot === timeSlot)
      if (!timeSlotObj) return ''
      
      // 显示详细的小节时间，比如 8:00-8:45, 8:55-9:40
      const [startHour, startMin] = timeSlotObj.startTime.split(':').map(Number)
      const [endHour, endMin] = timeSlotObj.endTime.split(':').map(Number)
      
      // 计算中间休息时间
      const firstEnd = `${startHour}:${(startMin + 45).toString().padStart(2, '0')}`
      const secondStart = `${startHour}:${(startMin + 55).toString().padStart(2, '0')}`
      
      return `${timeSlotObj.startTime}-${firstEnd}, ${secondStart}-${timeSlotObj.endTime}`
    },

    getDayName(dayOfWeek) {
      const day = this.weekDays.find(d => d.value === dayOfWeek)
      return day ? day.label : ''
    },

    getTimeSlotName(timeSlot) {
      const timeSlotObj = this.timeSlots.find(s => s.timeSlot === timeSlot)
      return timeSlotObj ? `${timeSlotObj.slotName} (${timeSlotObj.startTime}-${timeSlotObj.endTime})` : ''
    }
  }
}
</script>

<style scoped>
.schedule-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-controls {
  display: flex;
  align-items: center;
}

.schedule-grid-container {
  margin-top: 20px;
  overflow-x: auto;
}

.schedule-grid {
  min-width: 1200px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  overflow: hidden;
}

.grid-header {
  display: grid;
  grid-template-columns: 150px repeat(7, 1fr);
  background: #f5f7fa;
  border-bottom: 2px solid #e4e7ed;
}

.time-header,
.day-header {
  padding: 15px 10px;
  font-weight: bold;
  text-align: center;
  border-right: 1px solid #e4e7ed;
  color: #303133;
}

.grid-row {
  display: grid;
  grid-template-columns: 150px repeat(7, 1fr);
  border-bottom: 1px solid #e4e7ed;
}

.grid-row:last-child {
  border-bottom: none;
}

.time-cell {
  padding: 15px 10px;
  background: #fafbfc;
  border-right: 1px solid #e4e7ed;
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.time-slot-name {
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.time-range {
  font-size: 12px;
  color: #909399;
  margin-bottom: 3px;
}

.time-period {
  font-size: 11px;
  color: #c0c4cc;
  padding: 2px 6px;
  background: #f0f0f0;
  border-radius: 10px;
  align-self: center;
}

.course-cell {
  position: relative;
  min-height: 90px;
  border-right: 1px solid #e4e7ed;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
}

.course-cell:hover {
  background: #f0f9ff;
}

.course-cell.has-course {
  background: #e1f5fe;
}

.course-cell.has-course:hover {
  background: #bbdefb;
}

.course-content {
  width: 100%;
  text-align: center;
  position: relative;
}

.course-name {
  font-weight: bold;
  color: #1976d2;
  margin-bottom: 4px;
  font-size: 13px;
}

.course-time {
  font-size: 10px;
  color: #666;
  margin-bottom: 4px;
  line-height: 1.2;
}

.course-teacher {
  font-size: 11px;
  color: #888;
  margin-bottom: 6px;
}

.course-actions {
  display: flex;
  justify-content: center;
  gap: 5px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.course-cell:hover .course-actions {
  opacity: 1;
}

.empty-content {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-hint {
  color: #c0c4cc;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.course-cell:hover .add-hint {
  opacity: 1;
}

.no-week-selected {
  text-align: center;
  padding: 40px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .schedule-grid {
    min-width: 1000px;
  }
  
  .grid-row {
    grid-template-columns: 120px repeat(7, 1fr);
  }
  
  .grid-header {
    grid-template-columns: 120px repeat(7, 1fr);
  }
}

@media (max-width: 768px) {
  .schedule-grid-container {
    margin-left: -20px;
    margin-right: -20px;
  }
  
  .schedule-grid {
    min-width: 800px;
  }
  
  .course-cell {
    min-height: 70px;
    font-size: 11px;
  }
  
  .time-cell {
    padding: 10px 5px;
  }
  
  .course-time {
    font-size: 9px;
  }
}
</style> 