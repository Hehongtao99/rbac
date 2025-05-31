package com.example.controller;

import com.example.common.Result;
import com.example.dto.CourseSelectionDTO;
import com.example.service.CourseSelectionService;
import com.example.vo.AvailableCourseVO;
import com.example.vo.StudentCourseSelectionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生选课控制器
 */
@RestController
@RequestMapping("/api/course-selection")
@CrossOrigin
public class CourseSelectionController {

    @Autowired
    private CourseSelectionService courseSelectionService;

    /**
     * 获取学生可选课程列表
     */
    @GetMapping("/available/{studentId}")
    public Result<List<AvailableCourseVO>> getAvailableCourses(@PathVariable Long studentId) {
        List<AvailableCourseVO> courses = courseSelectionService.getAvailableCourses(studentId);
        return Result.success(courses);
    }

    /**
     * 获取学生已选课程列表
     */
    @GetMapping("/selected/{studentId}")
    public Result<List<StudentCourseSelectionVO>> getSelectedCourses(@PathVariable Long studentId) {
        List<StudentCourseSelectionVO> courses = courseSelectionService.getSelectedCourses(studentId);
        return Result.success(courses);
    }

    /**
     * 学生选课
     */
    @PostMapping("/select")
    public Result<Void> selectCourse(@RequestBody CourseSelectionDTO courseSelectionDTO) {
        courseSelectionService.selectCourse(courseSelectionDTO);
        return Result.success();
    }

    /**
     * 学生退课
     */
    @DeleteMapping("/drop/{studentId}/{courseApplicationId}")
    public Result<Void> dropCourse(@PathVariable Long studentId, @PathVariable Long courseApplicationId) {
        courseSelectionService.dropCourse(studentId, courseApplicationId);
        return Result.success();
    }
} 