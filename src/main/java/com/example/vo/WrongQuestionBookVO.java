package com.example.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WrongQuestionBookVO {
    private Long id;
    private String name; // 错题本名称
    private String description; // 错题本描述
    private Long userId; // 用户ID
    private String userName; // 用户名称
    private Integer questionCount; // 题目数量
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 