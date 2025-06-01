package com.example.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class WrongQuestionItemVO {
    private Long id;
    private Long bookId; // 错题本ID
    private Long questionId; // 题目ID
    private String wrongAnswer; // 错误答案
    private String correctAnswer; // 正确答案
    private String addReason; // 加入原因
    private Integer masteryLevel; // 掌握程度
    private String masteryLevelName; // 掌握程度名称
    private Integer practiceCount; // 练习次数
    private LocalDateTime lastPracticeTime; // 最后练习时间
    private LocalDateTime createTime;
    
    // 题目信息
    private String title; // 题目标题
    private String content; // 题目内容
    private Integer type; // 题目类型
    private String typeName; // 题目类型名称
    private List<String> options; // 选项
    private Integer difficulty; // 难度
    private String difficultyName; // 难度名称
    private Integer score; // 分值
    private String analysis; // 解析
} 