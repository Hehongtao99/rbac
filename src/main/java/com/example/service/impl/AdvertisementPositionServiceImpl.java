package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.AdvertisementPositionDTO;
import com.example.entity.AdvertisementPosition;
import com.example.entity.Organization;
import com.example.mapper.AdvertisementPositionMapper;
import com.example.mapper.OrganizationMapper;
import com.example.service.AdvertisementPositionService;
import com.example.vo.AdvertisementPositionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdvertisementPositionServiceImpl implements AdvertisementPositionService {
    
    @Autowired
    private AdvertisementPositionMapper advertisementPositionMapper;
    
    @Autowired
    private OrganizationMapper organizationMapper;
    
    @Override
    public List<AdvertisementPositionVO> getPositionsByRegionId(Long regionId) {
        LambdaQueryWrapper<AdvertisementPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdvertisementPosition::getRegionId, regionId)
               .eq(AdvertisementPosition::getStatus, true);
        
        List<AdvertisementPosition> positions = advertisementPositionMapper.selectList(wrapper);
        
        return positions.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    @Override
    public Page<AdvertisementPositionVO> getPositionsPage(
            int page, int size, Long regionId, 
            String code, String positionName) {
        
        Page<AdvertisementPosition> pageRequest = new Page<>(page, size);
        
        LambdaQueryWrapper<AdvertisementPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdvertisementPosition::getRegionId, regionId);
        
        if (StringUtils.hasText(code)) {
            wrapper.like(AdvertisementPosition::getCode, code);
        }
        if (StringUtils.hasText(positionName)) {
            wrapper.like(AdvertisementPosition::getPositionName, positionName);
        }
        
        wrapper.orderByDesc(AdvertisementPosition::getCreateTime);
        
        Page<AdvertisementPosition> pageResult = advertisementPositionMapper.selectPage(pageRequest, wrapper);
        
        // 转换为VO
        Page<AdvertisementPositionVO> voPageResult = new Page<>();
        BeanUtils.copyProperties(pageResult, voPageResult);
        
        List<AdvertisementPositionVO> voList = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPageResult.setRecords(voList);
        
        return voPageResult;
    }
    
    @Override
    public Boolean savePosition(AdvertisementPositionDTO dto) {
        AdvertisementPosition position = new AdvertisementPosition();
        BeanUtils.copyProperties(dto, position);
        
        // 设置默认值
        if (position.getStatus() == null) {
            position.setStatus(true);
        }
        position.setCreateTime(LocalDateTime.now());
        position.setUpdateTime(LocalDateTime.now());
        
        int result = advertisementPositionMapper.insert(position);
        return result > 0;
    }
    
    @Override
    public Boolean updatePosition(AdvertisementPositionDTO dto) {
        AdvertisementPosition position = new AdvertisementPosition();
        BeanUtils.copyProperties(dto, position);
        position.setUpdateTime(LocalDateTime.now());
        
        int result = advertisementPositionMapper.updateById(position);
        return result > 0;
    }
    
    @Override
    public Boolean deletePosition(Long id) {
        int result = advertisementPositionMapper.deleteById(id);
        return result > 0;
    }
    
    @Override
    public AdvertisementPositionVO getPositionById(Long id) {
        AdvertisementPosition position = advertisementPositionMapper.selectById(id);
        if (position == null) {
            return null;
        }
        return convertToVO(position);
    }
    
    private AdvertisementPositionVO convertToVO(AdvertisementPosition position) {
        AdvertisementPositionVO vo = new AdvertisementPositionVO();
        BeanUtils.copyProperties(position, vo);
        
        // 获取区域名称
        if (position.getRegionId() != null) {
            Organization organization = organizationMapper.selectById(position.getRegionId());
            if (organization != null) {
                vo.setRegionName(organization.getName());
            }
        }
        
        // 设置申请状态文本
        vo.setApplicationStatusText(getApplicationStatusText(position.getApplicationStatus()));
        
        return vo;
    }
    
    private String getApplicationStatusText(String applicationStatus) {
        if (applicationStatus == null) {
            return "可申请";
        }
        switch (applicationStatus) {
            case "AVAILABLE":
                return "可申请";
            case "APPLIED":
                return "已申请";
            case "APPROVED":
                return "已通过";
            case "REJECTED":
                return "已拒绝";
            default:
                return "可申请";
        }
    }
} 