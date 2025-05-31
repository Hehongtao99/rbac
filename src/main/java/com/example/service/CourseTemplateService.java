package com.example.service;

import com.example.common.PageResult;
import com.example.dto.CourseDTO;
import com.example.dto.CourseTemplatePageDTO;
import com.example.vo.CourseTemplateVO;

public interface CourseTemplateService {
    
    /**
     * 分页获取模板列表
     */
    PageResult<CourseTemplateVO> getTemplateList(CourseTemplatePageDTO pageDTO);
    
    /**
     * 分页获取启用的模板列表
     */
    PageResult<CourseTemplateVO> getEnabledTemplateList(CourseTemplatePageDTO pageDTO);
    
    /**
     * 创建模板
     */
    void createTemplate(CourseDTO templateDTO);
    
    /**
     * 更新模板
     */
    void updateTemplate(Long id, CourseDTO templateDTO);
    
    /**
     * 删除模板
     */
    void deleteTemplate(Long id);
    
    /**
     * 根据ID获取模板详情
     */
    CourseTemplateVO getTemplateById(Long id);
} 