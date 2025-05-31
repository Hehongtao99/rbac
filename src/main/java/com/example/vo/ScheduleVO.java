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
    private Long classId;
    private String className;
    private String academicYear;
    private Integer weekNumber;
    private Integer dayOfWeek;
    private String dayOfWeekName;
    private Integer timeSlot;
    private String timeRange;
    private Integer reducedHours;
    private LocalDateTime createTime;
} 