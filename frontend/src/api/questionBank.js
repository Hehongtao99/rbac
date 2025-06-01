import request from '@/utils/request'

// 获取题库列表
export function getQuestionBankList(params) {
  return request({
    url: '/question-bank/list',
    method: 'get',
    params
  })
}

// 获取题库详情
export function getQuestionBankById(id) {
  return request({
    url: `/question-bank/${id}`,
    method: 'get'
  })
}

// 创建题库
export function createQuestionBank(data) {
  return request({
    url: '/question-bank',
    method: 'post',
    data
  })
}

// 更新题库
export function updateQuestionBank(data) {
  return request({
    url: '/question-bank',
    method: 'put',
    data
  })
}

// 删除题库
export function deleteQuestionBank(id) {
  return request({
    url: `/question-bank/${id}`,
    method: 'delete'
  })
} 