package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_time_slot_config")
public class TimeSlotConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Integer timeSlot;    // 时间段 (1-6)
    
    private String slotName;     // 时间段名称 (如：第一大节)
    
    private String startTime;    // 开始时间
    
    private String endTime;      // 结束时间
    
    private String period;       // 时间段分类 (上午/下午/晚上)
    
    private String description;  // 描述
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 