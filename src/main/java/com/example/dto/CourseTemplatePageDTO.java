package com.example.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程模板分页查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseTemplatePageDTO extends BasePageDTO {
    
    /**
     * 模板状态筛选
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
     * 学院筛选
     */
    private Long collegeId;
    
    /**
     * 专业筛选
     */
    private Long majorId;
    
    /**
     * 是否只查询启用的模板
     */
    private Boolean enabledOnly = false;
} 