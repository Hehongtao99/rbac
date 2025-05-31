package com.example.service.impl;

import com.example.entity.Organization;
import com.example.entity.UserOrganization;
import com.example.mapper.OrganizationMapper;
import com.example.mapper.UserOrganizationMapper;
import com.example.service.TeacherClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TeacherClassServiceImpl implements TeacherClassService {

    @Autowired
    private UserOrganizationMapper userOrganizationMapper;
    
    @Autowired
    private OrganizationMapper organizationMapper;

    @Override
    public List<Map<String, Object>> getTeacherClasses(Long teacherId) {
        // 获取教师分配的组织ID列表
        List<UserOrganization> userOrganizations = userOrganizationMapper.selectByUserId(teacherId);
        
        if (userOrganizations.isEmpty()) {
            return List.of();
        }
        
        // 获取组织ID列表
        List<Long> orgIds = userOrganizations.stream()
                .map(UserOrganization::getOrganizationId)
                .collect(Collectors.toList());
        
        // 查询班级组织（orgLevel = 3）
        List<Organization> classes = organizationMapper.selectByIdsAndLevelAndStatus(orgIds, 3, 1);
        
        return classes.stream().map(clazz -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", clazz.getId());
            map.put("name", clazz.getOrgName());
            map.put("code", clazz.getOrgCode());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean hasClassPermission(Long teacherId, Long classId) {
        if (classId == null) {
            return false;
        }
        
        // 获取教师分配的班级列表
        List<Map<String, Object>> teacherClasses = getTeacherClasses(teacherId);
        
        // 检查是否包含指定班级
        return teacherClasses.stream()
                .anyMatch(clazz -> classId.equals(clazz.get("id")));
    }
} 