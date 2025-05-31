package com.example.mapper;

import com.example.entity.CourseTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseTemplateMapper {
    
    // 基础CRUD操作
    int insert(CourseTemplate courseTemplate);
    
    int deleteById(Long id);
    
    int updateById(CourseTemplate courseTemplate);
    
    CourseTemplate selectById(Long id);
    
    List<CourseTemplate> selectList(@Param("courseTemplate") CourseTemplate courseTemplate);
    
    // 根据模板名称查询
    CourseTemplate selectByTemplateName(@Param("templateName") String templateName);
    
    // 根据学年查询
    List<CourseTemplate> selectByAcademicYear(@Param("academicYear") String academicYear);
    
    // 分页查询
    List<CourseTemplate> selectPage(@Param("offset") int offset, @Param("limit") int limit, @Param("keyword") String keyword);
    
    // 条件分页查询
    List<CourseTemplate> selectPageByCondition(@Param("offset") int offset, @Param("limit") int limit, 
                                              @Param("keyword") String keyword, @Param("courseTemplate") CourseTemplate courseTemplate);
    
    // 统计总数
    long selectCount(@Param("courseTemplate") CourseTemplate courseTemplate);
} 