<template>
  <div class="schedule-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>课程表管理</span>
          <div class="header-controls">
            <el-button type="primary" @click="showQuickAdd = true">快速添加</el-button>
          </div>
        </div>
      </template>

      <!-- 筛选条件 -->
      <div class="filter-section">
        <el-form :model="filterForm" inline>
          <el-form-item label="学年">
            <el-select v-model="filterForm.academicYear" placeholder="选择学年" @change="loadSchedule" style="width: 150px;">
              <el-option label="2024-2025" value="2024-2025"></el-option>
              <el-option label="2023-2024" value="2023-2024"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="班级">
            <el-select v-model="filterForm.classId" placeholder="选择班级" @change="loadSchedule" clearable style="width: 200px;">
              <el-option label="所有班级" :value="null"></el-option>
              <el-option 
                v-for="clazz in teacherClasses" 
                :key="clazz.id"
                :label="clazz.name" 
                :value="clazz.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="周次">
            <el-select v-model="filterForm.weekNumber" placeholder="选择周次" @change="loadSchedule" style="width: 120px;">
              <el-option 
                v-for="week in 20" 
                :key="week" 
                :label="`第${week}周`" 
                :value="week">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>

      <!-- 课程表网格 -->
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
                <div v-if="getCellCourse(timeSlot.timeSlot, day.value).className" class="course-class">{{ getCellCourse(timeSlot.timeSlot, day.value).className }}</div>
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
        <el-form-item label="班级" prop="classId">
          <el-select v-model="addForm.classId" placeholder="请选择班级" style="width: 100%" @change="onClassChange">
            <el-option 
              v-for="clazz in teacherClasses" 
              :key="clazz.id"
              :label="clazz.name" 
              :value="clazz.id">
            </el-option>
          </el-select>
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
        <el-form-item label="班级" prop="classId">
          <el-select v-model="quickAddForm.classId" placeholder="请选择班级" style="width: 100%" @change="onQuickAddClassChange">
            <el-option 
              v-for="clazz in teacherClasses" 
              :key="clazz.id"
              :label="clazz.name" 
              :value="clazz.id">
            </el-option>
          </el-select>
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
        <el-form-item label="班级" v-if="editCourseForm.className">
          <el-input v-model="editCourseForm.className" readonly></el-input>
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
        weekNumber: null,
        classId: null
      },
      quickAddForm: {
        courseId: null,
        classId: null,
        weekNumbers: [],
        dayOfWeek: null,
        timeSlot: null
      },
      editCourseForm: {
        id: null,
        courseName: '',
        timeSlot: null,
        teacherName: '',
        className: ''
      },
      addForm: {
        courseId: null,
        classId: null,
        dayOfWeek: null,
        timeSlot: null
      },
      quickAddRules: {
        courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
        classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
        weekNumbers: [{ required: true, message: '请选择周次', trigger: 'change' }],
        dayOfWeek: [{ required: true, message: '请选择星期', trigger: 'change' }],
        timeSlot: [{ required: true, message: '请选择时间段', trigger: 'change' }]
      },
      addRules: {
        courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
        classId: [{ required: true, message: '请选择班级', trigger: 'change' }]
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
      scheduleData: {},
      availableCourses: [],
      teacherClasses: []
    }
  },
  created() {
    this.loadTimeSlots()
    this.loadTeacherClasses()
    this.loadAvailableCourses()
  },
  methods: {
    async loadTimeSlots() {
      try {
        const response = await scheduleApi.getAllTimeSlots()
        if (response.code === 200) {
          this.timeSlots = response.data || []
        }
      } catch (error) {
        console.error('获取时间段失败:', error)
      }
    },

    async loadTeacherClasses() {
      try {
        const response = await scheduleApi.getTeacherClasses()
        if (response.code === 200) {
          this.teacherClasses = response.data || []
          console.log('教师班级列表:', this.teacherClasses)
        }
      } catch (error) {
        console.error('获取教师班级失败:', error)
        ElMessage.error('获取班级列表失败')
      }
    },

    async loadAvailableCourses(classId = null) {
      try {
        let response
        if (classId) {
          // 如果指定了班级，获取该班级的可用课程（显示该班级的剩余课时）
          response = await scheduleApi.getAvailableCoursesForClass(this.filterForm.academicYear, classId)
        } else {
          // 否则获取所有可用课程（显示总课时）
          response = await scheduleApi.getAvailableCourses(this.filterForm.academicYear)
        }
        
        if (response.code === 200) {
          this.availableCourses = response.data || []
        }
      } catch (error) {
        console.error('获取可选课程失败:', error)
      }
    },

    async loadSchedule() {
      if (!this.filterForm.weekNumber) return
      
      try {
        const response = await scheduleApi.getWeeklyScheduleByClass(
          this.filterForm.academicYear, 
          this.filterForm.weekNumber,
          this.filterForm.classId
        )
        if (response.code === 200) {
          this.scheduleData = response.data || {}
        }
      } catch (error) {
        console.error('获取课程表失败:', error)
        ElMessage.error('获取课程表失败')
      }
    },

    getCellCourse(timeSlot, dayOfWeek) {
      const dayName = this.weekDays.find(d => d.value === dayOfWeek)?.label
      const daySchedules = this.scheduleData[dayName] || []
      return daySchedules.find(s => s.timeSlot === timeSlot)
    },

    handleCellClick(timeSlot, dayOfWeek) {
      if (this.teacherClasses.length === 0) {
        ElMessage.warning('您还没有分配到任何班级，无法添加课程')
        return
      }
      
      const existingCourse = this.getCellCourse(timeSlot, dayOfWeek)
      if (existingCourse) {
        this.editCourse(existingCourse)
      } else {
        this.addForm.dayOfWeek = dayOfWeek
        this.addForm.timeSlot = timeSlot
        this.addForm.courseId = null
        this.addForm.classId = this.filterForm.classId || null
        this.showAddDialog = true
        
        // 当打开添加对话框时，如果已选择班级，则加载该班级的可用课程
        if (this.addForm.classId) {
          this.loadAvailableCourses(this.addForm.classId)
        }
      }
    },

    async addSingleCourse() {
      if (!this.$refs.addFormRef) return
      
      try {
        await this.$refs.addFormRef.validate()
        
        this.addLoading = true
        
        const selectedClass = this.teacherClasses.find(c => c.id === this.addForm.classId)
        const selectedCourse = this.availableCourses.find(c => c.value === this.addForm.courseId)
        
        const data = {
          courseId: this.addForm.courseId,
          courseName: selectedCourse?.courseName || '',
          classId: this.addForm.classId,
          className: selectedClass?.name || '',
          academicYear: this.filterForm.academicYear,
          weekNumber: this.filterForm.weekNumber,
          dayOfWeek: this.addForm.dayOfWeek,
          timeSlot: this.addForm.timeSlot,
          reducedHours: 1
        }
        
        const response = await scheduleApi.addCourseToSchedule(data)
        if (response.code === 200) {
          ElMessage.success('添加课程成功')
          this.showAddDialog = false
          this.loadSchedule()
          // 重新加载当前班级的可用课程
          if (this.filterForm.classId) {
            this.loadAvailableCourses(this.filterForm.classId)
          } else {
            this.loadAvailableCourses()
          }
        } else {
          ElMessage.error(response.message || '添加课程失败')
        }
      } catch (error) {
        console.error('添加课程失败:', error)
        ElMessage.error(error.message || '添加课程失败')
      } finally {
        this.addLoading = false
      }
    },

    async quickAddCourse() {
      if (!this.$refs.quickAddFormRef) return
      
      try {
        await this.$refs.quickAddFormRef.validate()
        
        this.quickAddLoading = true
        
        const selectedClass = this.teacherClasses.find(c => c.id === this.quickAddForm.classId)
        const selectedCourse = this.availableCourses.find(c => c.value === this.quickAddForm.courseId)
        
        const promises = this.quickAddForm.weekNumbers.map(weekNumber => {
          const data = {
            courseId: this.quickAddForm.courseId,
            courseName: selectedCourse?.courseName || '',
            classId: this.quickAddForm.classId,
            className: selectedClass?.name || '',
            academicYear: this.filterForm.academicYear,
            weekNumber: weekNumber,
            dayOfWeek: this.quickAddForm.dayOfWeek,
            timeSlot: this.quickAddForm.timeSlot,
            reducedHours: 1
          }
          return scheduleApi.addCourseToSchedule(data)
        })
        
        await Promise.all(promises)
        
        ElMessage.success(`成功添加 ${this.quickAddForm.weekNumbers.length} 个课程`)
        this.showQuickAdd = false
        this.loadSchedule()
        // 重新加载当前班级的可用课程
        if (this.filterForm.classId) {
          this.loadAvailableCourses(this.filterForm.classId)
        } else {
          this.loadAvailableCourses()
        }
        
      } catch (error) {
        console.error('批量添加课程失败:', error)
        ElMessage.error(error.message || '批量添加课程失败')
      } finally {
        this.quickAddLoading = false
      }
    },

    editCourse(course) {
      this.editCourseForm = { ...course }
      this.showEditDialog = true
    },

    async removeCourse(course) {
      try {
        await ElMessageBox.confirm('确定要删除这个课程吗？', '确认删除', {
          type: 'warning'
        })
        
        const response = await scheduleApi.removeCourseFromSchedule(course.id)
        if (response.code === 200) {
          ElMessage.success('删除课程成功')
          this.loadSchedule()
          // 重新加载当前班级的可用课程
          if (this.filterForm.classId) {
            this.loadAvailableCourses(this.filterForm.classId)
          } else {
            this.loadAvailableCourses()
          }
        } else {
          ElMessage.error(response.message || '删除课程失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除课程失败:', error)
          ElMessage.error('删除课程失败')
        }
      }
    },

    onClassChange() {
      const selectedClass = this.teacherClasses.find(c => c.id === this.addForm.classId)
      if (selectedClass) {
        this.addForm.className = selectedClass.name
      }
      
      // 重置课程选择
      this.addForm.courseId = null
      
      // 重新加载该班级的可用课程
      if (this.addForm.classId) {
        this.loadAvailableCourses(this.addForm.classId)
      } else {
        this.loadAvailableCourses()
      }
    },

    onQuickAddClassChange() {
      const selectedClass = this.teacherClasses.find(c => c.id === this.quickAddForm.classId)
      if (selectedClass) {
        this.quickAddForm.className = selectedClass.name
      }
      
      // 重置课程选择
      this.quickAddForm.courseId = null
      
      // 重新加载该班级的可用课程
      if (this.quickAddForm.classId) {
        this.loadAvailableCourses(this.quickAddForm.classId)
      } else {
        this.loadAvailableCourses()
      }
    },

    getDayName(dayOfWeek) {
      const day = this.weekDays.find(d => d.value === dayOfWeek)
      return day ? day.label : ''
    },

    getTimeSlotName(timeSlot) {
      const slot = this.timeSlots.find(s => s.timeSlot === timeSlot)
      return slot ? `${slot.slotName} (${slot.startTime}-${slot.endTime})` : ''
    },

    getDetailedTimeDisplay(timeSlot) {
      const slot = this.timeSlots.find(s => s.timeSlot === timeSlot)
      return slot ? `${slot.startTime}-${slot.endTime}` : ''
    },

    getTimeSlotDisplay(timeSlot) {
      const slot = this.timeSlots.find(s => s.timeSlot === timeSlot)
      return slot ? `第${timeSlot}节 ${slot.startTime}-${slot.endTime}` : `第${timeSlot}节`
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

.filter-section {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 6px;
}

.schedule-grid-container {
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
  font-size: 10px;
  color: #c0c4cc;
}

.course-cell {
  position: relative;
  min-height: 120px;
  border-right: 1px solid #e4e7ed;
  padding: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.course-cell:hover {
  background-color: #f0f9ff;
}

.course-cell.has-course {
  background-color: #e3f2fd;
}

.course-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.course-name {
  font-weight: bold;
  color: #1976d2;
  font-size: 13px;
  margin-bottom: 4px;
}

.course-time {
  font-size: 11px;
  color: #666;
  margin-bottom: 2px;
}

.course-teacher {
  font-size: 11px;
  color: #666;
  margin-bottom: 2px;
}

.course-class {
  font-size: 10px;
  color: #999;
  margin-bottom: 4px;
}

.course-actions {
  display: flex;
  gap: 4px;
  margin-top: auto;
}

.empty-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  font-size: 12px;
}

.add-hint {
  text-align: center;
}

.no-week-selected {
  text-align: center;
  padding: 40px;
}

@media (max-width: 1200px) {
  .schedule-grid {
    min-width: 1000px;
  }
}
</style> 