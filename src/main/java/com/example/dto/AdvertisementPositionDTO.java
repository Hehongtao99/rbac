package com.example.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdvertisementPositionDTO {
    private Long id;
    
    private String code; // 广告位编号
    
    private String positionName; // 广告位置
    
    private BigDecimal area; // 面积（平方米）
    
    private Long regionId; // 区域ID
    
    private BigDecimal longitude; // 地图点位经度
    
    private BigDecimal latitude; // 地图点位纬度
    
    private String positionImage; // 广告位实图URLs，JSON格式存储多张图片
    
    private Boolean status; // 状态：true启用 false禁用
} 