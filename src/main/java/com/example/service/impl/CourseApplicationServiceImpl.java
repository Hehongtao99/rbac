package com.example.service.impl;

import com.example.common.PageResult;
import com.example.dto.CourseApplicationDTO;
import com.example.dto.CourseApplicationQueryDTO;
import com.example.dto.CourseApplicationReviewDTO;
import com.example.entity.CourseApplication;
import com.example.entity.CourseTemplate;
import com.example.entity.Organization;
import com.example.entity.User;
import com.example.entity.UserOrganization;
import com.example.mapper.CourseApplicationMapper;
import com.example.mapper.CourseTemplateMapper;
import com.example.mapper.OrganizationMapper;
import com.example.mapper.UserOrganizationMapper;
import com.example.service.CourseApplicationService;
import com.example.util.PageUtil;
import com.example.util.UserContextUtil;
import com.example.vo.CourseApplicationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseApplicationServiceImpl implements CourseApplicationService {

    @Autowired
    private CourseApplicationMapper courseApplicationMapper;
    
    @Autowired
    private CourseTemplateMapper courseTemplateMapper;
    
    @Autowired
    private UserOrganizationMapper userOrganizationMapper;
    
    @Autowired
    private OrganizationMapper organizationMapper;
    
    @Autowired
    private UserContextUtil userContextUtil;

    // 状态映射
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();
    
    static {
        STATUS_MAP.put(0, "待审核");
        STATUS_MAP.put(1, "已通过");
        STATUS_MAP.put(2, "已拒绝");
    }

    @Override
    public PageResult<CourseApplicationVO> getApplicationList(CourseApplicationQueryDTO queryDTO) {
        // 获取当前登录用户信息
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("获取用户信息失败，请重新登录");
        }
        
        int offset = PageUtil.calculateOffset(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 只查询当前教师的申请
        List<CourseApplicationVO> applications = courseApplicationMapper.selectPageByTeacherId(
            offset, queryDTO.getPageSize(), queryDTO.getKeyword(), currentUser.getId());
        
        // 统计当前教师的申请总数
        CourseApplication queryCondition = new CourseApplication();
        queryCondition.setTeacherId(currentUser.getId());
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().trim().isEmpty()) {
            queryCondition.setCourseName(queryDTO.getKeyword());
        }
        long total = courseApplicationMapper.selectCount(queryCondition);
        
        // 设置状态名称
        List<CourseApplicationVO> voList = applications.stream()
                .map(vo -> {
                    vo.setStatusName(STATUS_MAP.get(vo.getStatus()));
                    return vo;
                })
                .collect(Collectors.toList());
        
        return PageUtil.createPageResult(queryDTO.getPageNum(), queryDTO.getPageSize(), total, voList);
    }

    @Override
    public PageResult<CourseApplicationVO> getApplicationListForAdmin(CourseApplicationQueryDTO queryDTO) {
        int offset = PageUtil.calculateOffset(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        List<CourseApplicationVO> applications = courseApplicationMapper.selectPageForAdmin(
            offset, queryDTO.getPageSize(), queryDTO.getKeyword(), queryDTO.getStatus(), 
            queryDTO.getAcademicYear(), queryDTO.getSemester(), queryDTO.getTeacherId());
        
        long total = courseApplicationMapper.selectCountForAdmin(
            queryDTO.getKeyword(), queryDTO.getStatus(), queryDTO.getAcademicYear(), 
            queryDTO.getSemester(), queryDTO.getTeacherId());
        
        // 设置状态名称
        List<CourseApplicationVO> voList = applications.stream()
                .map(vo -> {
                    vo.setStatusName(STATUS_MAP.get(vo.getStatus()));
                    return vo;
                })
                .collect(Collectors.toList());
        
        return PageUtil.createPageResult(queryDTO.getPageNum(), queryDTO.getPageSize(), total, voList);
    }

    @Override
    public void createApplication(CourseApplicationDTO applicationDTO) {
        CourseApplication application = new CourseApplication();
        BeanUtils.copyProperties(applicationDTO, application);
        
        // 验证templateId是否存在
        if (application.getTemplateId() == null) {
            throw new RuntimeException("课程模板ID不能为空");
        }
        
        // 根据模板ID获取课程信息
        CourseTemplate template = courseTemplateMapper.selectById(application.getTemplateId());
        if (template == null) {
            throw new RuntimeException("课程模板不存在");
        }
        
        // 获取当前登录用户信息
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("获取用户信息失败，请重新登录");
        }
        
        // 检查教师是否有权限申请该课程模板（学院专业匹配）
        if (!checkTeacherPermission(currentUser.getId(), template)) {
            throw new RuntimeException("您没有权限申请该课程模板，请申请与您所在学院专业对应的课程");
        }
        
        // 检查是否已经申请过该课程模板
        List<CourseApplication> existingApplications = courseApplicationMapper.selectByTeacherIdAndTemplateId(
            currentUser.getId(), application.getTemplateId());
        
        System.out.println("=== 重复申请检查 ===");
        System.out.println("教师ID: " + currentUser.getId());
        System.out.println("模板ID: " + application.getTemplateId());
        System.out.println("已存在的申请数量: " + existingApplications.size());
        
        // 检查是否有待审核或已通过的申请
        boolean hasValidApplication = existingApplications.stream()
                .anyMatch(app -> {
                    System.out.println("申请ID: " + app.getId() + ", 状态: " + app.getStatus() + " (" + 
                        (app.getStatus() == 0 ? "待审核" : app.getStatus() == 1 ? "已通过" : app.getStatus() == 2 ? "已拒绝" : "未知") + ")");
                    return app.getStatus() == 0 || app.getStatus() == 1; // 0-待审核, 1-已通过
                });
        
        System.out.println("是否有有效申请: " + hasValidApplication);
        System.out.println("===================");
        
        if (hasValidApplication) {
            throw new RuntimeException("您已经申请过该课程模板，不能重复申请");
        }
        
        // 从模板获取基础信息，但保留前端传递的可修改字段
        application.setCourseName(template.getTemplateName());
        application.setAcademicYear(template.getAcademicYear());
        application.setSemester(template.getSemester());
        
        // 如果前端没有传递课时和人数，则使用模板的默认值
        if (application.getCourseHours() == null) {
            application.setCourseHours(template.getCourseHours());
        }
        if (application.getMaxStudents() == null) {
            application.setMaxStudents(template.getMaxStudents());
        }
        
        // 设置剩余课时等于总课时
        application.setRemainingHours(application.getCourseHours());
        
        // 设置申请状态和时间
        application.setStatus(0); // 待审核
        application.setApplyTime(LocalDateTime.now());
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        application.setDeleted(0);
        
        // 设置申请教师信息
        application.setTeacherId(currentUser.getId());
        application.setTeacherName(currentUser.getNickname() != null ? currentUser.getNickname() : currentUser.getUsername());
        
        courseApplicationMapper.insert(application);
    }

    /**
     * 检查教师是否有权限申请该课程模板
     * 教师只能申请与自己学院专业对应的课程
     */
    private boolean checkTeacherPermission(Long teacherId, CourseTemplate template) {
        // 如果课程模板没有设置学院专业限制，则允许所有教师申请
        if (template.getCollegeId() == null && template.getMajorId() == null) {
            return true;
        }
        
        // 获取教师的组织信息
        List<UserOrganization> userOrganizations = userOrganizationMapper.selectByUserId(teacherId);
        if (userOrganizations.isEmpty()) {
            return false; // 教师没有分配组织，不能申请任何课程
        }
        
        // 获取教师所属的组织ID列表
        List<Long> teacherOrgIds = userOrganizations.stream()
                .map(UserOrganization::getOrganizationId)
                .collect(Collectors.toList());
        
        // 获取教师的学院和专业信息
        TeacherOrganizationInfo teacherOrgInfo = getTeacherOrganizationInfo(teacherOrgIds);
        
        // 检查学院匹配
        if (template.getCollegeId() != null) {
            if (teacherOrgInfo.getCollegeId() == null || !teacherOrgInfo.getCollegeId().equals(template.getCollegeId())) {
                return false;
            }
        }
        
        // 检查专业匹配
        if (template.getMajorId() != null) {
            if (teacherOrgInfo.getMajorId() == null || !teacherOrgInfo.getMajorId().equals(template.getMajorId())) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 获取教师的学院专业信息
     */
    private TeacherOrganizationInfo getTeacherOrganizationInfo(List<Long> orgIds) {
        TeacherOrganizationInfo info = new TeacherOrganizationInfo();
        
        for (Long orgId : orgIds) {
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

    @Override
    public void updateApplication(Long id, CourseApplicationDTO applicationDTO) {
        CourseApplication existing = courseApplicationMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("申请不存在");
        }
        
        if (existing.getStatus() != 0) {
            throw new RuntimeException("只能修改待审核状态的申请");
        }
        
        // 验证当前用户是否为申请人
        Long currentUserId = userContextUtil.getCurrentUserId();
        if (currentUserId == null || !currentUserId.equals(existing.getTeacherId())) {
            throw new RuntimeException("只能修改自己的申请");
        }
        
        CourseApplication application = new CourseApplication();
        BeanUtils.copyProperties(applicationDTO, application);
        application.setId(id);
        application.setUpdateTime(LocalDateTime.now());
        
        courseApplicationMapper.updateById(application);
    }

    @Override
    public void reviewApplication(Long id, CourseApplicationReviewDTO reviewDTO) {
        CourseApplication application = courseApplicationMapper.selectById(id);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }
        
        application.setStatus(reviewDTO.getStatus());
        application.setReviewComment(reviewDTO.getReviewComment());
        application.setReviewTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        
        // 获取当前审核人信息
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser != null) {
            application.setReviewerId(currentUser.getId());
            application.setReviewerName(currentUser.getNickname() != null ? currentUser.getNickname() : currentUser.getUsername());
        } else {
            application.setReviewerId(1L);
            application.setReviewerName("管理员");
        }
        
        // 如果审核通过，确保剩余课时正确
        if (reviewDTO.getStatus() == 1 && application.getRemainingHours() == null) {
            application.setRemainingHours(application.getCourseHours());
        }
        
        courseApplicationMapper.updateById(application);
    }

    @Override
    public void deleteApplication(Long id) {
        CourseApplication application = courseApplicationMapper.selectById(id);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }
        
        if (application.getStatus() != 0) {
            throw new RuntimeException("只能删除待审核状态的申请");
        }
        
        // 验证当前用户是否为申请人
        Long currentUserId = userContextUtil.getCurrentUserId();
        if (currentUserId == null || !currentUserId.equals(application.getTeacherId())) {
            throw new RuntimeException("只能删除自己的申请");
        }
        
        courseApplicationMapper.deleteById(id);
    }

    @Override
    public CourseApplicationVO getApplicationById(Long id) {
        CourseApplication application = courseApplicationMapper.selectById(id);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }
        
        return convertToVO(application);
    }
    
    @Override
    public List<Long> getAppliedTemplateIds() {
        // 获取当前登录用户信息
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("获取用户信息失败，请重新登录");
        }
        
        // 查询当前教师的所有申请
        List<CourseApplication> applications = courseApplicationMapper.selectByTeacherId(currentUser.getId());
        
        System.out.println("=== 获取已申请模板ID ===");
        System.out.println("教师ID: " + currentUser.getId());
        System.out.println("教师的申请总数: " + applications.size());
        
        // 返回待审核和已通过的申请的模板ID列表
        List<Long> templateIds = applications.stream()
                .filter(app -> {
                    System.out.println("申请ID: " + app.getId() + ", 模板ID: " + app.getTemplateId() + ", 状态: " + app.getStatus() + " (" + 
                        (app.getStatus() == 0 ? "待审核" : app.getStatus() == 1 ? "已通过" : app.getStatus() == 2 ? "已拒绝" : "未知") + ")");
                    return app.getStatus() == 0 || app.getStatus() == 1; // 0-待审核, 1-已通过
                })
                .map(CourseApplication::getTemplateId)
                .distinct()
                .collect(Collectors.toList());
        
        System.out.println("已申请的模板ID列表: " + templateIds);
        System.out.println("=======================");
        return templateIds;
    }
    
    /**
     * 转换为VO对象
     */
    private CourseApplicationVO convertToVO(CourseApplication application) {
        CourseApplicationVO vo = new CourseApplicationVO();
        BeanUtils.copyProperties(application, vo);
        
        // 设置状态名称
        vo.setStatusName(STATUS_MAP.get(application.getStatus()));
        
        return vo;
    }
} 