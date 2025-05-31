package com.example.controller;

import com.example.common.Result;
import com.example.dto.LoginDTO;
import com.example.dto.RegisterDTO;
import com.example.service.UserService;
import com.example.util.UserContextUtil;
import com.example.vo.LoginVO;
import com.example.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserContextUtil userContextUtil;
    
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = userService.login(loginDTO);
        return Result.success(loginVO);
    }
    
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }
    
    @GetMapping("/userinfo")
    public Result<UserInfoVO> getUserInfo() {
        String username = userContextUtil.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("用户未登录或token无效");
        }
        
        UserInfoVO userInfo = userService.getUserInfo(username);
        return Result.success(userInfo);
    }
} 