package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Student;
import com.example.entity.User;
import com.example.entity.Role;
import com.example.entity.UserRole;
import com.example.dto.StudentDTO;
import com.example.mapper.StudentMapper;
import com.example.mapper.UserMapper;
import com.example.mapper.RoleMapper;
import com.example.mapper.UserRoleMapper;
import com.example.service.StudentService;
import com.example.common.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public Result<Object> getStudentList(Integer page, Integer size, String keyword) {
        // 1. 先找到学生角色
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(Role::getRoleCode, "STUDENT");
        Role studentRole = roleMapper.selectOne(roleWrapper);
        
        if (studentRole == null) {
            return Result.error("学生角色不存在");
        }
        
        // 2. 找到所有具有学生角色的用户ID
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getRoleId, studentRole.getId());
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        
        if (userRoles.isEmpty()) {
            Page<User> emptyPage = new Page<>(page, size);
            return Result.success(emptyPage);
        }
        
        List<Long> studentUserIds = userRoles.stream()
                .map(UserRole::getUserId)
                .collect(Collectors.toList());
        
        // 3. 分页查询这些用户
        Page<User> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(User::getId, studentUserIds);
        
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
    public Result<Object> createStudent(StudentDTO studentDTO) {
        // 检查学号是否已存在
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentNo, studentDTO.getStudentNo());
        Student existStudent = this.getOne(wrapper);
        
        if (existStudent != null) {
            return Result.error("学号已存在");
        }
        
        Student student = new Student();
        BeanUtils.copyProperties(studentDTO, student);
        student.setStatus(1); // 默认启用
        
        boolean success = this.save(student);
        if (success) {
            return Result.success("学生创建成功");
        } else {
            return Result.error("学生创建失败");
        }
    }

    @Override
    public Result<Object> updateStudent(Long id, StudentDTO studentDTO) {
        Student existStudent = this.getById(id);
        if (existStudent == null) {
            return Result.error("学生不存在");
        }
        
        // 检查学号是否已被其他学生使用
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentNo, studentDTO.getStudentNo())
               .ne(Student::getId, id);
        Student duplicateStudent = this.getOne(wrapper);
        
        if (duplicateStudent != null) {
            return Result.error("学号已被其他学生使用");
        }
        
        BeanUtils.copyProperties(studentDTO, existStudent);
        boolean success = this.updateById(existStudent);
        
        if (success) {
            return Result.success("学生更新成功");
        } else {
            return Result.error("学生更新失败");
        }
    }

    @Override
    public Result<Object> deleteStudent(Long id) {
        Student student = this.getById(id);
        if (student == null) {
            return Result.error("学生不存在");
        }
        
        boolean success = this.removeById(id);
        if (success) {
            return Result.success("学生删除成功");
        } else {
            return Result.error("学生删除失败");
        }
    }

    @Override
    public Result<Object> getStudentById(Long id) {
        Student student = this.getById(id);
        if (student == null) {
            return Result.error("学生不存在");
        }
        
        return Result.success(student);
    }
} 