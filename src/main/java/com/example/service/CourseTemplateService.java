package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.CourseTemplate;
import com.example.mapper.CourseTemplateMapper;
import com.example.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseTemplateService {

    @Autowired
    private CourseTemplateMapper courseTemplateMapper;

    public Result<Object> getTemplateList(Integer page, Integer size, String keyword) {
        Page<CourseTemplate> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<CourseTemplate> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(CourseTemplate::getTemplateName, keyword)
                   .or()
                   .like(CourseTemplate::getDescription, keyword);
        }
        
        wrapper.orderByDesc(CourseTemplate::getCreateTime);
        
        Page<CourseTemplate> result = courseTemplateMapper.selectPage(pageObj, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());
        
        return Result.success(data);
    }

    public Result<Object> getEnabledTemplateList(Integer page, Integer size, String keyword, String academicYear, String semester) {
        Page<CourseTemplate> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<CourseTemplate> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(CourseTemplate::getTemplateName, keyword)
                   .or()
                   .like(CourseTemplate::getDescription, keyword);
        }
        
        if (StringUtils.hasText(academicYear)) {
            wrapper.eq(CourseTemplate::getAcademicYear, academicYear);
        }
        
        if (StringUtils.hasText(semester)) {
            wrapper.eq(CourseTemplate::getSemester, semester);
        }
        
        wrapper.eq(CourseTemplate::getStatus, 1)
               .orderByDesc(CourseTemplate::getCreateTime);
        
        Page<CourseTemplate> result = courseTemplateMapper.selectPage(pageObj, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());
        
        return Result.success(data);
    }

    public Result<Object> createTemplate(CourseTemplate template) {
        try {
            if (template.getStatus() == null) {
                template.setStatus(1);
            }
            courseTemplateMapper.insert(template);
            return Result.success(template);
        } catch (Exception e) {
            return Result.error("创建失败：" + e.getMessage());
        }
    }

    public Result<Object> updateTemplate(CourseTemplate template) {
        try {
            courseTemplateMapper.updateById(template);
            return Result.success(template);
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    public Result<Object> deleteTemplate(Long id) {
        try {
            courseTemplateMapper.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

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