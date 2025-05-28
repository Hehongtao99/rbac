package com.example.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrganizationDTO {
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
} 