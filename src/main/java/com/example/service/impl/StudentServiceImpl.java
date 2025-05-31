package com.example.service.impl;

import com.example.entity.Student;
import com.example.entity.User;
import com.example.entity.Role;
import com.example.entity.UserRole;
import com.example.entity.UserOrganization;
import com.example.entity.Organization;
import com.example.dto.StudentDTO;
import com.example.dto.StudentPageDTO;
import com.example.mapper.StudentMapper;
import com.example.mapper.UserMapper;
import com.example.mapper.RoleMapper;
import com.example.mapper.UserRoleMapper;
import com.example.mapper.UserOrganizationMapper;
import com.example.mapper.OrganizationMapper;
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
    
    @Autowired
    private UserOrganizationMapper userOrganizationMapper;
    
    @Autowired
    private OrganizationMapper organizationMapper;

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
        
        // 生成当前学年学期格式：xxxx-xxxx年第x学期
        if (student.getGrade() != null && student.getCurrentSemester() != null && student.getCurrentYear() != null) {
            String currentAcademicYear = generateAcademicYearSemester(student.getGrade(), student.getCurrentYear(), student.getCurrentSemester());
            vo.setCurrentAcademicYear(currentAcademicYear);
        } else {
            vo.setCurrentAcademicYear("未设置");
        }
        
        // 获取学生的组织信息
        if (student.getUserId() != null) {
            setStudentOrganizationInfo(vo, student.getUserId());
        }
        
        return vo;
    }
    
    /**
     * 设置学生的组织信息
     */
    private void setStudentOrganizationInfo(StudentVO vo, Long userId) {
        try {
            // 获取用户关联的组织
            List<UserOrganization> userOrganizations = userOrganizationMapper.selectByUserId(userId);
            
            if (!userOrganizations.isEmpty()) {
                // 获取组织ID列表
                List<Long> orgIds = userOrganizations.stream()
                        .map(UserOrganization::getOrganizationId)
                        .collect(Collectors.toList());
                
                // 查询组织信息
                List<Organization> organizations = organizationMapper.selectByIdsAndStatus(orgIds, 1);
                
                // 按级别分类组织信息
                for (Organization org : organizations) {
                    if (org.getOrgLevel() == 1) { // 学院
                        vo.setCollegeName(org.getOrgName());
                    }
                    // major和className已经在学生表中有对应字段，这里不需要重复设置
                }
            }
        } catch (Exception e) {
            // 组织信息获取失败不影响主流程
            System.err.println("获取学生组织信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成学年学期格式：xxxx-xxxx年第x学期
     */
    private String generateAcademicYearSemester(String grade, Integer currentYear, Integer currentSemester) {
        try {
            int gradeYear = Integer.parseInt(grade);
            
            // 计算当前所在的学年
            // 如果是第一学期（上学期），学年起始为入学年 + (当前年级 - 1)
            // 如果是第二学期（下学期），学年起始同样为入学年 + (当前年级 - 1)
            int academicStartYear = gradeYear + (currentYear - 1);
            int academicEndYear = academicStartYear + 1;
            
            String semesterText = currentSemester == 1 ? "第一学期" : "第二学期";
            
            return String.format("%d-%d年%s", academicStartYear, academicEndYear, semesterText);
        } catch (Exception e) {
            return "格式错误";
        }
    }
} 