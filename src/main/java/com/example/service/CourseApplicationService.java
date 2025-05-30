package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.CourseApplication;
import com.example.entity.CourseTemplate;
import com.example.entity.User;
import com.example.mapper.CourseApplicationMapper;
import com.example.mapper.CourseTemplateMapper;
import com.example.common.Result;
import com.example.util.UserContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class CourseApplicationService {

    @Autowired
    private CourseApplicationMapper courseApplicationMapper;
    
    @Autowired
    private CourseTemplateMapper courseTemplateMapper;
    
    @Autowired
    private UserContextUtil userContextUtil;

    public Result<Object> getApplicationList(Integer page, Integer size, String keyword) {
        System.out.println("=== getApplicationList 开始 ===");
        
        try {
            Page<CourseApplication> pageObj = new Page<>(page, size);
            LambdaQueryWrapper<CourseApplication> wrapper = new LambdaQueryWrapper<>();
            
            if (StringUtils.hasText(keyword)) {
                wrapper.like(CourseApplication::getCourseName, keyword)
                       .or()
                       .like(CourseApplication::getTeacherName, keyword);
            }
            
            // 从JWT获取当前登录用户信息
            User currentUser = userContextUtil.getCurrentUser();
            if (currentUser == null) {
                System.err.println("getApplicationList: 无法获取当前用户信息");
                return Result.error("获取用户信息失败，请重新登录");
            }
            
            Long currentUserId = currentUser.getId();
            System.out.println("getApplicationList: 当前用户ID = " + currentUserId);
            System.out.println("getApplicationList: 当前用户名 = " + currentUser.getUsername());
            
            // 只查询当前教师的申请
            wrapper.eq(CourseApplication::getTeacherId, currentUserId);
            wrapper.orderByDesc(CourseApplication::getCreateTime);
            
            System.out.println("getApplicationList: 执行查询...");
            Page<CourseApplication> result = courseApplicationMapper.selectPage(pageObj, wrapper);
            
            System.out.println("getApplicationList: 查询结果总数 = " + result.getTotal());
            System.out.println("getApplicationList: 当前页记录数 = " + result.getRecords().size());
            
            Map<String, Object> data = new HashMap<>();
            data.put("records", result.getRecords());
            data.put("total", result.getTotal());
            data.put("current", result.getCurrent());
            data.put("size", result.getSize());
            
            System.out.println("=== getApplicationList 结束 ===");
            return Result.success(data);
        } catch (Exception e) {
            System.err.println("getApplicationList: 发生异常 - " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取申请列表失败：" + e.getMessage());
        }
    }

    public Result<Object> getApplicationListForAdmin(Integer page, Integer size, String keyword) {
        Page<CourseApplication> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<CourseApplication> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(CourseApplication::getCourseName, keyword)
                   .or()
                   .like(CourseApplication::getTeacherName, keyword);
        }
        
        wrapper.orderByAsc(CourseApplication::getStatus)
               .orderByDesc(CourseApplication::getCreateTime);
        
        Page<CourseApplication> result = courseApplicationMapper.selectPage(pageObj, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());
        
        return Result.success(data);
    }

    public Result<Object> createApplication(CourseApplication application) {
        try {
            // 根据模板ID获取课程信息
            if (application.getTemplateId() != null) {
                CourseTemplate template = courseTemplateMapper.selectById(application.getTemplateId());
                if (template != null) {
                    application.setCourseName(template.getTemplateName());
                    application.setCourseHours(template.getCourseHours());
                    application.setRemainingHours(template.getCourseHours()); // 设置剩余课时为总课时
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
            
            // 获取当前登录用户信息作为申请教师
            User currentUser = userContextUtil.getCurrentUser();
            if (currentUser != null) {
                application.setTeacherId(currentUser.getId());
                application.setTeacherName(currentUser.getNickname() != null ? currentUser.getNickname() : currentUser.getUsername());
            } else {
                return Result.error("获取用户信息失败，请重新登录");
            }
            
            courseApplicationMapper.insert(application);
            return Result.success(application);
        } catch (Exception e) {
            return Result.error("申请提交失败：" + e.getMessage());
        }
    }

    public Result<Object> updateApplication(CourseApplication application) {
        try {
            CourseApplication existing = courseApplicationMapper.selectById(application.getId());
            if (existing == null) {
                return Result.error("申请不存在");
            }
            
            if (existing.getStatus() != 0) {
                return Result.error("只能修改待审核状态的申请");
            }
            
            // 验证当前用户是否为申请人
            Long currentUserId = userContextUtil.getCurrentUserId();
            if (currentUserId == null || !currentUserId.equals(existing.getTeacherId())) {
                return Result.error("只能修改自己的申请");
            }
            
            courseApplicationMapper.updateById(application);
            return Result.success(application);
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    public Result<Object> reviewApplication(Long id, Integer status, String reviewComment) {
        try {
            CourseApplication application = courseApplicationMapper.selectById(id);
            if (application == null) {
                return Result.error("申请不存在");
            }
            
            application.setStatus(status);
            application.setReviewComment(reviewComment);
            application.setReviewTime(LocalDateTime.now());
            
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
            if (status == 1 && application.getRemainingHours() == null) {
                application.setRemainingHours(application.getCourseHours());
            }
            
            courseApplicationMapper.updateById(application);
            
            String statusText = status == 1 ? "通过" : "拒绝";
            return Result.success();
        } catch (Exception e) {
            return Result.error("审核失败：" + e.getMessage());
        }
    }

    public Result<Object> deleteApplication(Long id) {
        try {
            CourseApplication application = courseApplicationMapper.selectById(id);
            if (application == null) {
                return Result.error("申请不存在");
            }
            
            if (application.getStatus() != 0) {
                return Result.error("只能删除待审核状态的申请");
            }
            
            // 验证当前用户是否为申请人
            Long currentUserId = userContextUtil.getCurrentUserId();
            if (currentUserId == null || !currentUserId.equals(application.getTeacherId())) {
                return Result.error("只能删除自己的申请");
            }
            
            courseApplicationMapper.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    public Result<Object> getApplicationById(Long id) {
        try {
            CourseApplication application = courseApplicationMapper.selectById(id);
            if (application != null) {
                return Result.success(application);
            } else {
                return Result.error("申请不存在");
            }
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
} 