package com.example.mapper;

import com.example.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleMapper {
    
    // 基础CRUD操作
    int insert(UserRole userRole);
    
    int deleteById(Long id);
    
    int updateById(UserRole userRole);
    
    UserRole selectById(Long id);
    
    List<UserRole> selectList(@Param("userRole") UserRole userRole);
    
    // 根据用户ID查询
    List<UserRole> selectByUserId(@Param("userId") Long userId);
    
    // 根据角色ID查询
    List<UserRole> selectByRoleId(@Param("roleId") Long roleId);
    
    // 删除用户的所有角色
    int deleteByUserId(@Param("userId") Long userId);
    
    // 统计总数
    long selectCount(@Param("userRole") UserRole userRole);
} 