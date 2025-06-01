package com.example.dto;

import lombok.Data;

@Data
public class AiStreamEvent {
    private String type; // 事件类型：question, progress, complete, error
    private AiExtractedQuestion question; // 提取到的题目
    private Integer currentCount; // 当前已提取题目数量
    private Integer totalProcessed; // 已处理字符数
    private Integer totalLength; // 总字符数
    private String message; // 消息内容
    private Boolean hasMore; // 是否还有更多内容
    private String error; // 错误信息
    
    // 静态工厂方法
    public static AiStreamEvent question(AiExtractedQuestion question, int currentCount, int totalProcessed, int totalLength) {
        AiStreamEvent event = new AiStreamEvent();
        event.setType("question");
        event.setQuestion(question);
        event.setCurrentCount(currentCount);
        event.setTotalProcessed(totalProcessed);
        event.setTotalLength(totalLength);
        return event;
    }
    
    public static AiStreamEvent progress(String message, int totalProcessed, int totalLength) {
        AiStreamEvent event = new AiStreamEvent();
        event.setType("progress");
        event.setMessage(message);
        event.setTotalProcessed(totalProcessed);
        event.setTotalLength(totalLength);
        return event;
    }
    
    public static AiStreamEvent complete(int totalCount, String message) {
        AiStreamEvent event = new AiStreamEvent();
        event.setType("complete");
        event.setCurrentCount(totalCount);
        event.setMessage(message);
        event.setHasMore(false);
        return event;
    }
    
    public static AiStreamEvent error(String error) {
        AiStreamEvent event = new AiStreamEvent();
        event.setType("error");
        event.setError(error);
        return event;
    }
} 