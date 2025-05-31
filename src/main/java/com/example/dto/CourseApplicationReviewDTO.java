package com.example.dto;

import lombok.Data;

/**
 * 课程申请审核DTO
 */
@Data
public class CourseApplicationReviewDTO {
    
    private Integer status; // 1-通过 2-拒绝
    
    private String reviewComment;
} 