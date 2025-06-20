package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserRole {
    private Long id;
    
    private Long userId;
    
    private Long roleId;
    
    private LocalDateTime createTime;
    
    private Integer deleted;
} 