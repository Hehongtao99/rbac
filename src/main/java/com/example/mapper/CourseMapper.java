package com.example.mapper;

import com.example.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseMapper {
    
    // 基础CRUD操作
    int insert(Course course);
    
    int deleteById(Long id);
    
    int updateById(Course course);
    
    Course selectById(Long id);
    
    List<Course> selectList(@Param("course") Course course);
    
    // 根据教师ID查询课程
    List<Course> selectByTeacherId(@Param("teacherId") Long teacherId);
    
    // 分页查询
    List<Course> selectPage(@Param("offset") int offset, @Param("limit") int limit, @Param("keyword") String keyword);
    
    // 统计总数
    long selectCount(@Param("course") Course course);
} 