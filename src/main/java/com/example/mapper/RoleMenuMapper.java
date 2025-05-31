package com.example.mapper;

import com.example.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMenuMapper {
    
    // 基础CRUD操作
    int insert(RoleMenu roleMenu);
    
    int deleteById(Long id);
    
    int updateById(RoleMenu roleMenu);
    
    RoleMenu selectById(Long id);
    
    List<RoleMenu> selectList(@Param("roleMenu") RoleMenu roleMenu);
    
    // 根据角色ID查询
    List<RoleMenu> selectByRoleId(@Param("roleId") Long roleId);
    
    // 根据角色ID列表查询
    List<RoleMenu> selectByRoleIds(@Param("roleIds") List<Long> roleIds);
    
    // 根据菜单ID查询
    List<RoleMenu> selectByMenuId(@Param("menuId") Long menuId);
    
    // 删除角色的所有菜单权限
    int deleteByRoleId(@Param("roleId") Long roleId);
    
    // 统计总数
    long selectCount(@Param("roleMenu") RoleMenu roleMenu);
} 