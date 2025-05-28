package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("organization")
public class Organization {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private Long parentId;
    
    private Integer level; // 1:省 2:市 3:街道
    
    private String code;
    
    private String fullName;
    
    private String description; // 描述信息
    
    private BigDecimal longitude; // 经度
    
    private BigDecimal latitude; // 纬度
    
    @TableField("image_urls")
    private String imageUrls; // 图片URLs，JSON格式存储多张图片（映射到数据库的image_urls字段）
    
    // 为了向后兼容，添加regionImage的getter和setter
    public String getRegionImage() {
        return this.imageUrls;
    }
    
    public void setRegionImage(String regionImage) {
        this.imageUrls = regionImage;
    }
    
    private String regionType; // 区域类型：省、市、街道
    
    private Integer sort;
    
    private Boolean isEnabled;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private Integer status; // 状态：1启用 0禁用
} 