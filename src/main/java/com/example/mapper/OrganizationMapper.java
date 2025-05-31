package com.example.mapper;

import com.example.entity.Organization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrganizationMapper {
    
    // 基础CRUD操作
    int insert(Organization organization);
    
    int deleteById(Long id);
    
    int updateById(Organization organization);
    
    Organization selectById(Long id);
    
    List<Organization> selectList(@Param("organization") Organization organization);
    
    // 根据ID列表和状态查询
    List<Organization> selectByIdsAndStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);
    
    // 根据状态查询
    List<Organization> selectByStatus(@Param("status") Integer status);
    
    // 根据父级ID查询
    List<Organization> selectByParentId(@Param("parentId") Long parentId);
    
    // 根据父级ID和状态查询
    List<Organization> selectByParentIdAndStatus(@Param("parentId") Long parentId, @Param("status") Integer status);
    
    // 根据组织ID列表、级别和状态查询
    List<Organization> selectByIdsAndLevelAndStatus(@Param("orgIds") List<Long> orgIds, 
                                                   @Param("orgLevel") Integer orgLevel, 
                                                   @Param("status") Integer status);
    
    // 根据组织名称查询
    Organization selectByOrgName(@Param("orgName") String orgName);
    
    // 根据组织编码查询
    Organization selectByOrgCode(@Param("orgCode") String orgCode);
    
    // 根据组织名称查询（排除指定ID）
    Organization selectByOrgNameExcludeId(@Param("orgName") String orgName, @Param("excludeId") Long excludeId);
    
    // 根据组织编码查询（排除指定ID）
    Organization selectByOrgCodeExcludeId(@Param("orgCode") String orgCode, @Param("excludeId") Long excludeId);
    
    // 根据级别查询
    List<Organization> selectByLevel(@Param("orgLevel") Integer orgLevel);
    
    // 条件查询（支持名称、编码、类型筛选）
    List<Organization> selectByConditions(@Param("orgName") String orgName, 
                                         @Param("orgCode") String orgCode, 
                                         @Param("orgType") String orgType, 
                                         @Param("status") Integer status);
    
    // 统计总数
    long selectCount(@Param("organization") Organization organization);
} 