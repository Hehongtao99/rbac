package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("wrong_question_book")
public class WrongQuestionBook {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name; // 错题本名称
    
    private String description; // 错题本描述
    
    private Long userId; // 用户ID
    
    private Integer questionCount; // 题目数量
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 