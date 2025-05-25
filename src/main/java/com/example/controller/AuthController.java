package com.example.controller;

import com.example.common.Result;
import com.example.dto.LoginDTO;
import com.example.dto.RegisterDTO;
import com.example.service.UserService;
import com.example.util.JwtUtil;
import com.example.vo.LoginVO;
import com.example.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        try {
            LoginVO loginVO = userService.login(loginDTO);
            return Result.success(loginVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO registerDTO) {
        try {
            userService.register(registerDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/userinfo")
    public Result<UserInfoVO> getUserInfo(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String username = jwtUtil.getUsernameFromToken(token);
                UserInfoVO userInfo = userService.getUserInfo(username);
                return Result.success(userInfo);
            }
            return Result.error(401, "未授权");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
} 