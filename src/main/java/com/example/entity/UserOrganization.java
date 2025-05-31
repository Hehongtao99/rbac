package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserOrganization {
    
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 组织ID
     */
    private Long organizationId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 