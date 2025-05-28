package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Organization;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {
    
    List<Organization> selectByParentId(Long parentId);
    
    List<Organization> selectRootNodes();
} 