package com.example.dto;

import lombok.Data;

@Data
public class AddWrongQuestionDTO {
    private Long bookId; // 错题本ID
    private Long questionId; // 题目ID
    private String wrongAnswer; // 错误答案
    private String addReason; // 加入原因
} 