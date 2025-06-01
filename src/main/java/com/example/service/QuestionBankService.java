package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.QuestionBankDTO;
import com.example.vo.QuestionBankVO;

public interface QuestionBankService {
    
    /**
     * 分页查询题库列表
     */
    Page<QuestionBankVO> getQuestionBankList(Integer current, Integer size, String name);
    
    /**
     * 根据ID获取题库详情
     */
    QuestionBankVO getQuestionBankById(Long id);
    
    /**
     * 创建题库
     */
    void createQuestionBank(QuestionBankDTO questionBankDTO, Long userId);
    
    /**
     * 更新题库
     */
    void updateQuestionBank(QuestionBankDTO questionBankDTO);
    
    /**
     * 删除题库
     */
    void deleteQuestionBank(Long id);
} 