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
    List<CourseTemplate> selectPage(@Param("offset") int offset, @Param("limit") int limit, 
                                   @Param("keyword") String keyword, @Param("academicYear") String academicYear,
                                   @Param("semester") String semester, @Param("collegeId") Long collegeId,
                                   @Param("majorId") Long majorId, @Param("status") Integer status,
                                   @Param("enabledOnly") Boolean enabledOnly);
    
    // 分页查询总数
    long selectPageCount(@Param("keyword") String keyword, @Param("academicYear") String academicYear,
                        @Param("semester") String semester, @Param("collegeId") Long collegeId,
                        @Param("majorId") Long majorId, @Param("status") Integer status,
                        @Param("enabledOnly") Boolean enabledOnly);
    
    // 教师专用分页查询（根据教师学院专业筛选）
    List<CourseTemplate> selectPageForTeacher(@Param("offset") int offset, @Param("limit") int limit,
                                             @Param("keyword") String keyword, @Param("academicYear") String academicYear,
                                             @Param("semester") String semester, @Param("teacherCollegeId") Long teacherCollegeId,
                                             @Param("teacherMajorId") Long teacherMajorId);
    
    // 教师专用分页查询总数
    long selectPageCountForTeacher(@Param("keyword") String keyword, @Param("academicYear") String academicYear,
                                  @Param("semester") String semester, @Param("teacherCollegeId") Long teacherCollegeId,
                                  @Param("teacherMajorId") Long teacherMajorId);
    
    // 统计总数
    long selectCount(@Param("courseTemplate") CourseTemplate courseTemplate);
} 