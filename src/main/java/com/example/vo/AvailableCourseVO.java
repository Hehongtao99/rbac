package com.example.vo;

import lombok.Data;

@Data
public class AvailableCourseVO {
    private Long courseApplicationId;
    
    private String courseName;
    
    private String teacherName;
    
    private String academicYear;
    
    private String semester;
    
    private Integer courseHours;
    
    private Integer maxStudents;
    
    private Integer currentStudents;
    
    private Boolean isSelected;
    
    private String reason;
} 