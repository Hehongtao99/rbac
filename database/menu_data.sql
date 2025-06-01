-- 插入题库管理菜单数据
INSERT INTO `sys_menu` (`name`, `path`, `component`, `icon`, `type`, `parent_id`, `sort`, `status`, `create_time`, `update_time`) VALUES
('题库管理', '/question-bank', 'Layout', 'Collection', 1, NULL, 3, 1, NOW(), NOW());

-- 获取刚插入的题库管理菜单ID（假设为最新插入的ID）
SET @question_bank_menu_id = LAST_INSERT_ID();

-- 插入题库管理子菜单
INSERT INTO `sys_menu` (`name`, `path`, `component`, `icon`, `type`, `parent_id`, `sort`, `status`, `create_time`, `update_time`) VALUES
('题库列表', '/question-bank-manage', 'QuestionBankManage', 'Document', 2, @question_bank_menu_id, 1, 1, NOW(), NOW()),
('题目管理', '/question-manage', 'QuestionManage', 'Edit', 2, @question_bank_menu_id, 2, 1, NOW(), NOW());

-- 插入题库管理相关按钮权限
INSERT INTO `sys_menu` (`name`, `path`, `component`, `icon`, `type`, `parent_id`, `sort`, `status`, `create_time`, `update_time`) VALUES
('新增题库', 'question-bank:add', '', '', 3, (SELECT id FROM sys_menu WHERE path = '/question-bank-manage' AND type = 2), 1, 1, NOW(), NOW()),
('编辑题库', 'question-bank:edit', '', '', 3, (SELECT id FROM sys_menu WHERE path = '/question-bank-manage' AND type = 2), 2, 1, NOW(), NOW()),
('删除题库', 'question-bank:delete', '', '', 3, (SELECT id FROM sys_menu WHERE path = '/question-bank-manage' AND type = 2), 3, 1, NOW(), NOW()),
('新增题目', 'question:add', '', '', 3, (SELECT id FROM sys_menu WHERE path = '/question-manage' AND type = 2), 1, 1, NOW(), NOW()),
('编辑题目', 'question:edit', '', '', 3, (SELECT id FROM sys_menu WHERE path = '/question-manage' AND type = 2), 2, 1, NOW(), NOW()),
('删除题目', 'question:delete', '', '', 3, (SELECT id FROM sys_menu WHERE path = '/question-manage' AND type = 2), 3, 1, NOW(), NOW()),
('批量删除题目', 'question:batch-delete', '', '', 3, (SELECT id FROM sys_menu WHERE path = '/question-manage' AND type = 2), 4, 1, NOW(), NOW());

-- 为管理员角色分配题库管理权限（假设管理员角色ID为1）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT 1, id FROM `sys_menu` WHERE `path` LIKE '/question%' OR `path` LIKE 'question%' OR `name` = '题库管理'; 