package com.example.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String oldPassword; // 原密码
    private String newPassword; // 新密码
    private String confirmPassword; // 确认密码
} 