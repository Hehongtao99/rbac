import request from '../utils/request'

// 获取课程列表（包含模板和实例）
export const getCourseList = (params) => {
  return request({
    url: '/course/list',
    method: 'post',
    data: params
  })
}

// 获取课程模板列表（仅模板）
export const getCourseTemplateList = (params) => {
  return request({
    url: '/course/template-list',
    method: 'post',
    data: params
  })
}

// 获取开课实例列表（仅实例）
export const getCourseInstanceList = (params) => {
  return request({
    url: '/course/instance-list',
    method: 'post',
    data: params
  })
}

// 创建课程
export const createCourse = (data) => {
  return request({
    url: '/course/create',
    method: 'post',
    data
  })
}

// 更新课程
export const updateCourse = (id, data) => {
  return request({
    url: `/course/update/${id}`,
    method: 'put',
    data
  })
}

// 删除课程
export const deleteCourse = (id) => {
  return request({
    url: `/course/delete/${id}`,
    method: 'delete'
  })
}

// 获取课程详情
export const getCourseDetail = (id) => {
  return request({
    url: `/course/detail/${id}`,
    method: 'get'
  })
}

// 获取教师列表
export const getTeacherList = () => {
  return request({
    url: '/course/teachers',
    method: 'get'
  })
}

// 切换申请状态
export const toggleApplicationStatus = (id) => {
  return request({
    url: `/course/toggle-application/${id}`,
    method: 'post'
  })
} 