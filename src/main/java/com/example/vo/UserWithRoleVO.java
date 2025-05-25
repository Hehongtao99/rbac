package com.example.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserWithRoleVO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<String> roleNames; // 角色名称列表
    private List<Long> roleIds; // 角色ID列表
} 