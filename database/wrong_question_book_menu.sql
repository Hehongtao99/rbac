-- 错题本权限菜单数据插入SQL
-- 使用正确的字段名：menu_name, menu_code, menu_type

-- 插入错题本主菜单（如果不存在）
INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '错题本', 'wrong-question-book', 0, 'MENU', '/wrong-question-book', 'WrongQuestionBook', 'Collection', 5, 1, '错题本管理模块', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'wrong-question-book' AND `menu_type` = 'MENU');

-- 获取错题本菜单ID
SET @wrong_book_menu_id = (SELECT id FROM `sys_menu` WHERE `menu_code` = 'wrong-question-book' AND `menu_type` = 'MENU' LIMIT 1);

-- 插入错题本相关按钮权限（如果不存在）
INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '查看错题本', 'wrong-book:view', @wrong_book_menu_id, 'BUTTON', '', '', '', 1, 1, '查看错题本权限', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'wrong-book:view' AND `menu_type` = 'BUTTON');

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '新建错题本', 'wrong-book:create', @wrong_book_menu_id, 'BUTTON', '', '', '', 2, 1, '新建错题本权限', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'wrong-book:create' AND `menu_type` = 'BUTTON');

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '编辑错题本', 'wrong-book:edit', @wrong_book_menu_id, 'BUTTON', '', '', '', 3, 1, '编辑错题本权限', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'wrong-book:edit' AND `menu_type` = 'BUTTON');

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '删除错题本', 'wrong-book:delete', @wrong_book_menu_id, 'BUTTON', '', '', '', 4, 1, '删除错题本权限', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'wrong-book:delete' AND `menu_type` = 'BUTTON');

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '添加错题', 'wrong-book:add-question', @wrong_book_menu_id, 'BUTTON', '', '', '', 5, 1, '添加错题到错题本权限', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'wrong-book:add-question' AND `menu_type` = 'BUTTON');

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '移除错题', 'wrong-book:remove-question', @wrong_book_menu_id, 'BUTTON', '', '', '', 6, 1, '从错题本移除错题权限', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'wrong-book:remove-question' AND `menu_type` = 'BUTTON');

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '更新掌握程度', 'wrong-book:update-mastery', @wrong_book_menu_id, 'BUTTON', '', '', '', 7, 1, '更新题目掌握程度权限', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'wrong-book:update-mastery' AND `menu_type` = 'BUTTON');

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`, `create_time`, `update_time`) 
SELECT '错题练习', 'wrong-book:practice', @wrong_book_menu_id, 'BUTTON', '', '', '', 8, 1, '错题练习权限', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_code` = 'wrong-book:practice' AND `menu_type` = 'BUTTON');

-- 为管理员角色分配错题本权限（假设管理员角色ID为1）
-- 先删除可能存在的重复权限，再重新插入
DELETE FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` IN (
    SELECT id FROM `sys_menu` WHERE (`menu_code` LIKE 'wrong-%')
);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT 1, id FROM `sys_menu` WHERE (`menu_code` LIKE 'wrong-%');

-- 为普通用户角色分配错题本权限（假设普通用户角色ID为2）
-- 先删除可能存在的重复权限，再重新插入
DELETE FROM `sys_role_menu` WHERE `role_id` = 2 AND `menu_id` IN (
    SELECT id FROM `sys_menu` WHERE (`menu_code` LIKE 'wrong-%')
);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT 2, id FROM `sys_menu` WHERE (`menu_code` LIKE 'wrong-%'); 