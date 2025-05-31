package com.example.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 教师视图对象
 */
@Data
public class TeacherVO {
    
    private Long id;
    
    private String teacherName;
    
    private String teacherCode;
    
    private String email;
    
    private String phone;
    
    private String department;
    
    private String title; // 职称
    
    private Integer status;
    
    private String statusName;
    
    private List<String> organizationNames; // 所属组织名称列表
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 