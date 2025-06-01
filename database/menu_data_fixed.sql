-- 修复后的题库管理菜单数据插入SQL
-- 首先检查是否已存在相同的菜单，避免重复插入

-- 插入题库管理主菜单（如果不存在）
INSERT INTO `sys_menu` (`name`, `path`, `component`, `icon`, `type`, `parent_id`, `sort`, `status`, `create_time`, `update_time`) 
SELECT '题库管理', '/question-bank', 'Layout', 'Collection', 1, NULL, 3, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `name` = '题库管理' AND `type` = 1);

-- 获取题库管理菜单ID
SET @question_bank_menu_id = (SELECT id FROM `sys_menu` WHERE `name` = '题库管理' AND `type` = 1 LIMIT 1);

-- 插入题库管理子菜单（如果不存在）
INSERT INTO `sys_menu` (`name`, `path`, `component`, `icon`, `type`, `parent_id`, `sort`, `status`, `create_time`, `update_time`) 
SELECT '题库列表', '/question-bank-manage', 'QuestionBankManage', 'Document', 2, @question_bank_menu_id, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `path` = '/question-bank-manage' AND `type` = 2);

INSERT INTO `sys_menu` (`name`, `path`, `component`, `icon`, `type`, `parent_id`, `sort`, `status`, `create_time`, `update_time`) 
SELECT '题目管理', '/question-manage', 'QuestionManage', 'Edit', 2, @question_bank_menu_id, 2, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `path` = '/question-manage' AND `type` = 2);

-- 获取子菜单ID
SET @question_bank_list_id = (SELECT id FROM `sys_menu` WHERE `path` = '/question-bank-manage' AND `type` = 2 LIMIT 1);
SET @question_manage_id = (SELECT id FROM `sys_menu` WHERE `path` = '/question-manage' AND `type` = 2 LIMIT 1);

-- 插入题库管理相关按钮权限（如果不存在）
INSERT INTO `sys_menu` (`name`, `path`, `component`, `icon`, `type`, `parent_id`, `sort`, `status`, `create_time`, `update_time`) 
SELECT '新增题库', 'question-bank:add', '', '', 3, @question_bank_list_id, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `path` = 'question-bank:add' AND `type` = 3);

INSERT INTO `sys_menu` (`name`, `path`, `component`, `icon`, `type`, `parent_id`, `sort`, `status`, `create_time`, `update_time`) 
SELECT '编辑题库', 'question-bank:edit', '', '', 3, @question_bank_list_id, 2, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `path` = 'question-bank:edit' AND `type` = 3);

INSERT INTO `sys_menu` (`name`, `path`, `component`, `icon`, `type`, `parent_id`, `sort`, `status`, `create_time`, `update_time`) 
SELECT '删除题库', 'question-bank:delete', '', '', 3, @question_bank_list_id, 3, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `path` = 'question-bank:delete' AND `type` = 3);

INSERT INTO `sys_menu` (`name`, `path`, `component`, `icon`, `type`, `parent_id`, `sort`, `status`, `create_time`, `update_time`) 
SELECT '新增题目', 'question:add', '', '', 3, @question_manage_id, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `path` = 'question:add' AND `type` = 3);

INSERT INTO `sys_menu` (`name`, `path`, `component`, `icon`, `type`, `parent_id`, `sort`, `status`, `create_time`, `update_time`) 
SELECT '编辑题目', 'question:edit', '', '', 3, @question_manage_id, 2, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `path` = 'question:edit' AND `type` = 3);

INSERT INTO `sys_menu` (`name`, `path`, `component`, `icon`, `type`, `parent_id`, `sort`, `status`, `create_time`, `update_time`) 
SELECT '删除题目', 'question:delete', '', '', 3, @question_manage_id, 3, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `path` = 'question:delete' AND `type` = 3);

INSERT INTO `sys_menu` (`name`, `path`, `component`, `icon`, `type`, `parent_id`, `sort`, `status`, `create_time`, `update_time`) 
SELECT '批量删除题目', 'question:batch-delete', '', '', 3, @question_manage_id, 4, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `path` = 'question:batch-delete' AND `type` = 3);

-- 为管理员角色分配题库管理权限（假设管理员角色ID为1）
-- 先删除可能存在的重复权限，再重新插入
DELETE FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` IN (
    SELECT id FROM `sys_menu` WHERE (`path` LIKE '/question%' OR `path` LIKE 'question%' OR `name` = '题库管理')
);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT 1, id FROM `sys_menu` WHERE (`path` LIKE '/question%' OR `path` LIKE 'question%' OR `name` = '题库管理'); 