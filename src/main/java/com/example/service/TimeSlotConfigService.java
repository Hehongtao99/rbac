package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.TimeSlotConfig;

import java.util.List;

public interface TimeSlotConfigService extends IService<TimeSlotConfig> {
    
    /**
     * 获取所有时间段配置
     */
    List<TimeSlotConfig> getAllTimeSlots();
    
    /**
     * 初始化时间段配置
     */
    void initTimeSlotConfig();
} 