package com.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class GeminiResponse {
    private List<Choice> choices;
    private String id;
    private String object;
    private Long created;
    private String model;
    private Usage usage;
    
    @Data
    public static class Choice {
        private Integer index;
        private Message message;
        private String finish_reason;
    }
    
    @Data
    public static class Message {
        private String role;
        private String content;
    }
    
    @Data
    public static class Usage {
        private Integer prompt_tokens;
        private Integer completion_tokens;
        private Integer total_tokens;
    }
} 