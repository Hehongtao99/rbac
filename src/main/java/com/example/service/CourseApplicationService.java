package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.CourseApplication;
import com.example.entity.CourseTemplate;
import com.example.mapper.CourseApplicationMapper;
import com.example.mapper.CourseTemplateMapper;
import com.example.common.Result;
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

    public Result<Object> getApplicationList(Integer page, Integer size, String keyword) {
        Page<CourseApplication> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<CourseApplication> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(CourseApplication::getCourseName, keyword)
                   .or()
                   .like(CourseApplication::getTeacherName, keyword);
        }
        
        // 只查询当前教师的申请（这里简化处理，实际应该从登录信息获取）
        // wrapper.eq(CourseApplication::getTeacherId, getCurrentUserId());
        
        wrapper.orderByDesc(CourseApplication::getCreateTime);
        
        Page<CourseApplication> result = courseApplicationMapper.selectPage(pageObj, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());
        
        return Result.success(data);
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
                    application.setAcademicYear(template.getAcademicYear());
                    application.setSemester(template.getSemester());
                    application.setMaxStudents(template.getMaxStudents());
                }
            }
            
            application.setStatus(0); // 待审核
            application.setApplyTime(LocalDateTime.now());
            // 设置申请教师信息（这里简化处理，实际应该从登录信息获取）
            if (application.getTeacherId() == null) {
                application.setTeacherId(1L);
                application.setTeacherName("当前教师");
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
            // 设置审核人信息（这里简化处理，实际应该从登录信息获取）
            application.setReviewerId(1L);
            application.setReviewerName("管理员");
            
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