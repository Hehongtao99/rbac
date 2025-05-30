package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_schedule")
public class Schedule {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long courseId;
    
    private String courseName;
    
    private Long teacherId;
    
    private String teacherName;
    
    private String academicYear;
    
    private Integer weekNumber;  // 第几周 (1-20)
    
    private Integer dayOfWeek;   // 星期几 (1-7, 1为周一)
    
    private Integer timeSlot;    // 时间段 (1-6, 上午1-2, 下午3-4, 晚上5-6)
    
    private String timeRange;    // 时间范围文字描述
    
    private String classroom;    // 教室
    
    private Integer reducedHours; // 减少的课时数
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 