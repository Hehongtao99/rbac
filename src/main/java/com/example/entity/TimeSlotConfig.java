package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TimeSlotConfig {
    private Long id;
    
    private Integer timeSlot;    // 时间段 (1-6)
    
    private String slotName;     // 时间段名称 (如：第一大节)
    
    private String startTime;    // 开始时间
    
    private String endTime;      // 结束时间
    
    private String period;       // 时间段分类 (上午/下午/晚上)
    
    private String description;  // 描述
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private Integer deleted;
} 