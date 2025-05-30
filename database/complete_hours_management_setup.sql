-- 完整的课时管理设置脚本
SET NAMES utf8mb4;

-- 1. 添加剩余课时字段到课程申请表
ALTER TABLE `sys_course_application` 
ADD COLUMN `remaining_hours` int NOT NULL DEFAULT 0 COMMENT '剩余课时' AFTER `course_hours`;

-- 2. 将现有记录的剩余课时设置为课程总课时
UPDATE `sys_course_application` 
SET `remaining_hours` = `course_hours` 
WHERE `remaining_hours` = 0;

-- 3. 修复课程表的默认课时消耗为1
ALTER TABLE `sys_schedule` 
MODIFY COLUMN `reduced_hours` int(11) DEFAULT 1 COMMENT '减少的课时数';

-- 4. 将现有课程表记录中的课时消耗改为1
UPDATE `sys_schedule` 
SET `reduced_hours` = 1 
WHERE `reduced_hours` = 2;

-- 5. 清理测试数据（如果需要重新开始）
-- DELETE FROM `sys_course_application` WHERE `teacher_id` IN (2, 3);
-- DELETE FROM `sys_schedule` WHERE `teacher_id` IN (2, 3);

-- 6. 插入带有剩余课时的测试数据
INSERT INTO `sys_course_application` (
    `template_id`, `course_name`, `teacher_id`, `teacher_name`, 
    `academic_year`, `semester`, `max_students`, `course_hours`, `remaining_hours`,
    `reason`, `status`, `apply_time`, `review_time`, 
    `reviewer_id`, `reviewer_name`, `review_comment`
) VALUES 
(1, '软件工程', 2, '张老师', '2024-2025', '第一学期', 50, 28, 28, '申请开设软件工程课程', 1, NOW(), NOW(), 1, '管理员', '申请通过'),
(2, '人工智能导论', 2, '张老师', '2024-2025', '第一学期', 40, 30, 30, '申请开设人工智能导论课程', 1, NOW(), NOW(), 1, '管理员', '申请通过'),
(3, '数据库系统原理', 2, '张老师', '2024-2025', '第一学期', 45, 32, 32, '申请开设数据库系统原理课程', 1, NOW(), NOW(), 1, '管理员', '申请通过')
ON DUPLICATE KEY UPDATE
    `remaining_hours` = VALUES(`remaining_hours`);

INSERT INTO `sys_course_application` (
    `template_id`, `course_name`, `teacher_id`, `teacher_name`, 
    `academic_year`, `semester`, `max_students`, `course_hours`, `remaining_hours`,
    `reason`, `status`, `apply_time`, `review_time`, 
    `reviewer_id`, `reviewer_name`, `review_comment`
) VALUES 
(4, '计算机网络', 3, '李老师', '2024-2025', '第一学期', 35, 26, 26, '申请开设计算机网络课程', 1, NOW(), NOW(), 1, '管理员', '申请通过'),
(5, 'Web开发实践', 3, '李老师', '2024-2025', '第一学期', 30, 24, 24, '申请开设Web开发实践课程', 1, NOW(), NOW(), 1, '管理员', '申请通过')
ON DUPLICATE KEY UPDATE
    `remaining_hours` = VALUES(`remaining_hours`); 