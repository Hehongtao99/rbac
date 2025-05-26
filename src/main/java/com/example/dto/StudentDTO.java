package com.example.dto;

import lombok.Data;

@Data
public class StudentDTO {
    private Long id;
    private String studentNo;
    private String name;
    private String gender;
    private String phone;
    private String email;
    private String major;
    private String className;
    private String grade;
    private String educationSystem;
    private String enrollmentDate;
    private String graduationDate;
    private Integer currentYear;
    private Integer currentSemester;
    private String currentAcademicYear;
    private Integer status;
} 