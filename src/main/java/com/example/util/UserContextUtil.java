package com.example.util;

import com.example.entity.User;
import com.example.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户上下文工具类
 * 用于获取当前登录用户信息
 */
@Component
public class UserContextUtil {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 获取当前登录用户
     */
    public User getCurrentUser() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            
            HttpServletRequest request = attributes.getRequest();
            String token = extractToken(request);
            if (token == null) {
                return null;
            }
            
            // 验证token是否有效
            if (!jwtUtil.validateToken(token)) {
                return null;
            }
            
            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null || username.isEmpty()) {
                return null;
            }
            
            // 根据用户名查询用户信息
            return userMapper.selectByUsername(username);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取当前登录用户ID
     */
    public Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
    
    /**
     * 获取当前登录用户名
     */
    public String getCurrentUsername() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            
            HttpServletRequest request = attributes.getRequest();
            String token = extractToken(request);
            if (token == null) {
                return null;
            }
            
            return jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取当前登录用户昵称
     */
    public String getCurrentUserNickname() {
        User user = getCurrentUser();
        return user != null ? (user.getNickname() != null ? user.getNickname() : user.getUsername()) : null;
    }
    
    /**
     * 检查当前请求是否已认证
     */
    public boolean isAuthenticated() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return false;
            }
            
            HttpServletRequest request = attributes.getRequest();
            String token = extractToken(request);
            return token != null && jwtUtil.validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 从请求头中提取token
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
} 