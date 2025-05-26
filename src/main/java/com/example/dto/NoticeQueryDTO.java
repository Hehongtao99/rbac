package com.example.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoticeQueryDTO {
    /**
     * 公告标题（模糊查询）
     */
    private String title;
    
    /**
     * 公告状态
     */
    private Integer status;
    
    /**
     * 公告人姓名（模糊查询）
     */
    private String publisherName;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 页大小
     */
    private Integer pageSize = 10;
} 