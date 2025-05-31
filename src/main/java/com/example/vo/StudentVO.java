package com.example.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生视图对象
 */
@Data
public class StudentVO {
    
    private Long id;
    
    private Long userId;
    
    private String studentNo;
    
    private String name;
    
    private String gender;
    
    private String email;
    
    private String phone;
    
    private String major;
    
    private String className;
    
    private String grade; // 年级
    
    private String educationSystem; // 学制
    
    private LocalDateTime enrollmentDate;
    
    private LocalDateTime graduationDate;
    
    private Integer currentYear; // 当前学年
    
    private Integer currentSemester; // 当前学期
    
    private String currentAcademicYear; // 当前学年学期
    
    private Integer status;
    
    private String statusName;
    
    private String collegeName; // 学院名称
    
    private String majorName; // 专业名称
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 