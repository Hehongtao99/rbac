package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.AddWrongQuestionDTO;
import com.example.dto.WrongQuestionBookDTO;
import com.example.vo.WrongQuestionBookVO;
import com.example.vo.WrongQuestionItemVO;

public interface WrongQuestionBookService {
    
    /**
     * 创建错题本
     */
    WrongQuestionBookVO createWrongQuestionBook(WrongQuestionBookDTO dto, Long userId);
    
    /**
     * 获取用户错题本列表
     */
    Page<WrongQuestionBookVO> getUserWrongQuestionBooks(Long userId, Integer current, Integer size);
    
    /**
     * 获取错题本详情
     */
    WrongQuestionBookVO getWrongQuestionBookDetail(Long bookId, Long userId);
    
    /**
     * 添加错题到错题本
     */
    void addWrongQuestion(AddWrongQuestionDTO dto, Long userId);
    
    /**
     * 获取错题本中的题目列表
     */
    Page<WrongQuestionItemVO> getWrongQuestions(Long bookId, Long userId, Integer current, Integer size);
    
    /**
     * 从错题本中移除题目
     */
    void removeWrongQuestion(Long bookId, Long questionId, Long userId);
    
    /**
     * 更新题目掌握程度
     */
    void updateMasteryLevel(Long bookId, Long questionId, Integer masteryLevel, Long userId);
    
    /**
     * 删除错题本
     */
    void deleteWrongQuestionBook(Long bookId, Long userId);
    
    /**
     * 检查题目是否已在错题本中
     */
    boolean isQuestionInBook(Long bookId, Long questionId, Long userId);
} 