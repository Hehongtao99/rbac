package com.example.service.impl;

import com.example.common.Result;
import com.example.dto.AssignOrganizationRequest;
import com.example.dto.OrganizationQueryDTO;
import com.example.entity.*;
import com.example.mapper.*;
import com.example.service.AdminService;
import com.example.util.PasswordUtil;
import com.example.util.UserContextUtil;
import com.example.vo.MenuVO;
import com.example.vo.OrganizationVO;
import com.example.vo.UserWithRoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {
    
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
    
    @Autowired
    private StudentMapper studentMapper;
    
    @Autowired
    private TeacherMapper teacherMapper;
    
    @Override
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
    
    @Override
    public Result<Void> addUser(User user) {
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
    
    @Override
    public Result<Void> updateUser(Long id, User user) {
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
    
    @Override
    public Result<Void> deleteUser(Long id) {
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
    
    @Override
    public Result<User> getUserById(Long id) {
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
    
    @Override
    public Result<List<Long>> getUserRoles(Long userId) {
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
    
    @Override
    @Transactional
    public Result<Void> assignRole(Long userId, Long roleId) {
        try {
            // 检查用户是否存在
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 检查角色是否存在
            Role role = roleMapper.selectById(roleId);
            if (role == null) {
                return Result.error("角色不存在");
            }
            
            // 获取用户当前的所有角色
            List<UserRole> existUserRoles = userRoleMapper.selectByUserId(userId);
            
            // 检查用户是否已经拥有该角色
            boolean hasRole = existUserRoles.stream().anyMatch(ur -> ur.getRoleId().equals(roleId));
            if (hasRole) {
                return Result.error("用户已拥有该角色");
            }
            
            // 在角色切换前，先删除原有角色对应的学生或教师记录
            for (UserRole existUserRole : existUserRoles) {
                Role existRole = roleMapper.selectById(existUserRole.getRoleId());
                if (existRole != null) {
                    String existRoleCode = existRole.getRoleCode();
                    String existRoleName = existRole.getRoleName();
                    
                    // 如果原来是学生角色，删除学生记录
                    if ("STUDENT".equals(existRoleCode) || "学生".equals(existRoleName)) {
                        Student existStudent = studentMapper.selectByUserId(userId);
                        if (existStudent != null && existStudent.getDeleted() == 0) {
                            // 软删除学生记录
                            existStudent.setDeleted(1);
                            existStudent.setUpdateTime(LocalDateTime.now());
                            studentMapper.updateById(existStudent);
                        }
                    }
                    // 如果原来是教师角色，删除教师记录
                    else if ("TEACHER".equals(existRoleCode) || "教师".equals(existRoleName)) {
                        Teacher existTeacher = teacherMapper.selectByUserId(userId);
                        if (existTeacher != null && existTeacher.getDeleted() == 0) {
                            // 软删除教师记录
                            existTeacher.setDeleted(1);
                            existTeacher.setUpdateTime(LocalDateTime.now());
                            teacherMapper.updateById(existTeacher);
                        }
                    }
                }
            }
            
            // 先删除用户的所有旧角色（软删除）
            if (!existUserRoles.isEmpty()) {
                for (UserRole existUserRole : existUserRoles) {
                    existUserRole.setDeleted(1);
                    userRoleMapper.updateById(existUserRole);
                }
            }
            
            // 分配新角色
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setCreateTime(LocalDateTime.now());
            userRole.setDeleted(0);
            userRoleMapper.insert(userRole);
            
            // 根据新角色类型自动创建对应的学生或教师记录
            String roleCode = role.getRoleCode();
            String roleName = role.getRoleName();
            
            if ("STUDENT".equals(roleCode) || "学生".equals(roleName)) {
                // 分配学生角色，检查是否已存在学生记录
                Student existStudent = studentMapper.selectByUserId(userId);
                if (existStudent == null) {
                    // 创建新的学生记录
                    Student student = new Student();
                    student.setUserId(userId);
                    student.setStudentNo(generateStudentNo()); // 生成学号
                    student.setName(user.getNickname()); // 使用用户昵称作为姓名
                    student.setPhone(user.getPhone());
                    student.setEmail(user.getEmail());
                    student.setStatus(1);
                    student.setCreateTime(LocalDateTime.now());
                    student.setUpdateTime(LocalDateTime.now());
                    student.setDeleted(0);
                    
                    studentMapper.insert(student);
                } else {
                    // 如果记录已存在（无论是否删除），重新激活并更新信息
                    existStudent.setDeleted(0);
                    existStudent.setStatus(1);
                    existStudent.setName(user.getNickname()); // 更新姓名
                    existStudent.setPhone(user.getPhone()); // 更新手机号
                    existStudent.setEmail(user.getEmail()); // 更新邮箱
                    existStudent.setUpdateTime(LocalDateTime.now());
                    
                    studentMapper.updateById(existStudent);
                }
            } else if ("TEACHER".equals(roleCode) || "教师".equals(roleName)) {
                // 分配教师角色，检查是否已存在教师记录
                Teacher existTeacher = teacherMapper.selectByUserId(userId);
                if (existTeacher == null) {
                    // 创建新的教师记录
                    Teacher teacher = new Teacher();
                    teacher.setUserId(userId);
                    teacher.setTeacherNo(generateTeacherNo()); // 生成教师编号
                    teacher.setName(user.getNickname()); // 使用用户昵称作为姓名
                    teacher.setPhone(user.getPhone());
                    teacher.setEmail(user.getEmail());
                    teacher.setStatus(1);
                    teacher.setCreateTime(LocalDateTime.now());
                    teacher.setUpdateTime(LocalDateTime.now());
                    teacher.setDeleted(0);
                    
                    teacherMapper.insert(teacher);
                } else {
                    // 如果记录已存在（无论是否删除），重新激活并更新信息
                    existTeacher.setDeleted(0);
                    existTeacher.setStatus(1);
                    existTeacher.setName(user.getNickname()); // 更新姓名
                    existTeacher.setPhone(user.getPhone()); // 更新手机号
                    existTeacher.setEmail(user.getEmail()); // 更新邮箱
                    existTeacher.setUpdateTime(LocalDateTime.now());
                    
                    teacherMapper.updateById(existTeacher);
                }
            }
            
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 生成学号
     */
    private String generateStudentNo() {
        // 获取当前年份
        int currentYear = LocalDateTime.now().getYear();
        
        // 查询当年最大的学号
        String maxStudentNo = studentMapper.selectMaxStudentNoByYear(String.valueOf(currentYear));
        
        int nextNumber = 1;
        if (maxStudentNo != null && maxStudentNo.length() >= 7) {
            // 提取序号部分（后3位）
            String numberPart = maxStudentNo.substring(4);
            try {
                nextNumber = Integer.parseInt(numberPart) + 1;
            } catch (NumberFormatException e) {
                nextNumber = 1;
            }
        }
        
        // 格式：S + 年份 + 3位序号，如 S2024001
        return String.format("S%d%03d", currentYear, nextNumber);
    }
    
    /**
     * 生成教师编号
     */
    private String generateTeacherNo() {
        // 查询最大的教师编号
        String maxTeacherNo = teacherMapper.selectMaxTeacherNo();
        
        int nextNumber = 1;
        if (maxTeacherNo != null && maxTeacherNo.length() >= 4) {
            // 提取序号部分（T后面的数字）
            String numberPart = maxTeacherNo.substring(1);
            try {
                nextNumber = Integer.parseInt(numberPart) + 1;
            } catch (NumberFormatException e) {
                nextNumber = 1;
            }
        }
        
        // 格式：T + 3位序号，如 T001
        return String.format("T%03d", nextNumber);
    }
    
    @Override
    public Result<List<Role>> getAllRoles() {
        try {
            List<Role> roles = roleMapper.selectByStatus(1);
            return Result.success(roles);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @Override
    public Result<Void> addRole(Role role) {
        try {
            // 检查角色名是否已存在
            Role existRole = roleMapper.selectByRoleName(role.getRoleName());
            if (existRole != null) {
                return Result.error("角色名已存在");
            }
            
            // 检查角色代码是否已存在
            Role existRoleCode = roleMapper.selectByRoleCode(role.getRoleCode());
            if (existRoleCode != null) {
                return Result.error("角色代码已存在");
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
    
    @Override
    public Result<Void> updateRole(Long id, Role role) {
        try {
            // 检查角色是否存在
            Role existRole = roleMapper.selectById(id);
            if (existRole == null) {
                return Result.error("角色不存在");
            }
            
            // 检查角色名是否被其他角色使用
            Role duplicateRoleName = roleMapper.selectByRoleNameExcludeId(role.getRoleName(), id);
            if (duplicateRoleName != null) {
                return Result.error("角色名已被其他角色使用");
            }
            
            // 检查角色代码是否被其他角色使用
            Role duplicateRoleCode = roleMapper.selectByRoleCodeExcludeId(role.getRoleCode(), id);
            if (duplicateRoleCode != null) {
                return Result.error("角色代码已被其他角色使用");
            }
            
            // 更新角色信息
            role.setId(id);
            role.setUpdateTime(LocalDateTime.now());
            
            roleMapper.updateById(role);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @Override
    public Result<Void> deleteRole(Long id) {
        try {
            // 检查角色是否存在
            Role existRole = roleMapper.selectById(id);
            if (existRole == null) {
                return Result.error("角色不存在");
            }
            
            // 检查是否有用户使用该角色
            List<UserRole> userRoles = userRoleMapper.selectByRoleId(id);
            if (!userRoles.isEmpty()) {
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
    
    @Override
    public Result<Role> getRoleById(Long id) {
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
    
    @Override
    public Result<List<MenuVO>> getAllMenus() {
        try {
            // 获取所有启用的菜单和按钮权限
            List<Menu> menus = menuMapper.selectByTypeAndStatus(null, 1);
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
            
            // 构建菜单树
            List<MenuVO> menuTree = buildMenuTree(menuVOs, 0L);
            return Result.success(menuTree);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @Override
    public Result<List<Long>> getRoleMenus(Long roleId) {
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
    
    @Override
    @Transactional
    public Result<Void> assignMenu(Long roleId, List<Long> menuIds) {
        try {
            // 检查角色是否存在
            Role role = roleMapper.selectById(roleId);
            if (role == null) {
                return Result.error("角色不存在");
            }
            
            // 删除原有的角色菜单关联
            roleMenuMapper.deleteByRoleId(roleId);
            
            // 添加新的角色菜单关联
            if (menuIds != null && !menuIds.isEmpty()) {
                for (Long menuId : menuIds) {
                    RoleMenu roleMenu = new RoleMenu();
                    roleMenu.setRoleId(roleId);
                    roleMenu.setMenuId(menuId);
                    roleMenu.setCreateTime(LocalDateTime.now());
                    roleMenu.setDeleted(0);
                    roleMenuMapper.insert(roleMenu);
                }
            }
            
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @Override
    public Result<List<OrganizationVO>> getOrganizationTree(OrganizationQueryDTO queryDTO) {
        try {
            List<Organization> organizations;
            
            // 根据查询条件决定使用哪个查询方法
            if ((queryDTO.getOrgName() != null && !queryDTO.getOrgName().trim().isEmpty()) || 
                (queryDTO.getOrgCode() != null && !queryDTO.getOrgCode().trim().isEmpty()) || 
                (queryDTO.getOrgType() != null && !queryDTO.getOrgType().trim().isEmpty())) {
                // 有查询条件，使用条件查询
                organizations = organizationMapper.selectByConditions(
                    queryDTO.getOrgName() != null ? queryDTO.getOrgName().trim() : null,
                    queryDTO.getOrgCode() != null ? queryDTO.getOrgCode().trim() : null,
                    queryDTO.getOrgType() != null ? queryDTO.getOrgType().trim() : null,
                    1
                );
            } else {
                // 无查询条件，查询所有启用的组织
                organizations = organizationMapper.selectByStatus(1);
            }
            
            List<OrganizationVO> organizationVOs = organizations.stream().map(org -> {
                OrganizationVO orgVO = new OrganizationVO();
                orgVO.setId(org.getId());
                orgVO.setOrgName(org.getOrgName());
                orgVO.setOrgCode(org.getOrgCode());
                orgVO.setOrgType(org.getOrgType());
                orgVO.setParentId(org.getParentId());
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
            if ((queryDTO.getOrgName() != null && !queryDTO.getOrgName().trim().isEmpty()) || 
                (queryDTO.getOrgCode() != null && !queryDTO.getOrgCode().trim().isEmpty()) || 
                (queryDTO.getOrgType() != null && !queryDTO.getOrgType().trim().isEmpty())) {
                return Result.success(organizationVOs);
            } else {
                List<OrganizationVO> orgTree = buildOrganizationTree(organizationVOs, 0L);
                return Result.success(orgTree);
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @Override
    public Result<Void> addOrganization(Organization organization) {
        try {
            // 检查组织名称是否已存在
            Organization existOrgName = organizationMapper.selectByOrgName(organization.getOrgName());
            if (existOrgName != null) {
                return Result.error("组织名称已存在");
            }
            
            // 检查组织代码是否已存在
            Organization existOrgCode = organizationMapper.selectByOrgCode(organization.getOrgCode());
            if (existOrgCode != null) {
                return Result.error("组织代码已存在");
            }
            
            // 如果有父组织，检查父组织是否存在并设置层级
            if (organization.getParentId() != null) {
                Organization parentOrg = organizationMapper.selectById(organization.getParentId());
                if (parentOrg == null) {
                    return Result.error("父组织不存在");
                }
                organization.setOrgLevel(parentOrg.getOrgLevel() + 1);
            } else {
                organization.setOrgLevel(1);
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
    
    @Override
    public Result<Void> updateOrganization(Long id, Organization organization) {
        try {
            // 检查组织是否存在
            Organization existOrg = organizationMapper.selectById(id);
            if (existOrg == null) {
                return Result.error("组织不存在");
            }
            
            // 检查组织名称是否被其他组织使用
            Organization duplicateOrgName = organizationMapper.selectByOrgNameExcludeId(organization.getOrgName(), id);
            if (duplicateOrgName != null) {
                return Result.error("组织名称已被其他组织使用");
            }
            
            // 检查组织代码是否被其他组织使用
            Organization duplicateOrgCode = organizationMapper.selectByOrgCodeExcludeId(organization.getOrgCode(), id);
            if (duplicateOrgCode != null) {
                return Result.error("组织代码已被其他组织使用");
            }
            
            // 更新组织信息
            organization.setId(id);
            organization.setUpdateTime(LocalDateTime.now());
            
            organizationMapper.updateById(organization);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @Override
    public Result<Void> deleteOrganization(Long id) {
        try {
            // 检查组织是否存在
            Organization existOrg = organizationMapper.selectById(id);
            if (existOrg == null) {
                return Result.error("组织不存在");
            }
            
            // 检查是否有子组织
            List<Organization> childOrgs = organizationMapper.selectByParentId(id);
            if (!childOrgs.isEmpty()) {
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
    
    @Override
    public Result<Organization> getOrganizationById(Long id) {
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
    
    @Override
    @Transactional
    public Result<Void> assignOrganization(AssignOrganizationRequest request) {
        try {
            Long userId = request.getUserId();
            
            // 检查用户是否存在
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 删除用户原有的组织关联
            userOrganizationMapper.deleteByUserId(userId);
            
            LocalDateTime now = LocalDateTime.now();
            
            // 存储组织信息，用于后续更新学生或教师表
            Organization college = null;
            Organization major = null;
            Organization classOrg = null;
            
            // 添加新的用户组织关联
            if (request.getCollegeId() != null) {
                // 检查学院是否存在
                college = organizationMapper.selectById(request.getCollegeId());
                if (college == null) {
                    return Result.error("学院不存在");
                }
                
                UserOrganization userOrg = new UserOrganization();
                userOrg.setUserId(userId);
                userOrg.setOrganizationId(request.getCollegeId());
                userOrg.setCreateTime(now);
                userOrg.setUpdateTime(now);
                userOrganizationMapper.insert(userOrg);
            }
            
            if (request.getMajorId() != null) {
                // 检查专业是否存在
                major = organizationMapper.selectById(request.getMajorId());
                if (major == null) {
                    return Result.error("专业不存在");
                }
                
                UserOrganization userOrg = new UserOrganization();
                userOrg.setUserId(userId);
                userOrg.setOrganizationId(request.getMajorId());
                userOrg.setCreateTime(now);
                userOrg.setUpdateTime(now);
                userOrganizationMapper.insert(userOrg);
            }
            
            if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
                for (Long classId : request.getClassIds()) {
                    // 检查班级是否存在
                    Organization classOrgTemp = organizationMapper.selectById(classId);
                    if (classOrgTemp == null) {
                        return Result.error("班级ID " + classId + " 不存在");
                    }
                    
                    // 对于学生，只取第一个班级
                    if (classOrg == null) {
                        classOrg = classOrgTemp;
                    }
                    
                    UserOrganization userOrg = new UserOrganization();
                    userOrg.setUserId(userId);
                    userOrg.setOrganizationId(classId);
                    userOrg.setCreateTime(now);
                    userOrg.setUpdateTime(now);
                    userOrganizationMapper.insert(userOrg);
                }
            }
            
            // 获取用户角色，判断是学生还是教师
            List<UserRole> userRoles = userRoleMapper.selectByUserId(userId);
            boolean isStudent = false;
            boolean isTeacher = false;
            
            for (UserRole userRole : userRoles) {
                Role role = roleMapper.selectById(userRole.getRoleId());
                if (role != null) {
                    if ("学生".equals(role.getRoleName())) {
                        isStudent = true;
                    } else if ("教师".equals(role.getRoleName())) {
                        isTeacher = true;
                    }
                }
            }
            
            // 如果是学生，更新学生表中的组织信息
            if (isStudent) {
                updateStudentOrganizationInfo(userId, major, classOrg, now);
            }
            
            // 如果是教师，更新教师表中的组织信息
            if (isTeacher) {
                updateTeacherOrganizationInfo(userId, college, major, now);
            }
            
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新学生表中的组织信息
     */
    private void updateStudentOrganizationInfo(Long userId, Organization major, Organization classOrg, LocalDateTime updateTime) {
        try {
            // 查找对应的学生记录
            Student student = studentMapper.selectByUserId(userId);
            if (student != null) {
                // 更新专业和班级信息
                if (major != null) {
                    student.setMajor(major.getOrgName());
                }
                if (classOrg != null) {
                    student.setClassName(classOrg.getOrgName());
                }
                student.setUpdateTime(updateTime);
                
                studentMapper.updateById(student);
            }
        } catch (Exception e) {
            // 记录错误但不影响主流程
            System.err.println("更新学生组织信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新教师表中的组织信息
     */
    private void updateTeacherOrganizationInfo(Long userId, Organization college, Organization major, LocalDateTime updateTime) {
        try {
            // 查找对应的教师记录
            Teacher teacher = teacherMapper.selectByUserId(userId);
            if (teacher != null) {
                // 更新部门信息（可以是学院或专业）
                if (major != null) {
                    teacher.setDepartment(major.getOrgName());
                } else if (college != null) {
                    teacher.setDepartment(college.getOrgName());
                }
                teacher.setUpdateTime(updateTime);
                
                teacherMapper.updateById(teacher);
            }
        } catch (Exception e) {
            // 记录错误但不影响主流程
            System.err.println("更新教师组织信息失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<Long>> getUserOrganizations(Long userId) {
        try {
            List<UserOrganization> userOrgs = userOrganizationMapper.selectByUserId(userId);
            List<Long> orgIds = userOrgs.stream()
                    .map(UserOrganization::getOrganizationId)
                    .collect(Collectors.toList());
            return Result.success(orgIds);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @Override
    public Result<List<OrganizationVO>> getOrganizationsByLevel(Integer level) {
        try {
            List<Organization> organizations = organizationMapper.selectByLevel(level);
            List<OrganizationVO> organizationVOs = organizations.stream().map(org -> {
                OrganizationVO orgVO = new OrganizationVO();
                orgVO.setId(org.getId());
                orgVO.setOrgName(org.getOrgName());
                orgVO.setOrgCode(org.getOrgCode());
                orgVO.setOrgType(org.getOrgType());
                orgVO.setParentId(org.getParentId());
                orgVO.setOrgLevel(org.getOrgLevel());
                orgVO.setSort(org.getSort());
                orgVO.setDescription(org.getDescription());
                orgVO.setStatus(org.getStatus());
                orgVO.setCreateTime(org.getCreateTime());
                orgVO.setUpdateTime(org.getUpdateTime());
                
                return orgVO;
            }).collect(Collectors.toList());
            
            return Result.success(organizationVOs);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @Override
    public Result<List<OrganizationVO>> getOrganizationsByParent(Long parentId) {
        try {
            List<Organization> organizations = organizationMapper.selectByParentId(parentId);
            List<OrganizationVO> organizationVOs = organizations.stream().map(org -> {
                OrganizationVO orgVO = new OrganizationVO();
                orgVO.setId(org.getId());
                orgVO.setOrgName(org.getOrgName());
                orgVO.setOrgCode(org.getOrgCode());
                orgVO.setOrgType(org.getOrgType());
                orgVO.setParentId(org.getParentId());
                orgVO.setOrgLevel(org.getOrgLevel());
                orgVO.setSort(org.getSort());
                orgVO.setDescription(org.getDescription());
                orgVO.setStatus(org.getStatus());
                orgVO.setCreateTime(org.getCreateTime());
                orgVO.setUpdateTime(org.getUpdateTime());
                
                return orgVO;
            }).collect(Collectors.toList());
            
            return Result.success(organizationVOs);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 构建菜单树
     */
    private List<MenuVO> buildMenuTree(List<MenuVO> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> {
                    if (parentId == null) {
                        return menu.getParentId() == null || menu.getParentId() == 0;
                    } else {
                        return parentId.equals(menu.getParentId());
                    }
                })
                .map(menu -> {
                    menu.setChildren(buildMenuTree(menus, menu.getId()));
                    return menu;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 构建组织树
     */
    private List<OrganizationVO> buildOrganizationTree(List<OrganizationVO> organizations, Long parentId) {
        return organizations.stream()
                .filter(org -> {
                    if (parentId == null) {
                        return org.getParentId() == null || org.getParentId() == 0;
                    } else {
                        return parentId.equals(org.getParentId());
                    }
                })
                .map(org -> {
                    org.setChildren(buildOrganizationTree(organizations, org.getId()));
                    return org;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 构建完整的组织路径
     */
    private void buildFullOrgPath(Organization classOrg, OrganizationVO orgVO) {
        if (classOrg.getOrgLevel() == 3 && "班级".equals(classOrg.getOrgType())) {
            // 获取学院信息（父级的父级）
            if (classOrg.getParentId() != null) {
                Organization gradeOrg = organizationMapper.selectById(classOrg.getParentId());
                if (gradeOrg != null && gradeOrg.getParentId() != null) {
                    Organization collegeOrg = organizationMapper.selectById(gradeOrg.getParentId());
                    if (collegeOrg != null) {
                        // 注释掉setFullPath调用，因为OrganizationVO中没有这个字段
                        // orgVO.setFullPath(collegeOrg.getOrgName() + " - " + gradeOrg.getOrgName() + " - " + classOrg.getOrgName());
                    }
                }
            }
        }
    }
} 