package com.example.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 分页结果类，用于替代MyBatis-Plus的Page类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    
    /**
     * 当前页码
     */
    private long current;
    
    /**
     * 每页大小
     */
    private long size;
    
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 总页数
     */
    private long pages;
    
    /**
     * 数据列表
     */
    private List<T> records;
    
    /**
     * 构造方法
     */
    public PageResult(long current, long size, long total, List<T> records) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.records = records;
        this.pages = calculatePages(total, size);
    }
    
    /**
     * 计算总页数
     */
    private long calculatePages(long total, long size) {
        if (size == 0) {
            return 0;
        }
        return (total + size - 1) / size;
    }
    
    /**
     * 是否有下一页
     */
    public boolean hasNext() {
        return current < pages;
    }
    
    /**
     * 是否有上一页
     */
    public boolean hasPrevious() {
        return current > 1;
    }
} 