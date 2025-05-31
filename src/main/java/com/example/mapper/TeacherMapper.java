package com.example.mapper;

import com.example.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeacherMapper {
    
    // 基础CRUD操作
    int insert(Teacher teacher);
    
    int deleteById(Long id);
    
    int updateById(Teacher teacher);
    
    Teacher selectById(Long id);
    
    List<Teacher> selectList(@Param("teacher") Teacher teacher);
    
    // 根据教师编号查询
    Teacher selectByTeacherNo(@Param("teacherNo") String teacherNo);
    
    // 分页查询
    List<Teacher> selectPage(@Param("offset") int offset, @Param("limit") int limit, @Param("keyword") String keyword);
    
    // 统计总数
    long selectCount(@Param("teacher") Teacher teacher);

    // 根据用户ID查询教师
    Teacher selectByUserId(@Param("userId") Long userId);

    // 查询最大教师编号
    String selectMaxTeacherNo();
} 