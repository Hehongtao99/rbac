import axios from 'axios'
import { ElMessage } from 'element-plus'

// 为AI请求创建专门的axios实例，设置更长的超时时间
const aiRequest = axios.create({
  baseURL: '/api',
  timeout: 600000 // 10分钟超时
})

// 请求拦截器
aiRequest.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
aiRequest.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

// AI提取题目并自动添加到题库
export function extractAndAddQuestions(data) {
  return aiRequest({
    url: '/ai-question/extract-and-add',
    method: 'post',
    data
  })
}

// AI提取题目预览（不添加到题库）
export function extractQuestionsPreview(data) {
  return aiRequest({
    url: '/ai-question/extract-preview',
    method: 'post',
    data
  })
}

// AI流式提取题目（支持分段解析）
export function extractQuestionsStream(data) {
  return aiRequest({
    url: '/ai-question/extract-stream',
    method: 'post',
    data
  })
}

// AI实时流式提取题目（SSE方式）
export function extractQuestionsRealtimeStream(data, onMessage, onError, onComplete) {
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  
  // 构建URL参数，确保内容正确编码
  const params = new URLSearchParams()
  params.append('bankId', data.bankId)
  params.append('content', data.content)
  
  const url = `${baseURL}/api/ai-question/extract-realtime-stream?${params.toString()}`
  
  console.log('SSE连接URL:', url)
  console.log('请求参数:', { bankId: data.bankId, contentLength: data.content.length })
  
  return new Promise((resolve, reject) => {
    const eventSource = new EventSource(url)
    
    eventSource.onopen = () => {
      console.log('SSE连接已建立')
    }
    
    eventSource.onmessage = (event) => {
      console.log('收到默认消息:', event.data)
      try {
        const data = JSON.parse(event.data)
        onMessage && onMessage(data)
      } catch (e) {
        console.warn('解析默认SSE数据失败:', e, event.data)
      }
    }
    
    eventSource.addEventListener('question', (event) => {
      console.log('收到question事件:', event.data)
      try {
        const data = JSON.parse(event.data)
        onMessage && onMessage(data)
      } catch (e) {
        console.warn('解析question事件失败:', e, event.data)
      }
    })
    
    eventSource.addEventListener('progress', (event) => {
      console.log('收到progress事件:', event.data)
      try {
        const data = JSON.parse(event.data)
        onMessage && onMessage(data)
      } catch (e) {
        console.warn('解析progress事件失败:', e, event.data)
      }
    })
    
    eventSource.addEventListener('complete', (event) => {
      console.log('收到complete事件:', event.data)
      try {
        const data = JSON.parse(event.data)
        onMessage && onMessage(data)
        eventSource.close()
        onComplete && onComplete()
        resolve()
      } catch (e) {
        console.warn('解析complete事件失败:', e, event.data)
        eventSource.close()
        onComplete && onComplete()
        resolve()
      }
    })
    
    eventSource.addEventListener('error', (event) => {
      console.log('收到error事件:', event.data)
      try {
        const data = JSON.parse(event.data)
        onError && onError(data.error || '未知错误')
        eventSource.close()
        reject(new Error(data.error || '未知错误'))
      } catch (e) {
        onError && onError('解析错误事件失败')
        eventSource.close()
        reject(new Error('解析错误事件失败'))
      }
    })
    
    eventSource.onerror = (error) => {
      console.error('SSE连接错误:', error)
      console.error('EventSource readyState:', eventSource.readyState)
      onError && onError('连接中断或服务器错误')
      eventSource.close()
      reject(new Error('连接中断或服务器错误'))
    }
    
    // 设置超时处理
    setTimeout(() => {
      if (eventSource.readyState === EventSource.CONNECTING) {
        console.warn('SSE连接超时')
        eventSource.close()
        onError && onError('连接超时')
        reject(new Error('连接超时'))
      }
    }, 10000) // 10秒超时
  })
}

// 添加流式解析的题目到题库
export function addStreamQuestions(data) {
  return aiRequest({
    url: '/ai-question/add-stream-questions',
    method: 'post',
    data
  })
} 