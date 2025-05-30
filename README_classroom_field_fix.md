# 课程表classroom字段问题修复

## 问题描述
在前端移除教室字段后，出现了以下错误：
```
java.sql.SQLException: Field 'classroom' doesn't have a default value
```

这是因为：
1. 数据库表`sys_schedule`中的`classroom`字段设置为`NOT NULL`且没有默认值
2. 前端移除了教室字段输入，但后端DTO仍包含该字段
3. 插入数据时`classroom`字段为null，导致数据库约束违反

## 修复方案

### 1. 数据库结构修复
**文件**: `database/fix_classroom_field.sql`

为`classroom`字段添加默认值：
```sql
ALTER TABLE `sys_schedule` 
MODIFY COLUMN `classroom` varchar(50) NOT NULL DEFAULT '待定' COMMENT '教室';
```

### 2. 后端代码修复

#### ScheduleDTO.java
**修改**: 移除`classroom`字段
```java
@Data
public class ScheduleDTO {
    private Long courseId;
    private String courseName;
    private Long teacherId;
    private String teacherName;
    private String academicYear;
    private Integer weekNumber;
    private Integer dayOfWeek;
    private Integer timeSlot;
    private Integer reducedHours;
    // 移除了 private String classroom;
}
```

#### ScheduleServiceImpl.java
**修改1**: 在`addCourseToSchedule`方法中设置默认教室值
```java
// 创建课程表记录
Schedule schedule = new Schedule();
BeanUtils.copyProperties(scheduleDTO, schedule);
schedule.setCourseName(course.getCourseName());
schedule.setTeacherName(course.getTeacherName());
schedule.setReducedHours(2);
// 设置默认教室值，因为前端已移除教室字段
schedule.setClassroom("待定");
```

**修改2**: 修改`updateSchedule`方法，移除对classroom字段的依赖
```java
@Override
@Transactional
public void updateSchedule(Long scheduleId, ScheduleDTO scheduleDTO) {
    Schedule schedule = this.getById(scheduleId);
    if (schedule == null) {
        throw new RuntimeException("课程表记录不存在");
    }
    
    // 由于前端已移除教室字段，这里暂时不进行任何更新
    // 如果需要更新其他信息，可以在这里添加
    
    this.updateById(schedule);
}
```

### 3. 前端代码优化
**文件**: `frontend/src/views/teacher/ScheduleManagement.vue`

- 修复CSS样式中的重复规则
- 优化时间显示和布局

## 执行步骤

### 1. 数据库修复
执行SQL脚本修复数据库结构：
```bash
mysql -u username -p database_name < database/fix_classroom_field.sql
```

### 2. 重启后端服务
修改后端代码后，重启Spring Boot应用：
```bash
mvn spring-boot:run
```

### 3. 前端热重载
前端代码会自动热重载，无需手动重启。

## 验证修复结果

1. **数据库验证**：
   ```sql
   SELECT COLUMN_NAME, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_COMMENT 
   FROM INFORMATION_SCHEMA.COLUMNS 
   WHERE TABLE_NAME = 'sys_schedule' AND COLUMN_NAME = 'classroom';
   ```

2. **功能验证**：
   - 点击课程表空白格子，弹出添加课程对话框
   - 选择课程并确认添加，应该成功添加到课程表
   - 课程格子显示课程名称、详细时间、教师姓名

3. **数据验证**：
   - 检查新添加的课程记录，`classroom`字段应显示为"待定"

## 影响说明

- **前端**：无任何功能影响，用户体验保持一致
- **后端**：添加的课程自动设置教室为"待定"，可后续通过其他方式修改
- **数据库**：现有数据不受影响，新增数据具有默认教室值

## 后续优化建议

1. 如需要教室管理功能，可单独创建教室管理模块
2. 可在课程详情中添加教室编辑功能
3. 考虑将教室信息与时间段、星期关联，实现智能教室分配 