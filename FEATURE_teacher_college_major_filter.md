# 教师端学院专业筛选功能实现总结

## 功能概述

实现了教师端申请开课时，只能看到与自己学院专业对应的课程模板，确保教师只能申请符合权限的课程。

## 实现内容

### 1. 后端实现

#### Service 层新增方法

**CourseTemplateService 接口**
- 新增 `getAvailableTemplatesForTeacher()` 方法，专门为教师提供可申请的课程模板查询

**CourseTemplateServiceImpl 实现**
- 实现教师学院专业信息获取逻辑
- 根据教师的组织关系（学院、专业、班级）确定其所属学院专业
- 调用专门的 Mapper 方法进行筛选查询

#### Mapper 层新增方法

**CourseTemplateMapper 接口**
```java
// 教师专用分页查询（根据教师学院专业筛选）
List<CourseTemplate> selectPageForTeacher(@Param("offset") int offset, @Param("limit") int limit,
                                         @Param("keyword") String keyword, @Param("academicYear") String academicYear,
                                         @Param("semester") String semester, @Param("teacherCollegeId") Long teacherCollegeId,
                                         @Param("teacherMajorId") Long teacherMajorId);

// 教师专用分页查询总数
long selectPageCountForTeacher(@Param("keyword") String keyword, @Param("academicYear") String academicYear,
                              @Param("semester") String semester, @Param("teacherCollegeId") Long teacherCollegeId,
                              @Param("teacherMajorId") Long teacherMajorId);
```

#### XML 查询逻辑

**学院专业匹配规则**
```sql
AND (
    -- 情况1：课程模板没有设置学院专业限制，所有教师都可以申请
    (college_id IS NULL AND major_id IS NULL)
    OR (
        -- 情况2：课程模板设置了学院专业限制，需要匹配教师的学院专业
        (college_id IS NULL OR college_id = #{teacherCollegeId})
        AND
        (major_id IS NULL OR major_id = #{teacherMajorId})
    )
)
```

#### Controller 层新增接口

**CourseTemplateController**
```java
@PostMapping("/available-for-teacher")
public Result<PageResult<CourseTemplateVO>> getAvailableTemplatesForTeacher(@RequestBody CourseTemplatePageDTO pageDTO)
```

### 2. 前端实现

#### API 接口更新

**courseTemplate.js**
```javascript
// 获取教师可申请的课程模板列表
export function getAvailableTemplatesForTeacher(data) {
  return request({
    url: '/api/course-templates/available-for-teacher',
    method: 'post',
    data
  })
}
```

#### 页面组件更新

**CourseApplication.vue**
- 将原来的 `getEnabledCourseTemplateList` 替换为 `getAvailableTemplatesForTeacher`
- 确保教师只能看到符合权限的课程模板
- 保持原有的搜索、分页、申请等功能

### 3. 权限匹配逻辑

#### 教师组织信息获取
1. **学院级别**：直接匹配学院ID
2. **专业级别**：匹配专业ID，同时获取父级学院ID
3. **班级级别**：通过班级找到专业，再找到学院

#### 课程模板筛选规则
1. **无限制模板**：没有设置学院专业限制的课程，所有教师都可以申请
2. **学院限制**：只有对应学院的教师可以申请
3. **专业限制**：只有对应专业的教师可以申请
4. **学院+专业限制**：必须同时匹配学院和专业

### 4. 功能特性

#### 智能筛选
- ✅ 自动根据教师的组织关系筛选可申请的课程模板
- ✅ 支持多层级组织结构（学院→专业→班级）
- ✅ 灵活的权限匹配规则

#### 用户体验
- ✅ 教师无需手动筛选，系统自动显示可申请的课程
- ✅ 避免教师申请无权限的课程被拒绝
- ✅ 保持原有的搜索、分页功能

#### 数据安全
- ✅ 后端权限验证，确保数据安全
- ✅ 前后端双重保护，防止越权访问
- ✅ 与申请时的权限检查逻辑一致

### 5. 测试场景

#### 场景1：计算机学院教师
- 可以看到：无限制课程 + 计算机学院课程 + 对应专业课程
- 不能看到：其他学院的专业限制课程

#### 场景2：无组织分配教师
- 只能看到：无学院专业限制的课程模板
- 不能看到：任何有限制的课程模板

#### 场景3：多组织关联教师
- 根据最高级别组织确定权限
- 支持复杂的组织关系

### 6. 系统优势

#### 权限控制精确
- 从源头控制教师可见的课程模板
- 避免无效申请和审核工作量

#### 用户体验优化
- 教师界面更简洁，只显示相关课程
- 减少用户困惑和操作错误

#### 系统性能提升
- 减少不必要的数据传输
- 优化查询性能

## 验证结果

- ✅ 编译成功，无语法错误
- ✅ 权限逻辑正确实现
- ✅ 前后端接口对接完成
- ✅ 用户界面功能正常

## 影响范围

- ✅ 教师申请开课页面：只显示可申请的课程模板
- ✅ 后端权限控制：双重验证确保安全
- ✅ 数据库查询：优化查询性能
- ✅ 用户体验：提升操作便利性 