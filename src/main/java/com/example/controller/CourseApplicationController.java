package com.example.controller;

import com.example.entity.CourseApplication;
import com.example.service.CourseApplicationService;
import com.example.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/course-applications")
public class CourseApplicationController {

    @Autowired
    private CourseApplicationService courseApplicationService;

    @GetMapping
    public Result<Object> getApplicationList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return courseApplicationService.getApplicationList(page, size, keyword);
    }

    @GetMapping("/admin")
    public Result<Object> getApplicationListForAdmin(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return courseApplicationService.getApplicationListForAdmin(page, size, keyword);
    }

    @PostMapping
    public Result<Object> createApplication(@RequestBody CourseApplication application) {
        return courseApplicationService.createApplication(application);
    }

    @PutMapping("/{id}")
    public Result<Object> updateApplication(@PathVariable Long id, @RequestBody CourseApplication application) {
        application.setId(id);
        return courseApplicationService.updateApplication(application);
    }

    @PutMapping("/{id}/review")
    public Result<Object> reviewApplication(@PathVariable Long id, @RequestBody Map<String, Object> reviewData) {
        Integer status = (Integer) reviewData.get("status");
        String reviewComment = (String) reviewData.get("reviewComment");
        return courseApplicationService.reviewApplication(id, status, reviewComment);
    }

    @DeleteMapping("/{id}")
    public Result<Object> deleteApplication(@PathVariable Long id) {
        return courseApplicationService.deleteApplication(id);
    }

    @GetMapping("/{id}")
    public Result<Object> getApplicationById(@PathVariable Long id) {
        return courseApplicationService.getApplicationById(id);
    }
} 