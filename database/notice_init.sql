-- 创建通知表
CREATE TABLE IF NOT EXISTS `sys_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告标题',
  `publisher_id` bigint NOT NULL COMMENT '公告人ID',
  `publisher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告人姓名',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告内容（富文本）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '公告状态：0-草稿，1-已发布，2-已下线',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '公告时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_publisher_id`(`publisher_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_publish_time`(`publish_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- 插入通知管理相关菜单
INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`) VALUES
('通知管理', 'notice', 0, 'MENU', '/notice', '', 'Bell', 3, 1, '通知管理模块'),
('通知管理', 'notice-management', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'notice') t), 'MENU', '/notice/management', 'NoticeManagement', 'Edit', 1, 1, '通知管理页面'),
('通知公告', 'notice-view', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'notice') t), 'MENU', '/notice/view', 'NoticeView', 'View', 2, 1, '通知公告查看页面'),
('新增通知', 'notice:add', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'notice-management') t), 'BUTTON', '', '', '', 1, 1, '新增通知按钮'),
('编辑通知', 'notice:edit', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'notice-management') t), 'BUTTON', '', '', '', 2, 1, '编辑通知按钮'),
('删除通知', 'notice:delete', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'notice-management') t), 'BUTTON', '', '', '', 3, 1, '删除通知按钮'),
('发布通知', 'notice:publish', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'notice-management') t), 'BUTTON', '', '', '', 4, 1, '发布通知按钮'),
('下线通知', 'notice:offline', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'notice-management') t), 'BUTTON', '', '', '', 5, 1, '下线通知按钮');

-- 为管理员角色分配通知管理权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT 1, id FROM `sys_menu` WHERE `menu_code` IN ('notice', 'notice-management', 'notice:add', 'notice:edit', 'notice:delete', 'notice:publish', 'notice:offline');

-- 为普通用户角色分配通知查看权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT 2, id FROM `sys_menu` WHERE `menu_code` IN ('notice', 'notice-view');

-- 为教师角色分配通知查看权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT 3, id FROM `sys_menu` WHERE `menu_code` IN ('notice', 'notice-view');

-- 插入一些示例通知数据
INSERT INTO `sys_notice` (`title`, `publisher_id`, `publisher_name`, `content`, `status`, `publish_time`) VALUES
('系统维护通知', 1, 'admin', '<p>系统将于本周六晚上10点至次日上午8点进行维护升级，期间可能无法正常访问，请各位同学提前做好相关安排。</p><p>维护内容包括：</p><ul><li>服务器硬件升级</li><li>系统安全补丁更新</li><li>数据库优化</li></ul><p>如有紧急事务，请联系管理员。</p>', 1, NOW()),
('期末考试安排通知', 1, 'admin', '<p>各位同学：</p><p>期末考试即将开始，现将相关安排通知如下：</p><p><strong>考试时间：</strong>下周一至周五</p><p><strong>考试地点：</strong>各自班级教室</p><p><strong>注意事项：</strong></p><ol><li>请提前30分钟到达考场</li><li>携带学生证和身份证</li><li>不得携带电子设备</li></ol><p>预祝各位同学考试顺利！</p>', 1, NOW()),
('图书馆开放时间调整', 1, 'admin', '<p>因馆内设施维修，图书馆开放时间临时调整为：</p><p><strong>周一至周五：</strong>8:00-17:00</p><p><strong>周末：</strong>暂停开放</p><p>恢复正常开放时间另行通知，给大家带来的不便敬请谅解。</p>', 1, NOW()); 