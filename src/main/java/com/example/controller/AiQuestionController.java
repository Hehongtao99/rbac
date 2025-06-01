package com.example.controller;

import com.example.common.Result;
import com.example.dto.AiExtractedQuestion;
import com.example.dto.AiQuestionRequest;
import com.example.dto.AiStreamResponse;
import com.example.service.AiQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/ai-question")
@CrossOrigin
public class AiQuestionController {
    
    @Autowired
    private AiQuestionService aiQuestionService;
    
    /**
     * AI提取题目并自动添加到题库
     */
    @PostMapping("/extract-and-add")
    public Result<List<Long>> extractAndAddQuestions(@RequestBody AiQuestionRequest request) {
        // 这里应该从登录用户信息中获取用户ID，暂时使用固定值
        Long userId = 1L;
        List<Long> questionIds = aiQuestionService.extractAndAddQuestions(request, userId);
        return Result.success(questionIds);
    }
    
    /**
     * AI提取题目预览（不添加到题库）
     */
    @PostMapping("/extract-preview")
    public Result<List<AiExtractedQuestion>> extractQuestionsPreview(@RequestBody AiQuestionRequest request) {
        List<AiExtractedQuestion> questions = aiQuestionService.extractQuestions(request.getContent());
        return Result.success(questions);
    }
    
    /**
     * AI流式提取题目（支持分段解析）
     */
    @PostMapping("/extract-stream")
    public Result<AiStreamResponse> extractQuestionsStream(@RequestBody AiQuestionRequest request) {
        AiStreamResponse response = aiQuestionService.extractQuestionsStream(request);
        return Result.success(response);
    }
    
    /**
     * AI实时流式提取题目（SSE方式，每提取到一个题目就推送）
     */
    @GetMapping(value = "/extract-realtime-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public SseEmitter extractQuestionsRealtimeStream(
            @RequestParam Long bankId,
            @RequestParam String content) {
        
        System.out.println("收到实时流式提取请求:");
        System.out.println("bankId: " + bankId);
        System.out.println("content length: " + (content != null ? content.length() : 0));
        
        // 这里应该从登录用户信息中获取用户ID，暂时使用固定值
        Long userId = 1L;
        
        AiQuestionRequest request = new AiQuestionRequest();
        request.setBankId(bankId);
        request.setContent(content);
        
        SseEmitter emitter = aiQuestionService.extractQuestionsRealtimeStream(request, userId);
        System.out.println("SSE Emitter 已创建并返回");
        
        return emitter;
    }
    
    /**
     * 添加流式解析的题目到题库
     */
    @PostMapping("/add-stream-questions")
    public Result<List<Long>> addStreamQuestions(@RequestBody AiQuestionRequest request) {
        // 这里应该从登录用户信息中获取用户ID，暂时使用固定值
        Long userId = 1L;
        
        if (request.getProcessedQuestions() == null || request.getProcessedQuestions().isEmpty()) {
            throw new RuntimeException("没有可添加的题目");
        }
        
        List<Long> questionIds = aiQuestionService.addStreamQuestions(
            request.getProcessedQuestions(), request.getBankId(), userId);
        return Result.success(questionIds);
    }
} 