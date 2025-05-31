package com.example.mapper;

import com.example.entity.TimeSlotConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TimeSlotConfigMapper {
    
    // 基础CRUD操作
    int insert(TimeSlotConfig timeSlotConfig);
    
    int deleteById(Long id);
    
    int updateById(TimeSlotConfig timeSlotConfig);
    
    TimeSlotConfig selectById(Long id);
    
    List<TimeSlotConfig> selectList(@Param("timeSlotConfig") TimeSlotConfig timeSlotConfig);
    
    // 根据时间段查询
    TimeSlotConfig selectByTimeSlot(@Param("timeSlot") Integer timeSlot);
    
    // 根据时间段分类查询
    List<TimeSlotConfig> selectByPeriod(@Param("period") String period);
    
    // 统计总数
    long selectCount(@Param("timeSlotConfig") TimeSlotConfig timeSlotConfig);
} 