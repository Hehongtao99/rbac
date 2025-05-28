package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dto.OrganizationDTO;
import com.example.entity.Organization;
import com.example.mapper.OrganizationMapper;
import com.example.service.OrganizationService;
import com.example.vo.OrganizationTreeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, Organization> implements OrganizationService {

    @Override
    public List<OrganizationTreeVO> getOrganizationTree() {
        // 获取所有根节点（省级）
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Organization::getLevel, 1)
                .eq(Organization::getIsEnabled, true)
                .orderByAsc(Organization::getSort);
        
        List<Organization> rootNodes = this.list(wrapper);
        
        return rootNodes.stream().map(this::buildTree).collect(Collectors.toList());
    }

    @Override
    public List<OrganizationTreeVO> getOrganizationTreeByParentId(Long parentId) {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Organization::getParentId, parentId)
                .eq(Organization::getIsEnabled, true)
                .orderByAsc(Organization::getSort);
        
        List<Organization> children = this.list(wrapper);
        
        return children.stream().map(this::entityToVO).collect(Collectors.toList());
    }

    @Override
    public Boolean saveOrganization(OrganizationDTO dto) {
        Organization organization = new Organization();
        BeanUtils.copyProperties(dto, organization);
        organization.setCreateTime(LocalDateTime.now());
        organization.setUpdateTime(LocalDateTime.now());
        organization.setIsEnabled(true);
        
        return this.save(organization);
    }

    @Override
    public Boolean updateOrganization(OrganizationDTO dto) {
        Organization organization = new Organization();
        BeanUtils.copyProperties(dto, organization);
        organization.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(organization);
    }

    @Override
    public Boolean deleteOrganization(Long id) {
        // 检查是否有子节点
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Organization::getParentId, id);
        
        long count = this.count(wrapper);
        if (count > 0) {
            throw new RuntimeException("该节点下还有子节点，无法删除");
        }
        
        return this.removeById(id);
    }

    @Override
    public OrganizationTreeVO getOrganizationById(Long id) {
        Organization organization = this.getById(id);
        if (organization == null) {
            return null;
        }
        
        return entityToVO(organization);
    }

    @Override
    public List<OrganizationTreeVO> getChildrenByParentId(Long parentId) {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Organization::getParentId, parentId)
                .eq(Organization::getIsEnabled, true)
                .orderByAsc(Organization::getSort);
        
        List<Organization> children = this.list(wrapper);
        
        return children.stream().map(this::entityToVO).collect(Collectors.toList());
    }

    private OrganizationTreeVO buildTree(Organization organization) {
        OrganizationTreeVO vo = entityToVO(organization);
        
        // 递归获取子节点
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Organization::getParentId, organization.getId())
                .eq(Organization::getIsEnabled, true)
                .orderByAsc(Organization::getSort);
        
        List<Organization> children = this.list(wrapper);
        if (!children.isEmpty()) {
            vo.setChildren(children.stream().map(this::buildTree).collect(Collectors.toList()));
        } else {
            vo.setChildren(new ArrayList<>());
        }
        
        return vo;
    }

    private OrganizationTreeVO entityToVO(Organization organization) {
        OrganizationTreeVO vo = new OrganizationTreeVO();
        BeanUtils.copyProperties(organization, vo);
        return vo;
    }
} 