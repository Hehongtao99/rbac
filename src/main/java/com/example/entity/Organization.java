package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Organization {
    
    private Long id;
    
    /**
     * 组织名称
     */
    private String orgName;
    
    /**
     * 组织编码
     */
    private String orgCode;
    
    /**
     * 父级组织ID
     */
    private Long parentId;
    
    /**
     * 组织类型：COLLEGE-学院，MAJOR-专业，CLASS-班级
     */
    private String orgType;
    
    /**
     * 组织级别：1-学院，2-专业，3-班级
     */
    private Integer orgLevel;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 