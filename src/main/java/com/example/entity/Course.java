package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_course")
public class Course {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String courseName;
    
    private String courseDescription;
    
    private Integer maxStudents;
    
    private Integer currentStudents;
    
    private Long teacherId;
    
    private String teacherName;
    
    private Integer courseHours;
    
    private String academicYear;
    
    private Integer status;
    
    private Integer allowApplication;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 