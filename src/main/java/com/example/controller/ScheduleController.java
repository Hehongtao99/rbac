package com.example.controller;

import com.example.common.Result;
import com.example.dto.ScheduleDTO;
import com.example.dto.TimeConflictCheckDTO;
import com.example.entity.TimeSlotConfig;
import com.example.service.ScheduleService;
import com.example.service.TimeSlotConfigService;
import com.example.vo.ScheduleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 课程表控制器
 */
@RestController
@RequestMapping("/api/schedule")
@CrossOrigin
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;
    
    @Autowired
    private TimeSlotConfigService timeSlotConfigService;

    /**
     * 添加课程到课程表
     */
    @PostMapping("/add")
    public Result<Void> addCourseToSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        scheduleService.addCourseToSchedule(scheduleDTO);
        return Result.success();
    }

    /**
     * 更新课程表记录
     */
    @PutMapping("/{scheduleId}")
    public Result<Void> updateSchedule(@PathVariable Long scheduleId, @RequestBody ScheduleDTO scheduleDTO) {
        scheduleService.updateSchedule(scheduleId, scheduleDTO);
        return Result.success();
    }

    /**
     * 获取教师的课程表
     */
    @GetMapping("/teacher")
    public Result<List<ScheduleVO>> getTeacherSchedule(@RequestParam String academicYear) {
        List<ScheduleVO> schedule = scheduleService.getTeacherSchedule(academicYear);
        return Result.success(schedule);
    }

    /**
     * 获取某周的课程表
     */
    @GetMapping("/weekly")
    public Result<Map<String, List<ScheduleVO>>> getWeeklySchedule(
            @RequestParam String academicYear, 
            @RequestParam Integer weekNumber) {
        Map<String, List<ScheduleVO>> schedule = scheduleService.getWeeklySchedule(academicYear, weekNumber);
        return Result.success(schedule);
    }

    /**
     * 获取某个班级某周的课程表
     */
    @GetMapping("/weekly/class")
    public Result<Map<String, List<ScheduleVO>>> getWeeklyScheduleByClass(
            @RequestParam String academicYear, 
            @RequestParam Integer weekNumber,
            @RequestParam(required = false) Long classId) {
        Map<String, List<ScheduleVO>> schedule = scheduleService.getWeeklyScheduleByClass(academicYear, weekNumber, classId);
        return Result.success(schedule);
    }

    /**
     * 获取教师分配的班级列表
     */
    @GetMapping("/teacher-classes")
    public Result<List<Map<String, Object>>> getTeacherClasses() {
        List<Map<String, Object>> classes = scheduleService.getTeacherClasses();
        return Result.success(classes);
    }

    /**
     * 管理员获取所有教师的课程表
     */
    @GetMapping("/admin/all")
    public Result<List<ScheduleVO>> getAllSchedulesForAdmin(
            @RequestParam String academicYear,
            @RequestParam(required = false) Integer weekNumber,
            @RequestParam(required = false) String teacherName,
            @RequestParam(required = false) String courseName) {
        List<ScheduleVO> schedules = scheduleService.getAllSchedulesForAdmin(academicYear, weekNumber, teacherName, courseName);
        return Result.success(schedules);
    }

    /**
     * 管理员获取周课程表（网格形式）
     */
    @GetMapping("/admin/weekly")
    public Result<Map<String, Object>> getWeeklyScheduleForAdmin(
            @RequestParam String academicYear,
            @RequestParam Integer weekNumber,
            @RequestParam(required = false) Long teacherId) {
        Map<String, Object> result = scheduleService.getWeeklyScheduleForAdmin(academicYear, weekNumber, teacherId);
        return Result.success(result);
    }

    /**
     * 删除课程表中的课程
     */
    @DeleteMapping("/{scheduleId}")
    public Result<Void> removeCourseFromSchedule(@PathVariable Long scheduleId) {
        scheduleService.removeCourseFromSchedule(scheduleId);
        return Result.success();
    }

    /**
     * 检查时间冲突
     */
    @PostMapping("/check-conflict")
    public Result<Boolean> checkTimeConflict(@RequestBody TimeConflictCheckDTO checkDTO) {
        Boolean conflict = scheduleService.checkTimeConflict(
            checkDTO.getAcademicYear(), 
            checkDTO.getWeekNumber(), 
            checkDTO.getDayOfWeek(), 
            checkDTO.getTimeSlot(), 
            checkDTO.getClassId()
        );
        return Result.success(conflict);
    }

    /**
     * 获取可选择的课程列表
     */
    @GetMapping("/available-courses")
    public Result<List<Map<String, Object>>> getAvailableCourses(
            @RequestParam String academicYear,
            @RequestParam String semester) {
        List<Map<String, Object>> courses = scheduleService.getAvailableCourses(academicYear, semester);
        return Result.success(courses);
    }

    /**
     * 获取指定班级的可用课程列表
     */
    @GetMapping("/available-courses/class")
    public Result<List<Map<String, Object>>> getAvailableCoursesForClass(
            @RequestParam String academicYear,
            @RequestParam String semester,
            @RequestParam Long classId) {
        List<Map<String, Object>> courses = scheduleService.getAvailableCoursesForClass(classId, academicYear, semester);
        return Result.success(courses);
    }

    /**
     * 获取所有时间段配置
     */
    @GetMapping("/time-slots")
    public Result<List<TimeSlotConfig>> getAllTimeSlots() {
        List<TimeSlotConfig> timeSlots = timeSlotConfigService.getAllTimeSlots();
        return Result.success(timeSlots);
    }

    /**
     * 初始化时间段配置
     */
    @PostMapping("/init-time-slots")
    public Result<Void> initTimeSlotConfig() {
        timeSlotConfigService.initTimeSlotConfig();
        return Result.success();
    }
    
    /**
     * 获取学生课程表
     */
    @GetMapping("/student/{studentId}")
    public Result<List<ScheduleVO>> getStudentSchedule(@PathVariable Long studentId) {
        List<ScheduleVO> schedule = scheduleService.getStudentSchedule(studentId);
        return Result.success(schedule);
    }
} 