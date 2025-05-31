package com.example.dto;

import lombok.Data;

/**
 * 学生学期设置DTO
 */
@Data
public class StudentSemesterDTO {
    
    /**
     * 当前学年
     */
    private Integer currentYear;
    
    /**
     * 当前学期
     */
    private Integer currentSemester;
} 