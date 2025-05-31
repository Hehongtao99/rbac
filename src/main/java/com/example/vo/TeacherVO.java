package com.example.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 教师视图对象
 */
@Data
public class TeacherVO {
    
    private Long id;
    
    private Long userId;
    
    private String teacherNo;
    
    private String name;
    
    private String gender;
    
    private String email;
    
    private String phone;
    
    private String college; // 学院
    
    private String major; // 专业
    
    private List<ClassInfo> classes; // 班级列表
    
    private Integer status;
    
    private String statusName;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    @Data
    public static class ClassInfo {
        private Long id;
        private String name;
        private String code;
    }
} 