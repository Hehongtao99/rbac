package com.example.service;

import com.example.common.PageResult;
import com.example.dto.TeacherDTO;
import com.example.dto.TeacherPageDTO;
import com.example.vo.TeacherVO;

public interface TeacherService {
    
    /**
     * 分页获取教师列表
     */
    PageResult<TeacherVO> getTeacherList(TeacherPageDTO pageDTO);
    
    /**
     * 创建教师
     */
    void createTeacher(TeacherDTO teacherDTO);
    
    /**
     * 更新教师
     */
    void updateTeacher(Long id, TeacherDTO teacherDTO);
    
    /**
     * 删除教师
     */
    void deleteTeacher(Long id);
    
    /**
     * 根据ID获取教师详情
     */
    TeacherVO getTeacherById(Long id);
} 