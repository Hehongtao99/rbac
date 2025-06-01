package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.ExamAnswerDTO;
import com.example.dto.ExamStartDTO;
import com.example.vo.ExamVO;

public interface ExamService {
    
    /**
     * 开始考试
     */
    ExamVO startExam(ExamStartDTO examStartDTO, Long userId);
    
    /**
     * 获取考试详情
     */
    ExamVO getExamDetail(Long examId, Long userId);
    
    /**
     * 提交考试答案
     */
    ExamVO submitExam(ExamAnswerDTO examAnswerDTO, Long userId);
    
    /**
     * 获取用户考试列表
     */
    Page<ExamVO> getUserExamList(Long userId, Integer current, Integer size);
    
    /**
     * 获取考试结果
     */
    ExamVO getExamResult(Long examId, Long userId);
} 