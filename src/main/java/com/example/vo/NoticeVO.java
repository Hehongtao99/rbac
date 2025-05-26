package com.example.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoticeVO {
    private Long id;
    
    /**
     * 公告标题
     */
    private String title;
    
    /**
     * 公告人ID
     */
    private Long publisherId;
    
    /**
     * 公告人姓名
     */
    private String publisherName;
    
    /**
     * 公告内容（富文本）
     */
    private String content;
    
    /**
     * 公告状态：0-草稿，1-已发布，2-已下线
     */
    private Integer status;
    
    /**
     * 公告状态名称
     */
    private String statusName;
    
    /**
     * 公告时间
     */
    private LocalDateTime publishTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 