package com.example.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生分页查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentPageDTO extends BasePageDTO {
    
    /**
     * 学生状态筛选
     */
    private Integer status;
    
    /**
     * 年级筛选
     */
    private String grade;
    
    /**
     * 学制筛选
     */
    private String educationSystem;
    
    /**
     * 班级筛选
     */
    private String className;
} 