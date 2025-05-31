package com.example.service;

import com.example.entity.ClassCourseHours;
import com.example.entity.CourseApplication;

import java.util.List;
import java.util.Map;

public interface ClassCourseHoursService {
    
    /**
     * 获取或创建班级课程课时记录
     * @param application 课程申请
     * @param classId 班级ID
     * @param className 班级名称
     * @return 班级课程课时记录
     */
    ClassCourseHours getOrCreateClassCourseHours(CourseApplication application, Long classId, String className);
    
    /**
     * 使用课时（增加已使用课时，减少剩余课时）
     * @param classCourseHoursId 班级课程课时记录ID
     * @param hours 使用的课时数
     * @return 是否成功
     */
    boolean useHours(Long classCourseHoursId, Integer hours);
    
    /**
     * 恢复课时（减少已使用课时，增加剩余课时）
     * @param classCourseHoursId 班级课程课时记录ID
     * @param hours 恢复的课时数
     * @return 是否成功
     */
    boolean restoreHours(Long classCourseHoursId, Integer hours);
    
    /**
     * 获取教师在指定班级的可用课程列表
     * @param teacherId 教师ID
     * @param classId 班级ID
     * @param academicYear 学年
     * @param semester 学期
     * @return 可用课程列表
     */
    List<Map<String, Object>> getAvailableCoursesForClass(Long teacherId, Long classId, String academicYear, String semester);
    
    /**
     * 检查班级课程课时是否足够
     * @param classCourseHoursId 班级课程课时记录ID
     * @param requiredHours 需要的课时数
     * @return 是否足够
     */
    boolean hasEnoughHours(Long classCourseHoursId, Integer requiredHours);
    
    /**
     * 获取班级课程课时详情
     * @param applicationId 课程申请ID
     * @param classId 班级ID
     * @return 班级课程课时记录
     */
    ClassCourseHours getByApplicationAndClass(Long applicationId, Long classId);
} 