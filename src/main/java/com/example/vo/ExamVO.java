package com.example.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamVO {
    private Long id;
    private Long bankId;
    private String bankName; // 题库名称
    private Long userId;
    private String userName; // 用户名称
    private String title;
    private Integer totalScore;
    private Integer userScore;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private Integer status; // 状态：0-进行中 1-已完成 2-已超时
    private String statusName; // 状态名称
    private LocalDateTime createTime;
    private List<ExamQuestionVO> questions; // 考试题目列表
} 