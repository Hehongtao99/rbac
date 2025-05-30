package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Schedule;
import com.example.entity.Course;
import com.example.entity.TimeSlotConfig;
import com.example.entity.User;
import com.example.entity.CourseApplication;
import com.example.mapper.ScheduleMapper;
import com.example.mapper.CourseMapper;
import com.example.mapper.TimeSlotConfigMapper;
import com.example.mapper.UserMapper;
import com.example.mapper.CourseApplicationMapper;
import com.example.service.ScheduleService;
import com.example.dto.ScheduleDTO;
import com.example.vo.ScheduleVO;
import com.example.util.UserContextUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {

    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private TimeSlotConfigMapper timeSlotConfigMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserContextUtil userContextUtil;

    @Autowired
    private CourseApplicationMapper courseApplicationMapper;

    @Override
    @Transactional
    public void addCourseToSchedule(ScheduleDTO scheduleDTO) {
        // 从JWT获取当前登录用户信息
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("获取当前用户信息失败，请重新登录");
        }
        
        Long teacherId = currentUser.getId();
        String teacherName = currentUser.getNickname() != null && !currentUser.getNickname().isEmpty() 
                           ? currentUser.getNickname() 
                           : currentUser.getUsername();
        
        // 检查时间冲突 - 使用JWT获取的teacherId
        boolean conflict = checkTimeConflict(teacherId, 
                scheduleDTO.getAcademicYear(), scheduleDTO.getWeekNumber(), 
                scheduleDTO.getDayOfWeek(), scheduleDTO.getTimeSlot());
        if (conflict) {
            throw new RuntimeException("该时间段已有课程安排，无法添加");
        }
        
        // 获取申请信息（courseId现在是applicationId）
        CourseApplication application = courseApplicationMapper.selectById(scheduleDTO.getCourseId());
        if (application == null) {
            throw new RuntimeException("申请记录不存在");
        }
        
        // 验证申请是否属于当前教师且已通过
        if (!application.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("您没有权限添加此课程");
        }
        
        if (application.getStatus() != 1) {
            throw new RuntimeException("只能添加已审核通过的课程");
        }
        
        // 检查剩余课时是否足够
        Integer remainingHours = application.getRemainingHours();
        if (remainingHours == null) {
            remainingHours = application.getCourseHours(); // 如果没有剩余课时字段，使用总课时
        }
        if (remainingHours < 1) {
            throw new RuntimeException("课程剩余课时不足（剩余" + remainingHours + "课时）");
        }
        
        // 获取时间段信息
        LambdaQueryWrapper<TimeSlotConfig> timeWrapper = new LambdaQueryWrapper<>();
        timeWrapper.eq(TimeSlotConfig::getTimeSlot, scheduleDTO.getTimeSlot());
        TimeSlotConfig timeSlot = timeSlotConfigMapper.selectOne(timeWrapper);
        
        // 创建课程表记录
        Schedule schedule = new Schedule();
        // 复制基本属性，但不包括reducedHours
        schedule.setCourseId(scheduleDTO.getCourseId());
        schedule.setCourseName(scheduleDTO.getCourseName());
        schedule.setAcademicYear(scheduleDTO.getAcademicYear());
        schedule.setWeekNumber(scheduleDTO.getWeekNumber());
        schedule.setDayOfWeek(scheduleDTO.getDayOfWeek());
        schedule.setTimeSlot(scheduleDTO.getTimeSlot());
        
        // 重写关键属性，确保数据正确
        schedule.setCourseId(application.getId()); // 使用申请ID作为课程ID
        schedule.setTeacherId(teacherId); // 使用JWT获取的teacherId
        schedule.setCourseName(application.getCourseName());
        schedule.setTeacherName(teacherName); // 使用JWT获取的教师姓名
        schedule.setReducedHours(1); // 强制设置为1课时，不从DTO复制
        // 设置默认教室值，因为前端已移除教室字段
        schedule.setClassroom("待定");
        if (timeSlot != null) {
            schedule.setTimeRange(timeSlot.getStartTime() + "-" + timeSlot.getEndTime());
        }
        
        // 保存课程表
        this.save(schedule);
        
        // 扣减申请记录的剩余课时
        Integer currentRemainingHours = application.getRemainingHours();
        if (currentRemainingHours == null) {
            currentRemainingHours = application.getCourseHours();
        }
        
        System.out.println("=== 课时扣减调试信息 ===");
        System.out.println("课程申请ID: " + application.getId());
        System.out.println("课程名称: " + application.getCourseName());
        System.out.println("扣减前剩余课时: " + currentRemainingHours);
        System.out.println("本次课程设置的reducedHours: " + schedule.getReducedHours());
        
        application.setRemainingHours(currentRemainingHours - 1);
        System.out.println("扣减后剩余课时: " + application.getRemainingHours());
        
        courseApplicationMapper.updateById(application);
        System.out.println("=== 课时扣减完成 ===");
    }

    @Override
    @Transactional
    public void updateSchedule(Long scheduleId, ScheduleDTO scheduleDTO) {
        Schedule schedule = this.getById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("课程表记录不存在");
        }
        
        // 由于前端已移除教室字段，这里暂时不进行任何更新
        // 如果需要更新其他信息，可以在这里添加
        // 例如：schedule.setTimeRange(...);
        
        this.updateById(schedule);
    }

    @Override
    public List<ScheduleVO> getTeacherSchedule(Long teacherId, String academicYear) {
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Schedule::getTeacherId, teacherId)
               .eq(Schedule::getAcademicYear, academicYear)
               .orderBy(true, true, Schedule::getWeekNumber)
               .orderBy(true, true, Schedule::getDayOfWeek)
               .orderBy(true, true, Schedule::getTimeSlot);
        
        List<Schedule> schedules = this.list(wrapper);
        return schedules.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<ScheduleVO>> getWeeklySchedule(Long teacherId, String academicYear, Integer weekNumber) {
        // 从JWT获取当前登录用户信息
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("获取当前用户信息失败，请重新登录");
        }
        
        // 使用JWT获取的用户ID，忽略传入的teacherId参数
        Long actualTeacherId = currentUser.getId();
        
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Schedule::getTeacherId, actualTeacherId)
               .eq(Schedule::getAcademicYear, academicYear)
               .eq(Schedule::getWeekNumber, weekNumber)
               .orderBy(true, true, Schedule::getDayOfWeek)
               .orderBy(true, true, Schedule::getTimeSlot);
        
        List<Schedule> schedules = this.list(wrapper);
        List<ScheduleVO> scheduleVOs = schedules.stream().map(this::convertToVO).collect(Collectors.toList());
        
        // 按星期分组
        return scheduleVOs.stream().collect(Collectors.groupingBy(ScheduleVO::getDayOfWeekName));
    }

    @Override
    @Transactional
    public void removeCourseFromSchedule(Long scheduleId) {
        Schedule schedule = this.getById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("课程表记录不存在");
        }
        
        System.out.println("=== 删除课程调试信息 ===");
        System.out.println("删除的课程表ID: " + scheduleId);
        System.out.println("课程表记录中的reducedHours: " + schedule.getReducedHours());
        
        // 恢复申请记录的剩余课时
        CourseApplication application = courseApplicationMapper.selectById(schedule.getCourseId());
        if (application != null) {
            Integer currentRemainingHours = application.getRemainingHours();
            if (currentRemainingHours == null) {
                currentRemainingHours = 0;
            }
            System.out.println("删除前申请记录剩余课时: " + currentRemainingHours);
            System.out.println("准备恢复的课时数: " + schedule.getReducedHours());
            
            application.setRemainingHours(currentRemainingHours + schedule.getReducedHours());
            System.out.println("删除后申请记录剩余课时: " + application.getRemainingHours());
            courseApplicationMapper.updateById(application);
        }
        
        // 删除课程表记录
        this.removeById(scheduleId);
        System.out.println("=== 删除课程完成 ===");
    }

    @Override
    public boolean checkTimeConflict(Long teacherId, String academicYear, Integer weekNumber, Integer dayOfWeek, Integer timeSlot) {
        // 从JWT获取当前登录用户信息
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("获取当前用户信息失败，请重新登录");
        }
        
        // 使用JWT获取的用户ID，忽略传入的teacherId参数
        Long actualTeacherId = currentUser.getId();
        
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Schedule::getTeacherId, actualTeacherId)
               .eq(Schedule::getAcademicYear, academicYear)
               .eq(Schedule::getWeekNumber, weekNumber)
               .eq(Schedule::getDayOfWeek, dayOfWeek)
               .eq(Schedule::getTimeSlot, timeSlot);
        
        return this.count(wrapper) > 0;
    }

    @Override
    public List<Map<String, Object>> getAvailableCourses(Long teacherId, String academicYear) {
        // 从JWT获取当前登录用户信息
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("获取当前用户信息失败，请重新登录");
        }
        
        // 使用JWT获取的用户ID，忽略传入的teacherId参数
        Long actualTeacherId = currentUser.getId();
        System.out.println("=== getAvailableCourses ===");
        System.out.println("当前教师ID: " + actualTeacherId);
        System.out.println("学年: " + academicYear);
        
        // 查询教师申请通过的课程申请
        LambdaQueryWrapper<CourseApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseApplication::getTeacherId, actualTeacherId)
               .eq(CourseApplication::getAcademicYear, academicYear)
               .eq(CourseApplication::getStatus, 1); // 已审核通过
        
        List<CourseApplication> applications = courseApplicationMapper.selectList(wrapper);
        System.out.println("找到的申请记录数: " + applications.size());
        
        return applications.stream()
                .filter(application -> {
                    Integer remainingHours = application.getRemainingHours();
                    if (remainingHours == null) {
                        remainingHours = application.getCourseHours();
                    }
                    return remainingHours > 0; // 只显示还有剩余课时的课程
                })
                .map(application -> {
                    Map<String, Object> map = new HashMap<>();
                    Integer remainingHours = application.getRemainingHours();
                    if (remainingHours == null) {
                        remainingHours = application.getCourseHours();
                    }
                    map.put("value", application.getId());
                    map.put("label", application.getCourseName() + "（剩余" + remainingHours + "课时）");
                    map.put("courseHours", application.getCourseHours());
                    map.put("remainingHours", remainingHours);
                    map.put("courseName", application.getCourseName());
                    return map;
                }).collect(Collectors.toList());
    }

    private ScheduleVO convertToVO(Schedule schedule) {
        ScheduleVO vo = new ScheduleVO();
        BeanUtils.copyProperties(schedule, vo);
        
        // 设置星期名称
        String[] weekNames = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        if (schedule.getDayOfWeek() >= 1 && schedule.getDayOfWeek() <= 7) {
            vo.setDayOfWeekName(weekNames[schedule.getDayOfWeek()]);
        }
        
        return vo;
    }
} 