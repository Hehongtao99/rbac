import request from '../utils/request'

export const getTeacherList = (params) => {
  return request({
    url: '/teacher/list',
    method: 'get',
    params
  })
}

export const createTeacher = (data) => {
  return request({
    url: '/teacher/create',
    method: 'post',
    data
  })
}

export const updateTeacher = (id, data) => {
  return request({
    url: `/teacher/update/${id}`,
    method: 'put',
    data
  })
}

export const deleteTeacher = (id) => {
  return request({
    url: `/teacher/delete/${id}`,
    method: 'delete'
  })
}

export const getTeacherById = (id) => {
  return request({
    url: `/teacher/${id}`,
    method: 'get'
  })
} 