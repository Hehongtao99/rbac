package com.example.dto;

import lombok.Data;

/**
 * 组织查询DTO
 */
@Data
public class OrganizationQueryDTO {
    
    /**
     * 组织名称
     */
    private String orgName;
    
    /**
     * 组织编码
     */
    private String orgCode;
    
    /**
     * 组织类型
     */
    private String orgType;
} 