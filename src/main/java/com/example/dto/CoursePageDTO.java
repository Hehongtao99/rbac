package com.example.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程分页查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CoursePageDTO extends BasePageDTO {
    
    /**
     * 课程状态筛选
     */
    private Integer status;
    
    /**
     * 学年筛选
     */
    private String academicYear;
    
    /**
     * 学期筛选
     */
    private String semester;
    
    /**
     * 课程类型筛选
     */
    private String courseType;
    
    /**
     * 教师筛选
     */
    private String teacherFilter;
    
    /**
     * 课程筛选
     */
    private String courseFilter;
} 