package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Student {
    private Long id;
    
    private Long userId;
    
    private String studentNo;
    
    private String name;
    
    private String gender;
    
    private String phone;
    
    private String email;
    
    private String major;
    
    private String className;
    
    private String grade;
    
    private String educationSystem;
    
    private LocalDateTime enrollmentDate;
    
    private LocalDateTime graduationDate;
    
    private Integer currentYear;
    
    private Integer currentSemester;
    
    private String currentAcademicYear;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private Integer deleted;
} 