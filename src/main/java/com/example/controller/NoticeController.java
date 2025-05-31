package com.example.controller;

import com.example.common.Result;
import com.example.common.PageResult;
import com.example.dto.NoticeDTO;
import com.example.dto.NoticeQueryDTO;
import com.example.service.NoticeService;
import com.example.vo.NoticeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notice")
@CrossOrigin
public class NoticeController {
    
    @Autowired
    private NoticeService noticeService;
    
    /**
     * 分页查询通知列表
     */
    @PostMapping("/list")
    public Result<PageResult<NoticeVO>> getNoticeList(@RequestBody NoticeQueryDTO queryDTO) {
        PageResult<NoticeVO> result = noticeService.getNoticeList(queryDTO);
        return Result.success(result);
    }
    
    /**
     * 获取通知详情
     */
    @GetMapping("/{id}")
    public Result<NoticeVO> getNoticeById(@PathVariable Long id) {
        NoticeVO notice = noticeService.getNoticeById(id);
        return Result.success(notice);
    }
    
    /**
     * 新增通知
     */
    @PostMapping("/add")
    public Result<Void> addNotice(@RequestBody NoticeDTO noticeDTO) {
        noticeService.addNotice(noticeDTO);
        return Result.success();
    }
    
    /**
     * 编辑通知
     */
    @PutMapping("/update")
    public Result<Void> updateNotice(@RequestBody NoticeDTO noticeDTO) {
        noticeService.updateNotice(noticeDTO);
        return Result.success();
    }
    
    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return Result.success();
    }
    
    /**
     * 发布通知
     */
    @PutMapping("/publish/{id}")
    public Result<Void> publishNotice(@PathVariable Long id) {
        noticeService.publishNotice(id);
        return Result.success();
    }
    
    /**
     * 下线通知
     */
    @PutMapping("/offline/{id}")
    public Result<Void> offlineNotice(@PathVariable Long id) {
        noticeService.offlineNotice(id);
        return Result.success();
    }
} 