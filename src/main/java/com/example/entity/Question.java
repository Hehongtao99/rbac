package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("question")
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long bankId; // 题库ID
    
    private String title; // 题目标题
    
    private String content; // 题目内容
    
    private Integer type; // 题目类型：1-单选 2-多选 3-判断 4-问答 5-填空
    
    private String options; // 选项（JSON格式存储）
    
    private String answer; // 正确答案
    
    private String analysis; // 题目解析
    
    private Integer difficulty; // 难度等级：1-简单 2-中等 3-困难
    
    private Integer score; // 分值
    
    private Integer status; // 0-禁用 1-启用
    
    private Long createUserId; // 创建用户ID
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 