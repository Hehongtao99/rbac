import request from '../utils/request'

// 获取组织树
export const getOrganizationTree = () => {
  return request({
    url: '/admin/organizations/tree',
    method: 'get'
  })
}

// 根据父ID获取子节点
export const getOrganizationChildren = (parentId) => {
  return request({
    url: `/admin/organizations/children/${parentId}`,
    method: 'get'
  })
}

// 根据ID获取组织信息
export const getOrganizationById = (id) => {
  return request({
    url: `/admin/organizations/${id}`,
    method: 'get'
  })
}

// 新增组织
export const saveOrganization = (data) => {
  return request({
    url: '/admin/organizations',
    method: 'post',
    data
  })
}

// 更新组织
export const updateOrganization = (data) => {
  return request({
    url: '/admin/organizations',
    method: 'put',
    data
  })
}

// 删除组织
export const deleteOrganization = (id) => {
  return request({
    url: `/admin/organizations/${id}`,
    method: 'delete'
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