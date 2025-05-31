package com.example.util;

import com.example.common.PageResult;

import java.util.List;

public class PageUtil {
    
    /**
     * 创建分页结果
     */
    public static <T> PageResult<T> createPageResult(int pageNum, int pageSize, long total, List<T> records) {
        return new PageResult<>(pageNum, pageSize, total, records);
    }
    
    /**
     * 构建分页结果（别名方法，保持兼容性）
     */
    public static <T> PageResult<T> buildPageResult(List<T> records, int current, int size, long total) {
        return new PageResult<>(current, size, total, records);
    }
    
    /**
     * 计算偏移量
     */
    public static int calculateOffset(int pageNum, int pageSize) {
        return (pageNum - 1) * pageSize;
    }
    
    /**
     * 计算总页数
     */
    public static long calculatePages(long total, int pageSize) {
        return (total + pageSize - 1) / pageSize;
    }
} 