package com.example.service;

import com.example.common.PageResult;
import com.example.dto.CourseTemplateDTO;
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
     * 获取教师可申请的课程模板列表（根据教师的学院专业筛选）
     */
    PageResult<CourseTemplateVO> getAvailableTemplatesForTeacher(CourseTemplatePageDTO pageDTO);
    
    /**
     * 创建模板
     */
    void createTemplate(CourseTemplateDTO templateDTO);
    
    /**
     * 更新模板
     */
    void updateTemplate(Long id, CourseTemplateDTO templateDTO);
    
    /**
     * 删除模板
     */
    void deleteTemplate(Long id);
    
    /**
     * 根据ID获取模板详情
     */
    CourseTemplateVO getTemplateById(Long id);
} 