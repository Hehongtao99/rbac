package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.AdvertisementApplicationDTO;
import com.example.entity.AdvertisementApplication;
import com.example.entity.AdvertisementPosition;
import com.example.entity.Organization;
import com.example.entity.User;
import com.example.mapper.AdvertisementApplicationMapper;
import com.example.mapper.AdvertisementPositionMapper;
import com.example.mapper.OrganizationMapper;
import com.example.mapper.UserMapper;
import com.example.service.AdvertisementApplicationService;
import com.example.vo.AdvertisementApplicationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AdvertisementApplicationServiceImpl implements AdvertisementApplicationService {
    
    @Autowired
    private AdvertisementApplicationMapper advertisementApplicationMapper;
    
    @Autowired
    private AdvertisementPositionMapper advertisementPositionMapper;
    
    @Autowired
    private OrganizationMapper organizationMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public Page<AdvertisementApplicationVO> getApplicationPage(
            int page, int size, 
            String applicationCode, String positionName, BigDecimal area,
            String adSettingType, String adNature, String auditStatus, Long regionId) {
        
        Page<AdvertisementApplication> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AdvertisementApplication> wrapper = new LambdaQueryWrapper<>();
        
        // 搜索条件
        if (StringUtils.hasText(applicationCode)) {
            wrapper.like(AdvertisementApplication::getApplicationCode, applicationCode);
        }
        if (StringUtils.hasText(positionName)) {
            wrapper.like(AdvertisementApplication::getPositionName, positionName);
        }
        if (area != null) {
            wrapper.eq(AdvertisementApplication::getArea, area);
        }
        if (StringUtils.hasText(adSettingType)) {
            wrapper.like(AdvertisementApplication::getAdSettingType, adSettingType);
        }
        if (StringUtils.hasText(adNature)) {
            wrapper.like(AdvertisementApplication::getAdNature, adNature);
        }
        if (StringUtils.hasText(auditStatus)) {
            wrapper.eq(AdvertisementApplication::getAuditStatus, auditStatus);
        }
        if (regionId != null) {
            wrapper.eq(AdvertisementApplication::getRegionId, regionId);
        }
        
        wrapper.orderByDesc(AdvertisementApplication::getCreateTime);
        
        Page<AdvertisementApplication> result = advertisementApplicationMapper.selectPage(pageParam, wrapper);
        
        // 转换为VO
        Page<AdvertisementApplicationVO> voPage = new Page<>();
        BeanUtils.copyProperties(result, voPage, "records");
        
        voPage.setRecords(result.getRecords().stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList()));
        
        return voPage;
    }
    
    @Override
    public Boolean saveApplication(AdvertisementApplicationDTO dto) {
        // 获取广告位信息
        AdvertisementPosition position = advertisementPositionMapper.selectById(dto.getPositionId());
        if (position == null) {
            throw new RuntimeException("广告位不存在");
        }
        
        // 检查广告位是否可申请
        if (!"AVAILABLE".equals(position.getApplicationStatus()) && position.getApplicationStatus() != null) {
            throw new RuntimeException("该广告位已被申请，无法重复申请");
        }
        
        AdvertisementApplication application = new AdvertisementApplication();
        BeanUtils.copyProperties(dto, application);
        
        // 生成申请编号
        String applicationCode = generateApplicationCode();
        application.setApplicationCode(applicationCode);
        
        // 从广告位获取基础信息
        application.setPositionName(position.getPositionName());
        application.setRegionId(position.getRegionId());
        application.setArea(position.getArea());
        
        // 设置申请状态
        application.setAuditStatus("PENDING");
        application.setApplyTime(LocalDateTime.now());
        // TODO: 从当前登录用户获取
        application.setApplyUserId(1L);
        
        // 保存申请
        boolean saveResult = advertisementApplicationMapper.insert(application) > 0;
        
        if (saveResult) {
            // 更新广告位状态为已申请
            position.setApplicationStatus("APPLIED");
            advertisementPositionMapper.updateById(position);
        }
        
        return saveResult;
    }
    
    @Override
    public AdvertisementApplicationVO getApplicationById(Long id) {
        AdvertisementApplication application = advertisementApplicationMapper.selectById(id);
        if (application == null) {
            return null;
        }
        return convertToVO(application);
    }
    
    @Override
    public Boolean auditApplication(Long id, String auditStatus, String auditRemark) {
        // 获取申请信息
        AdvertisementApplication existingApplication = advertisementApplicationMapper.selectById(id);
        if (existingApplication == null) {
            throw new RuntimeException("申请记录不存在");
        }
        
        AdvertisementApplication application = new AdvertisementApplication();
        application.setId(id);
        application.setAuditStatus(auditStatus);
        application.setAuditRemark(auditRemark);
        application.setAuditTime(LocalDateTime.now());
        // TODO: 从当前登录用户获取
        application.setAuditUserId(1L);
        
        boolean updateResult = advertisementApplicationMapper.updateById(application) > 0;
        
        if (updateResult) {
            // 根据审核结果更新广告位状态
            AdvertisementPosition position = advertisementPositionMapper.selectById(existingApplication.getPositionId());
            if (position != null) {
                if ("APPROVED".equals(auditStatus)) {
                    // 审核通过，广告位状态变为已通过
                    position.setApplicationStatus("APPROVED");
                } else if ("REJECTED".equals(auditStatus)) {
                    // 审核拒绝，广告位状态变为可申请（释放）
                    position.setApplicationStatus("AVAILABLE");
                }
                advertisementPositionMapper.updateById(position);
            }
        }
        
        return updateResult;
    }
    
    private AdvertisementApplicationVO convertToVO(AdvertisementApplication application) {
        AdvertisementApplicationVO vo = new AdvertisementApplicationVO();
        BeanUtils.copyProperties(application, vo);
        
        // 设置审核状态文本
        vo.setAuditStatusText(getAuditStatusText(application.getAuditStatus()));
        
        // 获取区域名称
        if (application.getRegionId() != null) {
            Organization organization = organizationMapper.selectById(application.getRegionId());
            if (organization != null) {
                vo.setRegionName(organization.getName());
            }
        }
        
        // 获取申请人姓名
        if (application.getApplyUserId() != null) {
            User user = userMapper.selectById(application.getApplyUserId());
            if (user != null) {
                vo.setApplyUserName(user.getNickname());
            }
        }
        
        // 获取审核人姓名
        if (application.getAuditUserId() != null) {
            User user = userMapper.selectById(application.getAuditUserId());
            if (user != null) {
                vo.setAuditUserName(user.getNickname());
            }
        }
        
        return vo;
    }
    
    private String getAuditStatusText(String auditStatus) {
        switch (auditStatus) {
            case "PENDING":
                return "待审核";
            case "APPROVED":
                return "已通过";
            case "REJECTED":
                return "已拒绝";
            default:
                return "未知";
        }
    }
    
    private String generateApplicationCode() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 查询当天最后一个申请编号
        LambdaQueryWrapper<AdvertisementApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(AdvertisementApplication::getApplicationCode, "APP" + dateStr)
               .orderByDesc(AdvertisementApplication::getApplicationCode)
               .last("limit 1");
        
        AdvertisementApplication lastApplication = advertisementApplicationMapper.selectOne(wrapper);
        
        int sequence = 1;
        if (lastApplication != null) {
            String lastCode = lastApplication.getApplicationCode();
            String lastSequence = lastCode.substring(lastCode.length() - 3);
            sequence = Integer.parseInt(lastSequence) + 1;
        }
        
        return String.format("APP%s%03d", dateStr, sequence);
    }
} 