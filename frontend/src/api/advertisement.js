import request from '../utils/request'

// 分页查询广告申请列表
export const getApplicationPage = (params) => {
  return request({
    url: '/admin/advertisement/applications',
    method: 'get',
    params
  })
}

// 新增广告申请
export const saveApplication = (data) => {
  return request({
    url: '/admin/advertisement/applications',
    method: 'post',
    data
  })
}

// 获取广告申请详情
export const getApplicationById = (id) => {
  return request({
    url: `/admin/advertisement/applications/${id}`,
    method: 'get'
  })
}

// 审核广告申请
export const auditApplication = (id, auditStatus, auditRemark) => {
  return request({
    url: `/admin/advertisement/applications/${id}/audit`,
    method: 'put',
    params: {
      auditStatus,
      auditRemark
    }
  })
}

// 根据区域ID获取广告位列表
export const getPositionsByRegionId = (regionId) => {
  return request({
    url: '/admin/advertisement/positions',
    method: 'get',
    params: { regionId }
  })
}

// 获取广告位详情
export const getPositionById = (id) => {
  return request({
    url: `/admin/advertisement/positions/${id}`,
    method: 'get'
  })
}

// 上传文件
export const uploadFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/admin/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 分页查询广告位列表
export const getPositionsPage = (params) => {
  return request({
    url: '/admin/advertisement/positions/page',
    method: 'get',
    params
  })
}

// 新增广告位
export const savePosition = (data) => {
  return request({
    url: '/admin/advertisement/positions',
    method: 'post',
    data
  })
}

// 更新广告位
export const updatePosition = (data) => {
  return request({
    url: '/admin/advertisement/positions',
    method: 'put',
    data
  })
}

// 删除广告位
export const deletePosition = (id) => {
  return request({
    url: `/admin/advertisement/positions/${id}`,
    method: 'delete'
  })
} 