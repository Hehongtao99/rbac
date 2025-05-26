package com.example.controller;

import com.example.dto.StudentDTO;
import com.example.service.StudentService;
import com.example.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 获取学生列表
     */
    @GetMapping("/list")
    public Result<Object> getStudentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return studentService.getStudentList(page, size, keyword);
    }

    /**
     * 创建学生
     */
    @PostMapping("/create")
    public Result<Object> createStudent(@RequestBody StudentDTO studentDTO) {
        return studentService.createStudent(studentDTO);
    }

    /**
     * 更新学生
     */
    @PutMapping("/update/{id}")
    public Result<Object> updateStudent(@PathVariable Long id, @RequestBody StudentDTO studentDTO) {
        return studentService.updateStudent(id, studentDTO);
    }

    /**
     * 删除学生
     */
    @DeleteMapping("/delete/{id}")
    public Result<Object> deleteStudent(@PathVariable Long id) {
        return studentService.deleteStudent(id);
    }

    /**
     * 根据ID获取学生信息
     */
    @GetMapping("/{id}")
    public Result<Object> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }
} 