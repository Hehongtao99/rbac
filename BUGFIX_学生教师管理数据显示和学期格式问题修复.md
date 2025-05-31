# 学生教师管理数据显示和学期格式问题修复

## 问题描述
1. 学生管理和教师管理的数据显示不正确
2. 学生的当前学期显示格式错误，应该显示为"xxxx-xxxx年第x学期"而不是"大几几学期"

## 问题原因分析

### 1. VO类字段名不匹配
`TeacherVO`和`StudentVO`类的字段名与对应的实体类`Teacher`和`Student`字段名不一致，导致数据映射失败。

**原始TeacherVO问题字段：**
- `teacherName` 应为 `name`
- `teacherCode` 应为 `teacherNo`
- 缺少 `userId`, `gender` 等字段

**原始StudentVO问题字段：**
- `studentName` 应为 `name`
- `studentCode` 应为 `studentNo`
- 缺少 `userId`, `gender`, `major`, `className`, `enrollmentDate`, `graduationDate`, `currentAcademicYear` 等字段

### 2. 学期格式显示错误
前端`StudentManagement.vue`中的`formatCurrentSemester`函数显示为"大几几学期"格式，但需求是显示"xxxx-xxxx年第x学期"格式。

### 3. 后端学年学期计算缺失
后端`StudentServiceImpl`的`convertToVO`方法没有生成正确的学年学期字符串。

## 修复方案

### 1. 修复TeacherVO类
**文件：** `src/main/java/com/example/vo/TeacherVO.java`

```java
@Data
public class TeacherVO {
    private Long id;
    private Long userId;           // 新增
    private String teacherNo;      // 原teacherCode改名
    private String name;           // 原teacherName改名
    private String gender;         // 新增
    private String email;
    private String phone;
    private String department;
    private String title;
    private Integer status;
    private String statusName;
    private List<String> organizationNames;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

### 2. 修复StudentVO类
**文件：** `src/main/java/com/example/vo/StudentVO.java`

```java
@Data
public class StudentVO {
    private Long id;
    private Long userId;                    // 新增
    private String studentNo;               // 原studentCode改名
    private String name;                    // 原studentName改名
    private String gender;                  // 新增
    private String email;
    private String phone;
    private String major;                   // 新增
    private String className;               // 新增
    private String grade;
    private String educationSystem;
    private LocalDateTime enrollmentDate;   // 新增
    private LocalDateTime graduationDate;   // 新增
    private Integer currentYear;
    private Integer currentSemester;
    private String currentAcademicYear;     // 新增
    private Integer status;
    private String statusName;
    private String collegeName;
    private String majorName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

### 3. 增强StudentServiceImpl学年学期生成
**文件：** `src/main/java/com/example/service/impl/StudentServiceImpl.java`

在`convertToVO`方法中增加学年学期格式生成逻辑：

```java
private StudentVO convertToVO(Student student) {
    StudentVO vo = new StudentVO();
    BeanUtils.copyProperties(student, vo);
    
    // 设置状态名称
    vo.setStatusName(STATUS_MAP.get(student.getStatus()));
    
    // 生成当前学年学期格式：xxxx-xxxx年第x学期
    if (student.getGrade() != null && student.getCurrentSemester() != null && student.getCurrentYear() != null) {
        String currentAcademicYear = generateAcademicYearSemester(
            student.getGrade(), student.getCurrentYear(), student.getCurrentSemester());
        vo.setCurrentAcademicYear(currentAcademicYear);
    } else {
        vo.setCurrentAcademicYear("未设置");
    }
    
    return vo;
}

private String generateAcademicYearSemester(String grade, Integer currentYear, Integer currentSemester) {
    try {
        int gradeYear = Integer.parseInt(grade);
        int academicStartYear = gradeYear + (currentYear - 1);
        int academicEndYear = academicStartYear + 1;
        String semesterText = currentSemester == 1 ? "第一学期" : "第二学期";
        return String.format("%d-%d年%s", academicStartYear, academicEndYear, semesterText);
    } catch (Exception e) {
        return "格式错误";
    }
}
```

### 4. 修复前端学期格式显示
**文件：** `frontend/src/views/StudentManagement.vue`

修复`formatCurrentSemester`函数：

```javascript
const formatCurrentSemester = (student) => {
  // 如果有设置currentAcademicYear，直接返回
  if (student.currentAcademicYear && student.currentAcademicYear !== '未设置') {
    return student.currentAcademicYear
  }
  
  // 如果没有设置学期信息，返回未设置
  if (!student.currentYear || !student.currentSemester || !student.grade) {
    return '未设置'
  }
  
  try {
    const gradeYear = parseInt(student.grade)
    const academicStartYear = gradeYear + (student.currentYear - 1)
    const academicEndYear = academicStartYear + 1
    const semesterText = student.currentSemester === 1 ? '第一学期' : '第二学期'
    return `${academicStartYear}-${academicEndYear}年${semesterText}`
  } catch (error) {
    return '格式错误'
  }
}
```

## 修复效果

### 修复前
- 教师管理：字段显示为空或undefined
- 学生管理：字段显示为空或undefined
- 学期格式：显示为"大一上学期"、"大二下学期"等

### 修复后
- 教师管理：正确显示教师编号、姓名、性别、部门、职称等信息
- 学生管理：正确显示学号、姓名、性别、专业、班级等信息
- 学期格式：正确显示为"2020-2021年第一学期"、"2021-2022年第二学期"等

## 测试建议

1. **教师管理页面测试**
   - 验证教师列表数据正确显示
   - 验证教师详情查看功能

2. **学生管理页面测试**
   - 验证学生列表数据正确显示
   - 验证学生详情查看功能
   - 验证年级和学制分配功能
   - 验证学期设置功能，确认学期格式为"xxxx-xxxx年第x学期"

3. **数据完整性测试**
   - 确认所有字段都有正确的数据映射
   - 验证分页功能正常工作
   - 验证搜索功能正常工作

## 技术要点

1. **字段映射一致性**：确保VO类字段名与实体类字段名完全匹配，便于BeanUtils.copyProperties正确工作

2. **学年学期计算逻辑**：基于入学年级和当前学年计算出正确的学年起止年份

3. **前后端数据格式统一**：后端生成学年学期字符串，前端优先使用后端生成的格式，降低计算复杂度

4. **异常处理**：在日期和数字解析时加入try-catch处理，避免格式错误导致页面崩溃 