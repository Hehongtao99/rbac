package com.example.mapper;

import com.example.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScheduleMapper {
    
    // 基础CRUD操作
    int insert(Schedule schedule);
    
    int deleteById(Long id);
    
    int updateById(Schedule schedule);
    
    Schedule selectById(Long id);
    
    List<Schedule> selectList(@Param("schedule") Schedule schedule);
    
    // 根据课程ID查询
    List<Schedule> selectByCourseId(@Param("courseId") Long courseId);
    
    // 根据教师ID查询
    List<Schedule> selectByTeacherId(@Param("teacherId") Long teacherId);
    
    // 根据班级ID查询
    List<Schedule> selectByClassId(@Param("classId") Long classId);
    
    // 根据周次和星期查询
    List<Schedule> selectByWeekAndDay(@Param("weekNumber") Integer weekNumber, @Param("dayOfWeek") Integer dayOfWeek);
    
    // 查询时间冲突
    List<Schedule> selectConflict(@Param("teacherId") Long teacherId, @Param("weekNumber") Integer weekNumber, 
                                 @Param("dayOfWeek") Integer dayOfWeek, @Param("timeSlot") Integer timeSlot);
    
    // 查询班级时间冲突（检查指定班级在指定时间是否已有课程）
    List<Schedule> selectClassTimeConflict(@Param("classId") Long classId, @Param("academicYear") String academicYear,
                                          @Param("weekNumber") Integer weekNumber, @Param("dayOfWeek") Integer dayOfWeek, 
                                          @Param("timeSlot") Integer timeSlot);
    
    // 根据教师ID和学年查询
    List<Schedule> selectByTeacherAndAcademicYear(@Param("teacherId") Long teacherId, @Param("academicYear") String academicYear);
    
    // 根据教师ID、学年和周次查询
    List<Schedule> selectByTeacherAndAcademicYearAndWeekNumber(@Param("teacherId") Long teacherId, 
                                                              @Param("academicYear") String academicYear, 
                                                              @Param("weekNumber") Integer weekNumber);
    
    // 根据教师ID、学年、周次和班级ID查询
    List<Schedule> selectByTeacherAndAcademicYearAndWeekNumberAndClassId(@Param("teacherId") Long teacherId, 
                                                                        @Param("academicYear") String academicYear, 
                                                                        @Param("weekNumber") Integer weekNumber, 
                                                                        @Param("classId") Long classId);
    
    // 根据班级ID、学年和周次查询（查询该班级所有教师的课程）
    List<Schedule> selectByClassAndAcademicYearAndWeekNumber(@Param("classId") Long classId, 
                                                            @Param("academicYear") String academicYear, 
                                                            @Param("weekNumber") Integer weekNumber);
    
    // 管理员查询：根据学年、周次、教师姓名、课程名称查询
    List<Schedule> selectByAcademicYearAndWeekNumberAndTeacherNameAndCourseName(@Param("academicYear") String academicYear,
                                                                               @Param("weekNumber") Integer weekNumber,
                                                                               @Param("teacherName") String teacherName,
                                                                               @Param("courseName") String courseName);
    
    // 根据学生ID查询学生课程表
    List<Schedule> selectStudentSchedule(@Param("studentId") Long studentId);
    
    // 统计总数
    long selectCount(@Param("schedule") Schedule schedule);
} 