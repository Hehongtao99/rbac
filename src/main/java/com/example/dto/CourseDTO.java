package com.example.dto;

import lombok.Data;

@Data
public class CourseDTO {
    private Long id;
    private String courseName;
    private String courseDescription;
    private Integer maxStudents;
    private Integer currentStudents;
    private Integer courseHours;
    private String academicYear;
    private Integer status;
    private Integer allowApplication; // 是否允许申请：0-关闭申请，1-开放申请
} 