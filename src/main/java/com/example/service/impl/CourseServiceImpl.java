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
    public Result<Object> createCourse(CourseDTO courseDTO) {
        // 检查课程名称是否已存在
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCourseName, courseDTO.getCourseName());
        Course existCourse = this.getOne(wrapper);
        
        if (existCourse != null) {
            return Result.error("课程名称已存在");
        }
        
        // 获取教师姓名
        if (courseDTO.getTeacherId() != null) {
            Teacher teacher = teacherMapper.selectById(courseDTO.getTeacherId());
            if (teacher != null) {
                courseDTO.setTeacherName(teacher.getName());
            }
        }
        
        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        course.setCurrentStudents(0); // 初始化当前学生数为0
        course.setStatus(1); // 默认启用
        
        boolean success = this.save(course);
        if (success) {
            return Result.success("课程创建成功");
        } else {
            return Result.error("课程创建失败");
        }
    }

    @Override
    public Result<Object> updateCourse(Long id, CourseDTO courseDTO) {
        Course existCourse = this.getById(id);
        if (existCourse == null) {
            return Result.error("课程不存在");
        }
        
        // 检查课程名称是否已被其他课程使用
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCourseName, courseDTO.getCourseName())
               .ne(Course::getId, id);
        Course duplicateCourse = this.getOne(wrapper);
        
        if (duplicateCourse != null) {
            return Result.error("课程名称已被其他课程使用");
        }
        
        // 获取教师姓名
        if (courseDTO.getTeacherId() != null) {
            Teacher teacher = teacherMapper.selectById(courseDTO.getTeacherId());
            if (teacher != null) {
                courseDTO.setTeacherName(teacher.getName());
            }
        }
        
        BeanUtils.copyProperties(courseDTO, existCourse);
        boolean success = this.updateById(existCourse);
        
        if (success) {
            return Result.success("课程更新成功");
        } else {
            return Result.error("课程更新失败");
        }
    }

    @Override
    public Result<Object> deleteCourse(Long id) {
        Course course = this.getById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        
        boolean success = this.removeById(id);
        if (success) {
            return Result.success("课程删除成功");
        } else {
            return Result.error("课程删除失败");
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
}