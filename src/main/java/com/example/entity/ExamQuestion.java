package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("exam_question")
public class ExamQuestion {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long examId; // 考试ID
    
    private Long questionId; // 题目ID
    
    private Integer questionOrder; // 题目顺序
    
    private String userAnswer; // 用户答案
    
    private Integer isCorrect; // 是否正确：0-错误 1-正确
    
    private Integer score; // 题目分值
    
    private Integer userScore; // 用户得分
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 