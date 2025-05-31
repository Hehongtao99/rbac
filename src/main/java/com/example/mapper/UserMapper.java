package com.example.mapper;

import com.example.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    
    // 基础CRUD操作
    int insert(User user);
    
    int deleteById(Long id);
    
    int updateById(User user);
    
    User selectById(Long id);
    
    List<User> selectList(@Param("user") User user);
    
    // 根据用户名查询
    User selectByUsername(@Param("username") String username);
    
    // 根据状态查询
    List<User> selectByStatus(@Param("status") Integer status);
    
    // 根据用户名和ID查询（排除指定ID）
    User selectByUsernameExcludeId(@Param("username") String username, @Param("excludeId") Long excludeId);
    
    // 分页查询
    List<User> selectPage(@Param("offset") int offset, @Param("limit") int limit, @Param("keyword") String keyword);
    
    // 统计总数
    long selectCount(@Param("user") User user);
} 