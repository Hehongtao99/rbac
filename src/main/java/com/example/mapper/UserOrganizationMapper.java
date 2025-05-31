package com.example.mapper;

import com.example.entity.UserOrganization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserOrganizationMapper {
    
    // 基础CRUD操作
    int insert(UserOrganization userOrganization);
    
    int deleteById(Long id);
    
    int updateById(UserOrganization userOrganization);
    
    UserOrganization selectById(Long id);
    
    List<UserOrganization> selectList(@Param("userOrganization") UserOrganization userOrganization);
    
    // 根据用户ID查询
    List<UserOrganization> selectByUserId(@Param("userId") Long userId);
    
    // 根据组织ID查询
    List<UserOrganization> selectByOrganizationId(@Param("organizationId") Long organizationId);
    
    // 删除用户的所有组织关联
    int deleteByUserId(@Param("userId") Long userId);
    
    // 统计总数
    long selectCount(@Param("userOrganization") UserOrganization userOrganization);
} 