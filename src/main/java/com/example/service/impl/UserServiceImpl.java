package com.example.service.impl;

import com.example.dto.LoginDTO;
import com.example.dto.RegisterDTO;
import com.example.entity.Organization;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.entity.UserOrganization;
import com.example.entity.UserRole;
import com.example.mapper.OrganizationMapper;
import com.example.mapper.RoleMapper;
import com.example.mapper.UserMapper;
import com.example.mapper.UserOrganizationMapper;
import com.example.mapper.UserRoleMapper;
import com.example.service.MenuService;
import com.example.service.UserService;
import com.example.util.JwtUtil;
import com.example.vo.LoginVO;
import com.example.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private MenuService menuService;
    
    @Autowired
    private UserOrganizationMapper userOrganizationMapper;
    
    @Autowired
    private OrganizationMapper organizationMapper;
    
    @Autowired
    private PasswordUtil passwordUtil;
    
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 查询用户
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (!passwordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }
        
        // 生成token
        String token = jwtUtil.generateToken(user.getUsername());
        
        // 获取用户信息
        UserInfoVO userInfo = getUserInfo(user.getUsername());
        
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserInfo(userInfo);
        
        return loginVO;
    }
    
    @Override
    public void register(RegisterDTO registerDTO) {
        // 检查用户名是否存在
        User existUser = userMapper.selectByUsername(registerDTO.getUsername());
        
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordUtil.encode(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setDeleted(0);
        
        userMapper.insert(user);
        
        // 分配默认角色（学生角色）
        Role userRole = roleMapper.selectByRoleCode("STUDENT");
        
        if (userRole != null) {
            UserRole userRoleEntity = new UserRole();
            userRoleEntity.setUserId(user.getId());
            userRoleEntity.setRoleId(userRole.getId());
            userRoleEntity.setCreateTime(LocalDateTime.now());
            userRoleEntity.setDeleted(0);
            userRoleMapper.insert(userRoleEntity);
        }
    }
    
    @Override
    public UserInfoVO getUserInfo(String username) {
        // 查询用户
        User user = userMapper.selectByUsername(username);
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 查询用户角色
        List<UserRole> userRoles = userRoleMapper.selectByUserId(user.getId());
        
        List<String> roles = userRoles.stream()
                .map(ur -> {
                    Role role = roleMapper.selectById(ur.getRoleId());
                    return role != null ? role.getRoleName() : null;
                })
                .filter(roleName -> roleName != null)
                .collect(Collectors.toList());
        
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(user.getId());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setNickname(user.getNickname());
        userInfoVO.setEmail(user.getEmail());
        userInfoVO.setPhone(user.getPhone());
        userInfoVO.setRoles(roles);
        
        // 获取用户菜单和按钮权限
        userInfoVO.setMenus(menuService.getUserMenus(user.getId()));
        userInfoVO.setButtons(menuService.getUserButtons(user.getId()));
        
        // 获取用户组织信息
        List<UserInfoVO.OrganizationInfoVO> organizations = getUserOrganizations(user.getId());
        userInfoVO.setOrganizations(organizations);
        
        return userInfoVO;
    }
    
    /**
     * 获取用户组织信息
     */
    private List<UserInfoVO.OrganizationInfoVO> getUserOrganizations(Long userId) {
        // 查询用户关联的组织
        List<UserOrganization> userOrganizations = userOrganizationMapper.selectByUserId(userId);
        
        if (userOrganizations.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取组织ID列表
        List<Long> orgIds = userOrganizations.stream()
                .map(UserOrganization::getOrganizationId)
                .collect(Collectors.toList());
        
        // 查询组织信息
        List<Organization> organizations = organizationMapper.selectByIdsAndStatus(orgIds, 1);
        
        return organizations.stream().map(org -> {
            UserInfoVO.OrganizationInfoVO orgInfo = new UserInfoVO.OrganizationInfoVO();
            orgInfo.setId(org.getId());
            orgInfo.setOrgName(org.getOrgName());
            orgInfo.setOrgCode(org.getOrgCode());
            orgInfo.setOrgType(org.getOrgType());
            orgInfo.setOrgLevel(org.getOrgLevel());
            
            // 构建完整的组织路径信息
            buildOrganizationPath(org, orgInfo);
            
            return orgInfo;
        }).collect(Collectors.toList());
    }
    
    /**
     * 构建组织路径信息（学院-专业-班级）
     */
    private void buildOrganizationPath(Organization currentOrg, UserInfoVO.OrganizationInfoVO orgInfo) {
        Organization org = currentOrg;
        Organization college = null, major = null, classOrg = null;
        
        // 根据当前组织级别确定位置
        while (org != null) {
            if (org.getOrgLevel() == 1) { // 学院
                college = org;
            } else if (org.getOrgLevel() == 2) { // 专业
                major = org;
            } else if (org.getOrgLevel() == 3) { // 班级
                classOrg = org;
            }
            
            // 查找父级组织
            if (org.getParentId() != null && org.getParentId() != 0) {
                org = organizationMapper.selectById(org.getParentId());
            } else {
                break;
            }
        }
        
        // 设置组织路径信息
        if (college != null) {
            orgInfo.setCollegeName(college.getOrgName());
        }
        if (major != null) {
            orgInfo.setMajorName(major.getOrgName());
        }
        if (classOrg != null) {
            orgInfo.setClassName(classOrg.getOrgName());
        }
    }
} 