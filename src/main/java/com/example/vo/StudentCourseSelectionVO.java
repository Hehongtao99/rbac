package com.example.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StudentCourseSelectionVO {
    private Long id;
    
    private Long courseApplicationId;
    
    private String courseName;
    
    private String teacherName;
    
    private String academicYear;
    
    private String semester;
    
    private Integer courseHours;
    
    private LocalDateTime selectionTime;
    
    private Integer status;
} 