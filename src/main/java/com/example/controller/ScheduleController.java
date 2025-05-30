package com.example.controller;

import com.example.common.Result;
import com.example.dto.ScheduleDTO;
import com.example.entity.TimeSlotConfig;
import com.example.service.ScheduleService;
import com.example.service.TimeSlotConfigService;
import com.example.vo.ScheduleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedule")
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
     * 获取教师的课程表 - 从JWT获取teacherId
     */
    @GetMapping("/teacher")
    public Result<List<ScheduleVO>> getTeacherSchedule(@RequestParam String academicYear) {
        List<ScheduleVO> schedule = scheduleService.getTeacherSchedule(null, academicYear);
        return Result.success(schedule);
    }

    /**
     * 获取某周的课程表 - 从JWT获取teacherId
     */
    @GetMapping("/weekly")
    public Result<Map<String, List<ScheduleVO>>> getWeeklySchedule(@RequestParam String academicYear,
                                                                  @RequestParam Integer weekNumber) {
        try {
            Map<String, List<ScheduleVO>> schedule = scheduleService.getWeeklySchedule(null, academicYear, weekNumber);
            return Result.success(schedule);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
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
     * 检查时间冲突 - 从JWT获取teacherId
     */
    @GetMapping("/check-conflict")
    public Result<Boolean> checkTimeConflict(@RequestParam String academicYear,
                                           @RequestParam Integer weekNumber,
                                           @RequestParam Integer dayOfWeek,
                                           @RequestParam Integer timeSlot) {
        try {
            boolean conflict = scheduleService.checkTimeConflict(null, academicYear, weekNumber, dayOfWeek, timeSlot);
            return Result.success(conflict);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取可选择的课程列表 - 从JWT获取teacherId
     */
    @GetMapping("/available-courses")
    public Result<List<Map<String, Object>>> getAvailableCourses(@RequestParam String academicYear) {
        try {
            List<Map<String, Object>> courses = scheduleService.getAvailableCourses(null, academicYear);
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有时间段配置
     */
    @GetMapping("/time-slots")
    public Result<List<TimeSlotConfig>> getAllTimeSlots() {
        System.out.println("=== getAllTimeSlots 开始 ===");
        List<TimeSlotConfig> timeSlots = timeSlotConfigService.getAllTimeSlots();
        System.out.println("=== getAllTimeSlots 结果 ===");
        System.out.println("时间段数量: " + timeSlots.size());
        for (TimeSlotConfig timeSlot : timeSlots) {
            System.out.println("时间段: " + timeSlot.getTimeSlot() + " - " + timeSlot.getSlotName() + " (" + timeSlot.getStartTime() + "-" + timeSlot.getEndTime() + ")");
        }
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
} 