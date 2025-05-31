package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Schedule;
import com.example.dto.ScheduleDTO;
import com.example.vo.ScheduleVO;

import java.util.List;
import java.util.Map;

public interface ScheduleService extends IService<Schedule> {
    
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
    List<ScheduleVO> getTeacherSchedule(Long teacherId, String academicYear);
    
    /**
     * 获取某周的课程表
     */
    Map<String, List<ScheduleVO>> getWeeklySchedule(Long teacherId, String academicYear, Integer weekNumber);
    
    /**
     * 获取某个班级某周的课程表
     */
    Map<String, List<ScheduleVO>> getWeeklyScheduleByClass(Long teacherId, String academicYear, Integer weekNumber, Long classId);
    
    /**
     * 删除课程表中的课程
     */
    void removeCourseFromSchedule(Long scheduleId);
    
    /**
     * 检查时间冲突
     */
    boolean checkTimeConflict(Long teacherId, String academicYear, Integer weekNumber, Integer dayOfWeek, Integer timeSlot, Long classId);
    
    /**
     * 获取可选择的课程列表（教师申请通过的课程）
     */
    List<Map<String, Object>> getAvailableCourses(Long teacherId, String academicYear);
    
    /**
     * 获取教师分配的班级列表
     */
    List<Map<String, Object>> getTeacherClasses(Long teacherId);
    
    /**
     * 管理员获取所有教师的课程表
     */
    List<ScheduleVO> getAllSchedulesForAdmin(String academicYear, Integer weekNumber, String teacherName, String courseName);
    
    /**
     * 管理员获取周课程表（网格形式）
     */
    Map<String, Object> getWeeklyScheduleForAdmin(String academicYear, Integer weekNumber, Long teacherId);
} 