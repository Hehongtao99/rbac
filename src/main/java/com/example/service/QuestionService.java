package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.QuestionDTO;
import com.example.vo.QuestionVO;

public interface QuestionService {
    
    /**
     * 分页查询题目列表
     */
    Page<QuestionVO> getQuestionList(Integer current, Integer size, Long bankId, Integer type, String title);
    
    /**
     * 根据ID获取题目详情
     */
    QuestionVO getQuestionById(Long id);
    
    /**
     * 创建题目
     */
    void createQuestion(QuestionDTO questionDTO, Long userId);
    
    /**
     * 更新题目
     */
    void updateQuestion(QuestionDTO questionDTO);
    
    /**
     * 删除题目
     */
    void deleteQuestion(Long id);
    
    /**
     * 批量删除题目
     */
    void batchDeleteQuestion(Long[] ids);
} 