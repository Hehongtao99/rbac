package com.example.service;

import java.util.List;
import java.util.Map;

public interface TeacherClassService {
    
    /**
     * 获取教师分配的班级列表
     * @param teacherId 教师ID
     * @return 班级列表
     */
    List<Map<String, Object>> getTeacherClasses(Long teacherId);
    
    /**
     * 检查教师是否有权限操作指定班级
     * @param teacherId 教师ID
     * @param classId 班级ID
     * @return 是否有权限
     */
    boolean hasClassPermission(Long teacherId, Long classId);
} 