-- 插入我的申请菜单
-- 首先查找课程管理的父菜单ID
-- 假设课程管理菜单的menu_code为'course'

-- 插入我的申请菜单
INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`) 
SELECT '我的申请', 'my-applications', id, 'MENU', '/course/my-applications', '/course/my-applications', 'Document', 40, 1, '教师开课申请管理'
FROM `sys_menu` WHERE `menu_code` = 'course' AND `menu_type` = 'MENU';

-- 如果课程管理菜单不存在，则创建顶级菜单
INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`) 
SELECT '我的申请', 'my-applications', 0, 'MENU', '/course/my-applications', '/course/my-applications', 'Document', 40, 1, '教师开课申请管理'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'course' AND `menu_type` = 'MENU');

-- 为教师角色分配我的申请菜单权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.id, m.id
FROM `sys_role` r, `sys_menu` m
WHERE r.role_code = 'teacher' 
  AND m.menu_code = 'my-applications'
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_menu` rm 
    WHERE rm.role_id = r.id AND rm.menu_id = m.id
  ); 