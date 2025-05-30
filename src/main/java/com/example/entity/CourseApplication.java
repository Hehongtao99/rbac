package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_course_application")
public class CourseApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long templateId;
    
    private String courseName;
    
    private Long teacherId;
    
    private String teacherName;
    
    private String academicYear;
    
    private String semester;
    
    private Integer maxStudents;
    
    private Integer courseHours;
    
    private String reason;
    
    private Integer status;
    
    private LocalDateTime applyTime;
    
    private LocalDateTime reviewTime;
    
    private Long reviewerId;
    
    private String reviewerName;
    
    private String reviewComment;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 