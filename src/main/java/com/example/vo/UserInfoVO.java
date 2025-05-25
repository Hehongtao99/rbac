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
    private List<OrganizationInfoVO> organizations; // 用户关联的组织信息
    
    @Data
    public static class OrganizationInfoVO {
        private Long id;
        private String orgName;
        private String orgCode;
        private String orgType;
        private Integer orgLevel;
        private String collegeName;  // 学院名称
        private String majorName;    // 专业名称
        private String className;    // 班级名称
    }
} 