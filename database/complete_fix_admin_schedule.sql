-- 完整修复管理员课表查看功能的脚本
SET NAMES utf8mb4;

-- ========== 第一部分：修复菜单配置 ==========

-- 1. 先删除可能已经错误插入的菜单
DELETE FROM sys_role_menu WHERE menu_id IN (SELECT id FROM sys_menu WHERE menu_code = 'all-schedules');
DELETE FROM sys_menu WHERE menu_code = 'all-schedules';

-- 2. 查找课程管理的父菜单ID
SET @course_parent_id = (SELECT id FROM sys_menu WHERE menu_code = 'course' AND menu_type = 'MENU' LIMIT 1);

-- 3. 如果课程管理菜单不存在，先创建它
INSERT IGNORE INTO sys_menu (
    menu_name, menu_code, parent_id, menu_type, 
    path, component, icon, sort, status, description
) VALUES (
    '课程管理', 'course', 0, 'MENU', 
    '/course', '', 'Reading', 4, 1, '课程管理模块'
);

-- 4. 重新获取课程管理菜单ID
SET @course_parent_id = (SELECT id FROM sys_menu WHERE menu_code = 'course' AND menu_type = 'MENU' LIMIT 1);

-- 5. 添加"所有课程表"菜单项
INSERT INTO sys_menu (
    menu_name, menu_code, parent_id, menu_type, 
    path, component, icon, sort, status, description
) VALUES (
    '所有课程表', 'all-schedules', @course_parent_id, 'MENU', 
    '/course/all-schedules', 'AllSchedules', 'Calendar', 60, 1, '管理员查看所有教师课程表'
);

-- 6. 获取刚插入的菜单ID和管理员角色ID
SET @menu_id = LAST_INSERT_ID();
SET @admin_role_id = (SELECT id FROM sys_role WHERE role_code = 'ADMIN' LIMIT 1);

-- 7. 为管理员角色分配"所有课程表"菜单权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (@admin_role_id, @menu_id);

-- ========== 第二部分：确保数据结构正确 ==========

-- 8. 确保课程申请表有剩余课时字段
ALTER TABLE sys_course_application 
ADD COLUMN IF NOT EXISTS remaining_hours int NOT NULL DEFAULT 0 COMMENT '剩余课时' AFTER course_hours;

-- 9. 更新剩余课时
UPDATE sys_course_application 
SET remaining_hours = course_hours 
WHERE remaining_hours = 0;

-- 10. 确保课程表的课时消耗默认为1
ALTER TABLE sys_schedule 
MODIFY COLUMN reduced_hours int(11) DEFAULT 1 COMMENT '减少的课时数';

UPDATE sys_schedule 
SET reduced_hours = 1 
WHERE reduced_hours != 1;

-- ========== 第三部分：创建测试数据 ==========

-- 11. 删除旧的测试数据
DELETE FROM sys_schedule WHERE teacher_name IN ('管理员测试教师1', '管理员测试教师2');
DELETE FROM sys_course_application WHERE teacher_name IN ('管理员测试教师1', '管理员测试教师2');

-- 12. 插入测试申请记录
INSERT INTO sys_course_application (
    template_id, course_name, teacher_id, teacher_name,
    academic_year, semester, max_students, course_hours, remaining_hours,
    reason, status, apply_time, review_time,
    reviewer_id, reviewer_name, review_comment
) VALUES 
(1, '管理员测试课程1', 100, '管理员测试教师1', '2024-2025', '第一学期', 50, 32, 28, '管理员测试申请', 1, NOW(), NOW(), 1, '管理员', '测试通过'),
(2, '管理员测试课程2', 101, '管理员测试教师2', '2024-2025', '第一学期', 40, 28, 24, '管理员测试申请', 1, NOW(), NOW(), 1, '管理员', '测试通过'),
(3, '管理员测试课程3', 100, '管理员测试教师1', '2024-2025', '第一学期', 45, 30, 26, '管理员测试申请', 1, NOW(), NOW(), 1, '管理员', '测试通过');

-- 13. 获取申请ID
SET @app1_id = (SELECT id FROM sys_course_application WHERE course_name = '管理员测试课程1' LIMIT 1);
SET @app2_id = (SELECT id FROM sys_course_application WHERE course_name = '管理员测试课程2' LIMIT 1);
SET @app3_id = (SELECT id FROM sys_course_application WHERE course_name = '管理员测试课程3' LIMIT 1);

-- 14. 插入测试课程表记录
INSERT INTO sys_schedule (
    course_id, course_name, teacher_id, teacher_name,
    academic_year, week_number, day_of_week, time_slot,
    time_range, classroom, reduced_hours
) VALUES 
-- 教师1的课程
(@app1_id, '管理员测试课程1', 100, '管理员测试教师1', '2024-2025', 1, 1, 1, '08:00-09:40', 'A101', 1),
(@app1_id, '管理员测试课程1', 100, '管理员测试教师1', '2024-2025', 1, 3, 2, '10:00-11:40', 'A101', 1),
(@app3_id, '管理员测试课程3', 100, '管理员测试教师1', '2024-2025', 1, 5, 1, '08:00-09:40', 'A102', 1),
(@app3_id, '管理员测试课程3', 100, '管理员测试教师1', '2024-2025', 1, 5, 3, '14:00-15:40', 'A102', 1),

-- 教师2的课程
(@app2_id, '管理员测试课程2', 101, '管理员测试教师2', '2024-2025', 1, 2, 1, '08:00-09:40', 'B201', 1),
(@app2_id, '管理员测试课程2', 101, '管理员测试教师2', '2024-2025', 1, 4, 2, '10:00-11:40', 'B201', 1),
(@app2_id, '管理员测试课程2', 101, '管理员测试教师2', '2024-2025', 1, 4, 4, '16:00-17:40', 'B201', 1),
(@app2_id, '管理员测试课程2', 101, '管理员测试教师2', '2024-2025', 1, 6, 3, '14:00-15:40', 'B202', 1);

-- ========== 第四部分：验证配置和数据 ==========

SELECT '=== 菜单配置验证 ===' as info;
SELECT 
    m.id,
    m.menu_name,
    m.menu_code,
    m.path,
    m.parent_id,
    (SELECT menu_name FROM sys_menu p WHERE p.id = m.parent_id) as parent_menu_name,
    m.sort,
    m.status
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

SELECT '=== 测试数据验证 ===' as info;
SELECT 
    COUNT(*) as total_test_schedules
FROM sys_schedule 
WHERE teacher_name LIKE '管理员测试教师%' AND deleted = 0;

SELECT 
    s.id,
    s.course_name,
    s.teacher_name,
    s.academic_year,
    s.week_number,
    s.day_of_week,
    s.time_slot,
    s.classroom,
    s.deleted
FROM sys_schedule s
WHERE s.teacher_name LIKE '管理员测试教师%' AND s.deleted = 0
ORDER BY s.teacher_name, s.week_number, s.day_of_week, s.time_slot;

SELECT '=== 申请记录验证 ===' as info;
SELECT 
    ca.id,
    ca.course_name,
    ca.teacher_name,
    ca.academic_year,
    ca.status,
    ca.course_hours,
    ca.remaining_hours
FROM sys_course_application ca
WHERE ca.teacher_name LIKE '管理员测试教师%' AND ca.deleted = 0
ORDER BY ca.teacher_name; 