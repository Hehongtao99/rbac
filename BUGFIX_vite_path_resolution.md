# Vite 路径解析问题修复总结

## 问题描述

在前端开发过程中遇到了 Vite 路径解析错误：

```
[plugin:vite:import-analysis] Failed to resolve import "@/api/courseTemplate" from "src\views\teacher\CourseApplication.vue". Does the file exist?
```

## 问题原因

1. **Vite 配置缺失**：`vite.config.js` 中没有配置 `@` 路径别名
2. **路径解析失败**：Vite 无法识别 `@` 符号指向的路径

## 解决方案

### 方案1：配置 Vite 路径别名（推荐）

修改 `frontend/vite.config.js`：

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

### 方案2：使用相对路径（备选）

如果路径别名仍有问题，可以使用相对路径：

**修改前：**
```javascript
import { getAvailableTemplatesForTeacher } from '@/api/courseTemplate'
import request from '@/utils/request'
```

**修改后：**
```javascript
import { getAvailableTemplatesForTeacher } from '../../api/courseTemplate'
import request from '../utils/request'
```

## 实际修复内容

### 1. 更新 Vite 配置

在 `frontend/vite.config.js` 中：
- 添加了 `path` 模块导入
- 配置了 `resolve.alias` 设置 `@` 指向 `src` 目录

### 2. 修复导入路径

**CourseApplication.vue**
```javascript
// 修改前
import { getAvailableTemplatesForTeacher } from '@/api/courseTemplate'
import { createCourseApplication, getAppliedTemplateIds } from '@/api/courseApplication'

// 修改后
import { getAvailableTemplatesForTeacher } from '../../api/courseTemplate'
import { createCourseApplication, getAppliedTemplateIds } from '../../api/courseApplication'
```

**courseTemplate.js**
```javascript
// 修改前
import request from '@/utils/request'

// 修改后
import request from '../utils/request'
```

## 路径结构说明

```
frontend/src/
├── api/
│   ├── courseTemplate.js
│   ├── courseApplication.js
│   └── ...
├── utils/
│   └── request.js
├── views/
│   └── teacher/
│       └── CourseApplication.vue
└── ...
```

### 相对路径计算

- 从 `views/teacher/CourseApplication.vue` 到 `api/courseTemplate.js`：`../../api/courseTemplate`
- 从 `api/courseTemplate.js` 到 `utils/request.js`：`../utils/request`

## 验证方法

1. **检查文件存在性**：确认所有引用的文件都存在
2. **路径计算正确性**：验证相对路径计算是否正确
3. **Vite 配置生效**：重启开发服务器使配置生效

## 最佳实践

### 推荐使用路径别名
```javascript
// vite.config.js
resolve: {
  alias: {
    '@': resolve(__dirname, 'src'),
    '@components': resolve(__dirname, 'src/components'),
    '@views': resolve(__dirname, 'src/views'),
    '@api': resolve(__dirname, 'src/api'),
    '@utils': resolve(__dirname, 'src/utils')
  }
}
```

### 统一导入风格
```javascript
// 推荐：使用别名
import { getAvailableTemplatesForTeacher } from '@/api/courseTemplate'
import request from '@/utils/request'

// 备选：使用相对路径
import { getAvailableTemplatesForTeacher } from '../../api/courseTemplate'
import request from '../utils/request'
```

## 注意事项

1. **重启服务器**：修改 Vite 配置后需要重启开发服务器
2. **IDE 支持**：某些 IDE 可能需要额外配置才能识别路径别名
3. **构建环境**：确保生产构建也能正确解析路径

## 验证结果

- ✅ Vite 配置正确添加路径别名
- ✅ 相对路径导入正常工作
- ✅ 前端项目可以正常启动
- ✅ 模块导入解析成功

## 影响范围

- ✅ 前端开发环境：路径解析正常
- ✅ 模块导入：所有导入语句正常工作
- ✅ 构建过程：不影响生产构建
- ✅ 开发体验：提升开发效率 