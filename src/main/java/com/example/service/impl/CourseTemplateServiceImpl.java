package com.example.service.impl;

import com.example.common.PageResult;
import com.example.dto.CourseTemplateDTO;
import com.example.dto.CourseTemplatePageDTO;
import com.example.entity.CourseTemplate;
import com.example.entity.Organization;
import com.example.entity.User;
import com.example.entity.UserOrganization;
import com.example.mapper.CourseTemplateMapper;
import com.example.mapper.OrganizationMapper;
import com.example.mapper.UserOrganizationMapper;
import com.example.service.CourseTemplateService;
import com.example.util.PageUtil;
import com.example.util.UserContextUtil;
import com.example.vo.CourseTemplateVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseTemplateServiceImpl implements CourseTemplateService {

    @Autowired
    private CourseTemplateMapper courseTemplateMapper;
    
    @Autowired
    private OrganizationMapper organizationMapper;
    
    @Autowired
    private UserOrganizationMapper userOrganizationMapper;
    
    @Autowired
    private UserContextUtil userContextUtil;

    // 状态映射
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();
    
    static {
        STATUS_MAP.put(0, "禁用");
        STATUS_MAP.put(1, "启用");
    }

    @Override
    public PageResult<CourseTemplateVO> getTemplateList(CourseTemplatePageDTO pageDTO) {
        int offset = PageUtil.calculateOffset(pageDTO.getPage(), pageDTO.getSize());
        
        List<CourseTemplate> templates = courseTemplateMapper.selectPage(
            offset, pageDTO.getSize(), pageDTO.getKeyword(), pageDTO.getAcademicYear(),
            pageDTO.getSemester(), pageDTO.getCollegeId(), pageDTO.getMajorId(),
            pageDTO.getStatus(), pageDTO.getEnabledOnly());
        
        long total = courseTemplateMapper.selectPageCount(
            pageDTO.getKeyword(), pageDTO.getAcademicYear(), pageDTO.getSemester(),
            pageDTO.getCollegeId(), pageDTO.getMajorId(), pageDTO.getStatus(),
            pageDTO.getEnabledOnly());
        
        // 转换为VO
        List<CourseTemplateVO> voList = templates.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageUtil.createPageResult(pageDTO.getPage(), pageDTO.getSize(), total, voList);
    }

    @Override
    public PageResult<CourseTemplateVO> getEnabledTemplateList(CourseTemplatePageDTO pageDTO) {
        // 设置只查询启用的模板
        pageDTO.setEnabledOnly(true);
        return getTemplateList(pageDTO);
    }

    @Override
    public PageResult<CourseTemplateVO> getAvailableTemplatesForTeacher(CourseTemplatePageDTO pageDTO) {
        // 获取当前登录教师信息
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("获取用户信息失败，请重新登录");
        }
        
        // 获取教师的学院专业信息
        TeacherOrganizationInfo teacherOrgInfo = getTeacherOrganizationInfo(currentUser.getId());
        
        // 设置查询条件：只查询启用的模板
        pageDTO.setEnabledOnly(true);
        
        int offset = PageUtil.calculateOffset(pageDTO.getPage(), pageDTO.getSize());
        
        // 查询课程模板，根据教师的学院专业进行筛选
        List<CourseTemplate> templates = courseTemplateMapper.selectPageForTeacher(
            offset, pageDTO.getSize(), pageDTO.getKeyword(), pageDTO.getAcademicYear(),
            pageDTO.getSemester(), teacherOrgInfo.getCollegeId(), teacherOrgInfo.getMajorId());
        
        long total = courseTemplateMapper.selectPageCountForTeacher(
            pageDTO.getKeyword(), pageDTO.getAcademicYear(), pageDTO.getSemester(),
            teacherOrgInfo.getCollegeId(), teacherOrgInfo.getMajorId());
        
        // 转换为VO
        List<CourseTemplateVO> voList = templates.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageUtil.createPageResult(pageDTO.getPage(), pageDTO.getSize(), total, voList);
    }

    @Override
    public void createTemplate(CourseTemplateDTO templateDTO) {
        // 验证必填字段
        if (templateDTO.getTemplateName() == null || templateDTO.getTemplateName().trim().isEmpty()) {
            throw new RuntimeException("模板名称不能为空");
        }
        if (templateDTO.getAcademicYear() == null || templateDTO.getAcademicYear().trim().isEmpty()) {
            throw new RuntimeException("学年不能为空");
        }
        if (templateDTO.getSemester() == null || templateDTO.getSemester().trim().isEmpty()) {
            throw new RuntimeException("学期不能为空");
        }
        if (templateDTO.getCourseHours() == null || templateDTO.getCourseHours() <= 0) {
            throw new RuntimeException("课程学时必须大于0");
        }
        if (templateDTO.getMaxStudents() == null || templateDTO.getMaxStudents() <= 0) {
            throw new RuntimeException("计划人数必须大于0");
        }
        
        CourseTemplate template = new CourseTemplate();
        BeanUtils.copyProperties(templateDTO, template);
        
        template.setCreateTime(LocalDateTime.now());
        template.setUpdateTime(LocalDateTime.now());
        template.setDeleted(0);
        
        if (template.getStatus() == null) {
            template.setStatus(1);
        }
        
        courseTemplateMapper.insert(template);
    }

    @Override
    public void updateTemplate(Long id, CourseTemplateDTO templateDTO) {
        CourseTemplate existingTemplate = courseTemplateMapper.selectById(id);
        if (existingTemplate == null) {
            throw new RuntimeException("模板不存在");
        }
        
        // 验证必填字段
        if (templateDTO.getTemplateName() == null || templateDTO.getTemplateName().trim().isEmpty()) {
            throw new RuntimeException("模板名称不能为空");
        }
        if (templateDTO.getAcademicYear() == null || templateDTO.getAcademicYear().trim().isEmpty()) {
            throw new RuntimeException("学年不能为空");
        }
        if (templateDTO.getSemester() == null || templateDTO.getSemester().trim().isEmpty()) {
            throw new RuntimeException("学期不能为空");
        }
        if (templateDTO.getCourseHours() == null || templateDTO.getCourseHours() <= 0) {
            throw new RuntimeException("课程学时必须大于0");
        }
        if (templateDTO.getMaxStudents() == null || templateDTO.getMaxStudents() <= 0) {
            throw new RuntimeException("计划人数必须大于0");
        }
        
        CourseTemplate template = new CourseTemplate();
        BeanUtils.copyProperties(templateDTO, template);
        template.setId(id);
        template.setUpdateTime(LocalDateTime.now());
        
        courseTemplateMapper.updateById(template);
    }

    @Override
    public void deleteTemplate(Long id) {
        CourseTemplate template = courseTemplateMapper.selectById(id);
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }
        
        courseTemplateMapper.deleteById(id);
    }

    @Override
    public CourseTemplateVO getTemplateById(Long id) {
        CourseTemplate template = courseTemplateMapper.selectById(id);
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }
        
        return convertToVO(template);
    }
    
    /**
     * 转换为VO对象
     */
    private CourseTemplateVO convertToVO(CourseTemplate template) {
        CourseTemplateVO vo = new CourseTemplateVO();
        BeanUtils.copyProperties(template, vo);
        
        // 设置状态名称
        vo.setStatusName(STATUS_MAP.get(template.getStatus()));
        
        // 设置学院和专业名称
        if (template.getCollegeId() != null) {
            Organization college = organizationMapper.selectById(template.getCollegeId());
            if (college != null) {
                vo.setCollegeName(college.getOrgName());
            }
        }
        
        if (template.getMajorId() != null) {
            Organization major = organizationMapper.selectById(template.getMajorId());
            if (major != null) {
                vo.setMajorName(major.getOrgName());
            }
        }
        
        return vo;
    }
    
    /**
     * 获取教师的学院专业信息
     */
    private TeacherOrganizationInfo getTeacherOrganizationInfo(Long teacherId) {
        TeacherOrganizationInfo info = new TeacherOrganizationInfo();
        
        // 获取教师的组织信息
        List<UserOrganization> userOrganizations = userOrganizationMapper.selectByUserId(teacherId);
        if (userOrganizations.isEmpty()) {
            return info; // 教师没有分配组织，返回空信息
        }
        
        // 获取教师所属的组织ID列表
        List<Long> teacherOrgIds = userOrganizations.stream()
                .map(UserOrganization::getOrganizationId)
                .collect(Collectors.toList());
        
        for (Long orgId : teacherOrgIds) {
            Organization org = organizationMapper.selectById(orgId);
            if (org != null) {
                if (org.getOrgLevel() == 1) { // 学院
                    info.setCollegeId(org.getId());
                } else if (org.getOrgLevel() == 2) { // 专业
                    info.setMajorId(org.getId());
                    // 如果是专业，需要找到其父级学院
                    if (org.getParentId() != null && org.getParentId() != 0) {
                        Organization college = organizationMapper.selectById(org.getParentId());
                        if (college != null && college.getOrgLevel() == 1) {
                            info.setCollegeId(college.getId());
                        }
                    }
                } else if (org.getOrgLevel() == 3) { // 班级
                    // 如果是班级，需要找到其专业和学院
                    if (org.getParentId() != null && org.getParentId() != 0) {
                        Organization major = organizationMapper.selectById(org.getParentId());
                        if (major != null && major.getOrgLevel() == 2) {
                            info.setMajorId(major.getId());
                            if (major.getParentId() != null && major.getParentId() != 0) {
                                Organization college = organizationMapper.selectById(major.getParentId());
                                if (college != null && college.getOrgLevel() == 1) {
                                    info.setCollegeId(college.getId());
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return info;
    }
    
    /**
     * 教师组织信息内部类
     */
    private static class TeacherOrganizationInfo {
        private Long collegeId;
        private Long majorId;
        
        public Long getCollegeId() {
            return collegeId;
        }
        
        public void setCollegeId(Long collegeId) {
            this.collegeId = collegeId;
        }
        
        public Long getMajorId() {
            return majorId;
        }
        
        public void setMajorId(Long majorId) {
            this.majorId = majorId;
        }
    }
} 