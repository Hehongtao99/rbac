package com.example.service.impl;

import com.example.entity.Schedule;
import com.example.entity.Course;
import com.example.entity.TimeSlotConfig;
import com.example.entity.User;
import com.example.entity.CourseApplication;
import com.example.entity.ClassCourseHours;
import com.example.mapper.ScheduleMapper;
import com.example.mapper.CourseMapper;
import com.example.mapper.TimeSlotConfigMapper;
import com.example.mapper.UserMapper;
import com.example.mapper.CourseApplicationMapper;
import com.example.service.ScheduleService;
import com.example.service.TeacherClassService;
import com.example.service.ClassCourseHoursService;
import com.example.dto.ScheduleDTO;
import com.example.vo.ScheduleVO;
import com.example.util.UserContextUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;
    
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
    
    @Autowired
    private TeacherClassService teacherClassService;
    
    @Autowired
    private ClassCourseHoursService classCourseHoursService;

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
        
        // 验证教师是否有权限操作指定班级
        if (scheduleDTO.getClassId() != null && !teacherClassService.hasClassPermission(teacherId, scheduleDTO.getClassId())) {
            throw new RuntimeException("您没有权限为该班级安排课程");
        }
        
        // 检查时间冲突 - 检查该教师在该时间段是否已有任何班级的课程
        boolean conflict = checkTimeConflict(teacherId, 
                scheduleDTO.getAcademicYear(), scheduleDTO.getWeekNumber(), 
                scheduleDTO.getDayOfWeek(), scheduleDTO.getTimeSlot(), null); // 传入null检查所有班级
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
        
        // 获取或创建班级课程课时记录
        ClassCourseHours classCourseHours = classCourseHoursService.getOrCreateClassCourseHours(
            application, scheduleDTO.getClassId(), scheduleDTO.getClassName());
        
        // 检查该班级的剩余课时是否足够
        if (classCourseHours.getRemainingHours() < 1) {
            throw new RuntimeException("该班级课程剩余课时不足（剩余" + classCourseHours.getRemainingHours() + "课时）");
        }
        
        // 获取时间段信息
        TimeSlotConfig timeSlot = timeSlotConfigMapper.selectByTimeSlot(scheduleDTO.getTimeSlot());
        
        // 创建课程表记录
        Schedule schedule = new Schedule();
        schedule.setCourseId(application.getId()); // 使用申请ID作为课程ID
        schedule.setCourseName(application.getCourseName());
        schedule.setTeacherId(teacherId); // 使用JWT获取的teacherId
        schedule.setTeacherName(teacherName); // 使用JWT获取的教师姓名
        schedule.setClassId(scheduleDTO.getClassId());
        schedule.setClassName(scheduleDTO.getClassName());
        schedule.setClassCourseHoursId(classCourseHours.getId()); // 关联班级课程课时记录
        schedule.setAcademicYear(scheduleDTO.getAcademicYear());
        schedule.setWeekNumber(scheduleDTO.getWeekNumber());
        schedule.setDayOfWeek(scheduleDTO.getDayOfWeek());
        schedule.setTimeSlot(scheduleDTO.getTimeSlot());
        schedule.setReducedHours(1); // 每次课程消耗1课时
        if (timeSlot != null) {
            schedule.setTimeRange(timeSlot.getStartTime() + "-" + timeSlot.getEndTime());
        }
        schedule.setCreateTime(LocalDateTime.now());
        schedule.setUpdateTime(LocalDateTime.now());
        schedule.setDeleted(0);
        
        // 保存课程表
        scheduleMapper.insert(schedule);
        
        // 使用班级课程课时（扣减该班级的剩余课时）
        boolean success = classCourseHoursService.useHours(classCourseHours.getId(), 1);
        if (!success) {
            throw new RuntimeException("课时扣减失败，请重试");
        }
        
        System.out.println("=== 班级课时扣减调试信息 ===");
        System.out.println("课程申请ID: " + application.getId());
        System.out.println("课程名称: " + application.getCourseName());
        System.out.println("班级ID: " + scheduleDTO.getClassId());
        System.out.println("班级名称: " + scheduleDTO.getClassName());
        System.out.println("班级课程课时记录ID: " + classCourseHours.getId());
        System.out.println("扣减前该班级剩余课时: " + (classCourseHours.getRemainingHours() + 1));
        System.out.println("扣减后该班级剩余课时: " + classCourseHours.getRemainingHours());
        System.out.println("=== 班级课时扣减完成 ===");
    }

    @Override
    @Transactional
    public void updateSchedule(Long scheduleId, ScheduleDTO scheduleDTO) {
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("课程表记录不存在");
        }
        
        // 验证教师权限
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null || !currentUser.getId().equals(schedule.getTeacherId())) {
            throw new RuntimeException("您没有权限修改此课程表记录");
        }
        
        // 验证班级权限
        if (scheduleDTO.getClassId() != null && !teacherClassService.hasClassPermission(currentUser.getId(), scheduleDTO.getClassId())) {
            throw new RuntimeException("您没有权限为该班级安排课程");
        }
        
        // 如果要更新时间信息，需要检查新时间是否冲突
        if (scheduleDTO.getDayOfWeek() != null && scheduleDTO.getTimeSlot() != null) {
            // 检查新时间是否与现有课程冲突（排除当前课程）
            List<Schedule> conflicts = scheduleMapper.selectConflict(currentUser.getId(), 
                    schedule.getWeekNumber(), scheduleDTO.getDayOfWeek(), scheduleDTO.getTimeSlot());
            
            // 排除当前课程
            conflicts = conflicts.stream()
                    .filter(s -> !s.getId().equals(scheduleId))
                    .collect(Collectors.toList());
            
            if (!conflicts.isEmpty()) {
                throw new RuntimeException("目标时间段已有课程安排，无法移动");
            }
            
            // 更新时间信息
            schedule.setDayOfWeek(scheduleDTO.getDayOfWeek());
            schedule.setTimeSlot(scheduleDTO.getTimeSlot());
        }
        
        // 更新班级信息
        if (scheduleDTO.getClassId() != null) {
            schedule.setClassId(scheduleDTO.getClassId());
            schedule.setClassName(scheduleDTO.getClassName());
        }
        
        // 更新学年和周次信息（如果提供）
        if (scheduleDTO.getAcademicYear() != null) {
            schedule.setAcademicYear(scheduleDTO.getAcademicYear());
        }
        if (scheduleDTO.getWeekNumber() != null) {
            schedule.setWeekNumber(scheduleDTO.getWeekNumber());
        }
        
        scheduleMapper.updateById(schedule);
    }

    @Override
    public List<ScheduleVO> getTeacherSchedule(Long teacherId, String academicYear) {
        List<Schedule> schedules = scheduleMapper.selectByTeacherAndAcademicYear(teacherId, academicYear);
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
        
        List<Schedule> schedules = scheduleMapper.selectByTeacherAndAcademicYearAndWeekNumber(actualTeacherId, academicYear, weekNumber);
        List<ScheduleVO> scheduleVOs = schedules.stream().map(this::convertToVO).collect(Collectors.toList());
        
        // 按星期分组
        return scheduleVOs.stream().collect(Collectors.groupingBy(ScheduleVO::getDayOfWeekName));
    }

    @Override
    public Map<String, List<ScheduleVO>> getWeeklyScheduleByClass(Long teacherId, String academicYear, Integer weekNumber, Long classId) {
        // 从JWT获取当前登录用户信息
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("获取当前用户信息失败，请重新登录");
        }
        
        // 使用JWT获取的用户ID
        Long actualTeacherId = currentUser.getId();
        
        // 验证教师是否有权限查看该班级
        if (classId != null && !teacherClassService.hasClassPermission(actualTeacherId, classId)) {
            throw new RuntimeException("您没有权限查看该班级的课程表");
        }
        
        List<Schedule> schedules = scheduleMapper.selectByTeacherAndAcademicYearAndWeekNumberAndClassId(actualTeacherId, academicYear, weekNumber, classId);
        List<ScheduleVO> scheduleVOs = schedules.stream().map(this::convertToVO).collect(Collectors.toList());
        
        // 按星期分组
        return scheduleVOs.stream().collect(Collectors.groupingBy(ScheduleVO::getDayOfWeekName));
    }

    @Override
    @Transactional
    public void removeCourseFromSchedule(Long scheduleId) {
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("课程表记录不存在");
        }
        
        // 验证教师权限
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null || !currentUser.getId().equals(schedule.getTeacherId())) {
            throw new RuntimeException("您没有权限删除此课程表记录");
        }
        
        System.out.println("=== 删除课程调试信息 ===");
        System.out.println("删除的课程表ID: " + scheduleId);
        System.out.println("课程表记录中的reducedHours: " + schedule.getReducedHours());
        System.out.println("班级课程课时记录ID: " + schedule.getClassCourseHoursId());
        
        // 恢复班级课程课时
        if (schedule.getClassCourseHoursId() != null) {
            boolean success = classCourseHoursService.restoreHours(schedule.getClassCourseHoursId(), schedule.getReducedHours());
            if (!success) {
                throw new RuntimeException("课时恢复失败，请重试");
            }
            System.out.println("成功恢复班级课时: " + schedule.getReducedHours());
        } else {
            // 兼容旧数据：如果没有班级课程课时记录ID，则恢复到申请记录的剩余课时
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
        }
        
        // 删除课程表记录
        scheduleMapper.deleteById(scheduleId);
        System.out.println("=== 删除课程完成 ===");
    }

    @Override
    public boolean checkTimeConflict(Long teacherId, String academicYear, Integer weekNumber, Integer dayOfWeek, Integer timeSlot, Long classId) {
        // 从JWT获取当前登录用户信息
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("获取当前用户信息失败，请重新登录");
        }
        
        // 使用JWT获取的用户ID，忽略传入的teacherId参数
        Long actualTeacherId = currentUser.getId();
        
        List<Schedule> conflicts = scheduleMapper.selectConflict(actualTeacherId, weekNumber, dayOfWeek, timeSlot);
        
        // 如果指定了班级，检查该班级的时间冲突
        // 如果没有指定班级（classId为null），检查该教师在该时间段是否已经有任何班级的课程
        if (classId != null) {
            conflicts = conflicts.stream()
                    .filter(s -> s.getClassId().equals(classId))
                    .collect(Collectors.toList());
        }
        // 当classId为null时，不添加班级条件，这样会检查该教师在该时间段的所有班级课程
        
        return !conflicts.isEmpty();
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
        List<CourseApplication> applications = courseApplicationMapper.selectByTeacherIdAndAcademicYearAndStatus(actualTeacherId, academicYear, 1);
        System.out.println("找到的申请记录数: " + applications.size());
        
        return applications.stream()
                .map(application -> {
                    Map<String, Object> map = new HashMap<>();
                    Integer remainingHours = application.getRemainingHours();
                    if (remainingHours == null) {
                        remainingHours = application.getCourseHours();
                    }
                    map.put("value", application.getId());
                    map.put("label", application.getCourseName() + "（总课时" + application.getCourseHours() + "）");
                    map.put("courseHours", application.getCourseHours());
                    map.put("remainingHours", remainingHours);
                    map.put("courseName", application.getCourseName());
                    return map;
                }).collect(Collectors.toList());
    }
    
    /**
     * 获取教师在指定班级的可用课程列表（显示该班级的剩余课时）
     */
    public List<Map<String, Object>> getAvailableCoursesForClass(Long teacherId, Long classId, String academicYear) {
        // 从JWT获取当前登录用户信息
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("获取当前用户信息失败，请重新登录");
        }
        
        // 使用JWT获取的用户ID
        Long actualTeacherId = currentUser.getId();
        
        return classCourseHoursService.getAvailableCoursesForClass(actualTeacherId, classId, academicYear);
    }

    @Override
    public List<Map<String, Object>> getTeacherClasses(Long teacherId) {
        // 从JWT获取当前登录用户信息
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("获取当前用户信息失败，请重新登录");
        }
        
        // 使用JWT获取的用户ID
        Long actualTeacherId = currentUser.getId();
        
        return teacherClassService.getTeacherClasses(actualTeacherId);
    }

    @Override
    public List<ScheduleVO> getAllSchedulesForAdmin(String academicYear, Integer weekNumber, String teacherName, String courseName) {
        System.out.println("=== getAllSchedulesForAdmin 调试信息 ===");
        System.out.println("学年: " + academicYear);
        System.out.println("周次: " + weekNumber);
        System.out.println("教师姓名: " + teacherName);
        System.out.println("课程名称: " + courseName);
        
        List<Schedule> schedules = scheduleMapper.selectByAcademicYearAndWeekNumberAndTeacherNameAndCourseName(academicYear, weekNumber, teacherName, courseName);
        System.out.println("查询到的课程表数量: " + schedules.size());
        
        for (Schedule schedule : schedules) {
            System.out.println("课程表记录: " + schedule.getId() + " - " + schedule.getCourseName() + " - " + schedule.getTeacherName() + " - 第" + schedule.getWeekNumber() + "周 - " + schedule.getDayOfWeek() + " - " + schedule.getTimeSlot());
        }
        
        List<ScheduleVO> result = schedules.stream().map(this::convertToVO).collect(Collectors.toList());
        System.out.println("返回的VO数量: " + result.size());
        System.out.println("=== getAllSchedulesForAdmin 结束 ===");
        
        return result;
    }

    @Override
    public Map<String, Object> getWeeklyScheduleForAdmin(String academicYear, Integer weekNumber, Long teacherId) {
        Schedule querySchedule = new Schedule();
        querySchedule.setAcademicYear(academicYear);
        querySchedule.setWeekNumber(weekNumber);
        if (teacherId != null) {
            querySchedule.setTeacherId(teacherId);
        }
        
        List<Schedule> schedules = scheduleMapper.selectList(querySchedule);
        List<ScheduleVO> scheduleVOs = schedules.stream().map(this::convertToVO).collect(Collectors.toList());
        
        // 按星期分组
        Map<String, List<ScheduleVO>> weeklyData = scheduleVOs.stream()
                .collect(Collectors.groupingBy(ScheduleVO::getDayOfWeekName));
        
        // 获取所有教师列表
        List<Map<String, Object>> teachers = schedules.stream()
                .map(s -> {
                    Map<String, Object> teacher = new HashMap<>();
                    teacher.put("id", s.getTeacherId());
                    teacher.put("name", s.getTeacherName());
                    return teacher;
                })
                .distinct()
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("schedules", weeklyData);
        result.put("teachers", teachers);
        result.put("totalCount", schedules.size());
        
        return result;
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