package com.example.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QuestionBankVO {
    private Long id;
    private String name;
    private String description;
    private Integer status;
    private String createUserName;
    private LocalDateTime createTime;
    private Integer questionCount; // 题目数量
} 