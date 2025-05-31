package com.example.service;

import com.example.entity.Schedule;
import com.example.dto.ScheduleDTO;
import com.example.vo.ScheduleVO;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    
    /**
     * 添加课程到课程表
     */
    void addCourseToSchedule(ScheduleDTO scheduleDTO);
    
    /**
     * 更新课程表记录
     */
    void updateSchedule(Long scheduleId, ScheduleDTO scheduleDTO);
    
    /**
     * 获取教师的课程表
     */
    List<ScheduleVO> getTeacherSchedule(String academicYear);
    
    /**
     * 获取某周的课程表
     */
    Map<String, List<ScheduleVO>> getWeeklySchedule(String academicYear, Integer weekNumber);
    
    /**
     * 获取某个班级某周的课程表
     */
    Map<String, List<ScheduleVO>> getWeeklyScheduleByClass(String academicYear, Integer weekNumber, Long classId);
    
    /**
     * 删除课程表中的课程
     */
    void removeCourseFromSchedule(Long scheduleId);
    
    /**
     * 检查时间冲突
     */
    Boolean checkTimeConflict(String academicYear, Integer weekNumber, Integer dayOfWeek, Integer timeSlot, Long classId);
    
    /**
     * 获取可选择的课程列表（教师申请通过的课程）
     */
    List<Map<String, Object>> getAvailableCourses(String academicYear, String semester);
    
    /**
     * 获取指定班级的可用课程列表（显示该班级的剩余课时）
     */
    List<Map<String, Object>> getAvailableCoursesForClass(Long classId, String academicYear, String semester);
    
    /**
     * 获取教师分配的班级列表
     */
    List<Map<String, Object>> getTeacherClasses();
    
    /**
     * 管理员获取所有教师的课程表
     */
    List<ScheduleVO> getAllSchedulesForAdmin(String academicYear, Integer weekNumber, String teacherName, String courseName);
    
    /**
     * 管理员获取周课程表（网格形式）
     */
    Map<String, Object> getWeeklyScheduleForAdmin(String academicYear, Integer weekNumber, Long teacherId);
    
    /**
     * 获取学生课程表
     */
    List<ScheduleVO> getStudentSchedule(Long studentId);
} 