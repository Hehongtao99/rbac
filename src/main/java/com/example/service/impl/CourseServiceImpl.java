package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Course;
import com.example.entity.Teacher;
import com.example.dto.CourseDTO;
import com.example.mapper.CourseMapper;
import com.example.mapper.TeacherMapper;
import com.example.service.CourseService;
import com.example.common.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public Result<Object> getCourseList(Integer page, Integer size, String keyword) {
        Page<Course> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Course::getCourseName, keyword)
                    .or().like(Course::getCourseDescription, keyword)
                    .or().like(Course::getTeacherName, keyword)
                    .or().like(Course::getAcademicYear, keyword));
        }
        wrapper.orderByDesc(Course::getCreateTime);
        
        Page<Course> result = this.page(pageInfo, wrapper);
        return Result.success(result);
    }

    @Override
    public Result<Object> getCourseTemplateList(Integer page, Integer size, String keyword) {
        Page<Course> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        
        // 只获取课程模板（teacherId为空）
        wrapper.isNull(Course::getTeacherId);
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Course::getCourseName, keyword)
                    .or().like(Course::getCourseDescription, keyword)
                    .or().like(Course::getAcademicYear, keyword));
        }
        wrapper.orderByDesc(Course::getCreateTime);
        
        Page<Course> result = this.page(pageInfo, wrapper);
        return Result.success(result);
    }

    @Override
    public Result<Object> getCourseInstanceList(Integer page, Integer size, String keyword, String courseFilter, String teacherFilter) {
        Page<Course> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        
        // 只获取开课实例（teacherId不为空）
        wrapper.isNotNull(Course::getTeacherId);
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Course::getCourseName, keyword)
                    .or().like(Course::getCourseDescription, keyword)
                    .or().like(Course::getTeacherName, keyword)
                    .or().like(Course::getAcademicYear, keyword));
        }
        
        // 课程筛选
        if (StringUtils.hasText(courseFilter)) {
            wrapper.and(w -> w.like(Course::getCourseName, courseFilter));
        }
        
        // 教师筛选
        if (StringUtils.hasText(teacherFilter)) {
            wrapper.and(w -> w.like(Course::getTeacherName, teacherFilter));
        }
        
        wrapper.orderByDesc(Course::getCreateTime);
        
        Page<Course> result = this.page(pageInfo, wrapper);
        return Result.success(result);
    }

    @Override
    public Result<Object> createCourse(CourseDTO courseDTO) {
        // 检查课程名称是否已存在
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCourseName, courseDTO.getCourseName())
               .eq(Course::getAcademicYear, courseDTO.getAcademicYear());
        Course existCourse = this.getOne(wrapper);
        
        if (existCourse != null) {
            return Result.error("该学年下已存在同名课程");
        }
        
        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        
        // 创建课程模板时不指定教师信息
        course.setTeacherId(null);
        course.setTeacherName(null);
        course.setCurrentStudents(0); // 初始化当前学生数为0
        course.setStatus(1); // 默认启用
        course.setAllowApplication(1); // 默认开放申请
        
        boolean success = this.save(course);
        if (success) {
            return Result.success("课程模板创建成功");
        } else {
            return Result.error("课程模板创建失败");
        }
    }

    @Override
    public Result<Object> updateCourse(Long id, CourseDTO courseDTO) {
        Course existCourse = this.getById(id);
        if (existCourse == null) {
            return Result.error("课程不存在");
        }
        
        // 如果是已开课的课程实例（有教师信息），不允许编辑
        if (existCourse.getTeacherId() != null) {
            return Result.error("已开课的课程实例不能编辑，请联系申请教师进行修改");
        }
        
        // 检查课程名称是否已被其他课程使用
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCourseName, courseDTO.getCourseName())
               .eq(Course::getAcademicYear, courseDTO.getAcademicYear())
               .ne(Course::getId, id);
        Course duplicateCourse = this.getOne(wrapper);
        
        if (duplicateCourse != null) {
            return Result.error("该学年下已存在同名课程");
        }
        
        // 只更新课程模板信息，不涉及教师
        existCourse.setCourseName(courseDTO.getCourseName());
        existCourse.setCourseDescription(courseDTO.getCourseDescription());
        existCourse.setMaxStudents(courseDTO.getMaxStudents());
        existCourse.setCourseHours(courseDTO.getCourseHours());
        existCourse.setAcademicYear(courseDTO.getAcademicYear());
        existCourse.setStatus(courseDTO.getStatus());
        if (courseDTO.getAllowApplication() != null) {
            existCourse.setAllowApplication(courseDTO.getAllowApplication());
        }
        
        boolean success = this.updateById(existCourse);
        
        if (success) {
            return Result.success("课程模板更新成功");
        } else {
            return Result.error("课程模板更新失败");
        }
    }

    @Override
    public Result<Object> deleteCourse(Long id) {
        Course course = this.getById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        
        // 如果是已开课的课程实例（有教师信息），不允许删除
        if (course.getTeacherId() != null) {
            return Result.error("已开课的课程实例不能删除");
        }
        
        boolean success = this.removeById(id);
        if (success) {
            return Result.success("课程模板删除成功");
        } else {
            return Result.error("课程模板删除失败");
        }
    }

    @Override
    public Result<Object> getCourseById(Long id) {
        Course course = this.getById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        
        return Result.success(course);
    }

    @Override
    public Result<Object> getTeacherList() {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getStatus, 1)
               .orderByAsc(Teacher::getName);
        List<Teacher> teachers = teacherMapper.selectList(wrapper);
        return Result.success(teachers);
    }

    @Override
    public Result<Object> toggleApplicationStatus(Long id) {
        Course course = this.getById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        
        // 只有课程模板才能切换申请状态
        if (course.getTeacherId() != null) {
            return Result.error("开课实例不能修改申请状态");
        }
        
        // 切换申请状态
        Integer newStatus = course.getAllowApplication() == 1 ? 0 : 1;
        course.setAllowApplication(newStatus);
        
        boolean success = this.updateById(course);
        if (success) {
            String statusText = newStatus == 1 ? "开放申请" : "关闭申请";
            return Result.success("已" + statusText);
        } else {
            return Result.error("操作失败");
        }
    }
}