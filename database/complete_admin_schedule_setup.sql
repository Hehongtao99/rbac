-- 完整的管理员课表管理功能设置脚本
SET NAMES utf8mb4;

-- 首先执行课时管理修复（如果还没有执行）
-- 1. 添加剩余课时字段到课程申请表（如果不存在）
ALTER TABLE `sys_course_application` 
ADD COLUMN IF NOT EXISTS `remaining_hours` int NOT NULL DEFAULT 0 COMMENT '剩余课时' AFTER `course_hours`;

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
WHERE `reduced_hours` != 1;

-- 5. 添加"所有课程表"菜单项
INSERT INTO `sys_menu` (
    `menu_name`, `menu_code`, `parent_id`, `menu_type`, 
    `path`, `component`, `icon`, `sort`, `status`, `description`
) VALUES (
    '所有课程表', 'all-schedules', 12, 'MENU', 
    '/course/all-schedules', 'AllSchedules', 'Calendar', 60, 1, '管理员查看所有教师课程表'
);

-- 6. 获取刚插入的菜单ID和管理员角色ID
SET @menu_id = LAST_INSERT_ID();
SET @admin_role_id = (SELECT id FROM sys_role WHERE role_code = 'ADMIN' LIMIT 1);

-- 7. 为管理员角色分配"所有课程表"菜单权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@admin_role_id, @menu_id);

-- 8. 插入一些测试数据（如果需要）
INSERT IGNORE INTO `sys_course_application` (
    `template_id`, `course_name`, `teacher_id`, `teacher_name`, 
    `academic_year`, `semester`, `max_students`, `course_hours`, `remaining_hours`,
    `reason`, `status`, `apply_time`, `review_time`, 
    `reviewer_id`, `reviewer_name`, `review_comment`
) VALUES 
(1, '软件工程', 2, '张老师', '2024-2025', '第一学期', 50, 32, 30, '申请开设软件工程课程', 1, NOW(), NOW(), 1, '管理员', '申请通过'),
(2, '人工智能导论', 2, '张老师', '2024-2025', '第一学期', 40, 28, 26, '申请开设人工智能导论课程', 1, NOW(), NOW(), 1, '管理员', '申请通过'),
(3, '数据库系统原理', 3, '李老师', '2024-2025', '第一学期', 45, 30, 28, '申请开设数据库系统原理课程', 1, NOW(), NOW(), 1, '管理员', '申请通过');

-- 9. 插入一些测试课程表数据
INSERT IGNORE INTO `sys_schedule` (
    `course_id`, `course_name`, `teacher_id`, `teacher_name`, 
    `academic_year`, `week_number`, `day_of_week`, `time_slot`, 
    `time_range`, `classroom`, `reduced_hours`
) VALUES 
((SELECT id FROM sys_course_application WHERE course_name = '软件工程' AND teacher_name = '张老师' LIMIT 1), 
 '软件工程', 2, '张老师', '2024-2025', 1, 1, 1, '08:00-09:40', 'A101', 1),
((SELECT id FROM sys_course_application WHERE course_name = '软件工程' AND teacher_name = '张老师' LIMIT 1), 
 '软件工程', 2, '张老师', '2024-2025', 1, 3, 2, '10:00-11:40', 'A101', 1),
((SELECT id FROM sys_course_application WHERE course_name = '人工智能导论' AND teacher_name = '张老师' LIMIT 1), 
 '人工智能导论', 2, '张老师', '2024-2025', 1, 2, 1, '08:00-09:40', 'B201', 1),
((SELECT id FROM sys_course_application WHERE course_name = '数据库系统原理' AND teacher_name = '李老师' LIMIT 1), 
 '数据库系统原理', 3, '李老师', '2024-2025', 1, 4, 3, '14:00-15:40', 'C301', 1);

-- 10. 验证设置结果
SELECT '=== 菜单配置验证 ===' as info;
SELECT 
    m.id,
    m.menu_name,
    m.menu_code,
    m.path,
    m.sort,
    (SELECT menu_name FROM sys_menu p WHERE p.id = m.parent_id) as parent_menu
FROM sys_menu m 
WHERE m.menu_code = 'all-schedules';

SELECT '=== 权限分配验证 ===' as info;
SELECT 
    r.role_name,
    m.menu_name,
    rm.role_id,
    rm.menu_id
FROM sys_role_menu rm
JOIN sys_role r ON rm.role_id = r.id
JOIN sys_menu m ON rm.menu_id = m.id
WHERE m.menu_code = 'all-schedules';

SELECT '=== 课程表数据验证 ===' as info;
SELECT 
    s.id,
    s.course_name,
    s.teacher_name,
    s.academic_year,
    s.week_number,
    s.day_of_week,
    s.time_slot,
    s.reduced_hours
FROM sys_schedule s
ORDER BY s.teacher_name, s.week_number, s.day_of_week, s.time_slot;

SELECT '=== 申请记录验证 ===' as info;
SELECT 
    ca.id,
    ca.course_name,
    ca.teacher_name,
    ca.course_hours,
    ca.remaining_hours,
    ca.status
FROM sys_course_application ca
WHERE ca.status = 1
ORDER BY ca.teacher_name; 