package com.example.dto;

import lombok.Data;

/**
 * 学生年级分配DTO
 */
@Data
public class StudentGradeAssignDTO {
    
    /**
     * 年级
     */
    private String grade;
    
    /**
     * 学制
     */
    private String educationSystem;
} 