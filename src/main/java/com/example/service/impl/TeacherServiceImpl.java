package com.example.service.impl;

import com.example.common.PageResult;
import com.example.dto.TeacherDTO;
import com.example.entity.Teacher;
import com.example.entity.User;
import com.example.entity.Role;
import com.example.entity.UserRole;
import com.example.entity.UserOrganization;
import com.example.entity.Organization;
import com.example.mapper.TeacherMapper;
import com.example.mapper.UserMapper;
import com.example.mapper.RoleMapper;
import com.example.mapper.UserRoleMapper;
import com.example.mapper.UserOrganizationMapper;
import com.example.mapper.OrganizationMapper;
import com.example.service.TeacherService;
import com.example.util.PageUtil;
import com.example.vo.TeacherVO;
import com.example.dto.TeacherPageDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public PageResult<TeacherVO> getTeacherList(TeacherPageDTO pageDTO) {
        int offset = PageUtil.calculateOffset(pageDTO.getPage(), pageDTO.getSize());
        
        List<Teacher> teachers = teacherMapper.selectPage(offset, pageDTO.getSize(), pageDTO.getKeyword());
        long total = teacherMapper.selectCount(new Teacher());
        
        // 转换为VO
        List<TeacherVO> voList = teachers.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageUtil.createPageResult(pageDTO.getPage(), pageDTO.getSize(), total, voList);
    }

    @Override
    public void createTeacher(TeacherDTO teacherDTO) {
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(teacherDTO, teacher);
        
        teacher.setCreateTime(LocalDateTime.now());
        teacher.setUpdateTime(LocalDateTime.now());
        teacher.setStatus(1);
        teacher.setDeleted(0);
        
        teacherMapper.insert(teacher);
    }

    @Override
    public void updateTeacher(Long id, TeacherDTO teacherDTO) {
        Teacher existingTeacher = teacherMapper.selectById(id);
        if (existingTeacher == null) {
            throw new RuntimeException("教师不存在");
        }
        
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(teacherDTO, teacher);
        teacher.setId(id);
        teacher.setUpdateTime(LocalDateTime.now());
        
        teacherMapper.updateById(teacher);
    }

    @Override
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            throw new RuntimeException("教师不存在");
        }
        
        teacherMapper.deleteById(id);
    }

    @Override
    public TeacherVO getTeacherById(Long id) {
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            throw new RuntimeException("教师不存在");
        }
        
        return convertToVO(teacher);
    }
    
    /**
     * 转换为VO对象
     */
    private TeacherVO convertToVO(Teacher teacher) {
        TeacherVO vo = new TeacherVO();
        BeanUtils.copyProperties(teacher, vo);
        
        // 设置状态名称
        vo.setStatusName(STATUS_MAP.get(teacher.getStatus()));
        
        // 获取教师的组织信息
        if (teacher.getUserId() != null) {
            setTeacherOrganizationInfo(vo, teacher.getUserId());
        }
        
        return vo;
    }
    
    /**
     * 设置教师的组织信息
     */
    private void setTeacherOrganizationInfo(TeacherVO vo, Long userId) {
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
                List<TeacherVO.ClassInfo> classList = new ArrayList<>();
                
                for (Organization org : organizations) {
                    if (org.getOrgLevel() == 1) { // 学院
                        vo.setCollege(org.getOrgName());
                    } else if (org.getOrgLevel() == 2) { // 专业
                        vo.setMajor(org.getOrgName());
                    } else if (org.getOrgLevel() == 3) { // 班级
                        TeacherVO.ClassInfo classInfo = new TeacherVO.ClassInfo();
                        classInfo.setId(org.getId());
                        classInfo.setName(org.getOrgName());
                        classInfo.setCode(org.getOrgCode());
                        classList.add(classInfo);
                    }
                }
                
                vo.setClasses(classList);
            }
        } catch (Exception e) {
            // 组织信息获取失败不影响主流程
            System.err.println("获取教师组织信息失败: " + e.getMessage());
        }
    }
} 