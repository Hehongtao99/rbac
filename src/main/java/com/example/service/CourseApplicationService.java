package com.example.service;

import com.example.entity.CourseApplication;
import com.example.entity.CourseTemplate;
import com.example.entity.User;
import com.example.mapper.CourseApplicationMapper;
import com.example.mapper.CourseTemplateMapper;
import com.example.common.Result;
import com.example.common.PageResult;
import com.example.util.PageUtil;
import com.example.util.UserContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

public interface CourseApplicationService {
    
    /**
     * 获取申请列表（教师端）
     */
    Result<Object> getApplicationList(Integer page, Integer size, String keyword);
    
    /**
     * 获取申请列表（管理员端）
     */
    Result<Object> getApplicationListForAdmin(Integer page, Integer size, String keyword);
    
    /**
     * 创建申请
     */
    Result<Object> createApplication(CourseApplication application);
    
    /**
     * 更新申请
     */
    Result<Object> updateApplication(CourseApplication application);
    
    /**
     * 审核申请
     */
    Result<Object> reviewApplication(Long id, Integer status, String reviewComment);
    
    /**
     * 删除申请
     */
    Result<Object> deleteApplication(Long id);
    
    /**
     * 根据ID获取申请详情
     */
    Result<Object> getApplicationById(Long id);
} 