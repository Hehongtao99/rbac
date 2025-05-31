package com.example.service.impl;

import com.example.entity.Student;
import com.example.entity.User;
import com.example.entity.Role;
import com.example.entity.UserRole;
import com.example.dto.StudentDTO;
import com.example.dto.StudentPageDTO;
import com.example.mapper.StudentMapper;
import com.example.mapper.UserMapper;
import com.example.mapper.RoleMapper;
import com.example.mapper.UserRoleMapper;
import com.example.service.StudentService;
import com.example.common.Result;
import com.example.common.PageResult;
import com.example.util.PageUtil;
import com.example.vo.StudentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

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

    // 状态映射
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();
    
    static {
        STATUS_MAP.put(0, "禁用");
        STATUS_MAP.put(1, "启用");
    }

    @Override
    public PageResult<StudentVO> getStudentList(StudentPageDTO pageDTO) {
        int offset = PageUtil.calculateOffset(pageDTO.getPage(), pageDTO.getSize());
        
        List<Student> students = studentMapper.selectPage(offset, pageDTO.getSize(), pageDTO.getKeyword());
        long total = studentMapper.selectCount(new Student());
        
        // 转换为VO
        List<StudentVO> voList = students.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageUtil.createPageResult(pageDTO.getPage(), pageDTO.getSize(), total, voList);
    }

    @Override
    public void createStudent(StudentDTO studentDTO) {
        Student student = new Student();
        BeanUtils.copyProperties(studentDTO, student);
        
        student.setCreateTime(LocalDateTime.now());
        student.setUpdateTime(LocalDateTime.now());
        student.setStatus(1);
        student.setDeleted(0);
        
        studentMapper.insert(student);
    }

    @Override
    public void updateStudent(Long id, StudentDTO studentDTO) {
        Student existingStudent = studentMapper.selectById(id);
        if (existingStudent == null) {
            throw new RuntimeException("学生不存在");
        }
        
        Student student = new Student();
        BeanUtils.copyProperties(studentDTO, student);
        student.setId(id);
        student.setUpdateTime(LocalDateTime.now());
        
        studentMapper.updateById(student);
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        
        studentMapper.deleteById(id);
    }

    @Override
    public StudentVO getStudentById(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        
        return convertToVO(student);
    }

    @Override
    public void assignGradeAndEducation(Long id, String grade, String educationSystem) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        
        student.setGrade(grade);
        student.setEducationSystem(educationSystem);
        student.setUpdateTime(LocalDateTime.now());
        
        studentMapper.updateById(student);
    }

    @Override
    public void setStudentSemester(Long id, Integer currentYear, Integer currentSemester) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        
        student.setCurrentYear(currentYear);
        student.setCurrentSemester(currentSemester);
        student.setUpdateTime(LocalDateTime.now());
        
        studentMapper.updateById(student);
    }
    
    /**
     * 转换为VO对象
     */
    private StudentVO convertToVO(Student student) {
        StudentVO vo = new StudentVO();
        BeanUtils.copyProperties(student, vo);
        
        // 设置状态名称
        vo.setStatusName(STATUS_MAP.get(student.getStatus()));
        
        return vo;
    }
} 