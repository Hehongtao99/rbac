package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_course_template")
public class CourseTemplate {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String templateName;
    
    private String description;
    
    private Integer courseHours;
    
    private String academicYear;
    
    private String semester;
    
    private Integer maxStudents;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 