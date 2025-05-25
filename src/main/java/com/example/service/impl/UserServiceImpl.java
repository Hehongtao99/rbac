package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.dto.LoginDTO;
import com.example.dto.RegisterDTO;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.entity.UserRole;
import com.example.mapper.RoleMapper;
import com.example.mapper.UserMapper;
import com.example.mapper.UserRoleMapper;
import com.example.service.MenuService;
import com.example.service.UserService;
import com.example.util.JwtUtil;
import com.example.vo.LoginVO;
import com.example.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
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
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, registerDTO.getUsername());
        User existUser = userMapper.selectOne(wrapper);
        
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setStatus(1);
        
        userMapper.insert(user);
        
        // 分配默认角色（用户角色）
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(Role::getRoleCode, "USER");
        Role userRole = roleMapper.selectOne(roleWrapper);
        
        if (userRole != null) {
            UserRole userRoleEntity = new UserRole();
            userRoleEntity.setUserId(user.getId());
            userRoleEntity.setRoleId(userRole.getId());
            userRoleMapper.insert(userRoleEntity);
        }
    }
    
    @Override
    public UserInfoVO getUserInfo(String username) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 查询用户角色
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getUserId, user.getId());
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        
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
        
        return userInfoVO;
    }
} 