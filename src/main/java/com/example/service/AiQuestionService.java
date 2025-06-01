package com.example.service;

import com.example.dto.AiExtractedQuestion;
import com.example.dto.AiQuestionRequest;
import com.example.dto.AiStreamResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;

public interface AiQuestionService {
    
    /**
     * 使用AI提取题目并自动添加到题库
     */
    List<Long> extractAndAddQuestions(AiQuestionRequest request, Long userId);
    
    /**
     * 仅提取题目（不添加到题库）
     */
    List<AiExtractedQuestion> extractQuestions(String content);
    
    /**
     * 流式提取题目（支持分段解析）
     */
    AiStreamResponse extractQuestionsStream(AiQuestionRequest request);
    
    /**
     * 实时流式提取题目（SSE方式，每提取到一个题目就推送）
     */
    SseEmitter extractQuestionsRealtimeStream(AiQuestionRequest request, Long userId);
    
    /**
     * 添加流式解析的题目到题库
     */
    List<Long> addStreamQuestions(List<AiExtractedQuestion> questions, Long bankId, Long userId);
} 