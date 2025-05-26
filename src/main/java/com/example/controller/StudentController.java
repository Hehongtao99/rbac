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
    
    /**
     * 分配学生年级和学制
     */
    @PutMapping("/assign-grade-education/{id}")
    public Result<Object> assignGradeAndEducation(@PathVariable Long id, 
                                                   @RequestParam String grade, 
                                                   @RequestParam String educationSystem) {
        return studentService.assignGradeAndEducation(id, grade, educationSystem);
    }
    
    /**
     * 设置学生学期信息
     */
    @PutMapping("/set-semester/{id}")
    public Result<Object> setStudentSemester(@PathVariable Long id,
                                             @RequestParam Integer currentYear,
                                             @RequestParam Integer currentSemester) {
        return studentService.setStudentSemester(id, currentYear, currentSemester, null);
    }
} 