package com.example.service;

import com.example.common.Result;
import com.example.dto.AssignOrganizationRequest;
import com.example.dto.OrganizationQueryDTO;
import com.example.entity.Organization;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.vo.MenuVO;
import com.example.vo.OrganizationVO;
import com.example.vo.UserWithRoleVO;

import java.util.List;

/**
 * 管理员服务接口
 */
public interface AdminService {
    
    // 用户管理
    Result<List<UserWithRoleVO>> getAllUsers();
    Result<Void> addUser(User user);
    Result<Void> updateUser(Long id, User user);
    Result<Void> deleteUser(Long id);
    Result<User> getUserById(Long id);
    
    // 用户角色管理
    Result<List<Long>> getUserRoles(Long userId);
    Result<Void> assignRole(Long userId, Long roleId);
    
    // 角色管理
    Result<List<Role>> getAllRoles();
    Result<Void> addRole(Role role);
    Result<Void> updateRole(Long id, Role role);
    Result<Void> deleteRole(Long id);
    Result<Role> getRoleById(Long id);
    
    // 菜单管理
    Result<List<MenuVO>> getAllMenus();
    Result<List<Long>> getRoleMenus(Long roleId);
    Result<Void> assignMenu(Long roleId, List<Long> menuIds);
    
    // 组织管理
    Result<List<OrganizationVO>> getOrganizationTree(OrganizationQueryDTO queryDTO);
    Result<Void> addOrganization(Organization organization);
    Result<Void> updateOrganization(Long id, Organization organization);
    Result<Void> deleteOrganization(Long id);
    Result<Organization> getOrganizationById(Long id);
    Result<Void> assignOrganization(AssignOrganizationRequest request);
    Result<List<Long>> getUserOrganizations(Long userId);
    Result<List<OrganizationVO>> getOrganizationsByLevel(Integer level);
    Result<List<OrganizationVO>> getOrganizationsByParent(Long parentId);
} 