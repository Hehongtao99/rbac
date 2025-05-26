package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_notice")
public class Notice {
    @TableId(type = IdType.AUTO)
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
     * 公告时间
     */
    private LocalDateTime publishTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 