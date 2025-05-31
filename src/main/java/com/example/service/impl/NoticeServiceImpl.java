package com.example.service.impl;

import com.example.dto.NoticeDTO;
import com.example.dto.NoticeQueryDTO;
import com.example.entity.Notice;
import com.example.mapper.NoticeMapper;
import com.example.service.NoticeService;
import com.example.vo.NoticeVO;
import com.example.common.PageResult;
import com.example.util.PageUtil;
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
    public PageResult<NoticeVO> getNoticeList(NoticeQueryDTO queryDTO) {
        int offset = PageUtil.calculateOffset(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 构建查询关键字
        String keyword = null;
        if (StringUtils.hasText(queryDTO.getTitle()) || StringUtils.hasText(queryDTO.getPublisherName())) {
            keyword = StringUtils.hasText(queryDTO.getTitle()) ? queryDTO.getTitle() : queryDTO.getPublisherName();
        }
        
        List<Notice> notices = noticeMapper.selectPage(offset, queryDTO.getPageSize(), keyword);
        long total = noticeMapper.selectCount(new Notice());
        
        // 转换为VO
        List<NoticeVO> voList = notices.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageUtil.createPageResult(queryDTO.getPageNum(), queryDTO.getPageSize(), total, voList);
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
        notice.setCreateTime(LocalDateTime.now());
        notice.setUpdateTime(LocalDateTime.now());
        notice.setDeleted(0);
        
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
        notice.setUpdateTime(LocalDateTime.now());
        
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
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.updateById(notice);
    }
    
    @Override
    public void offlineNotice(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new RuntimeException("通知不存在");
        }
        
        notice.setStatus(2);
        notice.setUpdateTime(LocalDateTime.now());
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