# 题库管理功能使用说明

## 功能概述

本系统新增了完整的题库录入和题目管理功能，支持多种题型的创建和管理。

## 功能特性

### 题库管理
- ✅ 题库的增删改查
- ✅ 题库状态管理（启用/禁用）
- ✅ 题库搜索和分页
- ✅ 题目数量统计

### 题目管理
- ✅ 支持5种题型：单选题、多选题、判断题、问答题、填空题
- ✅ 题目的增删改查
- ✅ 批量删除题目
- ✅ 题目搜索和筛选
- ✅ 难度等级设置（简单、中等、困难）
- ✅ 分值设置
- ✅ 题目解析

## 数据库表结构

### 题库表 (question_bank)
```sql
CREATE TABLE `question_bank` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '题库名称',
  `description` text COMMENT '题库描述',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `create_user_id` bigint NOT NULL COMMENT '创建用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题库表';
```

### 题目表 (question)
```sql
CREATE TABLE `question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `bank_id` bigint NOT NULL COMMENT '题库ID',
  `title` varchar(200) NOT NULL COMMENT '题目标题',
  `content` text NOT NULL COMMENT '题目内容',
  `type` tinyint NOT NULL COMMENT '题目类型：1-单选 2-多选 3-判断 4-问答 5-填空',
  `options` json COMMENT '选项（JSON格式存储）',
  `answer` text NOT NULL COMMENT '正确答案',
  `analysis` text COMMENT '题目解析',
  `difficulty` tinyint NOT NULL DEFAULT '1' COMMENT '难度等级：1-简单 2-中等 3-困难',
  `score` int NOT NULL DEFAULT '1' COMMENT '分值',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `create_user_id` bigint NOT NULL COMMENT '创建用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目表';
```

## API接口

### 题库管理接口

#### 1. 获取题库列表
- **URL**: `GET /api/question-bank/list`
- **参数**: 
  - `current`: 当前页码
  - `size`: 每页大小
  - `name`: 题库名称（可选）

#### 2. 获取题库详情
- **URL**: `GET /api/question-bank/{id}`

#### 3. 创建题库
- **URL**: `POST /api/question-bank`
- **参数**: 
  ```json
  {
    "name": "题库名称",
    "description": "题库描述",
    "status": 1
  }
  ```

#### 4. 更新题库
- **URL**: `PUT /api/question-bank`
- **参数**: 
  ```json
  {
    "id": 1,
    "name": "题库名称",
    "description": "题库描述",
    "status": 1
  }
  ```

#### 5. 删除题库
- **URL**: `DELETE /api/question-bank/{id}`

### 题目管理接口

#### 1. 获取题目列表
- **URL**: `GET /api/question/list`
- **参数**: 
  - `current`: 当前页码
  - `size`: 每页大小
  - `bankId`: 题库ID（可选）
  - `type`: 题目类型（可选）
  - `title`: 题目标题（可选）

#### 2. 获取题目详情
- **URL**: `GET /api/question/{id}`

#### 3. 创建题目
- **URL**: `POST /api/question`
- **参数**: 
  ```json
  {
    "bankId": 1,
    "title": "题目标题",
    "content": "题目内容",
    "type": 1,
    "options": ["选项A", "选项B", "选项C", "选项D"],
    "answer": "A",
    "analysis": "题目解析",
    "difficulty": 1,
    "score": 5,
    "status": 1
  }
  ```

#### 4. 更新题目
- **URL**: `PUT /api/question`

#### 5. 删除题目
- **URL**: `DELETE /api/question/{id}`

#### 6. 批量删除题目
- **URL**: `DELETE /api/question/batch`
- **参数**: `[1, 2, 3]` (题目ID数组)

## 题目类型说明

1. **单选题 (type=1)**: 只能选择一个答案
2. **多选题 (type=2)**: 可以选择多个答案
3. **判断题 (type=3)**: 只有"正确"和"错误"两个选项
4. **问答题 (type=4)**: 开放性问题，需要文字回答
5. **填空题 (type=5)**: 需要填入缺失的内容

## 使用流程

1. **创建题库**: 在题库管理页面创建新的题库
2. **录入题目**: 在题目管理页面为指定题库录入各种类型的题目
3. **管理题目**: 可以编辑、删除、批量操作题目
4. **状态控制**: 可以启用或禁用题库和题目

## 权限控制

系统已配置相应的菜单权限和按钮权限：
- 题库管理菜单权限
- 题目管理菜单权限
- 新增、编辑、删除等按钮权限

## 注意事项

1. 删除题库前需要确保该题库下没有题目
2. 选择题至少需要2个选项
3. 判断题的答案只能是"正确"或"错误"
4. 所有题目都必须设置正确答案
5. 分值范围为1-100分

## 技术实现

- **后端**: Spring Boot + MyBatis-Plus
- **前端**: Vue 3 + Element Plus
- **数据库**: MySQL 8.0+
- **架构**: MVC三层架构，严格分离业务逻辑 