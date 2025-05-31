package com.example.dto;

import lombok.Data;

/**
 * 时间冲突检查DTO
 */
@Data
public class TimeConflictCheckDTO {
    
    /**
     * 学年
     */
    private String academicYear;
    
    /**
     * 周次
     */
    private Integer weekNumber;
    
    /**
     * 星期几（1-7）
     */
    private Integer dayOfWeek;
    
    /**
     * 时间段
     */
    private Integer timeSlot;
    
    /**
     * 班级ID（可选）
     */
    private Long classId;
} 