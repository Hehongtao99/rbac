package com.example.controller;

import com.example.common.Result;
import com.example.common.PageResult;
import com.example.dto.NoticeDTO;
import com.example.dto.NoticeQueryDTO;
import com.example.service.NoticeService;
import com.example.util.JwtUtil;
import com.example.vo.NoticeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/notice")
@CrossOrigin
public class NoticeController {
    
    @Autowired
    private NoticeService noticeService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 分页查询通知列表
     */
    @PostMapping("/list")
    public Result<PageResult<NoticeVO>> getNoticeList(@RequestBody NoticeQueryDTO queryDTO) {
        try {
            PageResult<NoticeVO> result = noticeService.getNoticeList(queryDTO);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取通知详情
     */
    @GetMapping("/{id}")
    public Result<NoticeVO> getNoticeById(@PathVariable Long id) {
        try {
            NoticeVO notice = noticeService.getNoticeById(id);
            return Result.success(notice);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 新增通知
     */
    @PostMapping("/add")
    public Result<Void> addNotice(@RequestBody NoticeDTO noticeDTO, HttpServletRequest request) {
        try {
            // 从token中获取用户信息
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String username = jwtUtil.getUsernameFromToken(token);
                // 这里简化处理，实际应该从用户表查询用户ID和姓名
                // 暂时用username作为publisherName，后续可以优化
                noticeService.addNotice(noticeDTO, 1L, username);
                return Result.success();
            }
            return Result.error(401, "未授权");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 编辑通知
     */
    @PutMapping("/update")
    public Result<Void> updateNotice(@RequestBody NoticeDTO noticeDTO) {
        try {
            noticeService.updateNotice(noticeDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotice(@PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 发布通知
     */
    @PutMapping("/publish/{id}")
    public Result<Void> publishNotice(@PathVariable Long id) {
        try {
            noticeService.publishNotice(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 下线通知
     */
    @PutMapping("/offline/{id}")
    public Result<Void> offlineNotice(@PathVariable Long id) {
        try {
            noticeService.offlineNotice(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
} 