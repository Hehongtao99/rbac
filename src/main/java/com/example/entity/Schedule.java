package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Schedule {
    private Long id;
    
    private Long courseId;
    
    private String courseName;
    
    private Long teacherId;
    
    private String teacherName;
    
    private Long classId;
    
    private String className;
    
    private Long classCourseHoursId; // 班级课程课时记录ID
    
    private String academicYear;
    
    private Integer weekNumber;  // 第几周 (1-20)
    
    private Integer dayOfWeek;   // 星期几 (1-7, 1为周一)
    
    private Integer timeSlot;    // 时间段 (1-6, 上午1-2, 下午3-4, 晚上5-6)
    
    private String timeRange;    // 时间范围文字描述
    
    private Integer reducedHours; // 减少的课时数
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private Integer deleted;
} 