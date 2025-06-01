package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.dto.AddWrongQuestionDTO;
import com.example.dto.WrongQuestionBookDTO;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.service.WrongQuestionBookService;
import com.example.util.JwtUtil;
import com.example.vo.WrongQuestionBookVO;
import com.example.vo.WrongQuestionItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/wrong-question-book")
@CrossOrigin
public class WrongQuestionBookController {
    
    @Autowired
    private WrongQuestionBookService wrongQuestionBookService;
    
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
     * 创建错题本
     */
    @PostMapping("/create")
    public Result<WrongQuestionBookVO> createWrongQuestionBook(@RequestBody WrongQuestionBookDTO dto, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            WrongQuestionBookVO vo = wrongQuestionBookService.createWrongQuestionBook(dto, userId);
            return Result.success(vo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户错题本列表
     */
    @GetMapping("/list")
    public Result<Page<WrongQuestionBookVO>> getUserWrongQuestionBooks(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            Page<WrongQuestionBookVO> page = wrongQuestionBookService.getUserWrongQuestionBooks(userId, current, size);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取错题本详情
     */
    @GetMapping("/{bookId}")
    public Result<WrongQuestionBookVO> getWrongQuestionBookDetail(@PathVariable Long bookId, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            WrongQuestionBookVO vo = wrongQuestionBookService.getWrongQuestionBookDetail(bookId, userId);
            return Result.success(vo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 添加错题到错题本
     */
    @PostMapping("/add-question")
    public Result<String> addWrongQuestion(@RequestBody AddWrongQuestionDTO dto, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            wrongQuestionBookService.addWrongQuestion(dto, userId);
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取错题本中的题目列表
     */
    @GetMapping("/{bookId}/questions")
    public Result<Page<WrongQuestionItemVO>> getWrongQuestions(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            Page<WrongQuestionItemVO> page = wrongQuestionBookService.getWrongQuestions(bookId, userId, current, size);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 从错题本中移除题目
     */
    @DeleteMapping("/{bookId}/questions/{questionId}")
    public Result<String> removeWrongQuestion(@PathVariable Long bookId, @PathVariable Long questionId, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            wrongQuestionBookService.removeWrongQuestion(bookId, questionId, userId);
            return Result.success("移除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新题目掌握程度
     */
    @PutMapping("/{bookId}/questions/{questionId}/mastery")
    public Result<String> updateMasteryLevel(
            @PathVariable Long bookId, 
            @PathVariable Long questionId, 
            @RequestParam Integer masteryLevel, 
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            wrongQuestionBookService.updateMasteryLevel(bookId, questionId, masteryLevel, userId);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除错题本
     */
    @DeleteMapping("/{bookId}")
    public Result<String> deleteWrongQuestionBook(@PathVariable Long bookId, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            wrongQuestionBookService.deleteWrongQuestionBook(bookId, userId);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 检查题目是否已在错题本中
     */
    @GetMapping("/{bookId}/questions/{questionId}/exists")
    public Result<Boolean> isQuestionInBook(@PathVariable Long bookId, @PathVariable Long questionId, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            boolean exists = wrongQuestionBookService.isQuestionInBook(bookId, questionId, userId);
            return Result.success(exists);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
} 