package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Teacher;
import com.example.entity.User;
import com.example.entity.Role;
import com.example.entity.UserRole;
import com.example.dto.TeacherDTO;
import com.example.mapper.TeacherMapper;
import com.example.mapper.UserMapper;
import com.example.mapper.RoleMapper;
import com.example.mapper.UserRoleMapper;
import com.example.service.TeacherService;
import com.example.common.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public Result<Object> getTeacherList(Integer page, Integer size, String keyword) {
        // 1. 先找到教师角色
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(Role::getRoleCode, "TEACHER");
        Role teacherRole = roleMapper.selectOne(roleWrapper);
        
        if (teacherRole == null) {
            return Result.error("教师角色不存在");
        }
        
        // 2. 找到所有具有教师角色的用户ID
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getRoleId, teacherRole.getId());
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        
        if (userRoles.isEmpty()) {
            Page<User> emptyPage = new Page<>(page, size);
            return Result.success(emptyPage);
        }
        
        List<Long> teacherUserIds = userRoles.stream()
                .map(UserRole::getUserId)
                .collect(Collectors.toList());
        
        // 3. 分页查询这些用户
        Page<User> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(User::getId, teacherUserIds);
        
        if (StringUtils.hasText(keyword)) {
            userWrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getEmail, keyword)
                    .or().like(User::getPhone, keyword));
        }
        userWrapper.orderByDesc(User::getCreateTime);
        
        Page<User> result = userMapper.selectPage(pageInfo, userWrapper);
        return Result.success(result);
    }

    @Override
    public Result<Object> createTeacher(TeacherDTO teacherDTO) {
        // 检查工号是否已存在
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getTeacherNo, teacherDTO.getTeacherNo());
        Teacher existTeacher = this.getOne(wrapper);
        
        if (existTeacher != null) {
            return Result.error("教师工号已存在");
        }
        
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(teacherDTO, teacher);
        teacher.setStatus(1); // 默认启用
        
        boolean success = this.save(teacher);
        if (success) {
            return Result.success("教师创建成功");
        } else {
            return Result.error("教师创建失败");
        }
    }

    @Override
    public Result<Object> updateTeacher(Long id, TeacherDTO teacherDTO) {
        Teacher existTeacher = this.getById(id);
        if (existTeacher == null) {
            return Result.error("教师不存在");
        }
        
        // 检查工号是否已被其他教师使用
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getTeacherNo, teacherDTO.getTeacherNo())
               .ne(Teacher::getId, id);
        Teacher duplicateTeacher = this.getOne(wrapper);
        
        if (duplicateTeacher != null) {
            return Result.error("教师工号已被其他教师使用");
        }
        
        BeanUtils.copyProperties(teacherDTO, existTeacher);
        boolean success = this.updateById(existTeacher);
        
        if (success) {
            return Result.success("教师更新成功");
        } else {
            return Result.error("教师更新失败");
        }
    }

    @Override
    public Result<Object> deleteTeacher(Long id) {
        Teacher teacher = this.getById(id);
        if (teacher == null) {
            return Result.error("教师不存在");
        }
        
        boolean success = this.removeById(id);
        if (success) {
            return Result.success("教师删除成功");
        } else {
            return Result.error("教师删除失败");
        }
    }

    @Override
    public Result<Object> getTeacherById(Long id) {
        Teacher teacher = this.getById(id);
        if (teacher == null) {
            return Result.error("教师不存在");
        }
        
        return Result.success(teacher);
    }
} 