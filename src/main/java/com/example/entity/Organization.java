package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("organization")
public class Organization {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private Long parentId;
    
    private Integer level; // 1:省 2:市 3:区 4:街道
    
    private String code;
    
    private String fullName;
    
    private Double longitude; // 经度，街道级别可用
    
    private Double latitude; // 纬度，街道级别可用
    
    private String imageUrls; // 图片URLs，JSON格式存储多张图片，街道级别可用
    
    private String description; // 描述信息
    
    private Integer sort;
    
    private Boolean isEnabled;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 