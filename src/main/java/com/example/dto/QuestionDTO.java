package com.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    private Long id;
    private Long bankId;
    private String title;
    private String content;
    private Integer type;
    private List<String> options; // 选项列表
    private String answer;
    private String analysis;
    private Integer difficulty;
    private Integer score;
    private Integer status;
} 