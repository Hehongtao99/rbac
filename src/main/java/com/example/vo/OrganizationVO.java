package com.example.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrganizationVO {
    
    private Long id;
    
    private String orgName;
    
    private String orgCode;
    
    private Long parentId;
    
    private String orgType;
    
    private Integer orgLevel;
    
    private Integer sort;
    
    private String phone;
    
    private String email;
    
    private String description;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    /**
     * 子组织列表
     */
    private List<OrganizationVO> children;
} 