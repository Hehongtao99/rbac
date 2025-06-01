package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.dto.ExamAnswerDTO;
import com.example.dto.ExamStartDTO;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.service.ExamService;
import com.example.util.JwtUtil;
import com.example.vo.ExamVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/exam")
public class ExamController {
    
    @Autowired
    private ExamService examService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, username);
            User user = userMapper.selectOne(wrapper);
            
            if (user != null) {
                return user.getId();
            }
        }
        throw new RuntimeException("用户未登录或token无效");
    }
    
    /**
     * 开始考试
     */
    @PostMapping("/start")
    public Result<ExamVO> startExam(@RequestBody ExamStartDTO examStartDTO, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            ExamVO examVO = examService.startExam(examStartDTO, userId);
            return Result.success(examVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取考试详情
     */
    @GetMapping("/{examId}")
    public Result<ExamVO> getExamDetail(@PathVariable Long examId, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            ExamVO examVO = examService.getExamDetail(examId, userId);
            return Result.success(examVO);
        } catch (NumberFormatException e) {
            return Result.error("考试ID格式错误");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("获取考试详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 提交考试答案
     */
    @PostMapping("/submit")
    public Result<ExamVO> submitExam(@RequestBody ExamAnswerDTO examAnswerDTO, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            ExamVO examVO = examService.submitExam(examAnswerDTO, userId);
            return Result.success(examVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户考试列表
     */
    @GetMapping("/list")
    public Result<Page<ExamVO>> getUserExamList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            Page<ExamVO> examPage = examService.getUserExamList(userId, current, size);
            return Result.success(examPage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取考试结果
     */
    @GetMapping("/result/{examId}")
    public Result<ExamVO> getExamResult(@PathVariable Long examId, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            ExamVO examVO = examService.getExamResult(examId, userId);
            return Result.success(examVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取考试记录（别名路由，兼容前端可能的调用）
     */
    @GetMapping("/record")
    public Result<Page<ExamVO>> getExamRecord(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            Page<ExamVO> examPage = examService.getUserExamList(userId, current, size);
            return Result.success(examPage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
} 