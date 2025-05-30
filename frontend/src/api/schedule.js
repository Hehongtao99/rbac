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

  // 删除课程表中的课程
  removeCourseFromSchedule(scheduleId) {
    return request.delete(`${API_BASE_URL}/${scheduleId}`)
  },

  // 检查时间冲突 - 后端从JWT获取teacherId
  checkTimeConflict(academicYear, weekNumber, dayOfWeek, timeSlot) {
    return request.get(`${API_BASE_URL}/check-conflict`, {
      params: { academicYear, weekNumber, dayOfWeek, timeSlot }
    })
  },

  // 获取可选择的课程列表 - 后端从JWT获取teacherId
  getAvailableCourses(academicYear) {
    return request.get(`${API_BASE_URL}/available-courses`, {
      params: { academicYear }
    })
  },

  // 获取所有时间段配置
  getAllTimeSlots() {
    return request.get(`${API_BASE_URL}/time-slots`)
  },

  // 初始化时间段配置
  initTimeSlotConfig() {
    return request.post(`${API_BASE_URL}/init-time-slots`)
  }
} 