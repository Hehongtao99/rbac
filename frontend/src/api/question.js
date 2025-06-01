import request from '@/utils/request'

// 获取题目列表
export function getQuestionList(params) {
  return request({
    url: '/question/list',
    method: 'get',
    params
  })
}

// 获取题目详情
export function getQuestionById(id) {
  return request({
    url: `/question/${id}`,
    method: 'get'
  })
}

// 创建题目
export function createQuestion(data) {
  return request({
    url: '/question',
    method: 'post',
    data
  })
}

// 更新题目
export function updateQuestion(data) {
  return request({
    url: '/question',
    method: 'put',
    data
  })
}

// 删除题目
export function deleteQuestion(id) {
  return request({
    url: `/question/${id}`,
    method: 'delete'
  })
}

// 批量删除题目
export function batchDeleteQuestion(ids) {
  return request({
    url: '/question/batch',
    method: 'delete',
    data: ids
  })
} 