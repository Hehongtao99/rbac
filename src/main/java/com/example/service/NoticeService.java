package com.example.service;

import com.example.common.PageResult;
import com.example.dto.NoticeDTO;
import com.example.dto.NoticeQueryDTO;
import com.example.vo.NoticeVO;

public interface NoticeService {
    /**
     * 分页查询通知列表
     */
    PageResult<NoticeVO> getNoticeList(NoticeQueryDTO queryDTO);
    
    /**
     * 根据ID获取通知详情
     */
    NoticeVO getNoticeById(Long id);
    
    /**
     * 新增通知
     */
    void addNotice(NoticeDTO noticeDTO, Long publisherId, String publisherName);
    
    /**
     * 编辑通知
     */
    void updateNotice(NoticeDTO noticeDTO);
    
    /**
     * 删除通知
     */
    void deleteNotice(Long id);
    
    /**
     * 发布通知
     */
    void publishNotice(Long id);
    
    /**
     * 下线通知
     */
    void offlineNotice(Long id);
} 