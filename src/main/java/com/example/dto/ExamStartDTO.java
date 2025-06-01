package com.example.dto;

import lombok.Data;

@Data
public class ExamStartDTO {
    private Long bankId; // 题库ID
    private String title; // 考试标题
    private Integer duration; // 考试时长（分钟）
} 