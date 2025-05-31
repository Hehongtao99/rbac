package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Teacher {
    private Long id;
    
    private String teacherNo;
    
    private String name;
    
    private String gender;
    
    private String phone;
    
    private String email;
    
    private String department;
    
    private String title;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private Integer deleted;
} 