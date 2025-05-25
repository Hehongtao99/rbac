package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.Result;
import com.example.entity.Menu;
import com.example.entity.Organization;
import com.example.entity.Role;
import com.example.entity.RoleMenu;
import com.example.entity.User;
import com.example.entity.UserRole;
import com.example.mapper.MenuMapper;
import com.example.mapper.OrganizationMapper;
import com.example.mapper.RoleMapper;
import com.example.mapper.RoleMenuMapper;
import com.example.mapper.UserMapper;
import com.example.mapper.UserRoleMapper;
import com.example.vo.MenuVO;
import com.example.vo.OrganizationVO;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.util.PasswordUtil;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private MenuMapper menuMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    
    @Autowired
    private OrganizationMapper organizationMapper;
    
    @Autowired
    private PasswordUtil passwordUtil;
    
    // 获取所有用户
    @GetMapping("/users")
    public Result<List<User>> getAllUsers() {
        try {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getStatus, 1);
            List<User> users = userMapper.selectList(wrapper);
            // 清空密码字段
            users.forEach(user -> user.setPassword(null));
            return Result.success(users);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 新增用户
    @PostMapping("/users")
    public Result<Void> addUser(@RequestBody User user) {
        try {
            // 检查用户名是否已存在
            LambdaQueryWrapper<User> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(User::getUsername, user.getUsername());
            User existUser = userMapper.selectOne(checkWrapper);
            if (existUser != null) {
                return Result.error("用户名已存在");
            }
            
            // 密码加密
            user.setPassword(passwordUtil.encode(user.getPassword()));
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            user.setStatus(1);
            
            userMapper.insert(user);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 更新用户
    @PutMapping("/users/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            // 检查用户是否存在
            User existUser = userMapper.selectById(id);
            if (existUser == null) {
                return Result.error("用户不存在");
            }
            
            // 检查用户名是否被其他用户使用
            LambdaQueryWrapper<User> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(User::getUsername, user.getUsername())
                    .ne(User::getId, id);
            User duplicateUser = userMapper.selectOne(checkWrapper);
            if (duplicateUser != null) {
                return Result.error("用户名已被其他用户使用");
            }
            
            // 更新用户信息
            user.setId(id);
            user.setUpdateTime(LocalDateTime.now());
            
            // 如果密码不为空，则更新密码
            if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                user.setPassword(passwordUtil.encode(user.getPassword()));
            } else {
                // 保持原密码
                user.setPassword(existUser.getPassword());
            }
            
            userMapper.updateById(user);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 删除用户
    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        try {
            // 检查用户是否存在
            User existUser = userMapper.selectById(id);
            if (existUser == null) {
                return Result.error("用户不存在");
            }
            
            // 软删除：设置状态为0
            existUser.setStatus(0);
            existUser.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(existUser);
            
            // 删除用户角色关联
            LambdaQueryWrapper<UserRole> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(UserRole::getUserId, id);
            userRoleMapper.delete(deleteWrapper);
            
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 获取单个用户信息
    @GetMapping("/users/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        try {
            User user = userMapper.selectById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 清空密码字段
            user.setPassword(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 分配用户角色
    @PostMapping("/assign-role")
    public Result<Void> assignRole(@RequestParam Long userId, @RequestParam Long roleId) {
        try {
            // 先删除用户现有角色
            LambdaQueryWrapper<UserRole> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(UserRole::getUserId, userId);
            userRoleMapper.delete(deleteWrapper);
            
            // 分配新角色
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
            
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 分配角色菜单权限
    @PostMapping("/assign-menu")
    public Result<Void> assignMenu(@RequestParam Long roleId, @RequestBody List<Long> menuIds) {
        try {
            // 先删除角色现有菜单权限
            LambdaQueryWrapper<RoleMenu> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(RoleMenu::getRoleId, roleId);
            roleMenuMapper.delete(deleteWrapper);
            
            // 分配新的菜单权限
            if (menuIds != null && !menuIds.isEmpty()) {
                List<RoleMenu> roleMenus = menuIds.stream().map(menuId -> {
                    RoleMenu roleMenu = new RoleMenu();
                    roleMenu.setRoleId(roleId);
                    roleMenu.setMenuId(menuId);
                    return roleMenu;
                }).collect(Collectors.toList());
                
                roleMenus.forEach(roleMenuMapper::insert);
            }
            
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 获取所有角色
    @GetMapping("/roles")
    public Result<List<Role>> getAllRoles() {
        try {
            LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Role::getStatus, 1)
                    .orderByAsc(Role::getId);
            List<Role> roles = roleMapper.selectList(wrapper);
            return Result.success(roles);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 新增角色
    @PostMapping("/roles")
    public Result<Void> addRole(@RequestBody Role role) {
        try {
            // 检查角色名称是否已存在
            LambdaQueryWrapper<Role> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(Role::getRoleName, role.getRoleName());
            Role existRole = roleMapper.selectOne(checkWrapper);
            if (existRole != null) {
                return Result.error("角色名称已存在");
            }
            
            // 检查角色编码是否已存在
            LambdaQueryWrapper<Role> codeWrapper = new LambdaQueryWrapper<>();
            codeWrapper.eq(Role::getRoleCode, role.getRoleCode());
            Role existCodeRole = roleMapper.selectOne(codeWrapper);
            if (existCodeRole != null) {
                return Result.error("角色编码已存在");
            }
            
            role.setCreateTime(LocalDateTime.now());
            role.setUpdateTime(LocalDateTime.now());
            role.setStatus(1);
            
            roleMapper.insert(role);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 更新角色
    @PutMapping("/roles/{id}")
    public Result<Void> updateRole(@PathVariable Long id, @RequestBody Role role) {
        try {
            // 检查角色是否存在
            Role existRole = roleMapper.selectById(id);
            if (existRole == null) {
                return Result.error("角色不存在");
            }
            
            // 检查角色名称是否被其他角色使用
            LambdaQueryWrapper<Role> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(Role::getRoleName, role.getRoleName())
                    .ne(Role::getId, id);
            Role duplicateRole = roleMapper.selectOne(checkWrapper);
            if (duplicateRole != null) {
                return Result.error("角色名称已被其他角色使用");
            }
            
            // 检查角色编码是否被其他角色使用
            LambdaQueryWrapper<Role> codeWrapper = new LambdaQueryWrapper<>();
            codeWrapper.eq(Role::getRoleCode, role.getRoleCode())
                    .ne(Role::getId, id);
            Role duplicateCodeRole = roleMapper.selectOne(codeWrapper);
            if (duplicateCodeRole != null) {
                return Result.error("角色编码已被其他角色使用");
            }
            
            // 更新角色信息
            role.setId(id);
            role.setUpdateTime(LocalDateTime.now());
            role.setCreateTime(existRole.getCreateTime()); // 保持原创建时间
            
            roleMapper.updateById(role);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 删除角色
    @DeleteMapping("/roles/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        try {
            // 检查角色是否存在
            Role existRole = roleMapper.selectById(id);
            if (existRole == null) {
                return Result.error("角色不存在");
            }
            
            // 检查是否有用户使用该角色
            LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
            userRoleWrapper.eq(UserRole::getRoleId, id);
            long userCount = userRoleMapper.selectCount(userRoleWrapper);
            if (userCount > 0) {
                return Result.error("该角色正在被用户使用，无法删除");
            }
            
            // 软删除：设置状态为0
            existRole.setStatus(0);
            existRole.setUpdateTime(LocalDateTime.now());
            roleMapper.updateById(existRole);
            
            // 删除角色菜单关联
            LambdaQueryWrapper<RoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
            roleMenuWrapper.eq(RoleMenu::getRoleId, id);
            roleMenuMapper.delete(roleMenuWrapper);
            
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 获取单个角色信息
    @GetMapping("/roles/{id}")
    public Result<Role> getRoleById(@PathVariable Long id) {
        try {
            Role role = roleMapper.selectById(id);
            if (role == null) {
                return Result.error("角色不存在");
            }
            
            return Result.success(role);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 获取所有菜单（树形结构）
    @GetMapping("/menus")
    public Result<List<MenuVO>> getAllMenus() {
        try {
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Menu::getStatus, 1)
                    .orderByAsc(Menu::getSort);
            List<Menu> menus = menuMapper.selectList(wrapper);
            
            // 转换为VO并构建树形结构
            List<MenuVO> menuVOs = menus.stream().map(menu -> {
                MenuVO menuVO = new MenuVO();
                menuVO.setId(menu.getId());
                menuVO.setMenuName(menu.getMenuName());
                menuVO.setMenuCode(menu.getMenuCode());
                menuVO.setParentId(menu.getParentId());
                menuVO.setMenuType(menu.getMenuType());
                menuVO.setPath(menu.getPath());
                menuVO.setComponent(menu.getComponent());
                menuVO.setIcon(menu.getIcon());
                menuVO.setSort(menu.getSort());
                return menuVO;
            }).collect(Collectors.toList());
            
            List<MenuVO> menuTree = buildMenuTree(menuVOs, 0L);
            return Result.success(menuTree);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 获取角色已分配的菜单ID列表
    @GetMapping("/role-menus/{roleId}")
    public Result<List<Long>> getRoleMenus(@PathVariable Long roleId) {
        try {
            LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RoleMenu::getRoleId, roleId);
            List<RoleMenu> roleMenus = roleMenuMapper.selectList(wrapper);
            
            List<Long> menuIds = roleMenus.stream()
                    .map(RoleMenu::getMenuId)
                    .collect(Collectors.toList());
            
            return Result.success(menuIds);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
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
    
    // ==================== 组织管理相关接口 ====================
    
    // 获取组织树
    @GetMapping("/organizations")
    public Result<List<OrganizationVO>> getOrganizationTree(
            @RequestParam(required = false) String orgName,
            @RequestParam(required = false) String orgCode,
            @RequestParam(required = false) String orgType) {
        try {
            LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Organization::getStatus, 1);
            
            // 添加查询条件
            if (orgName != null && !orgName.trim().isEmpty()) {
                wrapper.like(Organization::getOrgName, orgName.trim());
            }
            if (orgCode != null && !orgCode.trim().isEmpty()) {
                wrapper.like(Organization::getOrgCode, orgCode.trim());
            }
            if (orgType != null && !orgType.trim().isEmpty()) {
                wrapper.eq(Organization::getOrgType, orgType.trim());
            }
            
            wrapper.orderByAsc(Organization::getOrgLevel)
                    .orderByAsc(Organization::getSort);
            List<Organization> organizations = organizationMapper.selectList(wrapper);
            
            // 转换为VO并构建树形结构
            List<OrganizationVO> organizationVOs = organizations.stream().map(org -> {
                OrganizationVO orgVO = new OrganizationVO();
                orgVO.setId(org.getId());
                orgVO.setOrgName(org.getOrgName());
                orgVO.setOrgCode(org.getOrgCode());
                orgVO.setParentId(org.getParentId());
                orgVO.setOrgType(org.getOrgType());
                orgVO.setOrgLevel(org.getOrgLevel());
                orgVO.setSort(org.getSort());
                orgVO.setPhone(org.getPhone());
                orgVO.setEmail(org.getEmail());
                orgVO.setDescription(org.getDescription());
                orgVO.setStatus(org.getStatus());
                orgVO.setCreateTime(org.getCreateTime());
                orgVO.setUpdateTime(org.getUpdateTime());
                return orgVO;
            }).collect(Collectors.toList());
            
            // 如果有查询条件，返回扁平列表；否则返回树形结构
            if ((orgName != null && !orgName.trim().isEmpty()) || 
                (orgCode != null && !orgCode.trim().isEmpty()) || 
                (orgType != null && !orgType.trim().isEmpty())) {
                return Result.success(organizationVOs);
            } else {
                List<OrganizationVO> orgTree = buildOrganizationTree(organizationVOs, 0L);
                return Result.success(orgTree);
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 新增组织
    @PostMapping("/organizations")
    public Result<Void> addOrganization(@RequestBody Organization organization) {
        try {
            // 检查组织名称是否已存在
            LambdaQueryWrapper<Organization> nameWrapper = new LambdaQueryWrapper<>();
            nameWrapper.eq(Organization::getOrgName, organization.getOrgName());
            Organization existNameOrg = organizationMapper.selectOne(nameWrapper);
            if (existNameOrg != null) {
                return Result.error("组织名称已存在");
            }
            
            // 检查组织编码是否已存在
            LambdaQueryWrapper<Organization> codeWrapper = new LambdaQueryWrapper<>();
            codeWrapper.eq(Organization::getOrgCode, organization.getOrgCode());
            Organization existCodeOrg = organizationMapper.selectOne(codeWrapper);
            if (existCodeOrg != null) {
                return Result.error("组织编码已存在");
            }
            
            // 设置组织级别
            if (organization.getParentId() == null || organization.getParentId() == 0) {
                // 顶级组织（学院）
                organization.setParentId(0L);
                organization.setOrgLevel(1);
                organization.setOrgType("COLLEGE");
            } else {
                // 根据父级组织确定级别和类型
                Organization parentOrg = organizationMapper.selectById(organization.getParentId());
                if (parentOrg == null) {
                    return Result.error("父级组织不存在");
                }
                
                if (parentOrg.getOrgLevel() == 1) {
                    // 父级是学院，当前是专业
                    organization.setOrgLevel(2);
                    organization.setOrgType("MAJOR");
                } else if (parentOrg.getOrgLevel() == 2) {
                    // 父级是专业，当前是班级
                    organization.setOrgLevel(3);
                    organization.setOrgType("CLASS");
                } else {
                    return Result.error("不能在班级下继续创建组织");
                }
            }
            
            organization.setCreateTime(LocalDateTime.now());
            organization.setUpdateTime(LocalDateTime.now());
            organization.setStatus(1);
            
            organizationMapper.insert(organization);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 更新组织
    @PutMapping("/organizations/{id}")
    public Result<Void> updateOrganization(@PathVariable Long id, @RequestBody Organization organization) {
        try {
            // 检查组织是否存在
            Organization existOrg = organizationMapper.selectById(id);
            if (existOrg == null) {
                return Result.error("组织不存在");
            }
            
            // 检查组织名称是否被其他组织使用
            LambdaQueryWrapper<Organization> nameWrapper = new LambdaQueryWrapper<>();
            nameWrapper.eq(Organization::getOrgName, organization.getOrgName())
                    .ne(Organization::getId, id);
            Organization duplicateNameOrg = organizationMapper.selectOne(nameWrapper);
            if (duplicateNameOrg != null) {
                return Result.error("组织名称已被其他组织使用");
            }
            
            // 检查组织编码是否被其他组织使用
            LambdaQueryWrapper<Organization> codeWrapper = new LambdaQueryWrapper<>();
            codeWrapper.eq(Organization::getOrgCode, organization.getOrgCode())
                    .ne(Organization::getId, id);
            Organization duplicateCodeOrg = organizationMapper.selectOne(codeWrapper);
            if (duplicateCodeOrg != null) {
                return Result.error("组织编码已被其他组织使用");
            }
            
            // 更新组织信息
            organization.setId(id);
            organization.setUpdateTime(LocalDateTime.now());
            organization.setCreateTime(existOrg.getCreateTime()); // 保持原创建时间
            organization.setParentId(existOrg.getParentId()); // 不允许修改父级
            organization.setOrgLevel(existOrg.getOrgLevel()); // 不允许修改级别
            organization.setOrgType(existOrg.getOrgType()); // 不允许修改类型
            
            organizationMapper.updateById(organization);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 删除组织
    @DeleteMapping("/organizations/{id}")
    public Result<Void> deleteOrganization(@PathVariable Long id) {
        try {
            // 检查组织是否存在
            Organization existOrg = organizationMapper.selectById(id);
            if (existOrg == null) {
                return Result.error("组织不存在");
            }
            
            // 检查是否有子组织
            LambdaQueryWrapper<Organization> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.eq(Organization::getParentId, id)
                    .eq(Organization::getStatus, 1);
            long childCount = organizationMapper.selectCount(childWrapper);
            if (childCount > 0) {
                return Result.error("该组织下还有子组织，无法删除");
            }
            
            // 软删除：设置状态为0
            existOrg.setStatus(0);
            existOrg.setUpdateTime(LocalDateTime.now());
            organizationMapper.updateById(existOrg);
            
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 获取单个组织信息
    @GetMapping("/organizations/{id}")
    public Result<Organization> getOrganizationById(@PathVariable Long id) {
        try {
            Organization organization = organizationMapper.selectById(id);
            if (organization == null) {
                return Result.error("组织不存在");
            }
            
            return Result.success(organization);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 构建组织树
    private List<OrganizationVO> buildOrganizationTree(List<OrganizationVO> organizations, Long parentId) {
        List<OrganizationVO> tree = new ArrayList<>();
        
        for (OrganizationVO org : organizations) {
            if (parentId.equals(org.getParentId())) {
                List<OrganizationVO> children = buildOrganizationTree(organizations, org.getId());
                org.setChildren(children);
                tree.add(org);
            }
        }
        
        return tree;
    }
} 