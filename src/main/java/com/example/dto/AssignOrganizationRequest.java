package com.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class AssignOrganizationRequest {
    private Long userId;
    private Long collegeId;
    private Long majorId;
    private List<Long> classIds;
} 