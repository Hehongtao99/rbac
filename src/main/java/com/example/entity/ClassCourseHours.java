package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_class_course_hours")
public class ClassCourseHours {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long applicationId;
    
    private Long classId;
    
    private String className;
    
    private String courseName;
    
    private Long teacherId;
    
    private String teacherName;
    
    private String academicYear;
    
    private String semester;
    
    private Integer totalHours;
    
    private Integer usedHours;
    
    private Integer remainingHours;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 