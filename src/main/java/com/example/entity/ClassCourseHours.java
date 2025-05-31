package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClassCourseHours {
    private Long id;
    
    private Long applicationId;
    
    private Long classId;
    
    private String className;
    
    private String courseName;
    
    private Long teacherId;
    
    private String teacherName;
    
    private String academicYear;
    
    private String semester;
    
    private Integer totalHours;
    
    private Integer usedHours;
    
    private Integer remainingHours;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private Integer deleted;
} 