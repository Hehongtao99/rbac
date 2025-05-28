package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.AdvertisementApplicationDTO;
import com.example.vo.AdvertisementApplicationVO;

import java.math.BigDecimal;

public interface AdvertisementApplicationService {
    
    /**
     * 分页查询广告申请列表
     */
    Page<AdvertisementApplicationVO> getApplicationPage(
            int page, int size, 
            String applicationCode, String positionName, BigDecimal area,
            String adSettingType, String adNature, String auditStatus, Long regionId);
    
    /**
     * 新增广告申请
     */
    Boolean saveApplication(AdvertisementApplicationDTO dto);
    
    /**
     * 根据ID获取广告申请详情
     */
    AdvertisementApplicationVO getApplicationById(Long id);
    
    /**
     * 审核广告申请
     */
    Boolean auditApplication(Long id, String auditStatus, String auditRemark);
} 