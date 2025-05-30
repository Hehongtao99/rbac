package com.example.dto;

import lombok.Data;

@Data
public class ScheduleDTO {
    private Long courseId;
    private String courseName;
    private String academicYear;
    private Integer weekNumber;
    private Integer dayOfWeek;
    private Integer timeSlot;
    private Integer reducedHours;
} 