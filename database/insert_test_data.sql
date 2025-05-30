-- 插入测试申请数据
SET NAMES utf8mb4;

-- 为教师ID=2的用户插入一些申请通过的记录
INSERT INTO `sys_course_application` (
    `template_id`, `course_name`, `teacher_id`, `teacher_name`, 
    `academic_year`, `semester`, `max_students`, `course_hours`, `remaining_hours`,
    `reason`, `status`, `apply_time`, `review_time`, 
    `reviewer_id`, `reviewer_name`, `review_comment`
) VALUES 
(1, '软件工程', 2, '张老师', '2024-2025', '第一学期', 50, 28, 28, '申请开设软件工程课程', 1, NOW(), NOW(), 1, '管理员', '申请通过'),
(2, '人工智能导论', 2, '张老师', '2024-2025', '第一学期', 40, 30, 30, '申请开设人工智能导论课程', 1, NOW(), NOW(), 1, '管理员', '申请通过'),
(3, '数据库系统原理', 2, '张老师', '2024-2025', '第一学期', 45, 32, 32, '申请开设数据库系统原理课程', 1, NOW(), NOW(), 1, '管理员', '申请通过');

-- 为教师ID=3的用户插入一些申请通过的记录
INSERT INTO `sys_course_application` (
    `template_id`, `course_name`, `teacher_id`, `teacher_name`, 
    `academic_year`, `semester`, `max_students`, `course_hours`, `remaining_hours`,
    `reason`, `status`, `apply_time`, `review_time`, 
    `reviewer_id`, `reviewer_name`, `review_comment`
) VALUES 
(4, '计算机网络', 3, '李老师', '2024-2025', '第一学期', 35, 26, 26, '申请开设计算机网络课程', 1, NOW(), NOW(), 1, '管理员', '申请通过'),
(5, 'Web开发实践', 3, '李老师', '2024-2025', '第一学期', 30, 24, 24, '申请开设Web开发实践课程', 1, NOW(), NOW(), 1, '管理员', '申请通过'); 