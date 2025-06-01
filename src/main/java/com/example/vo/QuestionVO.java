package com.example.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionVO {
    private Long id;
    private Long bankId;
    private String bankName;
    private String title;
    private String content;
    private Integer type;
    private String typeName;
    private List<String> options;
    private String answer;
    private String analysis;
    private Integer difficulty;
    private String difficultyName;
    private Integer score;
    private Integer status;
    private String createUserName;
    private LocalDateTime createTime;
} 