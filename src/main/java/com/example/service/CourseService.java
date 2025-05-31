package com.example.service;

import com.example.common.PageResult;
import com.example.dto.CourseDTO;
import com.example.dto.CoursePageDTO;
import com.example.vo.CourseVO;
import com.example.vo.TeacherVO;

import java.util.List;

public interface CourseService {
    
    /**
     * 分页获取课程列表
     */
    PageResult<CourseVO> getCourseList(CoursePageDTO pageDTO);
    
    /**
     * 分页获取课程模板列表
     */
    PageResult<CourseVO> getCourseTemplateList(CoursePageDTO pageDTO);
    
    /**
     * 分页获取课程实例列表
     */
    PageResult<CourseVO> getCourseInstanceList(CoursePageDTO pageDTO);
    
    /**
     * 创建课程
     */
    void createCourse(CourseDTO courseDTO);
    
    /**
     * 更新课程
     */
    void updateCourse(Long id, CourseDTO courseDTO);
    
    /**
     * 删除课程
     */
    void deleteCourse(Long id);
    
    /**
     * 根据ID获取课程详情
     */
    CourseVO getCourseById(Long id);
    
    /**
     * 获取教师列表
     */
    List<TeacherVO> getTeacherList();
    
    /**
     * 切换课程申请状态
     */
    void toggleApplicationStatus(Long id);
} 