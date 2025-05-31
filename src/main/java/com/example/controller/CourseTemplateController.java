package com.example.controller;

import com.example.common.PageResult;
import com.example.common.Result;
import com.example.dto.CourseDTO;
import com.example.dto.CourseTemplatePageDTO;
import com.example.service.CourseTemplateService;
import com.example.vo.CourseTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 课程模板控制器
 */
@RestController
@RequestMapping("/api/course-templates")
@CrossOrigin
public class CourseTemplateController {

    @Autowired
    private CourseTemplateService courseTemplateService;

    /**
     * 分页获取课程模板列表
     */
    @PostMapping("/list")
    public Result<PageResult<CourseTemplateVO>> getTemplateList(@RequestBody CourseTemplatePageDTO pageDTO) {
        PageResult<CourseTemplateVO> result = courseTemplateService.getTemplateList(pageDTO);
        return Result.success(result);
    }

    /**
     * 分页获取启用的课程模板列表
     */
    @PostMapping("/enabled")
    public Result<PageResult<CourseTemplateVO>> getEnabledTemplateList(@RequestBody CourseTemplatePageDTO pageDTO) {
        // 设置只查询启用的模板
        pageDTO.setEnabledOnly(true);
        PageResult<CourseTemplateVO> result = courseTemplateService.getEnabledTemplateList(pageDTO);
        return Result.success(result);
    }

    /**
     * 创建课程模板
     */
    @PostMapping
    public Result<Void> createTemplate(@RequestBody CourseDTO templateDTO) {
        courseTemplateService.createTemplate(templateDTO);
        return Result.success();
    }

    /**
     * 更新课程模板
     */
    @PutMapping("/{id}")
    public Result<Void> updateTemplate(@PathVariable Long id, @RequestBody CourseDTO templateDTO) {
        courseTemplateService.updateTemplate(id, templateDTO);
        return Result.success();
    }

    /**
     * 删除课程模板
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTemplate(@PathVariable Long id) {
        courseTemplateService.deleteTemplate(id);
        return Result.success();
    }

    /**
     * 根据ID获取课程模板详情
     */
    @GetMapping("/{id}")
    public Result<CourseTemplateVO> getTemplateById(@PathVariable Long id) {
        CourseTemplateVO template = courseTemplateService.getTemplateById(id);
        return Result.success(template);
    }
} 