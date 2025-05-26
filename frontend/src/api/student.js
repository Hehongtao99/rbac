import request from '../utils/request'

export const getStudentList = (params) => {
  return request({
    url: '/student/list',
    method: 'get',
    params
  })
}

export const createStudent = (data) => {
  return request({
    url: '/student/create',
    method: 'post',
    data
  })
}

export const updateStudent = (id, data) => {
  return request({
    url: `/student/update/${id}`,
    method: 'put',
    data
  })
}

export const deleteStudent = (id) => {
  return request({
    url: `/student/delete/${id}`,
    method: 'delete'
  })
}

export const getStudentById = (id) => {
  return request({
    url: `/student/${id}`,
    method: 'get'
  })
}

export const assignGradeAndEducation = (id, grade, educationSystem) => {
  return request({
    url: `/student/assign-grade-education/${id}`,
    method: 'put',
    params: {
      grade,
      educationSystem
    }
  })
}

export const setSemesterInfo = (id, currentYear, currentSemester) => {
  return request({
    url: `/student/set-semester/${id}`,
    method: 'put',
    params: {
      currentYear,
      currentSemester
    }
  })
} 