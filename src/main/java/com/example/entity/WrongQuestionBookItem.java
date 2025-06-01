package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("wrong_question_book_item")
public class WrongQuestionBookItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long bookId; // 错题本ID
    
    private Long questionId; // 题目ID
    
    private Long userId; // 用户ID
    
    private String wrongAnswer; // 错误答案
    
    private String correctAnswer; // 正确答案
    
    private String addReason; // 加入原因
    
    private Integer masteryLevel; // 掌握程度：0-未掌握 1-部分掌握 2-已掌握
    
    private Integer practiceCount; // 练习次数
    
    private LocalDateTime lastPracticeTime; // 最后练习时间
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 