package com.example.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程申请视图对象
 */
@Data
public class CourseApplicationVO {
    
    private Long id;
    
    private Long templateId;  // 课程模板ID
    
    private String courseName;
    
    private String courseDescription;
    
    private Integer courseHours;  // 修改为courseHours，与数据库一致
    
    private Integer remainingHours;  // 剩余课时
    
    private Integer maxStudents;  // 计划人数
    
    private String academicYear;
    
    private String semester;
    
    private String reason;  // 修改为reason，与数据库一致
    
    private Integer status;
    
    private String statusName; // 状态名称：待审核、已通过、已拒绝
    
    private String reviewComment;
    
    private Long teacherId;
    
    private String teacherName;
    
    private Long reviewerId;  // 审核人ID
    
    private String reviewerName;  // 审核人姓名
    
    // 学院专业信息（从课程模板关联获取）
    private Long collegeId;
    
    private String collegeName;
    
    private Long majorId;
    
    private String majorName;
    
    private LocalDateTime applyTime;  // 修改为applyTime，与数据库一致
    
    private LocalDateTime reviewTime;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 