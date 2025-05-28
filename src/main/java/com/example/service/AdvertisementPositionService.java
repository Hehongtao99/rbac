package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.AdvertisementPositionDTO;
import com.example.vo.AdvertisementPositionVO;

import java.util.List;

public interface AdvertisementPositionService {
    
    /**
     * 根据区域ID获取广告位列表
     */
    List<AdvertisementPositionVO> getPositionsByRegionId(Long regionId);
    
    /**
     * 分页查询广告位列表
     */
    Page<AdvertisementPositionVO> getPositionsPage(
            int page, int size, Long regionId, 
            String code, String positionName);
    
    /**
     * 新增广告位
     */
    Boolean savePosition(AdvertisementPositionDTO dto);
    
    /**
     * 更新广告位
     */
    Boolean updatePosition(AdvertisementPositionDTO dto);
    
    /**
     * 删除广告位
     */
    Boolean deletePosition(Long id);
    
    /**
     * 根据ID获取广告位详情
     */
    AdvertisementPositionVO getPositionById(Long id);
} 