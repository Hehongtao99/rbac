package com.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.dto.QuestionBankDTO;
import com.example.service.QuestionBankService;
import com.example.vo.QuestionBankVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question-bank")
@CrossOrigin
public class QuestionBankController {
    
    @Autowired
    private QuestionBankService questionBankService;
    
    /**
     * 分页查询题库列表
     */
    @GetMapping("/list")
    public Result<Page<QuestionBankVO>> getQuestionBankList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {
        Page<QuestionBankVO> page = questionBankService.getQuestionBankList(current, size, name);
        return Result.success(page);
    }
    
    /**
     * 根据ID获取题库详情
     */
    @GetMapping("/{id}")
    public Result<QuestionBankVO> getQuestionBankById(@PathVariable Long id) {
        QuestionBankVO questionBankVO = questionBankService.getQuestionBankById(id);
        return Result.success(questionBankVO);
    }
    
    /**
     * 创建题库
     */
    @PostMapping
    public Result<Void> createQuestionBank(@RequestBody QuestionBankDTO questionBankDTO) {
        // 这里应该从登录用户信息中获取用户ID，暂时使用固定值
        Long userId = 1L;
        questionBankService.createQuestionBank(questionBankDTO, userId);
        return Result.success();
    }
    
    /**
     * 更新题库
     */
    @PutMapping
    public Result<Void> updateQuestionBank(@RequestBody QuestionBankDTO questionBankDTO) {
        questionBankService.updateQuestionBank(questionBankDTO);
        return Result.success();
    }
    
    /**
     * 删除题库
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteQuestionBank(@PathVariable Long id) {
        questionBankService.deleteQuestionBank(id);
        return Result.success();
    }
} 