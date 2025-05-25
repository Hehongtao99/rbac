package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.entity.Menu;
import com.example.entity.Role;
import com.example.entity.RoleMenu;
import com.example.entity.UserRole;
import com.example.mapper.MenuMapper;
import com.example.mapper.RoleMapper;
import com.example.mapper.RoleMenuMapper;
import com.example.mapper.UserRoleMapper;
import com.example.service.MenuService;
import com.example.vo.MenuVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {
    
    @Autowired
    private MenuMapper menuMapper;
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    
    @Override
    public List<MenuVO> getUserMenus(Long userId) {
        // 获取用户角色
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        
        // 检查是否是管理员角色（通过角色编码判断）
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(Role::getId, roleIds)
                .eq(Role::getRoleCode, "ADMIN");
        long adminCount = roleMapper.selectCount(roleWrapper);
        boolean isAdmin = adminCount > 0;
        
        List<Menu> menus;
        
        if (isAdmin) {
            // 管理员显示所有菜单
            LambdaQueryWrapper<Menu> menuWrapper = new LambdaQueryWrapper<>();
            menuWrapper.eq(Menu::getMenuType, "MENU")
                    .eq(Menu::getStatus, 1)
                    .orderByAsc(Menu::getSort);
            menus = menuMapper.selectList(menuWrapper);
        } else {
            // 普通用户根据权限显示菜单
            LambdaQueryWrapper<RoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
            roleMenuWrapper.in(RoleMenu::getRoleId, roleIds);
            List<RoleMenu> roleMenus = roleMenuMapper.selectList(roleMenuWrapper);
            
            if (roleMenus.isEmpty()) {
                return new ArrayList<>();
            }
            
            List<Long> menuIds = roleMenus.stream()
                    .map(RoleMenu::getMenuId)
                    .distinct()
                    .collect(Collectors.toList());
            
            // 获取菜单信息（只获取菜单类型，不包括按钮）
            LambdaQueryWrapper<Menu> menuWrapper = new LambdaQueryWrapper<>();
            menuWrapper.in(Menu::getId, menuIds)
                    .eq(Menu::getMenuType, "MENU")
                    .eq(Menu::getStatus, 1)
                    .orderByAsc(Menu::getSort);
            menus = menuMapper.selectList(menuWrapper);
        }
        
        // 转换为VO并构建树形结构
        List<MenuVO> menuVOs = menus.stream().map(menu -> {
            MenuVO menuVO = new MenuVO();
            BeanUtils.copyProperties(menu, menuVO);
            return menuVO;
        }).collect(Collectors.toList());
        
        return buildMenuTree(menuVOs, 0L);
    }
    
    @Override
    public List<String> getUserButtons(Long userId) {
        // 获取用户角色
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        
        // 检查是否是管理员角色（通过角色编码判断）
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(Role::getId, roleIds)
                .eq(Role::getRoleCode, "ADMIN");
        long adminCount = roleMapper.selectCount(roleWrapper);
        boolean isAdmin = adminCount > 0;
        
        List<Menu> buttons;
        
        if (isAdmin) {
            // 管理员拥有所有按钮权限
            LambdaQueryWrapper<Menu> menuWrapper = new LambdaQueryWrapper<>();
            menuWrapper.eq(Menu::getMenuType, "BUTTON")
                    .eq(Menu::getStatus, 1);
            buttons = menuMapper.selectList(menuWrapper);
        } else {
            // 普通用户根据权限获取按钮
            LambdaQueryWrapper<RoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
            roleMenuWrapper.in(RoleMenu::getRoleId, roleIds);
            List<RoleMenu> roleMenus = roleMenuMapper.selectList(roleMenuWrapper);
            
            if (roleMenus.isEmpty()) {
                return new ArrayList<>();
            }
            
            List<Long> menuIds = roleMenus.stream()
                    .map(RoleMenu::getMenuId)
                    .distinct()
                    .collect(Collectors.toList());
            
            // 获取按钮权限
            LambdaQueryWrapper<Menu> menuWrapper = new LambdaQueryWrapper<>();
            menuWrapper.in(Menu::getId, menuIds)
                    .eq(Menu::getMenuType, "BUTTON")
                    .eq(Menu::getStatus, 1);
            buttons = menuMapper.selectList(menuWrapper);
        }
        
        return buttons.stream()
                .map(Menu::getMenuCode)
                .collect(Collectors.toList());
    }
    
    private List<MenuVO> buildMenuTree(List<MenuVO> menus, Long parentId) {
        List<MenuVO> tree = new ArrayList<>();
        
        for (MenuVO menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                List<MenuVO> children = buildMenuTree(menus, menu.getId());
                menu.setChildren(children);
                tree.add(menu);
            }
        }
        
        return tree;
    }
} 