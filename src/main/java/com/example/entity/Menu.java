package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Menu {
    private Long id;
    
    private String menuName;
    
    private String menuCode;
    
    private Long parentId; // 父菜单ID，0表示顶级菜单
    
    private String menuType; // 菜单类型：MENU-菜单，BUTTON-按钮
    
    private String path; // 路由路径
    
    private String component; // 组件路径
    
    private String icon; // 图标
    
    private Integer sort; // 排序
    
    private Integer status; // 状态：0-禁用，1-启用
    
    private String description; // 描述
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private Integer deleted;
} 