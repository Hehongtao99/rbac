<template>
  <div class="all-schedules">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>全校课程表管理</span>
          <div class="header-controls">
            <el-button type="primary" @click="exportSchedule">导出课表</el-button>
          </div>
        </div>
      </template>

      <!-- 筛选条件 -->
      <div class="filter-section">
        <el-form :model="filterForm" inline>
          <el-form-item label="学年">
            <el-select v-model="filterForm.academicYear" placeholder="选择学年" @change="handleFilterChange">
              <el-option label="2024-2025" value="2024-2025"></el-option>
              <el-option label="2023-2024" value="2023-2024"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="周次">
            <el-select v-model="gridWeekNumber" placeholder="选择周次" @change="loadWeeklyGrid">
              <el-option 
                v-for="week in 20" 
                :key="week" 
                :label="`第${week}周`" 
                :value="week">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="教师筛选">
            <el-select v-model="gridTeacherId" placeholder="选择教师" @change="loadWeeklyGrid" clearable>
              <el-option label="所有教师" :value="null"></el-option>
              <el-option 
                v-for="teacher in teacherList" 
                :key="teacher.id"
                :label="teacher.name" 
                :value="teacher.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item v-if="viewMode === 'list'" label="教师">
            <el-input 
              v-model="filterForm.teacherName" 
              placeholder="教师姓名" 
              @input="debounceSearch"
              clearable>
            </el-input>
          </el-form-item>
          <el-form-item v-if="viewMode === 'list'" label="课程">
            <el-input 
              v-model="filterForm.courseName" 
              placeholder="课程名称" 
              @input="debounceSearch"
              clearable>
            </el-input>
          </el-form-item>
          <el-form-item v-if="viewMode === 'list'">
            <el-button type="primary" @click="loadSchedules">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 视图切换 -->
      <div class="view-switch">
        <el-radio-group v-model="viewMode" @change="handleViewChange">
          <el-radio-button label="grid">课程表视图</el-radio-button>
          <el-radio-button label="list">列表视图</el-radio-button>
        </el-radio-group>
        <div class="stats-info">
          <span>共找到 {{ totalCount }} 条课程安排</span>
        </div>
      </div>

      <!-- 列表视图 -->
      <div v-if="viewMode === 'list'" class="list-view">
        <el-table :data="scheduleList" v-loading="loading" border>
          <el-table-column prop="weekNumber" label="周次" width="80" sortable>
            <template #default="{ row }">
              第{{ row.weekNumber }}周
            </template>
          </el-table-column>
          <el-table-column prop="dayOfWeekName" label="星期" width="80"></el-table-column>
          <el-table-column prop="timeSlot" label="时间段" width="100">
            <template #default="{ row }">
              第{{ row.timeSlot }}节
            </template>
          </el-table-column>
          <el-table-column prop="timeRange" label="时间" width="120"></el-table-column>
          <el-table-column prop="courseName" label="课程名称" min-width="150"></el-table-column>
          <el-table-column prop="teacherName" label="教师" width="120"></el-table-column>
          <el-table-column prop="classroom" label="教室" width="100"></el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button type="text" size="small" @click="viewDetail(row)">
                详情
              </el-button>
              <el-button type="text" size="small" @click="editSchedule(row)">
                编辑
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 网格视图 -->
      <div v-if="viewMode === 'grid'" class="grid-view">
        <div class="schedule-grid-container" v-if="gridWeekNumber">
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
                <div class="time-slot-name">第{{ timeSlot.timeSlot }}节</div>
                <div class="time-range">{{ timeSlot.startTime }}-{{ timeSlot.endTime }}</div>
              </div>

              <!-- 课程格子 -->
              <div 
                v-for="day in weekDays" 
                :key="`${timeSlot.timeSlot}-${day.value}`"
                class="course-cell">
                
                <div 
                  v-for="course in getCellCourses(timeSlot.timeSlot, day.value)" 
                  :key="course.id"
                  class="course-item"
                  @click="viewDetail(course)">
                  <div class="course-name">{{ course.courseName }}</div>
                  <div class="course-teacher">{{ course.teacherName }}</div>
                  <div class="course-classroom">{{ course.classroom }}</div>
                </div>

                <div v-if="getCellCourses(timeSlot.timeSlot, day.value).length === 0" class="empty-cell">
                  <span>无课程</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="课程详情" width="500px">
      <div v-if="selectedSchedule" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="课程名称">{{ selectedSchedule.courseName }}</el-descriptions-item>
          <el-descriptions-item label="教师">{{ selectedSchedule.teacherName }}</el-descriptions-item>
          <el-descriptions-item label="学年">{{ selectedSchedule.academicYear }}</el-descriptions-item>
          <el-descriptions-item label="周次">第{{ selectedSchedule.weekNumber }}周</el-descriptions-item>
          <el-descriptions-item label="星期">{{ selectedSchedule.dayOfWeekName }}</el-descriptions-item>
          <el-descriptions-item label="时间段">第{{ selectedSchedule.timeSlot }}节</el-descriptions-item>
          <el-descriptions-item label="时间">{{ selectedSchedule.timeRange }}</el-descriptions-item>
          <el-descriptions-item label="教室">{{ selectedSchedule.classroom }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import scheduleApi from '../../api/schedule'
import { ElMessage } from 'element-plus'

export default {
  name: 'AllSchedules',
  data() {
    return {
      loading: false,
      viewMode: 'grid', // 改为默认网格视图
      totalCount: 0,
      filterForm: {
        academicYear: '2024-2025',
        weekNumber: null,
        teacherName: '',
        courseName: ''
      },
      gridWeekNumber: 1,
      gridTeacherId: null,
      scheduleList: [],
      weeklyGridData: {},
      teacherList: [],
      timeSlots: [],
      weekDays: [
        { label: '周一', value: 1 },
        { label: '周二', value: 2 },
        { label: '周三', value: 3 },
        { label: '周四', value: 4 },
        { label: '周五', value: 5 },
        { label: '周六', value: 6 },
        { label: '周日', value: 7 }
      ],
      showDetailDialog: false,
      selectedSchedule: null,
      searchTimer: null
    }
  },
  created() {
    this.loadTimeSlots()
    // 默认加载网格视图数据
    this.loadWeeklyGrid()
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

    async loadSchedules() {
      try {
        this.loading = true
        const params = {
          academicYear: this.filterForm.academicYear,
          weekNumber: this.filterForm.weekNumber,
          teacherName: this.filterForm.teacherName,
          courseName: this.filterForm.courseName
        }

        console.log('=== 管理员获取课表 - 请求参数 ===')
        console.log('请求参数:', params)

        const response = await scheduleApi.getAllSchedulesForAdmin(params)
        
        console.log('=== 管理员获取课表 - 响应数据 ===')
        console.log('完整响应:', response)
        console.log('响应状态码:', response.code)
        console.log('响应数据:', response.data)
        console.log('数据类型:', typeof response.data)
        console.log('数据长度:', response.data ? response.data.length : 'null')
        
        if (response.code === 200) {
          this.scheduleList = response.data || []
          this.totalCount = this.scheduleList.length
          console.log('设置后的 scheduleList 长度:', this.scheduleList.length)
          if (this.scheduleList.length > 0) {
            console.log('第一条数据:', this.scheduleList[0])
          }
        } else {
          console.error('请求失败，状态码:', response.code)
          ElMessage.error('获取课表数据失败: ' + (response.message || '未知错误'))
        }
      } catch (error) {
        console.error('获取课表数据失败:', error)
        console.error('错误详情:', error.response || error.message)
        ElMessage.error('获取课表数据失败: ' + (error.message || '网络错误'))
      } finally {
        this.loading = false
      }
    },

    async loadWeeklyGrid() {
      if (!this.gridWeekNumber) return
      
      try {
        this.loading = true
        const params = {
          academicYear: this.filterForm.academicYear,
          weekNumber: this.gridWeekNumber,
          teacherId: this.gridTeacherId
        }

        const response = await scheduleApi.getWeeklyScheduleForAdmin(params)
        if (response.code === 200) {
          this.weeklyGridData = response.data.schedules || {}
          this.teacherList = response.data.teachers || []
          this.totalCount = response.data.totalCount || 0
        }
      } catch (error) {
        console.error('获取周课表数据失败:', error)
        ElMessage.error('获取周课表数据失败')
      } finally {
        this.loading = false
      }
    },

    getCellCourses(timeSlot, dayOfWeek) {
      const dayName = this.weekDays.find(d => d.value === dayOfWeek)?.label
      const daySchedules = this.weeklyGridData[dayName] || []
      return daySchedules.filter(s => s.timeSlot === timeSlot)
    },

    handleViewChange() {
      if (this.viewMode === 'grid') {
        this.loadWeeklyGrid()
      } else {
        this.loadSchedules()
      }
    },

    handleFilterChange() {
      if (this.viewMode === 'grid') {
        this.loadWeeklyGrid()
      } else {
        this.loadSchedules()
      }
    },

    debounceSearch() {
      clearTimeout(this.searchTimer)
      this.searchTimer = setTimeout(() => {
        this.loadSchedules()
      }, 500)
    },

    resetFilter() {
      this.filterForm = {
        academicYear: '2024-2025',
        weekNumber: null,
        teacherName: '',
        courseName: ''
      }
      this.loadSchedules()
    },

    viewDetail(schedule) {
      this.selectedSchedule = schedule
      this.showDetailDialog = true
    },

    editSchedule(schedule) {
      // 跳转到编辑页面或弹出编辑对话框
      ElMessage.info('编辑功能待实现')
    },

    exportSchedule() {
      // 导出功能
      ElMessage.info('导出功能待实现')
    }
  }
}
</script>

<style scoped>
.all-schedules {
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

.view-switch {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.stats-info {
  font-size: 14px;
  color: #666;
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
}

.course-cell {
  position: relative;
  min-height: 120px;
  border-right: 1px solid #e4e7ed;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.course-item {
  background: #e3f2fd;
  border: 1px solid #2196f3;
  border-radius: 4px;
  padding: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 4px;
}

.course-item:hover {
  background: #bbdefb;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.course-name {
  font-weight: bold;
  color: #1976d2;
  font-size: 12px;
  margin-bottom: 2px;
}

.course-teacher {
  font-size: 11px;
  color: #666;
  margin-bottom: 2px;
}

.course-classroom {
  font-size: 10px;
  color: #999;
}

.empty-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #c0c4cc;
  font-size: 12px;
}

.detail-content {
  padding: 20px 0;
}

@media (max-width: 1200px) {
  .schedule-grid {
    min-width: 1000px;
  }
}
</style> 