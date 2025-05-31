package com.example.controller;

import com.example.common.PageResult;
import com.example.common.Result;
import com.example.dto.TeacherDTO;
import com.example.dto.TeacherPageDTO;
import com.example.service.TeacherService;
import com.example.vo.TeacherVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 教师控制器
 */
@RestController
@RequestMapping("/api/teacher")
@CrossOrigin
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    /**
     * 分页获取教师列表
     */
    @PostMapping("/list")
    public Result<PageResult<TeacherVO>> getTeacherList(@RequestBody TeacherPageDTO pageDTO) {
        PageResult<TeacherVO> result = teacherService.getTeacherList(pageDTO);
        return Result.success(result);
    }

    /**
     * 创建教师
     */
    @PostMapping("/create")
    public Result<Void> createTeacher(@RequestBody TeacherDTO teacherDTO) {
        teacherService.createTeacher(teacherDTO);
        return Result.success();
    }

    /**
     * 更新教师
     */
    @PutMapping("/update/{id}")
    public Result<Void> updateTeacher(@PathVariable Long id, @RequestBody TeacherDTO teacherDTO) {
        teacherService.updateTeacher(id, teacherDTO);
        return Result.success();
    }

    /**
     * 删除教师
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return Result.success();
    }

    /**
     * 根据ID获取教师信息
     */
    @GetMapping("/{id}")
    public Result<TeacherVO> getTeacherById(@PathVariable Long id) {
        TeacherVO teacher = teacherService.getTeacherById(id);
        return Result.success(teacher);
    }
} 