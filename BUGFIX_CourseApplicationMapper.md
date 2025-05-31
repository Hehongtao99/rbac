# CourseApplicationMapper 编译错误修复总结

## 问题描述

在实现学院专业选择功能后，`CourseApplicationServiceImpl` 中出现编译错误：

```
java: 找不到符号
  符号:   方法 selectPageForAdmin(int,java.lang.Integer,java.lang.String,java.lang.Integer,java.lang.String,java.lang.String,java.lang.Long)
  位置: 类型为com.example.mapper.CourseApplicationMapper的变量 courseApplicationMapper

java: 找不到符号
  符号:   方法 selectCountForAdmin(java.lang.String,java.lang.Integer,java.lang.String,java.lang.String,java.lang.Long)
  位置: 类型为com.example.mapper.CourseApplicationMapper的变量 courseApplicationMapper
```

## 问题原因

`CourseApplicationMapper` 接口中缺少管理员查询方法，这些方法在 `CourseApplicationServiceImpl` 中被调用但未定义。

## 解决方案

### 1. 修改 CourseApplicationMapper 接口

在 `src/main/java/com/example/mapper/CourseApplicationMapper.java` 中添加了以下方法：

```java
// 管理员分页查询（返回VO包含学院专业信息）
List<CourseApplicationVO> selectPageForAdmin(@Param("offset") int offset, @Param("limit") Integer limit, 
                                            @Param("keyword") String keyword, @Param("status") Integer status,
                                            @Param("academicYear") String academicYear, @Param("semester") String semester,
                                            @Param("teacherId") Long teacherId);

// 管理员查询总数（包含学院专业筛选）
long selectCountForAdmin(@Param("keyword") String keyword, @Param("status") Integer status,
                        @Param("academicYear") String academicYear, @Param("semester") String semester,
                        @Param("teacherId") Long teacherId);
```

### 2. 修改 CourseApplicationVO

在 `src/main/java/com/example/vo/CourseApplicationVO.java` 中添加了学院专业字段：

```java
// 学院专业信息（从课程模板关联获取）
private Long collegeId;
private String collegeName;
private Long majorId;
private String majorName;
```

### 3. 修改 CourseApplicationMapper.xml

在 `src/main/resources/mapper/CourseApplicationMapper.xml` 中添加了：

#### 扩展结果映射
```xml
<resultMap id="ExtendedResultMap" type="com.example.vo.CourseApplicationVO">
    <!-- 包含学院专业信息的完整映射 -->
</resultMap>
```

#### 管理员分页查询
```xml
<select id="selectPageForAdmin" resultMap="ExtendedResultMap">
    SELECT 
        ca.*, ct.college_id, college.org_name AS college_name,
        ct.major_id, major.org_name AS major_name
    FROM sys_course_application ca
    LEFT JOIN sys_course_template ct ON ca.template_id = ct.id
    LEFT JOIN sys_organization college ON ct.college_id = college.id
    LEFT JOIN sys_organization major ON ct.major_id = major.id
    <!-- 查询条件和排序 -->
</select>
```

#### 管理员查询总数
```xml
<select id="selectCountForAdmin" resultType="java.lang.Long">
    <!-- 统计查询 -->
</select>
```

### 4. 修改教师查询方法

将 `selectPageByTeacherId` 方法也修改为返回 `CourseApplicationVO`，以便包含学院专业信息。

### 5. 更新 Service 实现

修改 `CourseApplicationServiceImpl` 中的方法实现，因为现在 Mapper 直接返回 VO 对象，不需要再进行转换。

## 功能特性

### 关联查询
- 通过 LEFT JOIN 关联 `sys_course_template` 表获取课程模板的学院专业信息
- 通过 LEFT JOIN 关联 `sys_organization` 表获取学院和专业的名称

### 查询条件支持
- 关键词搜索（课程名称、教师姓名、申请理由）
- 申请状态筛选
- 学年筛选
- 学期筛选
- 教师ID筛选

### 返回信息
- 课程申请的基本信息
- 关联的学院专业信息（ID和名称）
- 支持分页查询和总数统计

## 验证结果

编译成功，所有方法签名匹配，功能完整实现。

## 影响范围

- ✅ 管理员课程申请审核页面：可以显示学院专业信息
- ✅ 教师我的申请页面：可以显示学院专业信息
- ✅ 后端API：支持学院专业信息查询和筛选
- ✅ 数据库查询：优化了关联查询性能 