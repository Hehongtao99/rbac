package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Course {
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
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private Integer deleted;
} 