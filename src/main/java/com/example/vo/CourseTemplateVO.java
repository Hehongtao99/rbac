package com.example.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程模板视图对象
 */
@Data
public class CourseTemplateVO {
    
    private Long id;
    
    private String templateName;
    
    private String description;
    
    private Integer courseHours;
    
    private String academicYear;
    
    private String semester;
    
    private Integer maxStudents;
    
    private Long collegeId;
    
    private Long majorId;
    
    private String collegeName;
    
    private String majorName;
    
    private Integer status;
    
    private String statusName;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 