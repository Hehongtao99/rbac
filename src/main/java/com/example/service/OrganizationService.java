package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.OrganizationDTO;
import com.example.entity.Organization;
import com.example.vo.OrganizationTreeVO;

import java.util.List;

public interface OrganizationService extends IService<Organization> {
    
    List<OrganizationTreeVO> getOrganizationTree();
    
    List<OrganizationTreeVO> getOrganizationTreeByParentId(Long parentId);
    
    Boolean saveOrganization(OrganizationDTO dto);
    
    Boolean updateOrganization(OrganizationDTO dto);
    
    Boolean deleteOrganization(Long id);
    
    OrganizationTreeVO getOrganizationById(Long id);
    
    List<OrganizationTreeVO> getChildrenByParentId(Long parentId);
} 