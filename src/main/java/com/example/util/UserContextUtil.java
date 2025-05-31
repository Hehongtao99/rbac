package com.example.util;

import com.example.entity.User;
import com.example.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

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
                System.err.println("UserContextUtil: RequestAttributes is null");
                return null;
            }
            
            HttpServletRequest request = attributes.getRequest();
            System.out.println("=== UserContextUtil.getCurrentUser() 开始 ===");
            System.out.println("UserContextUtil: 请求URI = " + request.getRequestURI());
            
            String token = request.getHeader("Authorization");
            System.out.println("UserContextUtil: Authorization header = " + token);
            
            if (token == null) {
                System.err.println("UserContextUtil: Authorization header is null");
                return null;
            }
            
            if (!token.startsWith("Bearer ")) {
                System.err.println("UserContextUtil: Authorization header does not start with Bearer");
                return null;
            }
            
            token = token.substring(7);
            if (token.isEmpty()) {
                System.err.println("UserContextUtil: Token is empty after removing Bearer prefix");
                return null;
            }
            
            // 验证token是否有效
            if (!jwtUtil.validateToken(token)) {
                System.err.println("UserContextUtil: Token validation failed");
                return null;
            }
            
            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null || username.isEmpty()) {
                System.err.println("UserContextUtil: Username from token is null or empty");
                return null;
            }
            
            // 根据用户名查询用户信息
            User user = userMapper.selectByUsername(username);
            
            if (user == null) {
                System.err.println("UserContextUtil: User not found for username: " + username);
                return null;
            }
            
            System.out.println("UserContextUtil: Successfully found user: " + user.getUsername() + " (" + user.getNickname() + ")");
            return user;
        } catch (Exception e) {
            System.err.println("UserContextUtil: Exception occurred: " + e.getMessage());
            e.printStackTrace();
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
     * 获取当前登录用户名称
     */
    public String getCurrentUserName() {
        User user = getCurrentUser();
        return user != null ? user.getNickname() : null;
    }
} 