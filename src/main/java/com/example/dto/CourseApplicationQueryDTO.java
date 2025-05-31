package com.example.dto;

import lombok.Data;

/**
 * 课程申请查询条件DTO
 */
@Data
public class CourseApplicationQueryDTO {
    
    private Integer pageNum = 1;
    
    private Integer pageSize = 10;
    
    private String keyword;
    
    private Integer status;
    
    private String academicYear;
    
    private String semester;
    
    private Long teacherId;
} 