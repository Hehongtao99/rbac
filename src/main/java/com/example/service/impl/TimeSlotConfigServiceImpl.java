package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.TimeSlotConfig;
import com.example.mapper.TimeSlotConfigMapper;
import com.example.service.TimeSlotConfigService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TimeSlotConfigServiceImpl extends ServiceImpl<TimeSlotConfigMapper, TimeSlotConfig> implements TimeSlotConfigService {

    @Override
    public List<TimeSlotConfig> getAllTimeSlots() {
        LambdaQueryWrapper<TimeSlotConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(TimeSlotConfig::getTimeSlot);
        return this.list(wrapper);
    }

    @Override
    public void initTimeSlotConfig() {
        // 检查是否已经初始化
        long count = this.count();
        if (count > 0) {
            return;
        }
        
        List<TimeSlotConfig> configs = new ArrayList<>();
        
        // 上午时间段
        TimeSlotConfig slot1 = new TimeSlotConfig();
        slot1.setTimeSlot(1);
        slot1.setSlotName("第一大节");
        slot1.setStartTime("08:00");
        slot1.setEndTime("09:40");
        slot1.setPeriod("上午");
        slot1.setDescription("上午第一大节（8:00-8:45, 8:55-9:40）");
        configs.add(slot1);
        
        TimeSlotConfig slot2 = new TimeSlotConfig();
        slot2.setTimeSlot(2);
        slot2.setSlotName("第二大节");
        slot2.setStartTime("10:00");
        slot2.setEndTime("11:40");
        slot2.setPeriod("上午");
        slot2.setDescription("上午第二大节（10:00-10:45, 10:55-11:40）");
        configs.add(slot2);
        
        // 下午时间段
        TimeSlotConfig slot3 = new TimeSlotConfig();
        slot3.setTimeSlot(3);
        slot3.setSlotName("第三大节");
        slot3.setStartTime("16:30");
        slot3.setEndTime("18:10");
        slot3.setPeriod("下午");
        slot3.setDescription("下午第一大节（16:30-17:15, 17:25-18:10）");
        configs.add(slot3);
        
        TimeSlotConfig slot4 = new TimeSlotConfig();
        slot4.setTimeSlot(4);
        slot4.setSlotName("第四大节");
        slot4.setStartTime("18:20");
        slot4.setEndTime("20:00");
        slot4.setPeriod("下午");
        slot4.setDescription("下午第二大节（18:20-19:05, 19:15-20:00）");
        configs.add(slot4);
        
        // 晚上时间段
        TimeSlotConfig slot5 = new TimeSlotConfig();
        slot5.setTimeSlot(5);
        slot5.setSlotName("第五大节");
        slot5.setStartTime("19:00");
        slot5.setEndTime("20:40");
        slot5.setPeriod("晚上");
        slot5.setDescription("晚上第一大节（19:00-19:45, 19:55-20:40）");
        configs.add(slot5);
        
        TimeSlotConfig slot6 = new TimeSlotConfig();
        slot6.setTimeSlot(6);
        slot6.setSlotName("第六大节");
        slot6.setStartTime("20:50");
        slot6.setEndTime("22:30");
        slot6.setPeriod("晚上");
        slot6.setDescription("晚上第二大节（20:50-21:35, 21:45-22:30）");
        configs.add(slot6);
        
        this.saveBatch(configs);
    }
} 