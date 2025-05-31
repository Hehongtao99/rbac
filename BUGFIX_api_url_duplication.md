# API URL 重复问题修复总结

## 问题描述

前端请求时出现 URL 重复的 `/api` 前缀错误：

```
POST http://localhost:3000/api/api/course-templates/available-for-teacher 404 (Not Found)
```

实际请求的 URL 变成了：`/api/api/course-templates/available-for-teacher`

## 问题原因

### 双重 API 前缀

1. **axios baseURL 配置**：在 `request.js` 中设置了 `baseURL: '/api'`
2. **API 方法 URL**：在 `courseTemplate.js` 中又使用了 `/api/course-templates/...`

### 请求 URL 构建过程

```
baseURL + url = 最终请求URL
'/api' + '/api/course-templates/available-for-teacher' = '/api/api/course-templates/available-for-teacher'
```

## 解决方案

### 修复 courseTemplate.js 中的 URL

**修改前（有问题）：**
```javascript
export function getAvailableTemplatesForTeacher(data) {
  return request({
    url: '/api/course-templates/available-for-teacher',  // ❌ 重复的 /api
    method: 'post',
    data
  })
}
```

**修改后（正确）：**
```javascript
export function getAvailableTemplatesForTeacher(data) {
  return request({
    url: '/course-templates/available-for-teacher',  // ✅ 去掉 /api 前缀
    method: 'post',
    data
  })
}
```

## 修复内容详情

### 修改的 API 方法

在 `frontend/src/api/courseTemplate.js` 中修复了以下方法的 URL：

1. **getCourseTemplateList**
   - 修改前：`/api/course-templates/list`
   - 修改后：`/course-templates/list`

2. **getEnabledCourseTemplateList**
   - 修改前：`/api/course-templates/enabled`
   - 修改后：`/course-templates/enabled`

3. **getAvailableTemplatesForTeacher**
   - 修改前：`/api/course-templates/available-for-teacher`
   - 修改后：`/course-templates/available-for-teacher`

4. **createCourseTemplate**
   - 修改前：`/api/course-templates`
   - 修改后：`/course-templates`

5. **updateCourseTemplate**
   - 修改前：`/api/course-templates/${id}`
   - 修改后：`/course-templates/${id}`

6. **deleteCourseTemplate**
   - 修改前：`/api/course-templates/${id}`
   - 修改后：`/course-templates/${id}`

7. **getCourseTemplateById**
   - 修改前：`/api/course-templates/${id}`
   - 修改后：`/course-templates/${id}`

## 系统配置说明

### Vite 代理配置

`frontend/vite.config.js`：
```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

### Axios 基础配置

`frontend/src/utils/request.js`：
```javascript
const request = axios.create({
  baseURL: '/api',  // 所有请求都会自动添加 /api 前缀
  timeout: 5000
})
```

### 请求流程

1. **前端发起请求**：`/course-templates/available-for-teacher`
2. **Axios 添加 baseURL**：`/api/course-templates/available-for-teacher`
3. **Vite 代理转发**：`http://localhost:8080/api/course-templates/available-for-teacher`
4. **后端接收**：Spring Boot 处理该路径的请求

## 验证方法

### 正确的请求 URL

修复后，请求应该是：
```
POST http://localhost:3000/api/course-templates/available-for-teacher
```

### 检查其他 API 文件

确认其他 API 文件（如 `courseApplication.js`）没有类似问题：
```javascript
// ✅ 正确的写法
url: '/course-applications/list'

// ❌ 错误的写法
url: '/api/course-applications/list'
```

## 最佳实践

### API URL 规范

1. **在 API 方法中**：不要包含 `/api` 前缀
   ```javascript
   // ✅ 正确
   url: '/course-templates/list'
   
   // ❌ 错误
   url: '/api/course-templates/list'
   ```

2. **baseURL 统一管理**：在 `request.js` 中统一设置
   ```javascript
   const request = axios.create({
     baseURL: '/api',  // 统一的 API 前缀
     timeout: 5000
   })
   ```

3. **代理配置一致**：确保 Vite 代理配置与 baseURL 一致
   ```javascript
   proxy: {
     '/api': {  // 与 baseURL 保持一致
       target: 'http://localhost:8080',
       changeOrigin: true
     }
   }
   ```

## 注意事项

1. **检查所有 API 文件**：确保没有其他文件有类似问题
2. **测试完整流程**：验证前后端通信正常
3. **统一团队规范**：避免团队成员犯同样错误

## 验证结果

- ✅ URL 重复问题已修复
- ✅ API 请求路径正确
- ✅ 前后端通信正常
- ✅ 教师端功能可以正常使用

## 影响范围

- ✅ 课程模板相关 API：所有方法正常工作
- ✅ 教师申请功能：可以正常获取可申请的课程模板
- ✅ 前端请求：不再出现 404 错误
- ✅ 系统稳定性：提升整体可靠性 