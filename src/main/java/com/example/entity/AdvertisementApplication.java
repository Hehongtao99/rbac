package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("advertisement_application")
public class AdvertisementApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String applicationCode; // 申请编号
    
    private Long positionId; // 广告位ID
    
    private Long regionId; // 区域ID
    
    private String positionName; // 广告位置
    
    private String adSettingType; // 户外广告设置类型
    
    private String adNature; // 广告性质
    
    private BigDecimal area; // 面积（平方米）
    
    private BigDecimal totalArea; // 总面积（平方米）
    
    private String specification; // 规格
    
    private Integer quantity; // 数量（块）
    
    private String auditStatus; // 审核状态：PENDING-待审核,APPROVED-已通过,REJECTED-已拒绝
    
    // 文件上传字段
    private String applicationFormUrl; // 重庆市大型户外广告设置申请表
    
    private String safetyCommitmentUrl; // 户外广告设施施工、运行安全承诺书
    
    private String siteAgreementUrl; // 大型户外广告场地协议
    
    private String effectDrawingUrl; // 大型户外广告设置实景效果图
    
    private String structureDesignUrl; // 广告设施结构设计图
    
    private String originalDrawingUrl; // 户外广告设置依托的建筑物原貌图
    
    private String propertyCertificateUrl; // 设置场地建筑物设施等的产权证书
    
    private Long applyUserId; // 申请人ID
    
    private LocalDateTime applyTime; // 申请时间
    
    private Long auditUserId; // 审核人ID
    
    private LocalDateTime auditTime; // 审核时间
    
    private String auditRemark; // 审核备注
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 