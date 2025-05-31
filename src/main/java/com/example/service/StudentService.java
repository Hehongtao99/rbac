package com.example.service;

import com.example.common.PageResult;
import com.example.dto.StudentDTO;
import com.example.dto.StudentPageDTO;
import com.example.vo.StudentVO;

public interface StudentService {
    
    /**
     * 分页获取学生列表
     */
    PageResult<StudentVO> getStudentList(StudentPageDTO pageDTO);
    
    /**
     * 创建学生
     */
    void createStudent(StudentDTO studentDTO);
    
    /**
     * 更新学生
     */
    void updateStudent(Long id, StudentDTO studentDTO);
    
    /**
     * 删除学生
     */
    void deleteStudent(Long id);
    
    /**
     * 根据ID获取学生详情
     */
    StudentVO getStudentById(Long id);
    
    /**
     * 分配学生年级和学制
     */
    void assignGradeAndEducation(Long id, String grade, String educationSystem);
    
    /**
     * 设置学生学期信息
     */
    void setStudentSemester(Long id, Integer currentYear, Integer currentSemester);
} 