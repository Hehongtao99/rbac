package com.example.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程申请视图对象
 */
@Data
public class CourseApplicationVO {
    
    private Long id;
    
    private String courseName;
    
    private String courseDescription;
    
    private Integer totalHours;
    
    private String academicYear;
    
    private String semester;
    
    private String applicationReason;
    
    private Integer status;
    
    private String statusName; // 状态名称：待审核、已通过、已拒绝
    
    private String reviewComment;
    
    private Long teacherId;
    
    private String teacherName;
    
    private LocalDateTime applicationTime;
    
    private LocalDateTime reviewTime;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 