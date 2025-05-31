package com.example.service;

import com.example.entity.TimeSlotConfig;

import java.util.List;

public interface TimeSlotConfigService {
    
    /**
     * 获取所有时间段配置
     */
    List<TimeSlotConfig> getAllTimeSlots();
    
    /**
     * 初始化时间段配置
     */
    void initTimeSlotConfig();
} 