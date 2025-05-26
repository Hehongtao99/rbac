import request from '../utils/request'

// 获取课程列表
export const getCourseList = (params) => {
  return request({
    url: '/course/list',
    method: 'get',
    params
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