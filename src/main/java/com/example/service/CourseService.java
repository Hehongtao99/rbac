package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Course;
import com.example.dto.CourseDTO;
import com.example.common.Result;

public interface CourseService extends IService<Course> {
    Result<Object> getCourseList(Integer page, Integer size, String keyword);
    Result<Object> getCourseTemplateList(Integer page, Integer size, String keyword);
    Result<Object> getCourseInstanceList(Integer page, Integer size, String keyword, String courseFilter, String teacherFilter);
    Result<Object> createCourse(CourseDTO courseDTO);
    Result<Object> updateCourse(Long id, CourseDTO courseDTO);
    Result<Object> deleteCourse(Long id);
    Result<Object> getCourseById(Long id);
    Result<Object> getTeacherList();
    Result<Object> toggleApplicationStatus(Long id);
} 