package com.example.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程申请数据传输对象
 */
@Data
public class CourseApplicationDTO {
    
    private Long id;
    
    private String courseName;
    
    private String courseDescription;
    
    private Integer totalHours;
    
    private String academicYear;
    
    private String semester;
    
    private String applicationReason;
    
    private Integer status; // 0-待审核 1-已通过 2-已拒绝
    
    private String reviewComment;
    
    private LocalDateTime applicationTime;
} 