package com.example.service;

import com.example.dto.LoginDTO;
import com.example.dto.RegisterDTO;
import com.example.vo.LoginVO;
import com.example.vo.UserInfoVO;

public interface UserService {
    LoginVO login(LoginDTO loginDTO);
    void register(RegisterDTO registerDTO);
    UserInfoVO getUserInfo(String username);
} 