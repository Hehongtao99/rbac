package com.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class ExamAnswerDTO {
    private Long examId; // 考试ID
    private List<QuestionAnswer> answers; // 答案列表
    
    @Data
    public static class QuestionAnswer {
        private Long questionId; // 题目ID
        private String answer; // 用户答案
    }
} 