package com.example.controller;

import com.example.common.Result;
import com.example.entity.Menu;
import com.example.entity.Organization;
import com.example.entity.Role;
import com.example.entity.RoleMenu;
import com.example.entity.User;
import com.example.entity.UserOrganization;
import com.example.entity.UserRole;
import com.example.mapper.MenuMapper;
import com.example.mapper.OrganizationMapper;
import com.example.mapper.RoleMapper;
import com.example.mapper.RoleMenuMapper;
import com.example.mapper.UserMapper;
import com.example.mapper.UserOrganizationMapper;
import com.example.mapper.UserRoleMapper;
import com.example.dto.AssignOrganizationRequest;
import com.example.vo.MenuVO;
import com.example.vo.OrganizationVO;
import com.example.vo.UserWithRoleVO;
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
    private UserOrganizationMapper userOrganizationMapper;
    
    @Autowired
    private PasswordUtil passwordUtil;
    
    // 获取所有用户
    @GetMapping("/users")
    public Result<List<UserWithRoleVO>> getAllUsers() {
        try {
            List<User> users = userMapper.selectByStatus(1);
            
            List<UserWithRoleVO> userWithRoleVOs = users.stream().map(user -> {
                UserWithRoleVO userVO = new UserWithRoleVO();
                userVO.setId(user.getId());
                userVO.setUsername(user.getUsername());
                userVO.setNickname(user.getNickname());
                userVO.setEmail(user.getEmail());
                userVO.setPhone(user.getPhone());
                userVO.setStatus(user.getStatus());
                userVO.setCreateTime(user.getCreateTime());
                userVO.setUpdateTime(user.getUpdateTime());
                
                // 获取用户角色信息
                List<UserRole> userRoles = userRoleMapper.selectByUserId(user.getId());
                
                List<String> roleNames = new ArrayList<>();
                List<Long> roleIds = new ArrayList<>();
                
                for (UserRole userRole : userRoles) {
                    Role role = roleMapper.selectById(userRole.getRoleId());
                    if (role != null) {
                        roleNames.add(role.getRoleName());
                        roleIds.add(role.getId());
                    }
                }
                
                userVO.setRoleNames(roleNames);
                userVO.setRoleIds(roleIds);
                
                return userVO;
            }).collect(Collectors.toList());
            
            return Result.success(userWithRoleVOs);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 新增用户
    @PostMapping("/users")
    public Result<Void> addUser(@RequestBody User user) {
        try {
            // 检查用户名是否已存在
            User existUser = userMapper.selectByUsername(user.getUsername());
            if (existUser != null) {
                return Result.error("用户名已存在");
            }
            
            // 密码加密
            user.setPassword(passwordUtil.encode(user.getPassword()));
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            user.setStatus(1);
            user.setDeleted(0);
            
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
            User duplicateUser = userMapper.selectByUsernameExcludeId(user.getUsername(), id);
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
            userRoleMapper.deleteByUserId(id);
            
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
    
    // 获取用户当前角色
    @GetMapping("/user-roles/{userId}")
    public Result<List<Long>> getUserRoles(@PathVariable Long userId) {
        try {
            List<UserRole> userRoles = userRoleMapper.selectByUserId(userId);
            
            List<Long> roleIds = userRoles.stream()
                    .map(UserRole::getRoleId)
                    .collect(Collectors.toList());
            
            return Result.success(roleIds);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 分配用户角色
    @PostMapping("/assign-role")
    public Result<Void> assignRole(@RequestParam Long userId, @RequestParam Long roleId) {
        try {
            // 先删除用户现有角色
            userRoleMapper.deleteByUserId(userId);
            
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
    
    // 分配菜单权限
    @PostMapping("/assign-menu")
    public Result<Void> assignMenu(@RequestParam Long roleId, @RequestBody List<Long> menuIds) {
        try {
            // 先删除角色现有菜单权限
            roleMenuMapper.deleteByRoleId(roleId);
            
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
            List<Role> roles = roleMapper.selectByStatus(1);
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
            Role existRole = roleMapper.selectByRoleName(role.getRoleName());
            if (existRole != null) {
                return Result.error("角色名称已存在");
            }
            
            // 检查角色编码是否已存在
            Role existCodeRole = roleMapper.selectByRoleCode(role.getRoleCode());
            if (existCodeRole != null) {
                return Result.error("角色编码已存在");
            }
            
            role.setCreateTime(LocalDateTime.now());
            role.setUpdateTime(LocalDateTime.now());
            role.setStatus(1);
            role.setDeleted(0);
            
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
            Role duplicateRole = roleMapper.selectByRoleNameExcludeId(role.getRoleName(), id);
            if (duplicateRole != null) {
                return Result.error("角色名称已被其他角色使用");
            }
            
            // 检查角色编码是否被其他角色使用
            Role duplicateCodeRole = roleMapper.selectByRoleCodeExcludeId(role.getRoleCode(), id);
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
            UserRole queryUserRole = new UserRole();
            queryUserRole.setRoleId(id);
            long userCount = userRoleMapper.selectCount(queryUserRole);
            if (userCount > 0) {
                return Result.error("该角色正在被用户使用，无法删除");
            }
            
            // 软删除：设置状态为0
            existRole.setStatus(0);
            existRole.setUpdateTime(LocalDateTime.now());
            roleMapper.updateById(existRole);
            
            // 删除角色菜单关联
            roleMenuMapper.deleteByRoleId(id);
            
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
            List<Menu> menus = menuMapper.selectByTypeAndStatus("MENU", 1);
            
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
            List<RoleMenu> roleMenus = roleMenuMapper.selectByRoleId(roleId);
            
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
            List<Organization> organizations;
            
            // 根据查询条件决定使用哪个查询方法
            if ((orgName != null && !orgName.trim().isEmpty()) || 
                (orgCode != null && !orgCode.trim().isEmpty()) || 
                (orgType != null && !orgType.trim().isEmpty())) {
                // 有查询条件，使用条件查询
                organizations = organizationMapper.selectByConditions(
                    orgName != null ? orgName.trim() : null,
                    orgCode != null ? orgCode.trim() : null,
                    orgType != null ? orgType.trim() : null,
                    1
                );
            } else {
                // 无查询条件，查询所有启用的组织
                organizations = organizationMapper.selectByStatus(1);
            }
            
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
            Organization existNameOrg = organizationMapper.selectByOrgName(organization.getOrgName());
            if (existNameOrg != null) {
                return Result.error("组织名称已存在");
            }
            
            // 检查组织编码是否已存在
            Organization existCodeOrg = organizationMapper.selectByOrgCode(organization.getOrgCode());
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
            Organization duplicateNameOrg = organizationMapper.selectByOrgNameExcludeId(organization.getOrgName(), id);
            if (duplicateNameOrg != null) {
                return Result.error("组织名称已被其他组织使用");
            }
            
            // 检查组织编码是否被其他组织使用
            Organization duplicateCodeOrg = organizationMapper.selectByOrgCodeExcludeId(organization.getOrgCode(), id);
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
            Organization queryOrg = new Organization();
            queryOrg.setParentId(id);
            queryOrg.setStatus(1);
            long childCount = organizationMapper.selectCount(queryOrg);
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
    
    // ==================== 用户组织分配相关接口 ====================
    
    // 分配用户组织（支持学院、专业、班级层级分配）
    @PostMapping("/assign-organization")
    public Result<Void> assignOrganization(@RequestBody AssignOrganizationRequest request) {
        Long userId = request.getUserId();
        Long collegeId = request.getCollegeId();
        Long majorId = request.getMajorId();
        List<Long> classIds = request.getClassIds();
        try {
            // 获取用户角色信息来判断分配规则
            List<UserRole> userRoles = userRoleMapper.selectByUserId(userId);
            
            if (userRoles.isEmpty()) {
                return Result.error("用户未分配角色");
            }
            
            // 获取角色编码
            List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
            List<Role> roles = roleMapper.selectByIds(roleIds);
            
            boolean isStudent = roles.stream().anyMatch(role -> "STUDENT".equals(role.getRoleCode()));
            boolean isTeacher = roles.stream().anyMatch(role -> "TEACHER".equals(role.getRoleCode()));
            
            // 构建要分配的组织ID列表
            List<Long> organizationIds = new ArrayList<>();
            
            if (isStudent) {
                // 学生必须完整分配：学院 + 专业 + 班级
                if (collegeId == null || majorId == null || classIds == null || classIds.isEmpty()) {
                    return Result.error("学生必须完整分配学院、专业和班级");
                }
                
                if (classIds.size() > 1) {
                    return Result.error("学生只能分配一个班级");
                }
                
                // 验证层级关系
                Organization college = organizationMapper.selectById(collegeId);
                Organization major = organizationMapper.selectById(majorId);
                Organization classOrg = organizationMapper.selectById(classIds.get(0));
                
                if (college == null || college.getOrgLevel() != 1) {
                    return Result.error("请选择正确的学院");
                }
                if (major == null || major.getOrgLevel() != 2 || !major.getParentId().equals(collegeId)) {
                    return Result.error("专业必须属于所选学院");
                }
                if (classOrg == null || classOrg.getOrgLevel() != 3 || !classOrg.getParentId().equals(majorId)) {
                    return Result.error("班级必须属于所选专业");
                }
                
                // 学生只分配到班级（因为班级包含了完整的层级信息）
                organizationIds.add(classIds.get(0));
                
            } else if (isTeacher) {
                // 教师可以分配多个班级，但至少要有学院和专业信息
                if (collegeId == null || majorId == null) {
                    return Result.error("教师至少需要分配学院和专业");
                }
                
                // 验证学院和专业的关系
                Organization college = organizationMapper.selectById(collegeId);
                Organization major = organizationMapper.selectById(majorId);
                
                if (college == null || college.getOrgLevel() != 1) {
                    return Result.error("请选择正确的学院");
                }
                if (major == null || major.getOrgLevel() != 2 || !major.getParentId().equals(collegeId)) {
                    return Result.error("专业必须属于所选学院");
                }
                
                // 如果指定了班级，验证所有班级关系并添加到分配列表
                if (classIds != null && !classIds.isEmpty()) {
                    for (Long classId : classIds) {
                        Organization classOrg = organizationMapper.selectById(classId);
                        if (classOrg == null || classOrg.getOrgLevel() != 3 || !classOrg.getParentId().equals(majorId)) {
                            return Result.error("所有班级必须属于所选专业");
                        }
                        organizationIds.add(classId);
                    }
                } else {
                    // 如果没有指定班级，则分配到专业级别
                    organizationIds.add(majorId);
                }
            } else {
                // 其他角色（如管理员等）的分配逻辑
                if (collegeId == null) {
                    return Result.error("请至少选择学院");
                }
                
                // 验证学院
                Organization college = organizationMapper.selectById(collegeId);
                if (college == null || college.getOrgLevel() != 1) {
                    return Result.error("请选择正确的学院");
                }
                
                if (majorId != null) {
                    // 验证专业
                    Organization major = organizationMapper.selectById(majorId);
                    if (major == null || major.getOrgLevel() != 2 || !major.getParentId().equals(collegeId)) {
                        return Result.error("专业必须属于所选学院");
                    }
                    
                    if (classIds != null && !classIds.isEmpty()) {
                        // 如果指定了班级，验证班级关系并分配到班级
                        if (classIds.size() > 1) {
                            return Result.error("非教师用户只能分配一个班级");
                        }
                        
                        Organization classOrg = organizationMapper.selectById(classIds.get(0));
                        if (classOrg == null || classOrg.getOrgLevel() != 3 || !classOrg.getParentId().equals(majorId)) {
                            return Result.error("班级必须属于所选专业");
                        }
                        organizationIds.add(classIds.get(0));
                    } else {
                        // 没有指定班级，分配到专业级别
                        organizationIds.add(majorId);
                    }
                } else {
                    // 只选择了学院，分配到学院级别
                    organizationIds.add(collegeId);
                }
            }
            
            // 先删除用户现有组织关联
            userOrganizationMapper.deleteByUserId(userId);
            
            // 分配新的组织关联
            if (!organizationIds.isEmpty()) {
                List<UserOrganization> userOrganizations = organizationIds.stream().map(orgId -> {
                    UserOrganization userOrg = new UserOrganization();
                    userOrg.setUserId(userId);
                    userOrg.setOrganizationId(orgId);
                    userOrg.setCreateTime(LocalDateTime.now());
                    userOrg.setUpdateTime(LocalDateTime.now());
                    return userOrg;
                }).collect(Collectors.toList());
                
                userOrganizations.forEach(userOrganizationMapper::insert);
            }
            
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 获取用户已分配的组织ID列表
    @GetMapping("/user-organizations/{userId}")
    public Result<List<Long>> getUserOrganizations(@PathVariable Long userId) {
        try {
            List<UserOrganization> userOrganizations = userOrganizationMapper.selectByUserId(userId);
            
            List<Long> organizationIds = userOrganizations.stream()
                    .map(UserOrganization::getOrganizationId)
                    .collect(Collectors.toList());
            
            return Result.success(organizationIds);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 根据级别获取组织列表
    @GetMapping("/organizations-by-level/{level}")
    public Result<List<OrganizationVO>> getOrganizationsByLevel(@PathVariable Integer level) {
        try {
            List<Organization> organizations = organizationMapper.selectByLevel(level);
            
            List<OrganizationVO> organizationVOs = organizations.stream().map(org -> {
                OrganizationVO orgVO = new OrganizationVO();
                orgVO.setId(org.getId());
                orgVO.setOrgName(org.getOrgName());
                orgVO.setOrgCode(org.getOrgCode());
                orgVO.setParentId(org.getParentId());
                orgVO.setOrgType(org.getOrgType());
                orgVO.setOrgLevel(org.getOrgLevel());
                orgVO.setSort(org.getSort());
                
                return orgVO;
            }).collect(Collectors.toList());
            
            return Result.success(organizationVOs);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 根据父级ID获取子组织列表
    @GetMapping("/organizations-by-parent/{parentId}")
    public Result<List<OrganizationVO>> getOrganizationsByParent(@PathVariable Long parentId) {
        try {
            List<Organization> organizations = organizationMapper.selectByParentIdAndStatus(parentId, 1);
            
            List<OrganizationVO> organizationVOs = organizations.stream().map(org -> {
                OrganizationVO orgVO = new OrganizationVO();
                orgVO.setId(org.getId());
                orgVO.setOrgName(org.getOrgName());
                orgVO.setOrgCode(org.getOrgCode());
                orgVO.setParentId(org.getParentId());
                orgVO.setOrgType(org.getOrgType());
                orgVO.setOrgLevel(org.getOrgLevel());
                orgVO.setSort(org.getSort());
                
                return orgVO;
            }).collect(Collectors.toList());
            
            return Result.success(organizationVOs);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 构建完整组织路径
    private void buildFullOrgPath(Organization classOrg, OrganizationVO orgVO) {
        StringBuilder fullPath = new StringBuilder();
        Organization current = classOrg;
        
        // 获取专业信息
        if (current.getParentId() != null && current.getParentId() != 0) {
            Organization major = organizationMapper.selectById(current.getParentId());
            if (major != null) {
                // 获取学院信息
                if (major.getParentId() != null && major.getParentId() != 0) {
                    Organization college = organizationMapper.selectById(major.getParentId());
                    if (college != null) {
                        fullPath.append(college.getOrgName()).append(" - ");
                    }
                }
                fullPath.append(major.getOrgName()).append(" - ");
            }
        }
        fullPath.append(classOrg.getOrgName());
        
        // 将完整路径设置到orgName中显示
        orgVO.setOrgName(fullPath.toString());
    }
} 