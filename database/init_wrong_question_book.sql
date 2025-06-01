-- 错题本功能完整初始化脚本
-- 请在数据库中执行此脚本来初始化错题本功能

-- 1. 创建错题本表
CREATE TABLE IF NOT EXISTS `wrong_question_book` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '错题本名称',
  `description` varchar(500) DEFAULT NULL COMMENT '错题本描述',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `question_count` int DEFAULT 0 COMMENT '题目数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='错题本表';

-- 2. 创建错题本题目关联表
CREATE TABLE IF NOT EXISTS `wrong_question_book_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_id` bigint NOT NULL COMMENT '错题本ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `wrong_answer` text COMMENT '错误答案',
  `correct_answer` text COMMENT '正确答案',
  `add_reason` varchar(200) DEFAULT NULL COMMENT '加入原因',
  `mastery_level` tinyint DEFAULT 0 COMMENT '掌握程度：0-未掌握 1-部分掌握 2-已掌握',
  `practice_count` int DEFAULT 0 COMMENT '练习次数',
  `last_practice_time` datetime DEFAULT NULL COMMENT '最后练习时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_book_question` (`book_id`, `question_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_mastery_level` (`mastery_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='错题本题目关联表';

-- 3. 创建错题本练习记录表
CREATE TABLE IF NOT EXISTS `wrong_question_practice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_id` bigint NOT NULL COMMENT '错题本ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) NOT NULL COMMENT '练习标题',
  `total_count` int NOT NULL COMMENT '总题数',
  `correct_count` int DEFAULT 0 COMMENT '正确题数',
  `wrong_count` int DEFAULT 0 COMMENT '错误题数',
  `score` int DEFAULT 0 COMMENT '得分',
  `total_score` int DEFAULT 0 COMMENT '总分',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `duration` int DEFAULT NULL COMMENT '用时（分钟）',
  `status` tinyint DEFAULT 0 COMMENT '状态：0-进行中 1-已完成',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='错题本练习记录表';

-- 4. 创建错题本练习题目表
CREATE TABLE IF NOT EXISTS `wrong_question_practice_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `practice_id` bigint NOT NULL COMMENT '练习记录ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `user_answer` text COMMENT '用户答案',
  `is_correct` tinyint DEFAULT 0 COMMENT '是否正确：0-错误 1-正确',
  `score` int DEFAULT 0 COMMENT '题目分值',
  `user_score` int DEFAULT 0 COMMENT '用户得分',
  `question_order` int DEFAULT 0 COMMENT '题目顺序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`),
  KEY `idx_practice_id` (`practice_id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_question_order` (`question_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='错题本练习题目表';

-- 5. 插入错题本权限菜单
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

-- 6. 为管理员角色分配错题本权限（假设管理员角色ID为1）
-- 先删除可能存在的重复权限，再重新插入
DELETE FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` IN (
    SELECT id FROM `sys_menu` WHERE (`menu_code` LIKE 'wrong-%')
);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT 1, id FROM `sys_menu` WHERE (`menu_code` LIKE 'wrong-%');

-- 7. 为普通用户角色分配错题本权限（假设普通用户角色ID为2）
-- 先删除可能存在的重复权限，再重新插入
DELETE FROM `sys_role_menu` WHERE `role_id` = 2 AND `menu_id` IN (
    SELECT id FROM `sys_menu` WHERE (`menu_code` LIKE 'wrong-%')
);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT 2, id FROM `sys_menu` WHERE (`menu_code` LIKE 'wrong-%');

-- 8. 插入默认错题本（可选）
INSERT IGNORE INTO `wrong_question_book` (`name`, `description`, `user_id`) VALUES 
('我的错题本', '默认错题本，收集所有做错的题目', 1);

-- 初始化完成
SELECT '错题本功能初始化完成！' as message; 