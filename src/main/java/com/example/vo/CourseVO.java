package com.example.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程视图对象
 */
@Data
public class CourseVO {
    
    private Long id;
    
    private String courseName;
    
    private String courseCode;
    
    private String courseDescription;
    
    private Integer totalHours;
    
    private Integer credits;
    
    private String courseType; // 课程类型
    
    private String academicYear;
    
    private String semester;
    
    private Long teacherId;
    
    private String teacherName;
    
    private Integer status;
    
    private String statusName;
    
    private Boolean allowApplication; // 是否允许申请
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 