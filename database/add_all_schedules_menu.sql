-- 添加管理员"所有课程表"菜单
SET NAMES utf8mb4;

-- 1. 添加"所有课程表"菜单项
INSERT INTO `sys_menu` (
    `menu_name`, `menu_code`, `parent_id`, `menu_type`, 
    `path`, `component`, `icon`, `sort`, `status`, `description`
) VALUES (
    '所有课程表', 'all-schedules', 12, 'MENU', 
    '/course/all-schedules', 'AllSchedules', 'Calendar', 60, 1, '管理员查看所有教师课程表'
);

-- 2. 获取刚插入的菜单ID和管理员角色ID
SET @menu_id = LAST_INSERT_ID();
SET @admin_role_id = (SELECT id FROM sys_role WHERE role_code = 'ADMIN' LIMIT 1);

-- 3. 为管理员角色分配"所有课程表"菜单权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@admin_role_id, @menu_id);

-- 4. 验证插入结果
SELECT 
    m.id,
    m.menu_name,
    m.menu_code,
    m.path,
    m.sort,
    (SELECT menu_name FROM sys_menu p WHERE p.id = m.parent_id) as parent_menu
FROM sys_menu m 
WHERE m.menu_code = 'all-schedules';

SELECT 
    r.role_name,
    m.menu_name,
    rm.role_id,
    rm.menu_id
FROM sys_role_menu rm
JOIN sys_role r ON rm.role_id = r.id
JOIN sys_menu m ON rm.menu_id = m.id
WHERE m.menu_code = 'all-schedules'; 