import request from '../utils/request'

const API_BASE_URL = '/schedule'

export default {
  // 添加课程到课程表
  addCourseToSchedule(data) {
    return request.post(`${API_BASE_URL}/add`, data)
  },

  // 更新课程表记录
  updateSchedule(scheduleId, data) {
    return request.put(`${API_BASE_URL}/${scheduleId}`, data)
  },

  // 获取教师的课程表 - 后端从JWT获取teacherId
  getTeacherSchedule(academicYear) {
    return request.get(`${API_BASE_URL}/teacher`, {
      params: { academicYear }
    })
  },

  // 获取某周的课程表 - 后端从JWT获取teacherId
  getWeeklySchedule(academicYear, weekNumber) {
    return request.get(`${API_BASE_URL}/weekly`, {
      params: { academicYear, weekNumber }
    })
  },

  // 获取某个班级某周的课程表 - 后端从JWT获取teacherId
  getWeeklyScheduleByClass(academicYear, weekNumber, classId) {
    return request.get(`${API_BASE_URL}/weekly/class`, {
      params: { academicYear, weekNumber, classId }
    })
  },

  // 获取教师分配的班级列表 - 后端从JWT获取teacherId
  getTeacherClasses() {
    return request.get(`${API_BASE_URL}/teacher-classes`)
  },

  // 删除课程表中的课程
  removeCourseFromSchedule(scheduleId) {
    return request.delete(`${API_BASE_URL}/${scheduleId}`)
  },

  // 检查时间冲突 - 后端从JWT获取teacherId
  checkTimeConflict(academicYear, weekNumber, dayOfWeek, timeSlot, classId) {
    return request.get(`${API_BASE_URL}/check-conflict`, {
      params: { academicYear, weekNumber, dayOfWeek, timeSlot, classId }
    })
  },

  // 获取可选择的课程列表 - 后端从JWT获取teacherId
  getAvailableCourses(academicYear) {
    return request.get(`${API_BASE_URL}/available-courses`, {
      params: { academicYear }
    })
  },

  // 获取指定班级的可用课程列表（显示该班级的剩余课时）- 后端从JWT获取teacherId
  getAvailableCoursesForClass(academicYear, classId) {
    return request.get(`${API_BASE_URL}/available-courses/class`, {
      params: { academicYear, classId }
    })
  },

  // 获取所有时间段配置
  getAllTimeSlots() {
    return request.get(`${API_BASE_URL}/time-slots`)
  },

  // 初始化时间段配置
  initTimeSlotConfig() {
    return request.post(`${API_BASE_URL}/init-time-slots`)
  },

  // 管理员获取所有教师的课程表
  getAllSchedulesForAdmin(params) {
    return request.get(`${API_BASE_URL}/admin/all`, { params })
  },

  // 管理员获取周课程表（网格形式）
  getWeeklyScheduleForAdmin(params) {
    return request.get(`${API_BASE_URL}/admin/weekly`, { params })
  }
} 