package com.example.service.impl;

import com.example.common.PageResult;
import com.example.dto.CourseDTO;
import com.example.dto.CourseTemplatePageDTO;
import com.example.entity.CourseTemplate;
import com.example.mapper.CourseTemplateMapper;
import com.example.service.CourseTemplateService;
import com.example.util.PageUtil;
import com.example.vo.CourseTemplateVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseTemplateServiceImpl implements CourseTemplateService {

    @Autowired
    private CourseTemplateMapper courseTemplateMapper;

    // 状态映射
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();
    
    static {
        STATUS_MAP.put(0, "禁用");
        STATUS_MAP.put(1, "启用");
    }

    @Override
    public PageResult<CourseTemplateVO> getTemplateList(CourseTemplatePageDTO pageDTO) {
        int offset = PageUtil.calculateOffset(pageDTO.getPage(), pageDTO.getSize());
        List<CourseTemplate> templates = courseTemplateMapper.selectPage(offset, pageDTO.getSize(), pageDTO.getKeyword());
        long total = courseTemplateMapper.selectCount(new CourseTemplate());
        
        // 转换为VO
        List<CourseTemplateVO> voList = templates.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageUtil.createPageResult(pageDTO.getPage(), pageDTO.getSize(), total, voList);
    }

    @Override
    public PageResult<CourseTemplateVO> getEnabledTemplateList(CourseTemplatePageDTO pageDTO) {
        int offset = PageUtil.calculateOffset(pageDTO.getPage(), pageDTO.getSize());
        
        // 构建查询条件
        CourseTemplate queryTemplate = new CourseTemplate();
        queryTemplate.setStatus(1); // 只查询启用的模板
        if (StringUtils.hasText(pageDTO.getAcademicYear())) {
            queryTemplate.setAcademicYear(pageDTO.getAcademicYear());
        }
        if (StringUtils.hasText(pageDTO.getSemester())) {
            queryTemplate.setSemester(pageDTO.getSemester());
        }
        
        List<CourseTemplate> templates = courseTemplateMapper.selectPageByCondition(offset, pageDTO.getSize(), pageDTO.getKeyword(), queryTemplate);
        long total = courseTemplateMapper.selectCount(queryTemplate);
        
        // 转换为VO
        List<CourseTemplateVO> voList = templates.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageUtil.createPageResult(pageDTO.getPage(), pageDTO.getSize(), total, voList);
    }

    @Override
    public void createTemplate(CourseDTO templateDTO) {
        CourseTemplate template = new CourseTemplate();
        BeanUtils.copyProperties(templateDTO, template);
        
        if (template.getStatus() == null) {
            template.setStatus(1);
        }
        template.setCreateTime(LocalDateTime.now());
        template.setUpdateTime(LocalDateTime.now());
        template.setDeleted(0);
        
        courseTemplateMapper.insert(template);
    }

    @Override
    public void updateTemplate(Long id, CourseDTO templateDTO) {
        CourseTemplate existingTemplate = courseTemplateMapper.selectById(id);
        if (existingTemplate == null) {
            throw new RuntimeException("模板不存在");
        }
        
        CourseTemplate template = new CourseTemplate();
        BeanUtils.copyProperties(templateDTO, template);
        template.setId(id);
        template.setUpdateTime(LocalDateTime.now());
        
        courseTemplateMapper.updateById(template);
    }

    @Override
    public void deleteTemplate(Long id) {
        CourseTemplate template = courseTemplateMapper.selectById(id);
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }
        
        courseTemplateMapper.deleteById(id);
    }

    @Override
    public CourseTemplateVO getTemplateById(Long id) {
        CourseTemplate template = courseTemplateMapper.selectById(id);
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }
        
        return convertToVO(template);
    }
    
    /**
     * 转换为VO对象
     */
    private CourseTemplateVO convertToVO(CourseTemplate template) {
        CourseTemplateVO vo = new CourseTemplateVO();
        BeanUtils.copyProperties(template, vo);
        
        // 设置状态名称
        vo.setStatusName(STATUS_MAP.get(template.getStatus()));
        
        return vo;
    }
} 