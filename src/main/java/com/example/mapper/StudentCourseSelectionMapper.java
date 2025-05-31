package com.example.mapper;

import com.example.entity.StudentCourseSelection;
import com.example.vo.AvailableCourseVO;
import com.example.vo.StudentCourseSelectionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentCourseSelectionMapper {
    
    /**
     * 插入选课记录
     */
    int insert(StudentCourseSelection studentCourseSelection);
    
    /**
     * 根据ID删除选课记录
     */
    int deleteById(Long id);
    
    /**
     * 根据ID更新选课记录
     */
    int updateById(StudentCourseSelection studentCourseSelection);
    
    /**
     * 根据ID查询选课记录
     */
    StudentCourseSelection selectById(Long id);
    
    /**
     * 条件查询选课记录列表
     */
    List<StudentCourseSelection> selectList(@Param("studentCourseSelection") StudentCourseSelection studentCourseSelection);
    
    /**
     * 统计选课记录数量
     */
    Long selectCount(@Param("studentCourseSelection") StudentCourseSelection studentCourseSelection);
    
    /**
     * 获取学生可选课程列表
     */
    List<AvailableCourseVO> getAvailableCourses(@Param("studentId") Long studentId);
    
    /**
     * 获取学生已选课程列表
     */
    List<StudentCourseSelectionVO> getSelectedCourses(@Param("studentId") Long studentId);
    
    /**
     * 检查学生是否已选择该课程
     */
    Integer checkCourseSelected(@Param("studentId") Long studentId, @Param("courseApplicationId") Long courseApplicationId);
    
    /**
     * 检查课程是否已满员
     */
    Integer checkCourseFull(@Param("courseApplicationId") Long courseApplicationId);
    
    /**
     * 检查同一科目是否已选择
     */
    Long checkSameSubject(@Param("studentId") Long studentId, @Param("courseName") String courseName);
    
    /**
     * 更新选课状态为退课
     */
    int updateStatusToDrop(@Param("studentId") Long studentId, @Param("courseApplicationId") Long courseApplicationId);
} 