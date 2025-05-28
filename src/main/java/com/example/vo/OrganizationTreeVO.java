package com.example.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrganizationTreeVO {
    private Long id;
    
    private String name;
    
    private Long parentId;
    
    private Integer level;
    
    private String code;
    
    private String fullName;
    
    private String description;
    
    private BigDecimal longitude;
    
    private BigDecimal latitude;
    
    private String regionImage;
    
    private String regionType;
    
    private Integer sort;
    
    private Boolean isEnabled;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private List<OrganizationTreeVO> children;
} 