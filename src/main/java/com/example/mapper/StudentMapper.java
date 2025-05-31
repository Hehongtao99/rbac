package com.example.mapper;

import com.example.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMapper {
    
    // 基础CRUD操作
    int insert(Student student);
    
    int deleteById(Long id);
    
    int updateById(Student student);
    
    Student selectById(Long id);
    
    List<Student> selectList(@Param("student") Student student);
    
    // 根据学号查询
    Student selectByStudentNo(@Param("studentNo") String studentNo);
    
    // 分页查询
    List<Student> selectPage(@Param("offset") int offset, @Param("limit") int limit, @Param("keyword") String keyword);
    
    // 统计总数
    long selectCount(@Param("student") Student student);
} 