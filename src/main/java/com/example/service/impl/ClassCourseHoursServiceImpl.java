package com.example.service.impl;

import com.example.entity.ClassCourseHours;
import com.example.entity.CourseApplication;
import com.example.mapper.ClassCourseHoursMapper;
import com.example.mapper.CourseApplicationMapper;
import com.example.service.ClassCourseHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClassCourseHoursServiceImpl implements ClassCourseHoursService {

    @Autowired
    private ClassCourseHoursMapper classCourseHoursMapper;
    
    @Autowired
    private CourseApplicationMapper courseApplicationMapper;

    @Override
    @Transactional
    public ClassCourseHours getOrCreateClassCourseHours(CourseApplication application, Long classId, String className) {
        // 先查询是否已存在
        List<ClassCourseHours> existingList = classCourseHoursMapper.selectByApplicationId(application.getId());
        ClassCourseHours existing = existingList.stream()
                .filter(c -> c.getClassId().equals(classId))
                .findFirst()
                .orElse(null);
        
        if (existing != null) {
            return existing;
        }
        
        // 不存在则创建新记录
        ClassCourseHours classCourseHours = new ClassCourseHours();
        classCourseHours.setApplicationId(application.getId());
        classCourseHours.setClassId(classId);
        classCourseHours.setClassName(className);
        classCourseHours.setCourseName(application.getCourseName());
        classCourseHours.setTeacherId(application.getTeacherId());
        classCourseHours.setTeacherName(application.getTeacherName());
        classCourseHours.setAcademicYear(application.getAcademicYear());
        classCourseHours.setSemester(application.getSemester());
        classCourseHours.setTotalHours(application.getCourseHours());
        classCourseHours.setUsedHours(0);
        classCourseHours.setRemainingHours(application.getCourseHours());
        classCourseHours.setStatus(1);
        classCourseHours.setCreateTime(LocalDateTime.now());
        classCourseHours.setUpdateTime(LocalDateTime.now());
        classCourseHours.setDeleted(0);
        
        classCourseHoursMapper.insert(classCourseHours);
        return classCourseHours;
    }

    @Override
    @Transactional
    public boolean useHours(Long classCourseHoursId, Integer hours) {
        ClassCourseHours classCourseHours = classCourseHoursMapper.selectById(classCourseHoursId);
        if (classCourseHours == null) {
            return false;
        }
        
        if (classCourseHours.getRemainingHours() < hours) {
            return false; // 剩余课时不足
        }
        
        classCourseHours.setUsedHours(classCourseHours.getUsedHours() + hours);
        classCourseHours.setRemainingHours(classCourseHours.getRemainingHours() - hours);
        classCourseHours.setUpdateTime(LocalDateTime.now());
        
        return classCourseHoursMapper.updateById(classCourseHours) > 0;
    }

    @Override
    @Transactional
    public boolean restoreHours(Long classCourseHoursId, Integer hours) {
        ClassCourseHours classCourseHours = classCourseHoursMapper.selectById(classCourseHoursId);
        if (classCourseHours == null) {
            return false;
        }
        
        if (classCourseHours.getUsedHours() < hours) {
            return false; // 已使用课时不足以恢复
        }
        
        classCourseHours.setUsedHours(classCourseHours.getUsedHours() - hours);
        classCourseHours.setRemainingHours(classCourseHours.getRemainingHours() + hours);
        classCourseHours.setUpdateTime(LocalDateTime.now());
        
        return classCourseHoursMapper.updateById(classCourseHours) > 0;
    }

    @Override
    public List<Map<String, Object>> getAvailableCoursesForClass(Long teacherId, Long classId, String academicYear) {
        // 获取教师已通过的课程申请
        List<CourseApplication> applications = courseApplicationMapper.selectByTeacherIdAndAcademicYearAndStatus(teacherId, academicYear, 1);
        
        return applications.stream().map(application -> {
            // 获取或创建该班级的课时记录
            ClassCourseHours classCourseHours = getByApplicationAndClass(application.getId(), classId);
            
            Integer remainingHours = 0;
            if (classCourseHours != null) {
                remainingHours = classCourseHours.getRemainingHours();
            } else {
                // 如果没有记录，说明该班级还没有使用过这门课程，剩余课时等于总课时
                remainingHours = application.getCourseHours();
            }
            
            Map<String, Object> map = new HashMap<>();
            map.put("id", application.getId());
            map.put("courseName", application.getCourseName());
            map.put("courseHours", application.getCourseHours());
            map.put("remainingHours", remainingHours);
            map.put("maxStudents", application.getMaxStudents());
            map.put("academicYear", application.getAcademicYear());
            map.put("semester", application.getSemester());
            map.put("classId", classId);
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean hasEnoughHours(Long classCourseHoursId, Integer requiredHours) {
        ClassCourseHours classCourseHours = classCourseHoursMapper.selectById(classCourseHoursId);
        if (classCourseHours == null) {
            return false;
        }
        
        return classCourseHours.getRemainingHours() >= requiredHours;
    }

    @Override
    public ClassCourseHours getByApplicationAndClass(Long applicationId, Long classId) {
        List<ClassCourseHours> list = classCourseHoursMapper.selectByApplicationId(applicationId);
        return list.stream()
                .filter(c -> c.getClassId().equals(classId))
                .findFirst()
                .orElse(null);
    }
} 