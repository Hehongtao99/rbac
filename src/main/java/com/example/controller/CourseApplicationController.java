package com.example.controller;

import com.example.common.PageResult;
import com.example.common.Result;
import com.example.dto.CourseApplicationDTO;
import com.example.dto.CourseApplicationQueryDTO;
import com.example.dto.CourseApplicationReviewDTO;
import com.example.service.CourseApplicationService;
import com.example.vo.CourseApplicationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程申请控制器
 */
@RestController
@RequestMapping("/api/course-applications")
@CrossOrigin
public class CourseApplicationController {

    @Autowired
    private CourseApplicationService courseApplicationService;

    /**
     * 分页查询课程申请列表
     */
    @PostMapping("/list")
    public Result<PageResult<CourseApplicationVO>> getApplicationList(@RequestBody CourseApplicationQueryDTO queryDTO) {
        PageResult<CourseApplicationVO> result = courseApplicationService.getApplicationList(queryDTO);
        return Result.success(result);
    }

    /**
     * 管理员分页查询课程申请列表
     */
    @PostMapping("/admin/list")
    public Result<PageResult<CourseApplicationVO>> getApplicationListForAdmin(@RequestBody CourseApplicationQueryDTO queryDTO) {
        PageResult<CourseApplicationVO> result = courseApplicationService.getApplicationListForAdmin(queryDTO);
        return Result.success(result);
    }

    /**
     * 创建课程申请
     */
    @PostMapping
    public Result<Void> createApplication(@RequestBody CourseApplicationDTO applicationDTO) {
        try {
            System.out.println("接收到课程申请数据: " + applicationDTO);
            courseApplicationService.createApplication(applicationDTO);
            return Result.success();
        } catch (Exception e) {
            System.err.println("创建课程申请失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新课程申请
     */
    @PutMapping("/{id}")
    public Result<Void> updateApplication(@PathVariable Long id, @RequestBody CourseApplicationDTO applicationDTO) {
        try {
            courseApplicationService.updateApplication(id, applicationDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 审核课程申请
     */
    @PutMapping("/{id}/review")
    public Result<Void> reviewApplication(@PathVariable Long id, @RequestBody CourseApplicationReviewDTO reviewDTO) {
        try {
            courseApplicationService.reviewApplication(id, reviewDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除课程申请
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteApplication(@PathVariable Long id) {
        try {
            courseApplicationService.deleteApplication(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前教师已申请的课程模板ID列表
     */
    @GetMapping("/applied-templates")
    public Result<List<Long>> getAppliedTemplateIds() {
        try {
            List<Long> appliedTemplateIds = courseApplicationService.getAppliedTemplateIds();
            return Result.success(appliedTemplateIds);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取课程申请详情
     */
    @GetMapping("/{id}")
    public Result<CourseApplicationVO> getApplicationById(@PathVariable Long id) {
        try {
            CourseApplicationVO application = courseApplicationService.getApplicationById(id);
            return Result.success(application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
} 