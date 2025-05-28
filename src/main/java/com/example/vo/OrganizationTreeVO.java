package com.example.vo;

import lombok.Data;

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
    
    private Double longitude;
    
    private Double latitude;
    
    private String imageUrls;
    
    private String description;
    
    private Integer sort;
    
    private Boolean isEnabled;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private List<OrganizationTreeVO> children;
} 