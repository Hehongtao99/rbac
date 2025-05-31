package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CourseTemplate {
    private Long id;
    
    private String templateName;
    
    private String description;
    
    private Integer courseHours;
    
    private String academicYear;
    
    private String semester;
    
    private Integer maxStudents;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private Integer deleted;
} 