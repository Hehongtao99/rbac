package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.ClassCourseHours;
import com.example.entity.CourseApplication;
import com.example.mapper.ClassCourseHoursMapper;
import com.example.mapper.CourseApplicationMapper;
import com.example.service.ClassCourseHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClassCourseHoursServiceImpl extends ServiceImpl<ClassCourseHoursMapper, ClassCourseHours> implements ClassCourseHoursService {

    @Autowired
    private CourseApplicationMapper courseApplicationMapper;

    @Override
    @Transactional
    public ClassCourseHours getOrCreateClassCourseHours(CourseApplication application, Long classId, String className) {
        // 先查询是否已存在
        LambdaQueryWrapper<ClassCourseHours> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassCourseHours::getApplicationId, application.getId())
               .eq(ClassCourseHours::getClassId, classId);
        
        ClassCourseHours existing = this.getOne(wrapper);
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
        
        this.save(classCourseHours);
        return classCourseHours;
    }

    @Override
    @Transactional
    public boolean useHours(Long classCourseHoursId, Integer hours) {
        ClassCourseHours classCourseHours = this.getById(classCourseHoursId);
        if (classCourseHours == null) {
            return false;
        }
        
        if (classCourseHours.getRemainingHours() < hours) {
            return false; // 剩余课时不足
        }
        
        classCourseHours.setUsedHours(classCourseHours.getUsedHours() + hours);
        classCourseHours.setRemainingHours(classCourseHours.getRemainingHours() - hours);
        
        return this.updateById(classCourseHours);
    }

    @Override
    @Transactional
    public boolean restoreHours(Long classCourseHoursId, Integer hours) {
        ClassCourseHours classCourseHours = this.getById(classCourseHoursId);
        if (classCourseHours == null) {
            return false;
        }
        
        if (classCourseHours.getUsedHours() < hours) {
            return false; // 已使用课时不足以恢复
        }
        
        classCourseHours.setUsedHours(classCourseHours.getUsedHours() - hours);
        classCourseHours.setRemainingHours(classCourseHours.getRemainingHours() + hours);
        
        return this.updateById(classCourseHours);
    }

    @Override
    public List<Map<String, Object>> getAvailableCoursesForClass(Long teacherId, Long classId, String academicYear) {
        // 获取教师已通过的课程申请
        LambdaQueryWrapper<CourseApplication> applicationWrapper = new LambdaQueryWrapper<>();
        applicationWrapper.eq(CourseApplication::getTeacherId, teacherId)
                         .eq(CourseApplication::getAcademicYear, academicYear)
                         .eq(CourseApplication::getStatus, 1); // 已通过
        
        List<CourseApplication> applications = courseApplicationMapper.selectList(applicationWrapper);
        
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
            map.put("value", application.getId());
            map.put("label", application.getCourseName() + "（该班级剩余" + remainingHours + "课时）");
            map.put("courseHours", application.getCourseHours());
            map.put("remainingHours", remainingHours);
            map.put("courseName", application.getCourseName());
            map.put("classId", classId);
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean hasEnoughHours(Long classCourseHoursId, Integer requiredHours) {
        ClassCourseHours classCourseHours = this.getById(classCourseHoursId);
        if (classCourseHours == null) {
            return false;
        }
        
        return classCourseHours.getRemainingHours() >= requiredHours;
    }

    @Override
    public ClassCourseHours getByApplicationAndClass(Long applicationId, Long classId) {
        LambdaQueryWrapper<ClassCourseHours> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassCourseHours::getApplicationId, applicationId)
               .eq(ClassCourseHours::getClassId, classId);
        
        return this.getOne(wrapper);
    }
} 