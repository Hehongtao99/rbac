package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Student;
import com.example.dto.StudentDTO;
import com.example.common.Result;

public interface StudentService extends IService<Student> {
    Result<Object> getStudentList(Integer page, Integer size, String keyword);
    Result<Object> createStudent(StudentDTO studentDTO);
    Result<Object> updateStudent(Long id, StudentDTO studentDTO);
    Result<Object> deleteStudent(Long id);
    Result<Object> getStudentById(Long id);
} 