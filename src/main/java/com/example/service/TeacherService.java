package com.example.service;

import com.example.entity.Teacher;
import com.example.dto.TeacherDTO;
import com.example.common.Result;

public interface TeacherService {
    Result<Object> getTeacherList(Integer page, Integer size, String keyword);
    Result<Object> createTeacher(TeacherDTO teacherDTO);
    Result<Object> updateTeacher(Long id, TeacherDTO teacherDTO);
    Result<Object> deleteTeacher(Long id);
    Result<Object> getTeacherById(Long id);
} 