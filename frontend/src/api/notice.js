import request from '../utils/request'

// 分页查询通知列表
export function getNoticeList(data) {
  return request({
    url: '/notice/list',
    method: 'post',
    data
  })
}

// 获取通知详情
export function getNoticeById(id) {
  return request({
    url: `/notice/${id}`,
    method: 'get'
  })
}

// 新增通知
export function addNotice(data) {
  return request({
    url: '/notice/add',
    method: 'post',
    data
  })
}

// 编辑通知
export function updateNotice(data) {
  return request({
    url: '/notice/update',
    method: 'put',
    data
  })
}

// 删除通知
export function deleteNotice(id) {
  return request({
    url: `/notice/${id}`,
    method: 'delete'
  })
}

// 发布通知
export function publishNotice(id) {
  return request({
    url: `/notice/publish/${id}`,
    method: 'put'
  })
}

// 下线通知
export function offlineNotice(id) {
  return request({
    url: `/notice/offline/${id}`,
    method: 'put'
  })
} 