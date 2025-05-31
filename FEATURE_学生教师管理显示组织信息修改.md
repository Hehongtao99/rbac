# 学生教师管理显示组织信息功能修改

## 修改目标

根据用户需求，对学生管理和教师管理页面进行以下改进：

1. **学生管理** - 增加学院字段显示
2. **教师管理** - 将部门、职称字段替换为学院、专业、班级字段（支持多班级显示）

## 修改内容

### 1. 学生管理修改

#### 前端修改
**文件：** `frontend/src/views/StudentManagement.vue`

**表格字段调整：**
- 新增学院列（`collegeName`）
- 调整列顺序：学号 → 姓名 → 性别 → **学院** → 专业 → 班级

**详情对话框调整：**
- 在专业字段前新增学院字段显示

#### 后端修改
**文件：** `src/main/java/com/example/service/impl/StudentServiceImpl.java`

**依赖注入：**
```java
@Autowired
private UserOrganizationMapper userOrganizationMapper;

@Autowired
private OrganizationMapper organizationMapper;
```

**新增方法：**
```java
/**
 * 设置学生的组织信息
 */
private void setStudentOrganizationInfo(StudentVO vo, Long userId) {
    // 获取用户关联的组织信息
    // 按级别分类，设置学院信息到vo.collegeName
}
```

**VO字段：** `StudentVO.collegeName` - 用于显示学院名称

### 2. 教师管理修改

#### 前端修改
**文件：** `frontend/src/views/TeacherManagement.vue`

**表格字段调整：**
- 移除：部门、职称列
- 新增：学院、专业、班级列
- 班级支持多个值显示（用逗号分隔）

**详情对话框调整：**
- 替换部门、职称字段为学院、专业、班级字段

**新增函数：**
```javascript
// 格式化班级列表显示
const formatClasses = (classes) => {
  if (!classes || !Array.isArray(classes) || classes.length === 0) {
    return '未分配'
  }
  return classes.map(c => c.name).join(', ')
}
```

#### 后端修改
**文件：** `src/main/java/com/example/vo/TeacherVO.java`

**字段调整：**
```java
private String college; // 学院
private String major; // 专业  
private List<ClassInfo> classes; // 班级列表

@Data
public static class ClassInfo {
    private Long id;
    private String name;
    private String code;
}
```

**文件：** `src/main/java/com/example/service/impl/TeacherServiceImpl.java`

**依赖注入：**
```java
@Autowired
private UserOrganizationMapper userOrganizationMapper;

@Autowired
private OrganizationMapper organizationMapper;
```

**新增方法：**
```java
/**
 * 设置教师的组织信息
 */
private void setTeacherOrganizationInfo(TeacherVO vo, Long userId) {
    // 获取用户关联的组织信息
    // 按级别分类：学院(level=1)、专业(level=2)、班级(level=3)
    // 支持教师管理多个班级
}
```

## 数据来源

### 组织信息获取流程
1. 通过 `UserOrganizationMapper.selectByUserId()` 获取用户关联的组织ID列表
2. 通过 `OrganizationMapper.selectByIdsAndStatus()` 获取组织详细信息
3. 按组织级别分类：
   - `org_level = 1` → 学院
   - `org_level = 2` → 专业  
   - `org_level = 3` → 班级

### 数据表关系
- `sys_user_organization` - 用户组织关联表
- `sys_organization` - 组织信息表
- `sys_student` - 学生表（已有major、class_name字段）
- `sys_teacher` - 教师表

## 显示效果

### 学生管理
- **表格：** 学号 | 姓名 | 性别 | **学院** | 专业 | 班级 | 手机号码 | 邮箱 | 年级 | 学制 | 当前学期 | 当前学年学期 | 预计毕业时间 | 状态 | 创建时间 | 操作
- **详情：** 包含完整的学院信息显示

### 教师管理  
- **表格：** 教师编号 | 姓名 | 性别 | 手机号码 | 邮箱 | **学院** | **专业** | **班级** | 状态 | 创建时间 | 操作
- **班级显示：** 软件工程2021级1班, 软件工程2021级2班（多班级用逗号分隔）
- **详情：** 包含完整的学院、专业、班级信息

## 技术要点

1. **数据一致性** - 组织信息从关联表动态获取，确保数据实时性
2. **容错处理** - 组织信息获取失败不影响主要功能，返回"未分配"状态
3. **多班级支持** - 教师可以管理多个班级，前端正确格式化显示
4. **性能优化** - 批量查询组织信息，避免N+1查询问题

## 相关文件清单

### 前端文件
- `frontend/src/views/StudentManagement.vue` - 学生管理页面
- `frontend/src/views/TeacherManagement.vue` - 教师管理页面

### 后端文件
- `src/main/java/com/example/vo/StudentVO.java` - 学生VO
- `src/main/java/com/example/vo/TeacherVO.java` - 教师VO  
- `src/main/java/com/example/service/impl/StudentServiceImpl.java` - 学生服务实现
- `src/main/java/com/example/service/impl/TeacherServiceImpl.java` - 教师服务实现

### 数据库表
- `sys_user_organization` - 用户组织关联
- `sys_organization` - 组织信息
- `sys_student` - 学生信息
- `sys_teacher` - 教师信息

## 测试建议

1. **学生管理测试**
   - 验证学院字段正确显示
   - 测试未分配组织的学生显示效果

2. **教师管理测试**  
   - 验证单个班级教师的显示
   - 验证多个班级教师的显示格式
   - 测试学院、专业信息的正确显示

3. **边界情况测试**
   - 用户未分配任何组织的显示效果
   - 组织信息查询失败的容错处理 