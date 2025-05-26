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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        Page<Student> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Student::getName, keyword)
                    .or().like(Student::getStudentNo, keyword)
                    .or().like(Student::getMajor, keyword)
                    .or().like(Student::getClassName, keyword)
                    .or().like(Student::getEmail, keyword)
                    .or().like(Student::getPhone, keyword));
        }
        wrapper.orderByDesc(Student::getCreateTime);
        
        Page<Student> result = this.page(pageInfo, wrapper);
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
    
    @Override
    public Result<Object> assignGradeAndEducation(Long id, String grade, String educationSystem) {
        Student student = this.getById(id);
        if (student == null) {
            return Result.error("学生不存在");
        }
        
        student.setGrade(grade);
        student.setEducationSystem(educationSystem);
        
        boolean success = this.updateById(student);
        if (success) {
            return Result.success("年级和学制分配成功");
        } else {
            return Result.error("年级和学制分配失败");
        }
    }
    
    @Override
    public Result<Object> setStudentSemester(Long id, Integer currentYear, Integer currentSemester, String enrollmentDate) {
        Student student = this.getById(id);
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
            
            boolean success = this.updateById(student);
            if (success) {
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
     * @param currentYear 当前大几
     * @param currentSemester 当前学期（1-上学期，2-下学期）
     * @param enrollmentDate 入学时间
     * @return 学年字符串，如"2024-2025学年第二学期"
     */
    private String calculateAcademicYear(Integer currentYear, Integer currentSemester, LocalDateTime enrollmentDate) {
        // 入学年份
        int enrollmentYear = enrollmentDate.getYear();
        
        // 计算当前学年的起始年份
        // 例如：2022年入学，现在大三，那么当前学年起始年份是 2022 + (3-1) = 2024
        int currentAcademicStartYear = enrollmentYear + (currentYear - 1);
        int currentAcademicEndYear = currentAcademicStartYear + 1;
        
        String semesterText = currentSemester == 1 ? "第一学期" : "第二学期";
        
        return currentAcademicStartYear + "-" + currentAcademicEndYear + "学年" + semesterText;
    }
} 