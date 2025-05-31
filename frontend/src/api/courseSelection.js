import request from '@/utils/request'

// 获取学生可选课程列表
export function getAvailableCourses(studentId) {
  return request({
    url: `/course-selection/available/${studentId}`,
    method: 'get'
  })
}

// 获取学生已选课程列表
export function getSelectedCourses(studentId) {
  return request({
    url: `/course-selection/selected/${studentId}`,
    method: 'get'
  })
}

// 学生选课
export function selectCourse(data) {
  return request({
    url: '/course-selection/select',
    method: 'post',
    data
  })
}

// 学生退课
export function dropCourse(studentId, courseApplicationId) {
  return request({
    url: `/course-selection/drop/${studentId}/${courseApplicationId}`,
    method: 'delete'
  })
} 