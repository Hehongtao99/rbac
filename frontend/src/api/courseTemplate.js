import request from '../utils/request'

// 获取课程模板列表
export const getCourseTemplateList = (params) => {
  return request({
    url: '/course-templates/list',
    method: 'post',
    data: params
  })
}

// 获取启用的课程模板列表
export const getEnabledCourseTemplateList = (params) => {
  return request({
    url: '/course-templates/enabled',
    method: 'post',
    data: params
  })
}

// 创建课程模板
export const createCourseTemplate = (data) => {
  return request({
    url: '/course-templates',
    method: 'post',
    data
  })
}

// 更新课程模板
export const updateCourseTemplate = (id, data) => {
  return request({
    url: `/course-templates/${id}`,
    method: 'put',
    data
  })
}

// 删除课程模板
export const deleteCourseTemplate = (id) => {
  return request({
    url: `/course-templates/${id}`,
    method: 'delete'
  })
}

// 获取课程模板详情
export const getCourseTemplateById = (id) => {
  return request({
    url: `/course-templates/${id}`,
    method: 'get'
  })
} 