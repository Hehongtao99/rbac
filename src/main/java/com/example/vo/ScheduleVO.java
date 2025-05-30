package com.example.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleVO {
    private Long id;
    private Long courseId;
    private String courseName;
    private Long teacherId;
    private String teacherName;
    private String academicYear;
    private Integer weekNumber;
    private Integer dayOfWeek;
    private String dayOfWeekName;
    private Integer timeSlot;
    private String timeRange;
    private String classroom;
    private Integer reducedHours;
    private LocalDateTime createTime;
} 