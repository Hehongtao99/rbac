package com.example.dto;

import lombok.Data;

@Data
public class TeacherDTO {
    private Long id;
    private String teacherNo;
    private String name;
    private String gender;
    private String phone;
    private String email;
    private String department;
    private String title;
    private Integer status;
} 