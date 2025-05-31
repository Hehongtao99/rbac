package com.example.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生视图对象
 */
@Data
public class StudentVO {
    
    private Long id;
    
    private String studentName;
    
    private String studentCode;
    
    private String email;
    
    private String phone;
    
    private String grade; // 年级
    
    private String educationSystem; // 学制
    
    private Integer currentYear; // 当前学年
    
    private Integer currentSemester; // 当前学期
    
    private Integer status;
    
    private String statusName;
    
    private String className; // 班级名称
    
    private String majorName; // 专业名称
    
    private String collegeName; // 学院名称
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 