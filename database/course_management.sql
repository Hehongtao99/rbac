-- 创建课程表
CREATE TABLE IF NOT EXISTS `sys_course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
  `course_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程描述',
  `max_students` int NOT NULL COMMENT '课程人数',
  `current_students` int DEFAULT 0 COMMENT '当前人数',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '教师姓名',
  `course_hours` int NOT NULL COMMENT '课程学时',
  `academic_year` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学年',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_course_name` (`course_name`),
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_academic_year`(`academic_year` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程信息表' ROW_FORMAT = Dynamic;

-- 插入课程管理相关菜单
INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`) VALUES
('课程管理', 'course', 0, 'MENU', '/course', '', 'Reading', 4, 1, '课程管理模块'),
('课程管理', 'course-management', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'course') t), 'MENU', '/course/management', 'CourseManagement', 'Edit', 1, 1, '课程管理页面'),
('新增课程', 'course:add', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'course-management') t), 'BUTTON', '', '', '', 1, 1, '新增课程按钮'),
('编辑课程', 'course:edit', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'course-management') t), 'BUTTON', '', '', '', 2, 1, '编辑课程按钮'),
('删除课程', 'course:delete', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'course-management') t), 'BUTTON', '', '', '', 3, 1, '删除课程按钮'),
('查看课程', 'course:view', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'course-management') t), 'BUTTON', '', '', '', 4, 1, '查看课程按钮');

-- 为管理员角色分配课程管理权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT 1, id FROM `sys_menu` WHERE `menu_code` IN ('course', 'course-management', 'course:add', 'course:edit', 'course:delete', 'course:view');

-- 为教师角色分配课程查看权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT 3, id FROM `sys_menu` WHERE `menu_code` IN ('course', 'course-management', 'course:view');

-- 插入一些示例课程数据（需要先有教师数据）
-- 请根据实际的教师ID修改以下插入语句
-- INSERT INTO `sys_course` (`course_name`, `course_description`, `max_students`, `teacher_id`, `teacher_name`, `course_hours`, `academic_year`) VALUES
-- ('计算机基础', '计算机基础课程，包含计算机系统结构、操作系统基础等内容', 50, 1, '张老师', 32, '2024-2025上期'),
-- ('数据结构与算法', '数据结构与算法设计，包含线性表、树、图等数据结构的实现与算法设计', 40, 1, '张老师', 48, '2024-2025上期'),
-- ('数据库系统原理', '数据库系统的基本概念、关系模型、SQL语言、数据库设计等', 45, 2, '李老师', 40, '2024-2025下期'); 