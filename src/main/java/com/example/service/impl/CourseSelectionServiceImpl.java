package com.example.service.impl;

import com.example.dto.CourseSelectionDTO;
import com.example.entity.CourseApplication;
import com.example.entity.Student;
import com.example.entity.StudentCourseSelection;
import com.example.mapper.CourseApplicationMapper;
import com.example.mapper.StudentCourseSelectionMapper;
import com.example.mapper.StudentMapper;
import com.example.service.CourseSelectionService;
import com.example.vo.AvailableCourseVO;
import com.example.vo.StudentCourseSelectionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseSelectionServiceImpl implements CourseSelectionService {

    @Autowired
    private StudentCourseSelectionMapper studentCourseSelectionMapper;
    
    @Autowired
    private StudentMapper studentMapper;
    
    @Autowired
    private CourseApplicationMapper courseApplicationMapper;

    @Override
    public List<AvailableCourseVO> getAvailableCourses(Long studentId) {
        return studentCourseSelectionMapper.getAvailableCourses(studentId);
    }

    @Override
    public List<StudentCourseSelectionVO> getSelectedCourses(Long studentId) {
        return studentCourseSelectionMapper.getSelectedCourses(studentId);
    }

    @Override
    @Transactional
    public void selectCourse(CourseSelectionDTO courseSelectionDTO) {
        Long studentId = courseSelectionDTO.getStudentId();
        Long courseApplicationId = courseSelectionDTO.getCourseApplicationId();
        
        // 检查学生是否已选择该课程
        Integer selectedCount = studentCourseSelectionMapper.checkCourseSelected(studentId, courseApplicationId);
        if (selectedCount > 0) {
            throw new RuntimeException("您已经选择了该课程");
        }
        
        // 检查课程是否已满员
        Integer isFull = studentCourseSelectionMapper.checkCourseFull(courseApplicationId);
        if (isFull > 0) {
            throw new RuntimeException("该课程已满员");
        }
        
        // 获取学生信息
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生信息不存在");
        }
        
        // 获取课程申请信息
        CourseApplication courseApplication = courseApplicationMapper.selectById(courseApplicationId);
        if (courseApplication == null) {
            throw new RuntimeException("课程信息不存在");
        }
        
        // 检查课程是否同一科目（课程名称相同）
        Long sameSubjectCount = studentCourseSelectionMapper.checkSameSubject(studentId, courseApplication.getCourseName());
        if (sameSubjectCount > 0) {
            throw new RuntimeException("同一科目不能选择多次");
        }
        
        // 创建选课记录
        StudentCourseSelection selection = new StudentCourseSelection();
        selection.setStudentId(studentId);
        selection.setStudentNo(student.getStudentNo());
        selection.setStudentName(student.getName());
        selection.setCourseApplicationId(courseApplicationId);
        selection.setCourseName(courseApplication.getCourseName());
        selection.setTeacherId(courseApplication.getTeacherId());
        selection.setTeacherName(courseApplication.getTeacherName());
        selection.setAcademicYear(courseApplication.getAcademicYear());
        selection.setSemester(courseApplication.getSemester());
        selection.setCourseHours(courseApplication.getCourseHours());
        selection.setSelectionTime(LocalDateTime.now());
        selection.setStatus(1);
        selection.setCreateTime(LocalDateTime.now());
        selection.setUpdateTime(LocalDateTime.now());
        selection.setDeleted(0);
        
        studentCourseSelectionMapper.insert(selection);
    }

    @Override
    @Transactional
    public void dropCourse(Long studentId, Long courseApplicationId) {
        // 检查学生是否已选择该课程
        Integer selectedCount = studentCourseSelectionMapper.checkCourseSelected(studentId, courseApplicationId);
        if (selectedCount == 0) {
            throw new RuntimeException("您未选择该课程");
        }
        
        // 更新选课记录状态为退课
        int result = studentCourseSelectionMapper.updateStatusToDrop(studentId, courseApplicationId);
        if (result == 0) {
            throw new RuntimeException("退课失败，请重试");
        }
    }
} 