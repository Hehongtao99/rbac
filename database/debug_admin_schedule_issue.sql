-- 调试管理员课表问题的脚本
SET NAMES utf8mb4;

-- 1. 检查菜单是否存在
SELECT '=== 菜单检查 ===' as info;
SELECT 
    id, menu_name, menu_code, path, parent_id, status
FROM sys_menu 
WHERE menu_code = 'all-schedules';

-- 2. 检查管理员角色
SELECT '=== 管理员角色检查 ===' as info;
SELECT 
    id, role_name, role_code, status
FROM sys_role 
WHERE role_code = 'ADMIN';

-- 3. 检查菜单权限分配
SELECT '=== 菜单权限检查 ===' as info;
SELECT 
    rm.id,
    r.role_name,
    m.menu_name,
    m.menu_code,
    rm.role_id,
    rm.menu_id
FROM sys_role_menu rm
JOIN sys_role r ON rm.role_id = r.id
JOIN sys_menu m ON rm.menu_id = m.id
WHERE m.menu_code = 'all-schedules';

-- 4. 检查课程表数据
SELECT '=== 课程表数据检查 ===' as info;
SELECT 
    COUNT(*) as total_schedules
FROM sys_schedule 
WHERE deleted = 0;

SELECT 
    academic_year,
    COUNT(*) as count
FROM sys_schedule 
WHERE deleted = 0
GROUP BY academic_year;

-- 5. 显示部分课程表数据
SELECT '=== 部分课程表数据样例 ===' as info;
SELECT 
    id,
    course_name,
    teacher_name,
    academic_year,
    week_number,
    day_of_week,
    time_slot,
    classroom,
    deleted
FROM sys_schedule 
WHERE deleted = 0
ORDER BY teacher_name, week_number, day_of_week, time_slot
LIMIT 10;

-- 6. 检查课程申请数据
SELECT '=== 课程申请数据检查 ===' as info;
SELECT 
    COUNT(*) as total_applications
FROM sys_course_application 
WHERE deleted = 0;

SELECT 
    status,
    COUNT(*) as count
FROM sys_course_application 
WHERE deleted = 0
GROUP BY status;

-- 7. 显示部分申请数据
SELECT '=== 部分申请数据样例 ===' as info;
SELECT 
    id,
    course_name,
    teacher_name,
    academic_year,
    status,
    course_hours,
    remaining_hours
FROM sys_course_application 
WHERE deleted = 0 AND status = 1
ORDER BY teacher_name
LIMIT 5;

-- 8. 检查是否有课程表对应的申请记录
SELECT '=== 课程表与申请关联检查 ===' as info;
SELECT 
    s.id as schedule_id,
    s.course_id,
    s.course_name as schedule_course_name,
    s.teacher_name as schedule_teacher,
    ca.id as application_id,
    ca.course_name as application_course_name,
    ca.teacher_name as application_teacher,
    ca.status
FROM sys_schedule s
LEFT JOIN sys_course_application ca ON s.course_id = ca.id
WHERE s.deleted = 0
LIMIT 5;

-- 9. 如果需要，创建测试数据
-- 先删除可能存在的测试数据
DELETE FROM sys_schedule WHERE course_name IN ('测试课程1', '测试课程2');
DELETE FROM sys_course_application WHERE course_name IN ('测试课程1', '测试课程2');

-- 插入测试申请记录
INSERT INTO sys_course_application (
    template_id, course_name, teacher_id, teacher_name,
    academic_year, semester, max_students, course_hours, remaining_hours,
    reason, status, apply_time, review_time,
    reviewer_id, reviewer_name, review_comment
) VALUES 
(1, '测试课程1', 2, '测试教师1', '2024-2025', '第一学期', 50, 32, 30, '测试申请', 1, NOW(), NOW(), 1, '管理员', '测试通过'),
(2, '测试课程2', 3, '测试教师2', '2024-2025', '第一学期', 40, 28, 26, '测试申请', 1, NOW(), NOW(), 1, '管理员', '测试通过');

-- 获取刚插入的申请ID
SET @app1_id = (SELECT id FROM sys_course_application WHERE course_name = '测试课程1' AND teacher_name = '测试教师1' LIMIT 1);
SET @app2_id = (SELECT id FROM sys_course_application WHERE course_name = '测试课程2' AND teacher_name = '测试教师2' LIMIT 1);

-- 插入测试课程表记录
INSERT INTO sys_schedule (
    course_id, course_name, teacher_id, teacher_name,
    academic_year, week_number, day_of_week, time_slot,
    time_range, classroom, reduced_hours
) VALUES 
(@app1_id, '测试课程1', 2, '测试教师1', '2024-2025', 1, 1, 1, '08:00-09:40', 'A101', 1),
(@app1_id, '测试课程1', 2, '测试教师1', '2024-2025', 1, 3, 2, '10:00-11:40', 'A101', 1),
(@app2_id, '测试课程2', 3, '测试教师2', '2024-2025', 1, 2, 1, '08:00-09:40', 'B201', 1),
(@app2_id, '测试课程2', 3, '测试教师2', '2024-2025', 1, 4, 3, '14:00-15:40', 'C301', 1);

-- 10. 最终验证
SELECT '=== 最终数据验证 ===' as info;
SELECT 
    s.id,
    s.course_name,
    s.teacher_name,
    s.academic_year,
    s.week_number,
    s.day_of_week,
    s.time_slot,
    s.deleted
FROM sys_schedule s
WHERE s.deleted = 0 AND s.academic_year = '2024-2025'
ORDER BY s.teacher_name, s.week_number, s.day_of_week, s.time_slot; 