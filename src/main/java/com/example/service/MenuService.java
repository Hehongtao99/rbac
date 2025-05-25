package com.example.service;

import com.example.vo.MenuVO;
import java.util.List;

public interface MenuService {
    List<MenuVO> getUserMenus(Long userId);
    List<String> getUserButtons(Long userId);
} 