package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_menu")
public class Menu {
    @TableId(type = IdType.AUTO)
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
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 