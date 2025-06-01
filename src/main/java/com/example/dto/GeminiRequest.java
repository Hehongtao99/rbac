package com.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class GeminiRequest {
    private List<Message> messages;
    private String model;
    private Double temperature;
    private Integer max_tokens;
    
    @Data
    public static class Message {
        private String role;
        private String content;
        
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
} 