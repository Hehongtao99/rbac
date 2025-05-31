import request from '../utils/request'

// 获取课程模板列表
export function getCourseTemplateList(data) {
  return request({
    url: '/course-templates/list',
    method: 'post',
    data
  })
}

// 获取启用的课程模板列表
export function getEnabledCourseTemplateList(data) {
  return request({
    url: '/course-templates/enabled',
    method: 'post',
    data
  })
}

// 获取教师可申请的课程模板列表
export function getAvailableTemplatesForTeacher(data) {
  return request({
    url: '/course-templates/available-for-teacher',
    method: 'post',
    data
  })
}

// 创建课程模板
export function createCourseTemplate(data) {
  return request({
    url: '/course-templates',
    method: 'post',
    data
  })
}

// 更新课程模板
export function updateCourseTemplate(id, data) {
  return request({
    url: `/course-templates/${id}`,
    method: 'put',
    data
  })
}

// 删除课程模板
export function deleteCourseTemplate(id) {
  return request({
    url: `/course-templates/${id}`,
    method: 'delete'
  })
}

// 获取课程模板详情
export function getCourseTemplateById(id) {
  return request({
    url: `/course-templates/${id}`,
    method: 'get'
  })
} 