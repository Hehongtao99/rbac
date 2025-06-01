import request from '../utils/request'

// 开始考试
export function startExam(data) {
  return request({
    url: '/exam/start',
    method: 'post',
    data
  })
}

// 获取考试详情
export function getExamDetail(examId) {
  return request({
    url: `/exam/${examId}`,
    method: 'get'
  })
}

// 提交考试答案
export function submitExam(data) {
  return request({
    url: '/exam/submit',
    method: 'post',
    data
  })
}

// 获取用户考试列表
export function getUserExamList(params) {
  return request({
    url: '/exam/list',
    method: 'get',
    params
  })
}

// 获取考试结果
export function getExamResult(examId) {
  return request({
    url: `/exam/result/${examId}`,
    method: 'get'
  })
}

// 获取考试记录
export function getExamRecord(params) {
  return request({
    url: '/exam/record',
    method: 'get',
    params
  })
} 