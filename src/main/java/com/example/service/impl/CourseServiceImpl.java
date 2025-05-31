package com.example.service.impl;

import com.example.common.PageResult;
import com.example.dto.CourseDTO;
import com.example.dto.CoursePageDTO;
import com.example.entity.Course;
import com.example.entity.Teacher;
import com.example.mapper.CourseMapper;
import com.example.mapper.TeacherMapper;
import com.example.service.CourseService;
import com.example.util.PageUtil;
import com.example.vo.CourseVO;
import com.example.vo.TeacherVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private TeacherMapper teacherMapper;

    // 状态映射
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();
    
    static {
        STATUS_MAP.put(0, "禁用");
        STATUS_MAP.put(1, "启用");
    }

    @Override
    public PageResult<CourseVO> getCourseList(CoursePageDTO pageDTO) {
        int offset = PageUtil.calculateOffset(pageDTO.getPage(), pageDTO.getSize());
        List<Course> courses = courseMapper.selectPage(offset, pageDTO.getSize(), pageDTO.getKeyword());
        long total = courseMapper.selectCount(new Course());
        
        // 转换为VO
        List<CourseVO> voList = courses.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageUtil.createPageResult(pageDTO.getPage(), pageDTO.getSize(), total, voList);
    }

    @Override
    public PageResult<CourseVO> getCourseTemplateList(CoursePageDTO pageDTO) {
        int offset = PageUtil.calculateOffset(pageDTO.getPage(), pageDTO.getSize());
        List<Course> courses = courseMapper.selectPage(offset, pageDTO.getSize(), pageDTO.getKeyword());
        
        // 过滤出课程模板（teacherId为空）
        List<Course> templates = courses.stream()
                .filter(course -> course.getTeacherId() == null)
                .collect(Collectors.toList());
        
        // 转换为VO
        List<CourseVO> voList = templates.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageUtil.createPageResult(pageDTO.getPage(), pageDTO.getSize(), templates.size(), voList);
    }

    @Override
    public PageResult<CourseVO> getCourseInstanceList(CoursePageDTO pageDTO) {
        int offset = PageUtil.calculateOffset(pageDTO.getPage(), pageDTO.getSize());
        List<Course> courses = courseMapper.selectPage(offset, pageDTO.getSize(), pageDTO.getKeyword());
        
        // 过滤出开课实例（teacherId不为空）
        List<Course> instances = courses.stream()
                .filter(course -> course.getTeacherId() != null)
                .collect(Collectors.toList());
        
        // 转换为VO
        List<CourseVO> voList = instances.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageUtil.createPageResult(pageDTO.getPage(), pageDTO.getSize(), instances.size(), voList);
    }

    @Override
    public void createCourse(CourseDTO courseDTO) {
        // 检查课程名称是否已存在
        List<Course> existCourses = courseMapper.selectList(new Course());
        boolean exists = existCourses.stream()
                .anyMatch(course -> course.getCourseName().equals(courseDTO.getCourseName()) 
                        && course.getAcademicYear().equals(courseDTO.getAcademicYear()));
        
        if (exists) {
            throw new RuntimeException("该学年下已存在同名课程");
        }
        
        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        
        // 创建课程模板时不指定教师信息
        course.setTeacherId(null);
        course.setTeacherName(null);
        course.setCurrentStudents(0);
        course.setStatus(1);
        course.setAllowApplication(1);
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());
        course.setDeleted(0);
        
        courseMapper.insert(course);
    }

    @Override
    public void updateCourse(Long id, CourseDTO courseDTO) {
        Course existCourse = courseMapper.selectById(id);
        if (existCourse == null) {
            throw new RuntimeException("课程不存在");
        }
        
        // 如果是已开课的课程实例（有教师信息），不允许编辑
        if (existCourse.getTeacherId() != null) {
            throw new RuntimeException("已开课的课程实例不能编辑，请联系申请教师进行修改");
        }
        
        // 检查课程名称是否已被其他课程使用
        List<Course> allCourses = courseMapper.selectList(new Course());
        boolean duplicateExists = allCourses.stream()
                .anyMatch(course -> course.getCourseName().equals(courseDTO.getCourseName()) 
                        && course.getAcademicYear().equals(courseDTO.getAcademicYear())
                        && !course.getId().equals(id));
        
        if (duplicateExists) {
            throw new RuntimeException("该学年下已存在同名课程");
        }
        
        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        course.setId(id);
        course.setUpdateTime(LocalDateTime.now());
        
        courseMapper.updateById(course);
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        
        // 如果是已开课的课程实例（有教师信息），不允许删除
        if (course.getTeacherId() != null) {
            throw new RuntimeException("已开课的课程实例不能删除");
        }
        
        courseMapper.deleteById(id);
    }

    @Override
    public CourseVO getCourseById(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        
        return convertToVO(course);
    }

    @Override
    public List<TeacherVO> getTeacherList() {
        Teacher queryTeacher = new Teacher();
        queryTeacher.setStatus(1);
        List<Teacher> teachers = teacherMapper.selectList(queryTeacher);
        
        return teachers.stream()
                .map(this::convertTeacherToVO)
                .collect(Collectors.toList());
    }

    @Override
    public void toggleApplicationStatus(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        
        // 只有课程模板才能切换申请状态
        if (course.getTeacherId() != null) {
            throw new RuntimeException("开课实例不能修改申请状态");
        }
        
        // 切换申请状态
        Integer newStatus = course.getAllowApplication() == 1 ? 0 : 1;
        course.setAllowApplication(newStatus);
        course.setUpdateTime(LocalDateTime.now());
        
        courseMapper.updateById(course);
    }
    
    /**
     * 转换为CourseVO对象
     */
    private CourseVO convertToVO(Course course) {
        CourseVO vo = new CourseVO();
        BeanUtils.copyProperties(course, vo);
        
        // 设置状态名称
        vo.setStatusName(STATUS_MAP.get(course.getStatus()));
        
        // 设置是否允许申请
        vo.setAllowApplication(course.getAllowApplication() == 1);
        
        return vo;
    }
    
    /**
     * 转换为TeacherVO对象
     */
    private TeacherVO convertTeacherToVO(Teacher teacher) {
        TeacherVO vo = new TeacherVO();
        BeanUtils.copyProperties(teacher, vo);
        
        // 设置状态名称
        vo.setStatusName(STATUS_MAP.get(teacher.getStatus()));
        
        return vo;
    }
}