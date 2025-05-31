package com.example.service.impl;

import com.example.entity.CourseTemplate;
import com.example.mapper.CourseTemplateMapper;
import com.example.service.CourseTemplateService;
import com.example.common.Result;
import com.example.common.PageResult;
import com.example.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseTemplateServiceImpl implements CourseTemplateService {

    @Autowired
    private CourseTemplateMapper courseTemplateMapper;

    @Override
    public Result<Object> getTemplateList(Integer page, Integer size, String keyword) {
        int offset = PageUtil.calculateOffset(page, size);
        List<CourseTemplate> templates = courseTemplateMapper.selectPage(offset, size, keyword);
        long total = courseTemplateMapper.selectCount(new CourseTemplate());
        
        PageResult<CourseTemplate> result = PageUtil.createPageResult(page, size, total, templates);
        return Result.success(result);
    }

    @Override
    public Result<Object> getEnabledTemplateList(Integer page, Integer size, String keyword, String academicYear, String semester) {
        int offset = PageUtil.calculateOffset(page, size);
        
        // 构建查询条件
        CourseTemplate queryTemplate = new CourseTemplate();
        queryTemplate.setStatus(1); // 只查询启用的模板
        if (StringUtils.hasText(academicYear)) {
            queryTemplate.setAcademicYear(academicYear);
        }
        if (StringUtils.hasText(semester)) {
            queryTemplate.setSemester(semester);
        }
        
        List<CourseTemplate> templates = courseTemplateMapper.selectPageByCondition(offset, size, keyword, queryTemplate);
        long total = courseTemplateMapper.selectCount(queryTemplate);
        
        PageResult<CourseTemplate> result = PageUtil.createPageResult(page, size, total, templates);
        return Result.success(result);
    }

    @Override
    public Result<Object> createTemplate(CourseTemplate template) {
        try {
            if (template.getStatus() == null) {
                template.setStatus(1);
            }
            template.setCreateTime(LocalDateTime.now());
            template.setUpdateTime(LocalDateTime.now());
            template.setDeleted(0);
            
            courseTemplateMapper.insert(template);
            return Result.success(template);
        } catch (Exception e) {
            return Result.error("创建失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Object> updateTemplate(CourseTemplate template) {
        try {
            template.setUpdateTime(LocalDateTime.now());
            courseTemplateMapper.updateById(template);
            return Result.success(template);
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Object> deleteTemplate(Long id) {
        try {
            courseTemplateMapper.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Object> getTemplateById(Long id) {
        try {
            CourseTemplate template = courseTemplateMapper.selectById(id);
            if (template != null) {
                return Result.success(template);
            } else {
                return Result.error("模板不存在");
            }
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
} 