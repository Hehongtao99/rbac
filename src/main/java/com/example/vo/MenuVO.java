package com.example.vo;

import lombok.Data;
import java.util.List;

@Data
public class MenuVO {
    private Long id;
    private String menuName;
    private String menuCode;
    private Long parentId;
    private String menuType;
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    private List<MenuVO> children; // 子菜单
} 