package com.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class AiQuestionRequest {
    private Long bankId; // 题库ID
    private String content; // 输入的包含题目和答案的内容
    private Integer startPosition; // 开始解析位置（用于分段解析）
    private String processedContent; // 已处理的内容（用于避免重复解析）
    private List<AiExtractedQuestion> processedQuestions; // 已解析的题目列表（用于批量添加）
} 