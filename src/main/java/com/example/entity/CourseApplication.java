package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CourseApplication {
    private Long id;
    
    private Long templateId;
    
    private String courseName;
    
    private Long teacherId;
    
    private String teacherName;
    
    private String academicYear;
    
    private String semester;
    
    private Integer maxStudents;
    
    private Integer courseHours;
    
    private Integer remainingHours; // 剩余课时
    
    private String reason;
    
    private Integer status;
    
    private LocalDateTime applyTime;
    
    private LocalDateTime reviewTime;
    
    private Long reviewerId;
    
    private String reviewerName;
    
    private String reviewComment;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private Integer deleted;
} 