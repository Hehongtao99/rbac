import request from '../utils/request'

// 获取课程申请列表
export const getCourseApplicationList = (params) => {
  return request({
    url: '/course-applications/list',
    method: 'post',
    data: params
  })
}

// 管理员获取课程申请列表
export const getCourseApplicationListForAdmin = (params) => {
  return request({
    url: '/course-applications/admin/list',
    method: 'post',
    data: params
  })
}

// 创建课程申请
export const createCourseApplication = (data) => {
  return request({
    url: '/course-applications',
    method: 'post',
    data
  })
}

// 更新课程申请
export const updateCourseApplication = (id, data) => {
  return request({
    url: `/course-applications/${id}`,
    method: 'put',
    data
  })
}

// 审核课程申请
export const reviewCourseApplication = (id, data) => {
  return request({
    url: `/course-applications/${id}/review`,
    method: 'put',
    data
  })
}

// 删除课程申请
export const deleteCourseApplication = (id) => {
  return request({
    url: `/course-applications/${id}`,
    method: 'delete'
  })
}

// 获取课程申请详情
export const getCourseApplicationById = (id) => {
  return request({
    url: `/course-applications/${id}`,
    method: 'get'
  })
} 