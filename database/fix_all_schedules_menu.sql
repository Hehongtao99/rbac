-- 修复管理员"所有课程表"菜单配置
SET NAMES utf8mb4;

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

-- 5. 添加"所有课程表"菜单项，使用正确的父菜单ID
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

-- 8. 验证配置结果
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

-- 9. 显示完整的课程管理菜单树
SELECT '=== 课程管理菜单树 ===' as info;
SELECT 
    m.id,
    m.menu_name,
    m.menu_code,
    m.parent_id,
    m.sort,
    m.status
FROM sys_menu m 
WHERE m.parent_id = @course_parent_id OR m.id = @course_parent_id
ORDER BY m.parent_id, m.sort; 