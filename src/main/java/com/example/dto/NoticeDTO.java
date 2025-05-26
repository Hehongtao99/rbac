package com.example.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoticeDTO {
    private Long id;
    
    /**
     * 公告标题
     */
    private String title;
    
    /**
     * 公告内容（富文本）
     */
    private String content;
    
    /**
     * 公告状态：0-草稿，1-已发布，2-已下线
     */
    private Integer status;
    
    /**
     * 公告时间
     */
    private LocalDateTime publishTime;
} 