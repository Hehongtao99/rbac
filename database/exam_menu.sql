-- 考试管理菜单数据插入SQL

-- 插入考试管理主菜单（如果不存在）
INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '考试管理', 'exam', 0, 'MENU', '/exam', '', 'Edit', 4, 1, '考试管理模块', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'exam' AND `menu_type` = 'MENU');

-- 获取考试管理菜单ID
SET @exam_menu_id = (SELECT id FROM `sys_menu` WHERE `menu_code` = 'exam' AND `menu_type` = 'MENU' LIMIT 1);

-- 插入考试管理子菜单（如果不存在）
INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '开始考试', 'exam-start', @exam_menu_id, 'MENU', '/exam-start', 'ExamStart', 'VideoPlay', 1, 1, '开始考试页面', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'exam-start' AND `menu_type` = 'MENU');

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '考试记录', 'exam-list', @exam_menu_id, 'MENU', '/exam-list', 'ExamList', 'Document', 2, 1, '考试记录页面', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'exam-list' AND `menu_type` = 'MENU');

-- 获取子菜单ID
SET @exam_start_id = (SELECT id FROM `sys_menu` WHERE `menu_code` = 'exam-start' AND `menu_type` = 'MENU' LIMIT 1);
SET @exam_list_id = (SELECT id FROM `sys_menu` WHERE `menu_code` = 'exam-list' AND `menu_type` = 'MENU' LIMIT 1);

-- 插入考试相关按钮权限
INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '开始考试', 'exam:start', @exam_start_id, 'BUTTON', '', '', '', 1, 1, '开始考试按钮', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'exam:start' AND `menu_type` = 'BUTTON');

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '提交考试', 'exam:submit', @exam_start_id, 'BUTTON', '', '', '', 2, 1, '提交考试按钮', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'exam:submit' AND `menu_type` = 'BUTTON');

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '查看结果', 'exam:result', @exam_list_id, 'BUTTON', '', '', '', 1, 1, '查看考试结果按钮', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'exam:result' AND `menu_type` = 'BUTTON');

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '继续考试', 'exam:continue', @exam_list_id, 'BUTTON', '', '', '', 2, 1, '继续考试按钮', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'exam:continue' AND `menu_type` = 'BUTTON');

-- 为管理员角色分配考试菜单权限
SET @admin_role_id = (SELECT id FROM `sys_role` WHERE `role_code` = 'ADMIN' LIMIT 1);

-- 分配主菜单权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_time`)
SELECT @admin_role_id, @exam_menu_id, NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = @admin_role_id AND `menu_id` = @exam_menu_id);

-- 分配子菜单权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_time`)
SELECT @admin_role_id, @exam_start_id, NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = @admin_role_id AND `menu_id` = @exam_start_id);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_time`)
SELECT @admin_role_id, @exam_list_id, NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = @admin_role_id AND `menu_id` = @exam_list_id);

-- 分配按钮权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_time`)
SELECT @admin_role_id, id, NOW()
FROM `sys_menu` 
WHERE `menu_code` IN ('exam:start', 'exam:submit', 'exam:result', 'exam:continue') 
AND `menu_type` = 'BUTTON'
AND NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = @admin_role_id AND `menu_id` = `sys_menu`.`id`); 