# 使用JWT获取用户信息修复

## 问题描述
在课程表功能中，虽然教师姓名显示问题得到修复，但发现了更严重的问题：
- 当前登录用户是"2"（用户名/昵称都是"2"）
- 但课程表显示的是"张老师"的课程
- 说明前端传递的teacherId不正确，存在用户身份混乱问题

## 问题根因
1. **前端依赖localStorage**：使用`localStorage.getItem('user')`获取用户信息可能不准确或已过期
2. **缺乏服务器端验证**：后端直接信任前端传递的teacherId参数
3. **安全隐患**：前端可以随意传递任何teacherId，访问其他教师的数据

## 解决方案：使用JWT获取用户信息

### 1. 后端修改 ✅

#### 修改ScheduleServiceImpl
所有方法都改为从JWT获取当前用户信息，而不是依赖前端传参：

**修改前**：
```java
public void addCourseToSchedule(ScheduleDTO scheduleDTO) {
    // 使用前端传递的teacherId（不安全）
    Long teacherId = scheduleDTO.getTeacherId();
}
```

**修改后**：
```java
public void addCourseToSchedule(ScheduleDTO scheduleDTO) {
    // 从JWT获取当前登录用户信息（安全）
    User currentUser = userContextUtil.getCurrentUser();
    if (currentUser == null) {
        throw new RuntimeException("获取当前用户信息失败，请重新登录");
    }
    
    Long teacherId = currentUser.getId();
    String teacherName = currentUser.getNickname() != null && !currentUser.getNickname().isEmpty() 
                       ? currentUser.getNickname() 
                       : currentUser.getUsername();
}
```

#### 涉及的方法修改：
- `addCourseToSchedule()` - 添加课程
- `getWeeklySchedule()` - 获取周课程表
- `getAvailableCourses()` - 获取可选课程
- `checkTimeConflict()` - 检查时间冲突
- `removeCourseFromSchedule()` - 删除课程

#### 安全验证增强：
```java
// 验证课程是否属于当前教师
if (!course.getTeacherId().equals(teacherId)) {
    throw new RuntimeException("您没有权限添加此课程");
}

// 验证课程表记录是否属于当前教师
if (!schedule.getTeacherId().equals(currentUser.getId())) {
    throw new RuntimeException("您没有权限删除此课程安排");
}
```

### 2. DTO简化 ✅

**ScheduleDTO.java** - 移除不需要的字段：
```java
@Data
public class ScheduleDTO {
    private Long courseId;
    private String courseName;
    // 移除：private Long teacherId;
    // 移除：private String teacherName;
    private String academicYear;
    private Integer weekNumber;
    private Integer dayOfWeek;
    private Integer timeSlot;
    private Integer reducedHours;
}
```

### 3. 前端简化 ✅

#### API接口修改：
**修改前**：
```javascript
getWeeklySchedule(teacherId, academicYear, weekNumber)
getAvailableCourses(teacherId, academicYear)
checkTimeConflict(teacherId, academicYear, weekNumber, dayOfWeek, timeSlot)
```

**修改后**：
```javascript
getWeeklySchedule(academicYear, weekNumber)  // 移除teacherId
getAvailableCourses(academicYear)            // 移除teacherId
checkTimeConflict(academicYear, weekNumber, dayOfWeek, timeSlot)  // 移除teacherId
```

#### 前端数据传递：
**修改前**：
```javascript
const scheduleData = {
    courseId: this.addForm.courseId,
    teacherId: this.currentUser.id,  // 不安全
    // ...
}
```

**修改后**：
```javascript
const scheduleData = {
    courseId: this.addForm.courseId,
    // 移除teacherId，后端从JWT自动获取
    // ...
}
```

### 4. Controller路由更新 ✅

**修改前**：
```java
@GetMapping("/weekly/{teacherId}")
@GetMapping("/available-courses/{teacherId}")
```

**修改后**：
```java
@GetMapping("/weekly")              // 移除路径参数
@GetMapping("/available-courses")   // 移除路径参数
```

## 安全优势

1. **用户身份验证**：所有操作都基于JWT中的真实用户身份
2. **防止越权访问**：无法通过修改前端参数访问其他用户数据
3. **数据一致性**：确保课程表操作与当前登录用户一致
4. **简化前端**：前端无需管理用户ID传递

## 执行步骤

### 1. 重启后端服务
代码修改完成后，重启Spring Boot应用。

### 2. 清除浏览器缓存
建议清除浏览器缓存和localStorage，重新登录。

### 3. 验证功能
- 使用正确的教师账号登录
- 验证课程表显示的是当前登录教师的课程
- 验证添加的新课程显示正确的教师姓名

## 预期效果

修复后：
- ✅ 课程表只显示当前登录教师的课程
- ✅ 添加的课程显示当前登录教师的正确姓名
- ✅ 无法访问其他教师的课程数据
- ✅ 系统安全性大幅提升

## 技术架构改进

```
修改前：
前端localStorage -> 前端传递teacherId -> 后端直接使用

修改后：
JWT Token -> 后端解析用户信息 -> 安全的用户身份验证
```

这种架构更安全、更可靠，完全消除了用户身份混乱的问题。 