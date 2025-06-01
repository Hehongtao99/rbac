package com.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class AiStreamResponse {
    private List<AiExtractedQuestion> questions; // 本次解析的题目
    private Boolean hasMore; // 是否还有更多内容需要解析
    private Integer nextPosition; // 下次开始解析的位置
    private String remainingContent; // 剩余未解析的内容
    private String processedContent; // 已处理的内容
} 