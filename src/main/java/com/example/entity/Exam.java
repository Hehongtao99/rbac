package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("exam")
public class Exam {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long bankId; // 题库ID
    
    private Long userId; // 考试用户ID
    
    private String title; // 考试标题
    
    private Integer totalScore; // 总分
    
    private Integer userScore; // 用户得分
    
    private LocalDateTime startTime; // 开始时间
    
    private LocalDateTime endTime; // 结束时间
    
    private Integer duration; // 考试时长（分钟）
    
    private Integer status; // 状态：0-进行中 1-已完成 2-已超时
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 