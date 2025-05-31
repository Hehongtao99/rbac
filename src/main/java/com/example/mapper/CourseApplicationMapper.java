package com.example.mapper;

import com.example.entity.CourseApplication;
import com.example.vo.CourseApplicationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseApplicationMapper {
    
    // 基础CRUD操作
    int insert(CourseApplication courseApplication);
    
    int deleteById(Long id);
    
    int updateById(CourseApplication courseApplication);
    
    CourseApplication selectById(Long id);
    
    List<CourseApplication> selectList(@Param("courseApplication") CourseApplication courseApplication);
    
    // 根据教师ID查询
    List<CourseApplication> selectByTeacherId(@Param("teacherId") Long teacherId);
    
    // 根据状态查询
    List<CourseApplication> selectByStatus(@Param("status") Integer status);
    
    // 根据模板ID查询
    List<CourseApplication> selectByTemplateId(@Param("templateId") Long templateId);
    
    // 根据教师ID、学年和状态查询
    List<CourseApplication> selectByTeacherIdAndAcademicYearAndStatus(@Param("teacherId") Long teacherId, 
                                                                     @Param("academicYear") String academicYear, 
                                                                     @Param("status") Integer status);
    
    // 检查教师是否已申请过某个模板（待审核或已通过状态）
    List<CourseApplication> selectByTeacherIdAndTemplateId(@Param("teacherId") Long teacherId, 
                                                          @Param("templateId") Long templateId);
    
    // 分页查询
    List<CourseApplication> selectPage(@Param("offset") int offset, @Param("limit") int limit, @Param("keyword") String keyword);
    
    // 根据教师ID分页查询（返回VO包含学院专业信息）
    List<CourseApplicationVO> selectPageByTeacherId(@Param("offset") int offset, @Param("limit") int limit, 
                                                   @Param("keyword") String keyword, @Param("teacherId") Long teacherId);
    
    // 管理员分页查询（返回VO包含学院专业信息）
    List<CourseApplicationVO> selectPageForAdmin(@Param("offset") int offset, @Param("limit") Integer limit, 
                                                @Param("keyword") String keyword, @Param("status") Integer status,
                                                @Param("academicYear") String academicYear, @Param("semester") String semester,
                                                @Param("teacherId") Long teacherId);
    
    // 管理员查询总数（包含学院专业筛选）
    long selectCountForAdmin(@Param("keyword") String keyword, @Param("status") Integer status,
                            @Param("academicYear") String academicYear, @Param("semester") String semester,
                            @Param("teacherId") Long teacherId);
    
    // 统计总数
    long selectCount(@Param("courseApplication") CourseApplication courseApplication);
} 