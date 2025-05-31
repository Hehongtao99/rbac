package com.example.dto;

import lombok.Data;

@Data
public class CourseDTO {
    private Long id;
    private String courseName;
    private String templateName; // 课程模板名称
    private String courseDescription;
    private String description; // 课程模板描述
    private Integer maxStudents;
    private Integer currentStudents;
    private Integer courseHours;
    private String academicYear;
    private String semester; // 学期
    private Integer status;
    private Integer allowApplication; // 是否允许申请：0-关闭申请，1-开放申请
    
    @Override
    public String toString() {
        return "CourseDTO{" +
                "id=" + id +
                ", courseName='" + courseName + '\'' +
                ", templateName='" + templateName + '\'' +
                ", courseDescription='" + courseDescription + '\'' +
                ", description='" + description + '\'' +
                ", maxStudents=" + maxStudents +
                ", currentStudents=" + currentStudents +
                ", courseHours=" + courseHours +
                ", academicYear='" + academicYear + '\'' +
                ", semester='" + semester + '\'' +
                ", status=" + status +
                ", allowApplication=" + allowApplication +
                '}';
    }
} 