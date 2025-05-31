package com.example.mapper;

import com.example.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {
    
    // 基础CRUD操作
    int insert(Role role);
    
    int deleteById(Long id);
    
    int updateById(Role role);
    
    Role selectById(Long id);
    
    List<Role> selectList(@Param("role") Role role);
    
    // 根据角色编码查询
    Role selectByRoleCode(@Param("roleCode") String roleCode);
    
    // 根据ID列表查询
    List<Role> selectByIds(@Param("ids") List<Long> ids);
    
    // 根据状态查询
    List<Role> selectByStatus(@Param("status") Integer status);
    
    // 根据角色名称查询
    Role selectByRoleName(@Param("roleName") String roleName);
    
    // 根据角色名称查询（排除指定ID）
    Role selectByRoleNameExcludeId(@Param("roleName") String roleName, @Param("excludeId") Long excludeId);
    
    // 根据角色编码查询（排除指定ID）
    Role selectByRoleCodeExcludeId(@Param("roleCode") String roleCode, @Param("excludeId") Long excludeId);
    
    // 统计总数
    long selectCount(@Param("role") Role role);
} 