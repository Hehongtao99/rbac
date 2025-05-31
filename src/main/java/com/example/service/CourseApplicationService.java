package com.example.service;

import com.example.common.PageResult;
import com.example.dto.CourseApplicationDTO;
import com.example.dto.CourseApplicationQueryDTO;
import com.example.dto.CourseApplicationReviewDTO;
import com.example.vo.CourseApplicationVO;

public interface CourseApplicationService {
    
    /**
     * 分页获取申请列表
     */
    PageResult<CourseApplicationVO> getApplicationList(CourseApplicationQueryDTO queryDTO);
    
    /**
     * 管理员分页获取申请列表
     */
    PageResult<CourseApplicationVO> getApplicationListForAdmin(CourseApplicationQueryDTO queryDTO);
    
    /**
     * 创建申请
     */
    void createApplication(CourseApplicationDTO applicationDTO);
    
    /**
     * 更新申请
     */
    void updateApplication(Long id, CourseApplicationDTO applicationDTO);
    
    /**
     * 审核申请
     */
    void reviewApplication(Long id, CourseApplicationReviewDTO reviewDTO);
    
    /**
     * 删除申请
     */
    void deleteApplication(Long id);
    
    /**
     * 根据ID获取申请详情
     */
    CourseApplicationVO getApplicationById(Long id);
} 