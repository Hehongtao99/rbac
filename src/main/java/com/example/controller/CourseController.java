package com.example.controller;

import com.example.common.PageResult;
import com.example.common.Result;
import com.example.dto.CourseDTO;
import com.example.dto.CoursePageDTO;
import com.example.service.CourseService;
import com.example.vo.CourseVO;
import com.example.vo.TeacherVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程控制器
 */
@RestController
@RequestMapping("/api/course")
@CrossOrigin
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 分页获取课程列表
     */
    @PostMapping("/list")
    public Result<PageResult<CourseVO>> getCourseList(@RequestBody CoursePageDTO pageDTO) {
        PageResult<CourseVO> result = courseService.getCourseList(pageDTO);
        return Result.success(result);
    }

    /**
     * 分页获取课程模板列表
     */
    @PostMapping("/template-list")
    public Result<PageResult<CourseVO>> getCourseTemplateList(@RequestBody CoursePageDTO pageDTO) {
        PageResult<CourseVO> result = courseService.getCourseTemplateList(pageDTO);
        return Result.success(result);
    }

    /**
     * 分页获取课程实例列表
     */
    @PostMapping("/instance-list")
    public Result<PageResult<CourseVO>> getCourseInstanceList(@RequestBody CoursePageDTO pageDTO) {
        PageResult<CourseVO> result = courseService.getCourseInstanceList(pageDTO);
        return Result.success(result);
    }

    /**
     * 创建课程
     */
    @PostMapping("/create")
    public Result<Void> createCourse(@RequestBody CourseDTO courseDTO) {
        courseService.createCourse(courseDTO);
        return Result.success();
    }

    /**
     * 更新课程
     */
    @PutMapping("/update/{id}")
    public Result<Void> updateCourse(@PathVariable Long id, @RequestBody CourseDTO courseDTO) {
        courseService.updateCourse(id, courseDTO);
        return Result.success();
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return Result.success();
    }

    /**
     * 根据ID获取课程详情
     */
    @GetMapping("/detail/{id}")
    public Result<CourseVO> getCourseById(@PathVariable Long id) {
        CourseVO course = courseService.getCourseById(id);
        return Result.success(course);
    }

    /**
     * 获取教师列表
     */
    @GetMapping("/teachers")
    public Result<List<TeacherVO>> getTeacherList() {
        List<TeacherVO> teachers = courseService.getTeacherList();
        return Result.success(teachers);
    }

    /**
     * 切换课程申请状态
     */
    @PostMapping("/toggle-application/{id}")
    public Result<Void> toggleApplicationStatus(@PathVariable Long id) {
        courseService.toggleApplicationStatus(id);
        return Result.success();
    }
} 