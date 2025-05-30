package com.example.controller;

import com.example.entity.CourseTemplate;
import com.example.service.CourseTemplateService;
import com.example.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course-templates")
public class CourseTemplateController {

    @Autowired
    private CourseTemplateService courseTemplateService;

    @GetMapping
    public Result<Object> getTemplateList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return courseTemplateService.getTemplateList(page, size, keyword);
    }

    @GetMapping("/enabled")
    public Result<Object> getEnabledTemplateList(@RequestParam(required = false) String keyword) {
        return courseTemplateService.getEnabledTemplateList(keyword);
    }

    @PostMapping
    public Result<Object> createTemplate(@RequestBody CourseTemplate template) {
        return courseTemplateService.createTemplate(template);
    }

    @PutMapping("/{id}")
    public Result<Object> updateTemplate(@PathVariable Long id, @RequestBody CourseTemplate template) {
        template.setId(id);
        return courseTemplateService.updateTemplate(template);
    }

    @DeleteMapping("/{id}")
    public Result<Object> deleteTemplate(@PathVariable Long id) {
        return courseTemplateService.deleteTemplate(id);
    }

    @GetMapping("/{id}")
    public Result<Object> getTemplateById(@PathVariable Long id) {
        return courseTemplateService.getTemplateById(id);
    }
} 