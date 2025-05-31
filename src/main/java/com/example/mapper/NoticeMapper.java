package com.example.mapper;

import com.example.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {
    
    // 基础CRUD操作
    int insert(Notice notice);
    
    int deleteById(Long id);
    
    int updateById(Notice notice);
    
    Notice selectById(Long id);
    
    List<Notice> selectList(@Param("notice") Notice notice);
    
    // 根据状态查询
    List<Notice> selectByStatus(@Param("status") Integer status);
    
    // 分页查询
    List<Notice> selectPage(@Param("offset") int offset, @Param("limit") int limit, @Param("keyword") String keyword);
    
    // 统计总数
    long selectCount(@Param("notice") Notice notice);
} 