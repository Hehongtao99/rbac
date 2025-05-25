package com.example.vo;

import lombok.Data;
import java.util.List;

@Data
public class UserInfoVO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private List<String> roles;
    private List<MenuVO> menus; // 用户菜单
    private List<String> buttons; // 用户按钮权限
} 