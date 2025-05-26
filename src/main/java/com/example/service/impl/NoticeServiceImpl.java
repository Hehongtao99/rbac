package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.NoticeDTO;
import com.example.dto.NoticeQueryDTO;
import com.example.entity.Notice;
import com.example.mapper.NoticeMapper;
import com.example.service.NoticeService;
import com.example.vo.NoticeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NoticeServiceImpl implements NoticeService {
    
    @Autowired
    private NoticeMapper noticeMapper;
    
    // 状态映射
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();
    
    static {
        STATUS_MAP.put(0, "草稿");
        STATUS_MAP.put(1, "已发布");
        STATUS_MAP.put(2, "已下线");
    }
    
    @Override
    public Page<NoticeVO> getNoticeList(NoticeQueryDTO queryDTO) {
        Page<Notice> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        
        // 条件查询
        if (StringUtils.hasText(queryDTO.getTitle())) {
            wrapper.like(Notice::getTitle, queryDTO.getTitle());
        }
        
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Notice::getStatus, queryDTO.getStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getPublisherName())) {
            wrapper.like(Notice::getPublisherName, queryDTO.getPublisherName());
        }
        
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(Notice::getPublishTime, queryDTO.getStartTime());
        }
        
        if (queryDTO.getEndTime() != null) {
            wrapper.le(Notice::getPublishTime, queryDTO.getEndTime());
        }
        
        // 按发布时间倒序
        wrapper.orderByDesc(Notice::getPublishTime, Notice::getCreateTime);
        
        Page<Notice> noticePage = noticeMapper.selectPage(page, wrapper);
        
        // 转换为VO
        Page<NoticeVO> voPage = new Page<>();
        BeanUtils.copyProperties(noticePage, voPage, "records");
        
        List<NoticeVO> voList = noticePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    public NoticeVO getNoticeById(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new RuntimeException("通知不存在");
        }
        return convertToVO(notice);
    }
    
    @Override
    public void addNotice(NoticeDTO noticeDTO, Long publisherId, String publisherName) {
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDTO, notice);
        
        notice.setPublisherId(publisherId);
        notice.setPublisherName(publisherName);
        
        // 如果是发布状态，设置发布时间
        if (noticeDTO.getStatus() != null && noticeDTO.getStatus() == 1) {
            notice.setPublishTime(noticeDTO.getPublishTime() != null ? 
                    noticeDTO.getPublishTime() : LocalDateTime.now());
        }
        
        noticeMapper.insert(notice);
    }
    
    @Override
    public void updateNotice(NoticeDTO noticeDTO) {
        Notice existNotice = noticeMapper.selectById(noticeDTO.getId());
        if (existNotice == null) {
            throw new RuntimeException("通知不存在");
        }
        
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDTO, notice);
        
        // 如果状态从非发布改为发布，设置发布时间
        if (noticeDTO.getStatus() != null && noticeDTO.getStatus() == 1 
                && existNotice.getStatus() != 1) {
            notice.setPublishTime(noticeDTO.getPublishTime() != null ? 
                    noticeDTO.getPublishTime() : LocalDateTime.now());
        }
        
        noticeMapper.updateById(notice);
    }
    
    @Override
    public void deleteNotice(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new RuntimeException("通知不存在");
        }
        noticeMapper.deleteById(id);
    }
    
    @Override
    public void publishNotice(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new RuntimeException("通知不存在");
        }
        
        notice.setStatus(1);
        notice.setPublishTime(LocalDateTime.now());
        noticeMapper.updateById(notice);
    }
    
    @Override
    public void offlineNotice(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new RuntimeException("通知不存在");
        }
        
        notice.setStatus(2);
        noticeMapper.updateById(notice);
    }
    
    /**
     * 转换为VO对象
     */
    private NoticeVO convertToVO(Notice notice) {
        NoticeVO vo = new NoticeVO();
        BeanUtils.copyProperties(notice, vo);
        
        // 设置状态名称
        vo.setStatusName(STATUS_MAP.get(notice.getStatus()));
        
        return vo;
    }
} 