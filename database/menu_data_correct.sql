-- 正确的题库管理菜单数据插入SQL
-- 使用正确的字段名：menu_name, menu_code, menu_type

-- 插入题库管理主菜单（如果不存在）
INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '题库管理', 'question-bank', 0, 'MENU', '/question-bank', '', 'Collection', 3, 1, '题库管理模块', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'question-bank' AND `menu_type` = 'MENU');

-- 获取题库管理菜单ID
SET @question_bank_menu_id = (SELECT id FROM `sys_menu` WHERE `menu_code` = 'question-bank' AND `menu_type` = 'MENU' LIMIT 1);

-- 插入题库管理子菜单（如果不存在）
INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '题库列表', 'question-bank-manage', @question_bank_menu_id, 'MENU', '/question-bank-manage', 'QuestionBankManage', 'Document', 1, 1, '题库列表页面', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'question-bank-manage' AND `menu_type` = 'MENU');

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '题目管理', 'question-manage', @question_bank_menu_id, 'MENU', '/question-manage', 'QuestionManage', 'Edit', 2, 1, '题目管理页面', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'question-manage' AND `menu_type` = 'MENU');

-- 获取子菜单ID
SET @question_bank_list_id = (SELECT id FROM `sys_menu` WHERE `menu_code` = 'question-bank-manage' AND `menu_type` = 'MENU' LIMIT 1);
SET @question_manage_id = (SELECT id FROM `sys_menu` WHERE `menu_code` = 'question-manage' AND `menu_type` = 'MENU' LIMIT 1);

-- 插入题库管理相关按钮权限（如果不存在）
INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '新增题库', 'question-bank:add', @question_bank_list_id, 'BUTTON', '', '', '', 1, 1, '新增题库按钮', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'question-bank:add' AND `menu_type` = 'BUTTON');

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '编辑题库', 'question-bank:edit', @question_bank_list_id, 'BUTTON', '', '', '', 2, 1, '编辑题库按钮', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'question-bank:edit' AND `menu_type` = 'BUTTON');

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '删除题库', 'question-bank:delete', @question_bank_list_id, 'BUTTON', '', '', '', 3, 1, '删除题库按钮', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'question-bank:delete' AND `menu_type` = 'BUTTON');

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '新增题目', 'question:add', @question_manage_id, 'BUTTON', '', '', '', 1, 1, '新增题目按钮', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'question:add' AND `menu_type` = 'BUTTON');

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '编辑题目', 'question:edit', @question_manage_id, 'BUTTON', '', '', '', 2, 1, '编辑题目按钮', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'question:edit' AND `menu_type` = 'BUTTON');

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '删除题目', 'question:delete', @question_manage_id, 'BUTTON', '', '', '', 3, 1, '删除题目按钮', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'question:delete' AND `menu_type` = 'BUTTON');

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '批量删除题目', 'question:batch-delete', @question_manage_id, 'BUTTON', '', '', '', 4, 1, '批量删除题目按钮', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'question:batch-delete' AND `menu_type` = 'BUTTON');

-- 为管理员角色分配题库管理权限（假设管理员角色ID为1）
-- 先删除可能存在的重复权限，再重新插入
DELETE FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` IN (
    SELECT id FROM `sys_menu` WHERE (`menu_code` LIKE 'question%')
);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT 1, id FROM `sys_menu` WHERE (`menu_code` LIKE 'question%'); 