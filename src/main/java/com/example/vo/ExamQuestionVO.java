package com.example.vo;

import lombok.Data;
import java.util.List;

@Data
public class ExamQuestionVO {
    private Long id;
    private Long examId;
    private Long questionId;
    private Integer questionOrder;
    private String userAnswer;
    private Integer isCorrect;
    private Integer score;
    private Integer userScore;
    
    // 题目信息
    private String title;
    private String content;
    private Integer type; // 题目类型：1-单选 2-多选 3-判断 4-问答 5-填空
    private String typeName;
    private List<String> options;
    private String answer; // 正确答案（考试结束后显示）
    private String analysis; // 题目解析（考试结束后显示）
    private Integer difficulty;
    private String difficultyName;
} 