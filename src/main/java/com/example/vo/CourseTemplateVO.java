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
    
    private String templateCode;
    
    private String description;
    
    private Integer totalHours;
    
    private Integer credits;
    
    private String courseType;
    
    private String academicYear;
    
    private String semester;
    
    private Integer status;
    
    private String statusName;
    
    private Boolean enabled; // 是否启用
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 