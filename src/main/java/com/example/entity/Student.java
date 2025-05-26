package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_student")
public class Student {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String studentNo;
    
    private String name;
    
    private String gender;
    
    private String phone;
    
    private String email;
    
    private String major;
    
    private String className;
    
    private String grade;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 