<template>
  <div class="schedule-management">
    <!-- 页面加载状态 -->
    <div v-if="pageLoading" class="loading-container" v-loading="true" element-loading-text="正在加载课程表数据...">
      <div style="height: 400px;"></div>
    </div>

    <!-- 主要内容 -->
    <el-card v-else class="box-card">
      <template #header>
        <div class="card-header">
          <span>课程表管理 
            <span v-if="filterForm.weekNumber" class="header-info">
              - {{ filterForm.academicYear }} 第{{ filterForm.weekNumber }}周
              <span v-if="filterForm.classId">
                ({{ getSelectedClassName() }})
              </span>
              <span v-else>(所有班级)</span>
            </span>
          </span>
          <div class="header-controls">
            <el-button type="primary" @click="showQuickAdd = true">快速添加</el-button>
          </div>
        </div>
      </template>

      <!-- 筛选条件 -->
      <div class="filter-section">
        <el-form :model="filterForm" inline>
          <el-form-item label="学年">
            <el-select v-model="filterForm.academicYear" placeholder="选择学年" @change="onFilterChange" style="width: 150px;">
              <el-option label="2024-2025" value="2024-2025"></el-option>
              <el-option label="2023-2024" value="2023-2024"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="班级">
            <el-select v-model="filterForm.classId" placeholder="选择班级" @change="onFilterChange" clearable style="width: 200px;">
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
            <el-select v-model="filterForm.weekNumber" placeholder="选择周次" @change="onFilterChange" style="width: 120px;">
              <el-option 
                v-for="week in 20" 
                :key="week" 
                :label="`第${week}周`" 
                :value="week">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        
        <!-- 班级颜色图例 -->
        <div v-if="filterForm.classId === null && teacherClasses.length > 0" class="class-legend">
          <div class="legend-title">班级颜色图例：</div>
          <div class="legend-items">
            <div 
              v-for="clazz in teacherClasses" 
              :key="clazz.id"
              class="legend-item">
              <div 
                class="legend-color" 
                :style="{ backgroundColor: getClassColor(clazz.id) }">
              </div>
              <span class="legend-text">{{ clazz.name }}</span>
            </div>
          </div>
        </div>
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
                'has-course': filterForm.classId ? getCellCourse(timeSlot.timeSlot, day.value) : getCellCourses(timeSlot.timeSlot, day.value).length > 0,
                'has-other-class': filterForm.classId && hasOtherClassCourse(timeSlot.timeSlot, day.value),
                'disabled': filterForm.classId && hasOtherClassCourse(timeSlot.timeSlot, day.value),
                'drag-over': isDragging
              }"
              @click="handleCellClick(timeSlot.timeSlot, day.value)"
              @dragover.prevent="handleDragOver"
              @drop="handleDrop($event, timeSlot.timeSlot, day.value)">
              
              <!-- 选择特定班级时显示单个课程 -->
              <div v-if="filterForm.classId && getCellCourse(timeSlot.timeSlot, day.value)" 
                   class="course-content"
                   :draggable="getCellCourse(timeSlot.timeSlot, day.value).classId === filterForm.classId"
                   @dragstart="handleDragStart($event, getCellCourse(timeSlot.timeSlot, day.value))"
                   @dragend="handleDragEnd">
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
                <div v-if="getCellCourse(timeSlot.timeSlot, day.value).classId === filterForm.classId" class="drag-handle">⋮⋮</div>
              </div>

              <!-- 选择特定班级时，显示其他班级的课程（淡化显示） -->
              <div v-else-if="filterForm.classId && hasOtherClassCourse(timeSlot.timeSlot, day.value)" class="other-class-content">
                <div 
                  v-for="course in getOtherClassCourses(timeSlot.timeSlot, day.value)" 
                  :key="course.id"
                  class="other-course-item"
                  :style="{ backgroundColor: getClassColor(course.classId) }">
                  <div class="course-name">{{ course.courseName }}</div>
                  <div class="course-class">{{ course.className }}</div>
                  <div class="course-teacher">{{ course.teacherName }}</div>
                </div>
                <div class="disabled-overlay">
                  <div class="disabled-text">时间冲突</div>
                </div>
              </div>

              <!-- 选择所有班级时显示多个课程 -->
              <div v-else-if="!filterForm.classId && getCellCourses(timeSlot.timeSlot, day.value).length > 0" class="multiple-courses">
                <div 
                  v-for="course in getCellCourses(timeSlot.timeSlot, day.value)" 
                  :key="course.id"
                  class="course-item"
                  :style="{ backgroundColor: getClassColor(course.classId) }"
                  draggable="true"
                  @dragstart="handleDragStart($event, course)"
                  @dragend="handleDragEnd"
                  @click.stop="editCourse(course)">
                  <div class="course-name">{{ course.courseName }}</div>
                  <div class="course-class">{{ course.className }}</div>
                  <div class="course-actions">
                    <el-button type="text" size="small" @click.stop="editCourse(course)">
                      <el-icon><Edit /></el-icon>
                    </el-button>
                    <el-button type="text" size="small" @click.stop="removeCourse(course)">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                  <div class="drag-handle">⋮⋮</div>
                </div>
              </div>

              <!-- 无课程时显示 -->
              <div v-else class="empty-content">
                <div class="add-hint">点击添加课程</div>
                <div v-if="isDragging" class="drop-hint">拖拽到此处</div>
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
import { Edit, Delete } from '@element-plus/icons-vue'
import draggable from 'vuedraggable'

export default {
  name: 'ScheduleManagement',
  components: {
    Edit,
    Delete,
    draggable
  },
  data() {
    return {
      showQuickAdd: false,
      showEditDialog: false,
      showAddDialog: false,
      quickAddLoading: false,
      addLoading: false,
      pageLoading: true, // 页面加载状态
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
      teacherClasses: [],
      // 班级颜色映射
      classColors: [
        '#e3f2fd', // 浅蓝色
        '#f3e5f5', // 浅紫色
        '#e8f5e8', // 浅绿色
        '#fff3e0', // 浅橙色
        '#fce4ec', // 浅粉色
        '#e0f2f1', // 浅青色
        '#f9fbe7', // 浅黄绿色
        '#fff8e1', // 浅黄色
        '#f1f8e9', // 浅草绿色
        '#e8eaf6'  // 浅靛蓝色
      ],
      classColorMap: {}, // 班级ID到颜色的映射
      // 拖拽相关
      dragOptions: {
        animation: 200,
        group: 'schedule',
        disabled: false,
        ghostClass: 'ghost'
      },
      isDragging: false,
      draggedCourse: null
    }
  },
  created() {
    this.initializeData()
  },
  beforeUnmount() {
    // 页面销毁前保存当前查看位置
    this.saveViewPosition()
  },
  methods: {
    // 初始化数据
    async initializeData() {
      try {
        this.pageLoading = true
        
        // 恢复上次查看的位置
        this.restoreViewPosition()
        
        // 加载基础数据
        await Promise.all([
          this.loadTimeSlots(),
          this.loadTeacherClasses(),
          this.loadAvailableCourses()
        ])
        
        // 如果没有设置周次，默认设置为当前周
        if (!this.filterForm.weekNumber) {
          this.filterForm.weekNumber = this.getCurrentWeek()
        }
        
        // 自动加载课程表数据
        await this.loadSchedule()
      } catch (error) {
        console.error('初始化数据失败:', error)
        ElMessage.error('加载数据失败，请刷新页面重试')
      } finally {
        this.pageLoading = false
      }
    },

    // 获取当前周次（可以根据实际需求调整逻辑）
    getCurrentWeek() {
      // 这里可以根据当前日期计算当前是第几周
      // 假设学期开始时间为9月1日，可以根据实际情况调整
      const now = new Date()
      const currentYear = now.getFullYear()
      const currentMonth = now.getMonth() + 1 // 月份从0开始，需要+1
      
      // 简单的学期周次计算逻辑
      // 如果是9月到次年1月，按秋季学期计算
      // 如果是2月到7月，按春季学期计算
      let semesterStart
      if (currentMonth >= 9 || currentMonth <= 1) {
        // 秋季学期：9月第一周为第1周
        semesterStart = new Date(currentMonth >= 9 ? currentYear : currentYear - 1, 8, 1) // 9月1日
      } else {
        // 春季学期：2月第一周为第1周
        semesterStart = new Date(currentYear, 1, 1) // 2月1日
      }
      
      // 计算周差
      const timeDiff = now.getTime() - semesterStart.getTime()
      const weekDiff = Math.floor(timeDiff / (7 * 24 * 60 * 60 * 1000)) + 1
      
      // 限制在1-20周范围内
      const week = Math.max(1, Math.min(20, weekDiff))
      
      return week
    },

    // 保存当前查看位置到本地存储
    saveViewPosition() {
      const viewPosition = {
        academicYear: this.filterForm.academicYear,
        weekNumber: this.filterForm.weekNumber,
        classId: this.filterForm.classId,
        timestamp: Date.now()
      }
      localStorage.setItem('scheduleViewPosition', JSON.stringify(viewPosition))
    },

    // 恢复上次查看的位置
    restoreViewPosition() {
      try {
        const saved = localStorage.getItem('scheduleViewPosition')
        if (saved) {
          const viewPosition = JSON.parse(saved)
          // 检查保存的数据是否在24小时内（可选）
          const isRecent = Date.now() - viewPosition.timestamp < 24 * 60 * 60 * 1000
          if (isRecent) {
            this.filterForm.academicYear = viewPosition.academicYear || '2024-2025'
            this.filterForm.weekNumber = viewPosition.weekNumber
            this.filterForm.classId = viewPosition.classId
          }
        }
      } catch (error) {
        console.error('恢复查看位置失败:', error)
      }
    },

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
          // 为每个班级分配颜色
          this.assignClassColors()
        }
      } catch (error) {
        console.error('获取教师班级失败:', error)
        ElMessage.error('获取班级列表失败')
      }
    },

    // 为班级分配颜色
    assignClassColors() {
      this.classColorMap = {}
      this.teacherClasses.forEach((clazz, index) => {
        this.classColorMap[clazz.id] = this.classColors[index % this.classColors.length]
      })
    },

    // 获取班级对应的颜色
    getClassColor(classId) {
      return this.classColorMap[classId] || '#e3f2fd'
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
        // 当选择特定班级时，需要先加载所有班级的数据以显示其他班级的课程
        if (this.filterForm.classId) {
          // 先加载所有班级的课程数据
          const allResponse = await scheduleApi.getWeeklyScheduleByClass(
            this.filterForm.academicYear, 
            this.filterForm.weekNumber,
            null // 获取所有班级
          )
          if (allResponse.code === 200) {
            this.scheduleData = allResponse.data || {}
          }
        } else {
          // 选择所有班级时，直接加载所有数据
          const response = await scheduleApi.getWeeklyScheduleByClass(
            this.filterForm.academicYear, 
            this.filterForm.weekNumber,
            this.filterForm.classId
          )
          if (response.code === 200) {
            this.scheduleData = response.data || {}
          }
        }
        
        // 加载完成后保存当前查看位置
        this.saveViewPosition()
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

    // 获取某个时间段的所有课程（用于显示所有班级时）
    getCellCourses(timeSlot, dayOfWeek) {
      const dayName = this.weekDays.find(d => d.value === dayOfWeek)?.label
      const daySchedules = this.scheduleData[dayName] || []
      return daySchedules.filter(s => s.timeSlot === timeSlot)
    },

    // 检查是否有其他班级的课程
    hasOtherClassCourse(timeSlot, dayOfWeek) {
      if (!this.filterForm.classId) return false
      const dayName = this.weekDays.find(d => d.value === dayOfWeek)?.label
      const daySchedules = this.scheduleData[dayName] || []
      const otherClassCourses = daySchedules.filter(s => s.timeSlot === timeSlot && s.classId !== this.filterForm.classId)
      return otherClassCourses.length > 0
    },

    // 获取其他班级的课程
    getOtherClassCourses(timeSlot, dayOfWeek) {
      if (!this.filterForm.classId) return []
      const dayName = this.weekDays.find(d => d.value === dayOfWeek)?.label
      const daySchedules = this.scheduleData[dayName] || []
      return daySchedules.filter(s => s.timeSlot === timeSlot && s.classId !== this.filterForm.classId)
    },

    async handleCellClick(timeSlot, dayOfWeek) {
      if (this.teacherClasses.length === 0) {
        ElMessage.warning('您还没有分配到任何班级，无法添加课程')
        return
      }
      
      // 检查该时间段是否已有课程
      if (this.filterForm.classId === null) {
        // 选择所有班级时，检查是否有任何班级在该时间段有课程
        const existingCourses = this.getCellCourses(timeSlot, dayOfWeek)
        if (existingCourses.length > 0) {
          ElMessage.warning('该时间段已有课程安排，无法添加新课程')
          return
        }
        // 选择所有班级时，打开添加对话框但不预选班级，强制用户选择
        this.addForm.classId = null
      } else {
        // 选择特定班级时，首先检查是否有其他班级的课程
        if (this.hasOtherClassCourse(timeSlot, dayOfWeek)) {
          ElMessage.warning('该时间段已有其他班级的课程，无法添加')
          return
        }
        
        // 检查该班级是否有课程
        const existingCourse = this.getCellCourse(timeSlot, dayOfWeek)
        if (existingCourse) {
          this.editCourse(existingCourse)
          return
        }
        // 预选当前筛选的班级
        this.addForm.classId = this.filterForm.classId
      }
      
      // 打开添加课程对话框
      this.addForm.dayOfWeek = dayOfWeek
      this.addForm.timeSlot = timeSlot
      this.addForm.courseId = null
      this.showAddDialog = true
      
      // 当打开添加对话框时，如果已选择班级，则加载该班级的可用课程
      if (this.addForm.classId) {
        this.loadAvailableCourses(this.addForm.classId)
      }
    },

    async addSingleCourse() {
      if (!this.$refs.addFormRef) return
      
      try {
        await this.$refs.addFormRef.validate()
        
        // 在添加前再次检查冲突
        const conflictResponse = await scheduleApi.checkTimeConflict(
          this.filterForm.academicYear,
          this.filterForm.weekNumber,
          this.addForm.dayOfWeek,
          this.addForm.timeSlot,
          this.addForm.classId
        )
        
        if (conflictResponse.code === 200 && conflictResponse.data === true) {
          ElMessage.error('该时间段已有课程安排，无法添加')
          return
        }
        
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
        
        const promises = this.quickAddForm.weekNumbers.map(async weekNumber => {
          // 检查每个周次的冲突
          const conflictResponse = await scheduleApi.checkTimeConflict(
            this.filterForm.academicYear,
            weekNumber,
            this.quickAddForm.dayOfWeek,
            this.quickAddForm.timeSlot,
            this.quickAddForm.classId
          )
          
          if (conflictResponse.code === 200 && conflictResponse.data === true) {
            throw new Error(`第${weekNumber}周该时间段已有课程安排`)
          }
          
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
    },

    onFilterChange() {
      // 筛选条件改变时，重新加载课程表并保存位置
      this.loadSchedule()
    },

    getSelectedClassName() {
      const selectedClass = this.teacherClasses.find(c => c.id === this.filterForm.classId)
      return selectedClass ? selectedClass.name : '所有班级'
    },

    handleDragStart(event, course) {
      // 如果选择了特定班级，只允许拖拽当前班级的课程
      if (this.filterForm.classId && course.classId !== this.filterForm.classId) {
        event.preventDefault()
        ElMessage.warning('只能拖拽当前班级的课程')
        return
      }
      
      this.isDragging = true
      this.draggedCourse = course
      event.dataTransfer.setData('text/plain', JSON.stringify(course))
      event.dataTransfer.effectAllowed = 'move'
      
      // 添加拖拽样式
      event.target.classList.add('dragging')
    },

    handleDragEnd(event) {
      this.isDragging = false
      this.draggedCourse = null
      
      // 移除拖拽样式
      event.target.classList.remove('dragging')
    },

    handleDragOver(event) {
      event.preventDefault()
      event.dataTransfer.dropEffect = 'move'
    },

    async handleDrop(event, timeSlot, dayOfWeek) {
      event.preventDefault()
      
      if (!this.draggedCourse) return
      
      const course = this.draggedCourse
      
      // 检查是否拖拽到相同位置
      if (course.timeSlot === timeSlot && course.dayOfWeek === dayOfWeek) {
        this.isDragging = false
        this.draggedCourse = null
        return
      }
      
      try {
        // 检查目标位置是否有冲突
        const hasConflict = await this.checkDropConflict(timeSlot, dayOfWeek, course)
        if (hasConflict) {
          ElMessage.warning('目标时间段已有课程，无法移动')
          this.isDragging = false
          this.draggedCourse = null
          return
        }
        
        // 执行课程移动
        await this.moveCourse(course, timeSlot, dayOfWeek)
        
        ElMessage.success('课程移动成功')
        
        // 重新加载课程表
        await this.loadSchedule()
        
      } catch (error) {
        console.error('移动课程失败:', error)
        ElMessage.error(error.message || '移动课程失败')
      } finally {
        this.isDragging = false
        this.draggedCourse = null
      }
    },

    async checkDropConflict(timeSlot, dayOfWeek, draggedCourse) {
      try {
        // 检查目标位置是否有其他课程
        const response = await scheduleApi.checkTimeConflict(
          this.filterForm.academicYear,
          this.filterForm.weekNumber,
          dayOfWeek,
          timeSlot,
          draggedCourse.classId
        )
        
        return response.code === 200 && response.data === true
      } catch (error) {
        console.error('检查冲突失败:', error)
        return true // 出错时认为有冲突，保险起见
      }
    },

    async moveCourse(course, newTimeSlot, newDayOfWeek) {
      try {
        // 调用后端API更新课程时间
        const updateData = {
          academicYear: course.academicYear,
          weekNumber: course.weekNumber,
          dayOfWeek: newDayOfWeek,
          timeSlot: newTimeSlot,
          classId: course.classId,
          className: course.className
        }
        
        const response = await scheduleApi.updateSchedule(course.id, updateData)
        
        if (response.code !== 200) {
          throw new Error(response.message || '更新课程失败')
        }
        
        return response
      } catch (error) {
        throw error
      }
    }
  }
}
</script>

<style scoped>
.schedule-management {
  padding: 20px;
}

.loading-container {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-info {
  font-size: 14px;
  color: #666;
  font-weight: normal;
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

.course-cell.has-other-class {
  background-color: #f5f5f5;
  opacity: 0.7;
}

.course-cell.disabled {
  cursor: not-allowed;
  position: relative;
}

.course-cell.disabled:hover {
  background-color: #f5f5f5 !important;
}

.course-cell.drag-over {
  background-color: #e8f5e8 !important;
  border: 2px dashed #67c23a;
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
}

.course-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  cursor: move;
  transition: all 0.3s ease;
  border-radius: 6px;
}

.course-content[draggable="false"] {
  cursor: default;
  opacity: 0.8;
}

.course-content:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.course-content[draggable="false"]:hover {
  transform: none;
  box-shadow: none;
}

.course-content.dragging {
  opacity: 0.7;
  transform: rotate(5deg) scale(1.05);
  z-index: 1000;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
}

.drag-handle {
  position: absolute;
  top: 2px;
  right: 2px;
  color: #c0c4cc;
  font-size: 12px;
  cursor: move;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.course-content:hover .drag-handle,
.course-item:hover .drag-handle {
  opacity: 1;
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

.multiple-courses {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 4px;
}

.course-item {
  border-radius: 4px;
  padding: 6px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  cursor: move;
  transition: all 0.3s ease;
  position: relative;
}

.course-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.course-item.dragging {
  opacity: 0.7;
  transform: rotate(3deg) scale(1.05);
  z-index: 1000;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
}

.course-item .course-name {
  font-weight: bold;
  color: #1976d2;
  font-size: 12px;
  margin-bottom: 2px;
}

.course-item .course-class {
  font-size: 10px;
  color: #666;
  margin-bottom: 4px;
}

.course-item .course-actions {
  display: flex;
  gap: 2px;
  justify-content: flex-end;
}

.course-item .course-actions .el-button {
  padding: 2px;
  min-height: auto;
}

.class-legend {
  margin-top: 15px;
  padding: 15px;
  background: #f0f2f5;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}

.legend-title {
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
  font-size: 14px;
}

.legend-items {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.legend-item {
  display: flex;
  align-items: center;
  padding: 5px 10px;
  background: white;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 3px;
  margin-right: 8px;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.legend-text {
  font-size: 13px;
  color: #606266;
  font-weight: 500;
}

.other-class-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 4px;
  position: relative;
}

.other-course-item {
  border-radius: 4px;
  padding: 6px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  opacity: 0.6;
  pointer-events: none;
}

.other-course-item .course-name {
  font-weight: bold;
  color: #1976d2;
  font-size: 12px;
  margin-bottom: 2px;
}

.other-course-item .course-class {
  font-size: 10px;
  color: #666;
  margin-bottom: 2px;
}

.other-course-item .course-teacher {
  font-size: 10px;
  color: #666;
}

.disabled-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(1px);
}

.disabled-text {
  font-size: 12px;
  color: #909399;
  font-weight: 500;
  background-color: rgba(255, 255, 255, 0.9);
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.drop-hint {
  text-align: center;
  color: #67c23a;
  font-size: 12px;
  margin-top: 4px;
  font-weight: 500;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% { opacity: 0.6; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.05); }
  100% { opacity: 0.6; transform: scale(1); }
}
</style> 