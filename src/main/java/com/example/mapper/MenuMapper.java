package com.example.mapper;

import com.example.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper {
    
    // 基础CRUD操作
    int insert(Menu menu);
    
    int deleteById(Long id);
    
    int updateById(Menu menu);
    
    Menu selectById(Long id);
    
    List<Menu> selectList(@Param("menu") Menu menu);
    
    // 根据菜单类型和状态查询
    List<Menu> selectByTypeAndStatus(@Param("menuType") String menuType, @Param("status") Integer status);
    
    // 根据ID列表查询
    List<Menu> selectByIds(@Param("ids") List<Long> ids);
    
    // 统计总数
    long selectCount(@Param("menu") Menu menu);
} 