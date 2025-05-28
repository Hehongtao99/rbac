package com.example.controller;

import com.example.common.Result;
import com.example.dto.OrganizationDTO;
import com.example.service.OrganizationService;
import com.example.vo.OrganizationTreeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @GetMapping("/organizations/tree")
    public Result<List<OrganizationTreeVO>> getOrganizationTree() {
        List<OrganizationTreeVO> tree = organizationService.getOrganizationTree();
        return Result.success(tree);
    }

    @GetMapping("/organizations/children/{parentId}")
    public Result<List<OrganizationTreeVO>> getChildren(@PathVariable Long parentId) {
        List<OrganizationTreeVO> children = organizationService.getChildrenByParentId(parentId);
        return Result.success(children);
    }

    @GetMapping("/organizations/{id}")
    public Result<OrganizationTreeVO> getOrganizationById(@PathVariable Long id) {
        OrganizationTreeVO organization = organizationService.getOrganizationById(id);
        return Result.success(organization);
    }

    @PostMapping("/organizations")
    public Result<Boolean> saveOrganization(@RequestBody OrganizationDTO dto) {
        Boolean result = organizationService.saveOrganization(dto);
        return Result.success(result);
    }

    @PutMapping("/organizations")
    public Result<Boolean> updateOrganization(@RequestBody OrganizationDTO dto) {
        Boolean result = organizationService.updateOrganization(dto);
        return Result.success(result);
    }

    @DeleteMapping("/organizations/{id}")
    public Result<Boolean> deleteOrganization(@PathVariable Long id) {
        try {
            Boolean result = organizationService.deleteOrganization(id);
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
} 