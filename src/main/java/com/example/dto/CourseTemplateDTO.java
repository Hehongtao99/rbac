package com.example.dto;

import lombok.Data;

/**
 * 课程模板数据传输对象
 */
@Data
public class CourseTemplateDTO {
    
    private Long id;
    
    private String templateName;
    
    private String description;
    
    private Integer courseHours;
    
    private String academicYear;
    
    private String semester;
    
    private Integer maxStudents;
    
    private Long collegeId;
    
    private Long majorId;
    
    private Integer status;
    
    @Override
    public String toString() {
        return "CourseTemplateDTO{" +
                "id=" + id +
                ", templateName='" + templateName + '\'' +
                ", description='" + description + '\'' +
                ", courseHours=" + courseHours +
                ", academicYear='" + academicYear + '\'' +
                ", semester='" + semester + '\'' +
                ", maxStudents=" + maxStudents +
                ", collegeId=" + collegeId +
                ", majorId=" + majorId +
                ", status=" + status +
                '}';
    }
} 