package com.example.dto;

import lombok.Data;

@Data
public class OrganizationDTO {
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
} 