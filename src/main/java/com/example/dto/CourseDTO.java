package com.example.dto;

import lombok.Data;

@Data
public class CourseDTO {
    private Long id;
    private String courseName;
    private String courseDescription;
    private Integer maxStudents;
    private Integer currentStudents;
    private Long teacherId;
    private String teacherName;
    private Integer courseHours;
    private String academicYear;
    private Integer status;
} 