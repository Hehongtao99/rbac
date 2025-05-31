package com.example.controller;

import com.example.common.Result;
import com.example.entity.Organization;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.service.AdminService;
import com.example.dto.AssignOrganizationRequest;
import com.example.dto.OrganizationQueryDTO;
import com.example.vo.MenuVO;
import com.example.vo.OrganizationVO;
import com.example.vo.UserWithRoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    // 获取所有用户
    @GetMapping("/users")
    public Result<List<UserWithRoleVO>> getAllUsers() {
        return adminService.getAllUsers();
    }
    
    // 新增用户
    @PostMapping("/users")
    public Result<Void> addUser(@RequestBody User user) {
        return adminService.addUser(user);
    }
    
    // 更新用户
    @PutMapping("/users/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody User user) {
        return adminService.updateUser(id, user);
    }
    
    // 删除用户
    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        return adminService.deleteUser(id);
    }
    
    // 获取单个用户信息
    @GetMapping("/users/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        return adminService.getUserById(id);
    }
    
    // 获取用户角色
    @GetMapping("/user-roles/{userId}")
    public Result<List<Long>> getUserRoles(@PathVariable Long userId) {
        return adminService.getUserRoles(userId);
    }
    
    // 分配角色
    @PostMapping("/assign-role")
    public Result<Void> assignRole(@RequestParam Long userId, @RequestParam Long roleId) {
        return adminService.assignRole(userId, roleId);
    }
    
    // 分配菜单
    @PostMapping("/assign-menu")
    public Result<Void> assignMenu(@RequestParam Long roleId, @RequestBody List<Long> menuIds) {
        return adminService.assignMenu(roleId, menuIds);
    }
    
    // 获取所有角色
    @GetMapping("/roles")
    public Result<List<Role>> getAllRoles() {
        return adminService.getAllRoles();
    }
    
    // 新增角色
    @PostMapping("/roles")
    public Result<Void> addRole(@RequestBody Role role) {
        return adminService.addRole(role);
    }
    
    // 更新角色
    @PutMapping("/roles/{id}")
    public Result<Void> updateRole(@PathVariable Long id, @RequestBody Role role) {
        return adminService.updateRole(id, role);
    }
    
    // 删除角色
    @DeleteMapping("/roles/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        return adminService.deleteRole(id);
    }
    
    // 获取单个角色信息
    @GetMapping("/roles/{id}")
    public Result<Role> getRoleById(@PathVariable Long id) {
        return adminService.getRoleById(id);
    }
    
    // 获取所有菜单
    @GetMapping("/menus")
    public Result<List<MenuVO>> getAllMenus() {
        return adminService.getAllMenus();
    }
    
    // 获取角色菜单
    @GetMapping("/role-menus/{roleId}")
    public Result<List<Long>> getRoleMenus(@PathVariable Long roleId) {
        return adminService.getRoleMenus(roleId);
    }
    
    // 获取组织树
    @PostMapping("/organizations/query")
    public Result<List<OrganizationVO>> getOrganizationTree(@RequestBody OrganizationQueryDTO queryDTO) {
        return adminService.getOrganizationTree(queryDTO);
    }
    
    // 新增组织
    @PostMapping("/organizations")
    public Result<Void> addOrganization(@RequestBody Organization organization) {
        return adminService.addOrganization(organization);
    }
    
    // 更新组织
    @PutMapping("/organizations/{id}")
    public Result<Void> updateOrganization(@PathVariable Long id, @RequestBody Organization organization) {
        return adminService.updateOrganization(id, organization);
    }
    
    // 删除组织
    @DeleteMapping("/organizations/{id}")
    public Result<Void> deleteOrganization(@PathVariable Long id) {
        return adminService.deleteOrganization(id);
    }
    
    // 获取单个组织信息
    @GetMapping("/organizations/{id}")
    public Result<Organization> getOrganizationById(@PathVariable Long id) {
        return adminService.getOrganizationById(id);
    }
    
    // 分配组织
    @PostMapping("/assign-organization")
    public Result<Void> assignOrganization(@RequestBody AssignOrganizationRequest request) {
        return adminService.assignOrganization(request);
    }
    
    // 获取用户组织
    @GetMapping("/user-organizations/{userId}")
    public Result<List<Long>> getUserOrganizations(@PathVariable Long userId) {
        return adminService.getUserOrganizations(userId);
    }
    
    // 根据层级获取组织
    @GetMapping("/organizations-by-level/{level}")
    public Result<List<OrganizationVO>> getOrganizationsByLevel(@PathVariable Integer level) {
        return adminService.getOrganizationsByLevel(level);
    }
    
    // 根据父级获取组织
    @GetMapping("/organizations-by-parent/{parentId}")
    public Result<List<OrganizationVO>> getOrganizationsByParent(@PathVariable Long parentId) {
        return adminService.getOrganizationsByParent(parentId);
    }
} 