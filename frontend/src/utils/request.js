import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    console.log('=== 前端请求拦截器 ===')
    console.log('请求URL:', config.url)
    console.log('localStorage中的token:', token ? `${token.substring(0, 20)}...` : 'null')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      console.log('设置Authorization header:', `Bearer ${token.substring(0, 20)}...`)
    } else {
      console.log('没有token，跳过Authorization header设置')
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    console.log('=== 前端响应拦截器 ===')
    console.log('响应状态码:', response.status)
    console.log('响应数据code:', res.code)
    console.log('响应数据:', res)
    
    if (res.code === 200) {
      return res
    } else {
      console.log('响应错误:', res.message)
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    console.log('=== 前端响应拦截器 - 错误处理 ===')
    console.log('错误状态码:', error.response?.status)
    console.log('错误信息:', error.message)
    console.log('错误响应数据:', error.response?.data)
    
    if (error.response?.status === 401) {
      console.log('检测到401状态码，清除token并跳转到登录页')
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      window.location.href = '/login'
      return Promise.reject(error)
    }
    
    // 如果有响应数据，显示后端的错误信息
    const message = error.response?.data?.message || error.response?.data?.msg || error.message || '网络错误'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request 