package com.example.controller;

import com.example.common.PageResult;
import com.example.common.Result;
import com.example.dto.StudentDTO;
import com.example.dto.StudentGradeAssignDTO;
import com.example.dto.StudentPageDTO;
import com.example.dto.StudentSemesterDTO;
import com.example.service.StudentService;
import com.example.vo.StudentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 学生控制器
 */
@RestController
@RequestMapping("/api/student")
@CrossOrigin
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 分页获取学生列表
     */
    @PostMapping("/list")
    public Result<PageResult<StudentVO>> getStudentList(@RequestBody StudentPageDTO pageDTO) {
        PageResult<StudentVO> result = studentService.getStudentList(pageDTO);
        return Result.success(result);
    }

    /**
     * 创建学生
     */
    @PostMapping("/create")
    public Result<Void> createStudent(@RequestBody StudentDTO studentDTO) {
        studentService.createStudent(studentDTO);
        return Result.success();
    }

    /**
     * 更新学生
     */
    @PutMapping("/update/{id}")
    public Result<Void> updateStudent(@PathVariable Long id, @RequestBody StudentDTO studentDTO) {
        studentService.updateStudent(id, studentDTO);
        return Result.success();
    }

    /**
     * 删除学生
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return Result.success();
    }

    /**
     * 根据ID获取学生信息
     */
    @GetMapping("/{id}")
    public Result<StudentVO> getStudentById(@PathVariable Long id) {
        StudentVO student = studentService.getStudentById(id);
        return Result.success(student);
    }
    
    /**
     * 分配学生年级和学制
     */
    @PutMapping("/assign-grade-education/{id}")
    public Result<Void> assignGradeAndEducation(@PathVariable Long id, @RequestBody StudentGradeAssignDTO assignDTO) {
        studentService.assignGradeAndEducation(id, assignDTO.getGrade(), assignDTO.getEducationSystem());
        return Result.success();
    }
    
    /**
     * 设置学生学期信息
     */
    @PutMapping("/set-semester/{id}")
    public Result<Void> setStudentSemester(@PathVariable Long id, @RequestBody StudentSemesterDTO semesterDTO) {
        studentService.setStudentSemester(id, semesterDTO.getCurrentYear(), semesterDTO.getCurrentSemester());
        return Result.success();
    }
} 