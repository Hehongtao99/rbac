import request from '../utils/request'

// 创建错题本
export function createWrongQuestionBook(data) {
  return request({
    url: '/wrong-question-book/create',
    method: 'post',
    data
  })
}

// 获取用户错题本列表
export function getUserWrongQuestionBooks(params) {
  return request({
    url: '/wrong-question-book/list',
    method: 'get',
    params
  })
}

// 获取错题本详情
export function getWrongQuestionBookDetail(bookId) {
  return request({
    url: `/wrong-question-book/${bookId}`,
    method: 'get'
  })
}

// 添加错题到错题本
export function addWrongQuestion(data) {
  return request({
    url: '/wrong-question-book/add-question',
    method: 'post',
    data
  })
}

// 获取错题本中的题目列表
export function getWrongQuestions(bookId, params) {
  return request({
    url: `/wrong-question-book/${bookId}/questions`,
    method: 'get',
    params
  })
}

// 从错题本中移除题目
export function removeWrongQuestion(bookId, questionId) {
  return request({
    url: `/wrong-question-book/${bookId}/questions/${questionId}`,
    method: 'delete'
  })
}

// 更新题目掌握程度
export function updateMasteryLevel(bookId, questionId, masteryLevel) {
  return request({
    url: `/wrong-question-book/${bookId}/questions/${questionId}/mastery`,
    method: 'put',
    params: { masteryLevel }
  })
}

// 删除错题本
export function deleteWrongQuestionBook(bookId) {
  return request({
    url: `/wrong-question-book/${bookId}`,
    method: 'delete'
  })
}

// 检查题目是否已在错题本中
export function isQuestionInBook(bookId, questionId) {
  return request({
    url: `/wrong-question-book/${bookId}/questions/${questionId}/exists`,
    method: 'get'
  })
} 