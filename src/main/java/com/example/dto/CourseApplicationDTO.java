package com.example.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程申请数据传输对象
 */
@Data
public class CourseApplicationDTO {
    
    private Long id;
    
    private Long templateId;  // 课程模板ID
    
    private String courseName;
    
    private String courseDescription;
    
    private Integer courseHours;  // 修改为courseHours，与前端一致
    
    private Integer maxStudents;  // 添加计划人数字段
    
    private String academicYear;
    
    private String semester;
    
    private String reason;  // 修改为reason，与前端一致
    
    private Integer status; // 0-待审核 1-已通过 2-已拒绝
    
    private String reviewComment;
    
    private LocalDateTime applyTime;  // 修改为applyTime，与数据库一致
    
    @Override
    public String toString() {
        return "CourseApplicationDTO{" +
                "id=" + id +
                ", templateId=" + templateId +
                ", courseName='" + courseName + '\'' +
                ", courseDescription='" + courseDescription + '\'' +
                ", courseHours=" + courseHours +
                ", maxStudents=" + maxStudents +
                ", academicYear='" + academicYear + '\'' +
                ", semester='" + semester + '\'' +
                ", reason='" + reason + '\'' +
                ", status=" + status +
                ", reviewComment='" + reviewComment + '\'' +
                ", applyTime=" + applyTime +
                '}';
    }
} 