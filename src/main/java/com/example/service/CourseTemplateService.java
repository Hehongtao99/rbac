package com.example.service;

import com.example.entity.CourseTemplate;
import com.example.common.Result;

public interface CourseTemplateService {
    
    /**
     * 获取模板列表
     */
    Result<Object> getTemplateList(Integer page, Integer size, String keyword);
    
    /**
     * 获取启用的模板列表
     */
    Result<Object> getEnabledTemplateList(Integer page, Integer size, String keyword, String academicYear, String semester);
    
    /**
     * 创建模板
     */
    Result<Object> createTemplate(CourseTemplate template);
    
    /**
     * 更新模板
     */
    Result<Object> updateTemplate(CourseTemplate template);
    
    /**
     * 删除模板
     */
    Result<Object> deleteTemplate(Long id);
    
    /**
     * 根据ID获取模板详情
     */
    Result<Object> getTemplateById(Long id);
} 