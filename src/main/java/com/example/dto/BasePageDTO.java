package com.example.dto;

import lombok.Data;

/**
 * 分页查询基础DTO
 */
@Data
public class BasePageDTO {
    
    /**
     * 页码，默认第1页
     */
    private Integer page = 1;
    
    /**
     * 每页大小，默认10条
     */
    private Integer size = 10;
    
    /**
     * 搜索关键词
     */
    private String keyword;
    
    /**
     * 获取页码，确保不为null且大于0
     */
    public Integer getPage() {
        return page == null || page < 1 ? 1 : page;
    }
    
    /**
     * 获取每页大小，确保不为null且在合理范围内
     */
    public Integer getSize() {
        if (size == null || size < 1) {
            return 10;
        }
        if (size > 100) {
            return 100; // 限制最大每页100条
        }
        return size;
    }
} 