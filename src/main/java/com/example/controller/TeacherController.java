package com.example.controller;

import com.example.dto.TeacherDTO;
import com.example.service.TeacherService;
import com.example.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    /**
     * 获取教师列表
     */
    @GetMapping("/list")
    public Result<Object> getTeacherList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return teacherService.getTeacherList(page, size, keyword);
    }

    /**
     * 创建教师
     */
    @PostMapping("/create")
    public Result<Object> createTeacher(@RequestBody TeacherDTO teacherDTO) {
        return teacherService.createTeacher(teacherDTO);
    }

    /**
     * 更新教师
     */
    @PutMapping("/update/{id}")
    public Result<Object> updateTeacher(@PathVariable Long id, @RequestBody TeacherDTO teacherDTO) {
        return teacherService.updateTeacher(id, teacherDTO);
    }

    /**
     * 删除教师
     */
    @DeleteMapping("/delete/{id}")
    public Result<Object> deleteTeacher(@PathVariable Long id) {
        return teacherService.deleteTeacher(id);
    }

    /**
     * 根据ID获取教师信息
     */
    @GetMapping("/{id}")
    public Result<Object> getTeacherById(@PathVariable Long id) {
        return teacherService.getTeacherById(id);
    }
} 