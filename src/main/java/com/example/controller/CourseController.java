package com.example.controller;

import com.example.dto.CourseDTO;
import com.example.service.CourseService;
import com.example.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/list")
    public Result<Object> getCourseList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return courseService.getCourseList(page, size, keyword);
    }

    @PostMapping("/create")
    public Result<Object> createCourse(@RequestBody CourseDTO courseDTO) {
        return courseService.createCourse(courseDTO);
    }

    @PutMapping("/update/{id}")
    public Result<Object> updateCourse(@PathVariable Long id, @RequestBody CourseDTO courseDTO) {
        return courseService.updateCourse(id, courseDTO);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Object> deleteCourse(@PathVariable Long id) {
        return courseService.deleteCourse(id);
    }

    @GetMapping("/detail/{id}")
    public Result<Object> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    @GetMapping("/teachers")
    public Result<Object> getTeacherList() {
        return courseService.getTeacherList();
    }
} 