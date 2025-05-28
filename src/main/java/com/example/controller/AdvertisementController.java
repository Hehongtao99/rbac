package com.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.dto.AdvertisementApplicationDTO;
import com.example.dto.AdvertisementPositionDTO;
import com.example.service.AdvertisementApplicationService;
import com.example.service.AdvertisementPositionService;
import com.example.vo.AdvertisementApplicationVO;
import com.example.vo.AdvertisementPositionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdvertisementController {
    
    @Autowired
    private AdvertisementApplicationService advertisementApplicationService;
    
    @Autowired
    private AdvertisementPositionService advertisementPositionService;
    
    /**
     * 分页查询广告申请列表
     */
    @GetMapping("/advertisement/applications")
    public Result<Page<AdvertisementApplicationVO>> getApplicationPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String applicationCode,
            @RequestParam(required = false) String positionName,
            @RequestParam(required = false) BigDecimal area,
            @RequestParam(required = false) String adSettingType,
            @RequestParam(required = false) String adNature,
            @RequestParam(required = false) String auditStatus,
            @RequestParam(required = false) Long regionId) {
        
        Page<AdvertisementApplicationVO> result = advertisementApplicationService.getApplicationPage(
                page, size, applicationCode, positionName, area, adSettingType, adNature, auditStatus, regionId);
        return Result.success(result);
    }
    
    /**
     * 新增广告申请
     */
    @PostMapping("/advertisement/applications")
    public Result<Boolean> saveApplication(@RequestBody AdvertisementApplicationDTO dto) {
        Boolean result = advertisementApplicationService.saveApplication(dto);
        return Result.success(result);
    }
    
    /**
     * 获取广告申请详情
     */
    @GetMapping("/advertisement/applications/{id}")
    public Result<AdvertisementApplicationVO> getApplicationById(@PathVariable Long id) {
        AdvertisementApplicationVO result = advertisementApplicationService.getApplicationById(id);
        return Result.success(result);
    }
    
    /**
     * 审核广告申请
     */
    @PutMapping("/advertisement/applications/{id}/audit")
    public Result<Boolean> auditApplication(@PathVariable Long id, 
                                          @RequestParam String auditStatus,
                                          @RequestParam(required = false) String auditRemark) {
        Boolean result = advertisementApplicationService.auditApplication(id, auditStatus, auditRemark);
        return Result.success(result);
    }
    
    /**
     * 根据区域ID获取广告位列表
     */
    @GetMapping("/advertisement/positions")
    public Result<List<AdvertisementPositionVO>> getPositionsByRegionId(@RequestParam Long regionId) {
        List<AdvertisementPositionVO> result = advertisementPositionService.getPositionsByRegionId(regionId);
        return Result.success(result);
    }
    
    /**
     * 分页查询广告位列表（用于管理）
     */
    @GetMapping("/advertisement/positions/page")
    public Result<Page<AdvertisementPositionVO>> getPositionsPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam Long regionId,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String positionName) {
        Page<AdvertisementPositionVO> result = advertisementPositionService.getPositionsPage(
                page, size, regionId, code, positionName);
        return Result.success(result);
    }
    
    /**
     * 新增广告位
     */
    @PostMapping("/advertisement/positions")
    public Result<Boolean> savePosition(@RequestBody AdvertisementPositionDTO dto) {
        Boolean result = advertisementPositionService.savePosition(dto);
        return Result.success(result);
    }
    
    /**
     * 更新广告位
     */
    @PutMapping("/advertisement/positions")
    public Result<Boolean> updatePosition(@RequestBody AdvertisementPositionDTO dto) {
        Boolean result = advertisementPositionService.updatePosition(dto);
        return Result.success(result);
    }
    
    /**
     * 删除广告位
     */
    @DeleteMapping("/advertisement/positions/{id}")
    public Result<Boolean> deletePosition(@PathVariable Long id) {
        Boolean result = advertisementPositionService.deletePosition(id);
        return Result.success(result);
    }
    
    /**
     * 获取广告位详情
     */
    @GetMapping("/advertisement/positions/{id}")
    public Result<AdvertisementPositionVO> getPositionById(@PathVariable Long id) {
        AdvertisementPositionVO result = advertisementPositionService.getPositionById(id);
        return Result.success(result);
    }
} 