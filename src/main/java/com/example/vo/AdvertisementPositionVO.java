package com.example.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdvertisementPositionVO {
    private Long id;
    
    private String code; // 广告位编号
    
    private String positionName; // 广告位置
    
    private BigDecimal area; // 面积（平方米）
    
    private Long regionId; // 区域ID
    
    private String regionName; // 区域名称
    
    private BigDecimal longitude; // 地图点位经度
    
    private BigDecimal latitude; // 地图点位纬度
    
    private String positionImage; // 广告位实图URLs，JSON格式存储多张图片
    
    private Boolean status; // 状态
    
    private String applicationStatus; // 申请状态：AVAILABLE-可申请,APPLIED-已申请,APPROVED-已通过,REJECTED-已拒绝
    
    private String applicationStatusText; // 申请状态文本
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 