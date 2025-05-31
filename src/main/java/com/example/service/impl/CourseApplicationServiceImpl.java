package com.example.service.impl;

import com.example.common.PageResult;
import com.example.dto.CourseApplicationDTO;
import com.example.dto.CourseApplicationQueryDTO;
import com.example.dto.CourseApplicationReviewDTO;
import com.example.entity.CourseApplication;
import com.example.entity.CourseTemplate;
import com.example.entity.User;
import com.example.mapper.CourseApplicationMapper;
import com.example.mapper.CourseTemplateMapper;
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
        List<CourseApplication> applications = courseApplicationMapper.selectPageByTeacherId(
            offset, queryDTO.getPageSize(), queryDTO.getKeyword(), currentUser.getId());
        
        // 统计当前教师的申请总数
        CourseApplication queryApplication = new CourseApplication();
        queryApplication.setTeacherId(currentUser.getId());
        long total = courseApplicationMapper.selectCount(queryApplication);
        
        // 转换为VO
        List<CourseApplicationVO> voList = applications.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageUtil.createPageResult(queryDTO.getPageNum(), queryDTO.getPageSize(), total, voList);
    }

    @Override
    public PageResult<CourseApplicationVO> getApplicationListForAdmin(CourseApplicationQueryDTO queryDTO) {
        int offset = PageUtil.calculateOffset(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        List<CourseApplication> applications = courseApplicationMapper.selectPage(
            offset, queryDTO.getPageSize(), queryDTO.getKeyword());
        
        long total = courseApplicationMapper.selectCount(new CourseApplication());
        
        // 转换为VO
        List<CourseApplicationVO> voList = applications.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageUtil.createPageResult(queryDTO.getPageNum(), queryDTO.getPageSize(), total, voList);
    }

    @Override
    public void createApplication(CourseApplicationDTO applicationDTO) {
        CourseApplication application = new CourseApplication();
        BeanUtils.copyProperties(applicationDTO, application);
        
        // 根据模板ID获取课程信息
        if (application.getTemplateId() != null) {
            CourseTemplate template = courseTemplateMapper.selectById(application.getTemplateId());
            if (template != null) {
                application.setCourseName(template.getTemplateName());
                application.setCourseHours(template.getCourseHours());
                application.setRemainingHours(template.getCourseHours());
                application.setAcademicYear(template.getAcademicYear());
                application.setSemester(template.getSemester());
                application.setMaxStudents(template.getMaxStudents());
            }
        }
        
        // 如果没有从模板获取课时，确保剩余课时不为空
        if (application.getRemainingHours() == null && application.getCourseHours() != null) {
            application.setRemainingHours(application.getCourseHours());
        }
        
        application.setStatus(0); // 待审核
        application.setApplyTime(LocalDateTime.now());
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        application.setDeleted(0);
        
        // 获取当前登录用户信息作为申请教师
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser != null) {
            application.setTeacherId(currentUser.getId());
            application.setTeacherName(currentUser.getNickname() != null ? currentUser.getNickname() : currentUser.getUsername());
        } else {
            throw new RuntimeException("获取用户信息失败，请重新登录");
        }
        
        courseApplicationMapper.insert(application);
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