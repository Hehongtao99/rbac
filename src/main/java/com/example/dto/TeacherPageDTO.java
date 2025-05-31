package com.example.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教师分页查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TeacherPageDTO extends BasePageDTO {
    
    /**
     * 教师状态筛选
     */
    private Integer status;
    
    /**
     * 职位筛选
     */
    private String position;
    
    /**
     * 部门筛选
     */
    private String department;
} 