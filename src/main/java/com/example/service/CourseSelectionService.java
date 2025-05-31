package com.example.service;

import com.example.dto.CourseSelectionDTO;
import com.example.vo.AvailableCourseVO;
import com.example.vo.StudentCourseSelectionVO;

import java.util.List;

public interface CourseSelectionService {
    
    /**
     * 获取学生可选课程列表
     */
    List<AvailableCourseVO> getAvailableCourses(Long studentId);
    
    /**
     * 获取学生已选课程列表
     */
    List<StudentCourseSelectionVO> getSelectedCourses(Long studentId);
    
    /**
     * 学生选课
     */
    void selectCourse(CourseSelectionDTO courseSelectionDTO);
    
    /**
     * 学生退课
     */
    void dropCourse(Long studentId, Long courseApplicationId);
} 