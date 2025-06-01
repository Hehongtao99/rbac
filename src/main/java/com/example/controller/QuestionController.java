package com.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.dto.QuestionDTO;
import com.example.service.QuestionService;
import com.example.vo.QuestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question")
@CrossOrigin
public class QuestionController {
    
    @Autowired
    private QuestionService questionService;
    
    /**
     * 分页查询题目列表
     */
    @GetMapping("/list")
    public Result<Page<QuestionVO>> getQuestionList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long bankId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String title) {
        Page<QuestionVO> page = questionService.getQuestionList(current, size, bankId, type, title);
        return Result.success(page);
    }
    
    /**
     * 根据ID获取题目详情
     */
    @GetMapping("/{id}")
    public Result<QuestionVO> getQuestionById(@PathVariable Long id) {
        QuestionVO questionVO = questionService.getQuestionById(id);
        return Result.success(questionVO);
    }
    
    /**
     * 创建题目
     */
    @PostMapping
    public Result<Void> createQuestion(@RequestBody QuestionDTO questionDTO) {
        // 这里应该从登录用户信息中获取用户ID，暂时使用固定值
        Long userId = 1L;
        questionService.createQuestion(questionDTO, userId);
        return Result.success();
    }
    
    /**
     * 更新题目
     */
    @PutMapping
    public Result<Void> updateQuestion(@RequestBody QuestionDTO questionDTO) {
        questionService.updateQuestion(questionDTO);
        return Result.success();
    }
    
    /**
     * 删除题目
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return Result.success();
    }
    
    /**
     * 批量删除题目
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteQuestion(@RequestBody Long[] ids) {
        questionService.batchDeleteQuestion(ids);
        return Result.success();
    }
} 