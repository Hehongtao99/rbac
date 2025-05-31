package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RoleMenu {
    private Long id;
    
    private Long roleId;
    
    private Long menuId;
    
    private LocalDateTime createTime;
    
    private Integer deleted;
} 