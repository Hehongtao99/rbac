package com.example.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdvertisementApplicationDTO {
    private Long id;
    
    private Long positionId; // 广告位ID
    
    private String adSettingType; // 户外广告设置类型
    
    private String adNature; // 广告性质
    
    private BigDecimal totalArea; // 总面积（平方米）
    
    private String specification; // 规格
    
    private Integer quantity; // 数量（块）
    
    // 文件上传字段
    private String applicationFormUrl; // 重庆市大型户外广告设置申请表
    
    private String safetyCommitmentUrl; // 户外广告设施施工、运行安全承诺书
    
    private String siteAgreementUrl; // 大型户外广告场地协议
    
    private String effectDrawingUrl; // 大型户外广告设置实景效果图
    
    private String structureDesignUrl; // 广告设施结构设计图
    
    private String originalDrawingUrl; // 户外广告设置依托的建筑物原貌图
    
    private String propertyCertificateUrl; // 设置场地建筑物设施等的产权证书
} 