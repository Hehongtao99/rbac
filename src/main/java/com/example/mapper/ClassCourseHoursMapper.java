package com.example.mapper;

import com.example.entity.ClassCourseHours;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassCourseHoursMapper {
    
    // 基础CRUD操作
    int insert(ClassCourseHours classCourseHours);
    
    int deleteById(Long id);
    
    int updateById(ClassCourseHours classCourseHours);
    
    ClassCourseHours selectById(Long id);
    
    List<ClassCourseHours> selectList(@Param("classCourseHours") ClassCourseHours classCourseHours);
    
    // 根据申请ID查询
    List<ClassCourseHours> selectByApplicationId(@Param("applicationId") Long applicationId);
    
    // 根据班级ID查询
    List<ClassCourseHours> selectByClassId(@Param("classId") Long classId);
    
    // 根据教师ID查询
    List<ClassCourseHours> selectByTeacherId(@Param("teacherId") Long teacherId);
    
    // 根据学年查询
    List<ClassCourseHours> selectByAcademicYear(@Param("academicYear") String academicYear);
    
    // 统计总数
    long selectCount(@Param("classCourseHours") ClassCourseHours classCourseHours);
} 