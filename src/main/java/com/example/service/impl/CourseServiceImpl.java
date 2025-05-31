package com.example.service.impl;

import com.example.entity.Course;
import com.example.entity.Teacher;
import com.example.dto.CourseDTO;
import com.example.mapper.CourseMapper;
import com.example.mapper.TeacherMapper;
import com.example.service.CourseService;
import com.example.common.Result;
import com.example.common.PageResult;
import com.example.util.PageUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public Result<Object> getCourseList(Integer page, Integer size, String keyword) {
        int offset = PageUtil.calculateOffset(page, size);
        List<Course> courses = courseMapper.selectPage(offset, size, keyword);
        long total = courseMapper.selectCount(new Course());
        
        PageResult<Course> result = PageUtil.createPageResult(page, size, total, courses);
        return Result.success(result);
    }

    @Override
    public Result<Object> getCourseTemplateList(Integer page, Integer size, String keyword) {
        // 这个方法需要在CourseMapper中添加专门的查询方法
        int offset = PageUtil.calculateOffset(page, size);
        List<Course> courses = courseMapper.selectPage(offset, size, keyword);
        // 过滤出课程模板（teacherId为空）
        List<Course> templates = courses.stream()
                .filter(course -> course.getTeacherId() == null)
                .collect(java.util.stream.Collectors.toList());
        
        PageResult<Course> result = PageUtil.createPageResult(page, size, templates.size(), templates);
        return Result.success(result);
    }

    @Override
    public Result<Object> getCourseInstanceList(Integer page, Integer size, String keyword, String courseFilter, String teacherFilter) {
        // 这个方法需要在CourseMapper中添加专门的查询方法
        int offset = PageUtil.calculateOffset(page, size);
        List<Course> courses = courseMapper.selectPage(offset, size, keyword);
        // 过滤出开课实例（teacherId不为空）
        List<Course> instances = courses.stream()
                .filter(course -> course.getTeacherId() != null)
                .collect(java.util.stream.Collectors.toList());
        
        PageResult<Course> result = PageUtil.createPageResult(page, size, instances.size(), instances);
        return Result.success(result);
    }

    @Override
    public Result<Object> createCourse(CourseDTO courseDTO) {
        // 检查课程名称是否已存在（需要在CourseMapper中添加查询方法）
        List<Course> existCourses = courseMapper.selectList(new Course());
        boolean exists = existCourses.stream()
                .anyMatch(course -> course.getCourseName().equals(courseDTO.getCourseName()) 
                        && course.getAcademicYear().equals(courseDTO.getAcademicYear()));
        
        if (exists) {
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
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());
        course.setDeleted(0);
        
        int result = courseMapper.insert(course);
        if (result > 0) {
            return Result.success("课程模板创建成功");
        } else {
            return Result.error("课程模板创建失败");
        }
    }

    @Override
    public Result<Object> updateCourse(Long id, CourseDTO courseDTO) {
        Course existCourse = courseMapper.selectById(id);
        if (existCourse == null) {
            return Result.error("课程不存在");
        }
        
        // 如果是已开课的课程实例（有教师信息），不允许编辑
        if (existCourse.getTeacherId() != null) {
            return Result.error("已开课的课程实例不能编辑，请联系申请教师进行修改");
        }
        
        // 检查课程名称是否已被其他课程使用
        List<Course> allCourses = courseMapper.selectList(new Course());
        boolean duplicateExists = allCourses.stream()
                .anyMatch(course -> course.getCourseName().equals(courseDTO.getCourseName()) 
                        && course.getAcademicYear().equals(courseDTO.getAcademicYear())
                        && !course.getId().equals(id));
        
        if (duplicateExists) {
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
        existCourse.setUpdateTime(LocalDateTime.now());
        
        int result = courseMapper.updateById(existCourse);
        if (result > 0) {
            return Result.success("课程模板更新成功");
        } else {
            return Result.error("课程模板更新失败");
        }
    }

    @Override
    public Result<Object> deleteCourse(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        
        // 如果是已开课的课程实例（有教师信息），不允许删除
        if (course.getTeacherId() != null) {
            return Result.error("已开课的课程实例不能删除");
        }
        
        int result = courseMapper.deleteById(id);
        if (result > 0) {
            return Result.success("课程模板删除成功");
        } else {
            return Result.error("课程模板删除失败");
        }
    }

    @Override
    public Result<Object> getCourseById(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        
        return Result.success(course);
    }

    @Override
    public Result<Object> getTeacherList() {
        Teacher queryTeacher = new Teacher();
        queryTeacher.setStatus(1);
        List<Teacher> teachers = teacherMapper.selectList(queryTeacher);
        return Result.success(teachers);
    }

    @Override
    public Result<Object> toggleApplicationStatus(Long id) {
        Course course = courseMapper.selectById(id);
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
        course.setUpdateTime(LocalDateTime.now());
        
        int result = courseMapper.updateById(course);
        if (result > 0) {
            String statusText = newStatus == 1 ? "开放申请" : "关闭申请";
            return Result.success("已" + statusText);
        } else {
            return Result.error("操作失败");
        }
    }
}