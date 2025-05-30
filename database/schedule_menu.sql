-- 插入课程表管理菜单
-- 首先查找课程管理的父菜单ID
-- 假设课程管理菜单的menu_code为'course'

-- 插入课程表管理菜单
INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`) 
SELECT '课程表管理', 'schedule-management', id, 'MENU', '/course/schedule', '/course/schedule', 'Calendar', 50, 1, '教师课程表管理'
FROM `sys_menu` WHERE `menu_code` = 'course' AND `menu_type` = 'MENU';

-- 如果课程管理菜单不存在，则创建顶级菜单
INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`) 
SELECT '课程表管理', 'schedule-management', 0, 'MENU', '/course/schedule', '/course/schedule', 'Calendar', 50, 1, '教师课程表管理'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'course' AND `menu_type` = 'MENU'); 