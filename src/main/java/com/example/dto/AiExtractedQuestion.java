package com.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class AiExtractedQuestion {
    private String title; // 题目标题
    private String content; // 题目内容
    private Integer type; // 题目类型：1-单选 2-多选 3-判断 4-问答 5-填空
    private List<String> options; // 选项（选择题使用）
    private String answer; // 正确答案
    private String analysis; // 题目解析（如果有的话）
    private Integer difficulty; // 难度等级，默认为1（简单）
    private Integer score; // 分值，默认为5分
} 