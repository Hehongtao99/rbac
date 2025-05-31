package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StudentCourseSelection {
    private Long id;
    
    private Long studentId;
    
    private String studentNo;
    
    private String studentName;
    
    private Long courseApplicationId;
    
    private String courseName;
    
    private Long teacherId;
    
    private String teacherName;
    
    private String academicYear;
    
    private String semester;
    
    private Integer courseHours;
    
    private LocalDateTime selectionTime;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private Integer deleted;
} 