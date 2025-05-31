package com.example.service.impl;

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
import com.example.common.PageResult;
import com.example.util.PageUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public Result<Object> getTeacherList(Integer page, Integer size, String keyword) {
        int offset = PageUtil.calculateOffset(page, size);
        List<Teacher> teachers = teacherMapper.selectPage(offset, size, keyword);
        long total = teacherMapper.selectCount(new Teacher());
        
        PageResult<Teacher> result = PageUtil.createPageResult(page, size, total, teachers);
        return Result.success(result);
    }

    @Override
    public Result<Object> createTeacher(TeacherDTO teacherDTO) {
        // 检查工号是否已存在
        Teacher existTeacher = teacherMapper.selectByTeacherNo(teacherDTO.getTeacherNo());
        
        if (existTeacher != null) {
            return Result.error("教师工号已存在");
        }
        
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(teacherDTO, teacher);
        teacher.setStatus(1); // 默认启用
        teacher.setCreateTime(LocalDateTime.now());
        teacher.setUpdateTime(LocalDateTime.now());
        teacher.setDeleted(0);
        
        int result = teacherMapper.insert(teacher);
        if (result > 0) {
            return Result.success("教师创建成功");
        } else {
            return Result.error("教师创建失败");
        }
    }

    @Override
    public Result<Object> updateTeacher(Long id, TeacherDTO teacherDTO) {
        Teacher existTeacher = teacherMapper.selectById(id);
        if (existTeacher == null) {
            return Result.error("教师不存在");
        }
        
        // 检查工号是否已被其他教师使用
        Teacher duplicateTeacher = teacherMapper.selectByTeacherNo(teacherDTO.getTeacherNo());
        if (duplicateTeacher != null && !duplicateTeacher.getId().equals(id)) {
            return Result.error("教师工号已被其他教师使用");
        }
        
        BeanUtils.copyProperties(teacherDTO, existTeacher);
        existTeacher.setUpdateTime(LocalDateTime.now());
        
        int result = teacherMapper.updateById(existTeacher);
        if (result > 0) {
            return Result.success("教师更新成功");
        } else {
            return Result.error("教师更新失败");
        }
    }

    @Override
    public Result<Object> deleteTeacher(Long id) {
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            return Result.error("教师不存在");
        }
        
        int result = teacherMapper.deleteById(id);
        if (result > 0) {
            return Result.success("教师删除成功");
        } else {
            return Result.error("教师删除失败");
        }
    }

    @Override
    public Result<Object> getTeacherById(Long id) {
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            return Result.error("教师不存在");
        }
        
        return Result.success(teacher);
    }
} 