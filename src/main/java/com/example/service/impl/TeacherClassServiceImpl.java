package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        LambdaQueryWrapper<UserOrganization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOrganization::getUserId, teacherId);
        List<UserOrganization> userOrganizations = userOrganizationMapper.selectList(wrapper);
        
        if (userOrganizations.isEmpty()) {
            return List.of();
        }
        
        // 获取组织ID列表
        List<Long> orgIds = userOrganizations.stream()
                .map(UserOrganization::getOrganizationId)
                .collect(Collectors.toList());
        
        // 查询班级组织（orgLevel = 3）
        LambdaQueryWrapper<Organization> orgWrapper = new LambdaQueryWrapper<>();
        orgWrapper.in(Organization::getId, orgIds)
                 .eq(Organization::getOrgLevel, 3)  // 班级级别
                 .eq(Organization::getStatus, 1);   // 启用状态
        
        List<Organization> classes = organizationMapper.selectList(orgWrapper);
        
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