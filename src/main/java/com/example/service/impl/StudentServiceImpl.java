package com.example.service.impl;

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
import com.example.common.PageResult;
import com.example.util.PageUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public Result<Object> getStudentList(Integer page, Integer size, String keyword) {
        int offset = PageUtil.calculateOffset(page, size);
        List<Student> students = studentMapper.selectPage(offset, size, keyword);
        long total = studentMapper.selectCount(new Student());
        
        PageResult<Student> result = PageUtil.createPageResult(page, size, total, students);
        return Result.success(result);
    }

    @Override
    public Result<Object> createStudent(StudentDTO studentDTO) {
        // 检查学号是否已存在
        Student existStudent = studentMapper.selectByStudentNo(studentDTO.getStudentNo());
        
        if (existStudent != null) {
            return Result.error("学号已存在");
        }
        
        Student student = new Student();
        BeanUtils.copyProperties(studentDTO, student);
        student.setStatus(1); // 默认启用
        student.setCreateTime(LocalDateTime.now());
        student.setUpdateTime(LocalDateTime.now());
        student.setDeleted(0);
        
        int result = studentMapper.insert(student);
        if (result > 0) {
            return Result.success("学生创建成功");
        } else {
            return Result.error("学生创建失败");
        }
    }

    @Override
    public Result<Object> updateStudent(Long id, StudentDTO studentDTO) {
        Student existStudent = studentMapper.selectById(id);
        if (existStudent == null) {
            return Result.error("学生不存在");
        }
        
        // 检查学号是否已被其他学生使用
        Student duplicateStudent = studentMapper.selectByStudentNo(studentDTO.getStudentNo());
        if (duplicateStudent != null && !duplicateStudent.getId().equals(id)) {
            return Result.error("学号已被其他学生使用");
        }
        
        BeanUtils.copyProperties(studentDTO, existStudent);
        existStudent.setUpdateTime(LocalDateTime.now());
        
        int result = studentMapper.updateById(existStudent);
        if (result > 0) {
            return Result.success("学生更新成功");
        } else {
            return Result.error("学生更新失败");
        }
    }

    @Override
    public Result<Object> deleteStudent(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            return Result.error("学生不存在");
        }
        
        int result = studentMapper.deleteById(id);
        if (result > 0) {
            return Result.success("学生删除成功");
        } else {
            return Result.error("学生删除失败");
        }
    }

    @Override
    public Result<Object> getStudentById(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            return Result.error("学生不存在");
        }
        
        return Result.success(student);
    }
    
    @Override
    public Result<Object> assignGradeAndEducation(Long id, String grade, String educationSystem) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            return Result.error("学生不存在");
        }
        
        student.setGrade(grade);
        student.setEducationSystem(educationSystem);
        student.setUpdateTime(LocalDateTime.now());
        
        int result = studentMapper.updateById(student);
        if (result > 0) {
            return Result.success("年级和学制分配成功");
        } else {
            return Result.error("年级和学制分配失败");
        }
    }
    
    @Override
    public Result<Object> setStudentSemester(Long id, Integer currentYear, Integer currentSemester, String enrollmentDate) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            return Result.error("学生不存在");
        }
        
        try {
            // 计算毕业时间
            String educationSystemStr = student.getEducationSystem();
            if (educationSystemStr == null) {
                return Result.error("请先设置学制");
            }
            
            // 根据年级计算入学时间（入学年份9月1日）
            String gradeStr = student.getGrade();
            if (gradeStr == null) {
                return Result.error("请先设置年级");
            }
            
            // 从年级中提取入学年份，如"2022"
            int enrollmentYear = Integer.parseInt(gradeStr);
            LocalDateTime enrollmentDateTime = LocalDateTime.of(enrollmentYear, 9, 1, 8, 0, 0);
            
            // 提取学制年数
            int educationYears = Integer.parseInt(educationSystemStr.replace("年", ""));
            LocalDateTime graduationDateTime = enrollmentDateTime.plusYears(educationYears);
            
            // 计算当前学年
            String currentAcademicYear = calculateAcademicYear(currentYear, currentSemester, enrollmentDateTime);
            
            // 更新学生信息
            student.setEnrollmentDate(enrollmentDateTime);
            student.setGraduationDate(graduationDateTime);
            student.setCurrentYear(currentYear);
            student.setCurrentSemester(currentSemester);
            student.setCurrentAcademicYear(currentAcademicYear);
            student.setUpdateTime(LocalDateTime.now());
            
            int result = studentMapper.updateById(student);
            if (result > 0) {
                return Result.success("学期信息设置成功");
            } else {
                return Result.error("学期信息设置失败");
            }
        } catch (Exception e) {
            return Result.error("计算失败: " + e.getMessage());
        }
    }
    
    /**
     * 计算当前学年
     */
    private String calculateAcademicYear(Integer currentYear, Integer currentSemester, LocalDateTime enrollmentDate) {
        // 计算入学年份
        int enrollmentYear = enrollmentDate.getYear();
        
        // 计算当前学年的起始年份
        int academicStartYear = enrollmentYear + (currentYear - 1);
        
        // 格式化学年字符串
        return String.format("%d-%d学年第%d学期", academicStartYear, academicStartYear + 1, currentSemester);
    }
} 