-- 课程管理系统相关表结构
SET NAMES utf8mb4;

-- 创建课程模板表
CREATE TABLE IF NOT EXISTS `sys_course_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `course_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程类型：理论课、实验课、实践课',
  `credits` int NOT NULL DEFAULT 1 COMMENT '学分',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程模板表' ROW_FORMAT = Dynamic;

-- 创建开课申请表
CREATE TABLE IF NOT EXISTS `sys_course_application` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `template_id` bigint NOT NULL COMMENT '课程模板ID',
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
  `teacher_id` bigint NOT NULL COMMENT '申请教师ID',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请教师姓名',
  `academic_year` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学年',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学期',
  `max_students` int NOT NULL COMMENT '计划人数',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请理由',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-待审核，1-已通过，2-已拒绝',
  `apply_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `reviewer_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核人姓名',
  `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核意见',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_template_id`(`template_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_academic_year`(`academic_year` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '开课申请表' ROW_FORMAT = Dynamic;

-- 创建课程实例表（开课申请通过后生成的实际课程）
CREATE TABLE IF NOT EXISTS `sys_course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `application_id` bigint NULL DEFAULT NULL COMMENT '关联的申请ID',
  `template_id` bigint NOT NULL COMMENT '课程模板ID',
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
  `course_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '课程描述',
  `course_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程类型',
  `credits` int NOT NULL DEFAULT 1 COMMENT '学分',
  `max_students` int NOT NULL COMMENT '最大学生数',
  `current_students` int DEFAULT 0 COMMENT '当前学生数',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '教师姓名',
  `academic_year` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学年',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学期',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `allow_application` tinyint NOT NULL DEFAULT 1 COMMENT '是否允许申请：0-不允许，1-允许',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_template_id`(`template_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_academic_year`(`academic_year` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程信息表' ROW_FORMAT = Dynamic;

-- 插入一些示例课程模板数据
INSERT INTO `sys_course_template` (`template_name`, `description`, `course_type`, `credits`) VALUES
('Java程序设计', 'Java面向对象程序设计，包含基础语法、面向对象编程、集合框架等内容', '理论课', 3),
('数据库系统原理', '数据库系统的基本概念、关系模型、SQL语言、数据库设计等', '理论课', 3),
('计算机网络', '计算机网络的基本概念、网络协议、网络安全等', '理论课', 3),
('软件工程', '软件开发生命周期、需求分析、系统设计、测试等', '理论课', 3),
('数据结构与算法', '线性表、树、图等数据结构的实现与算法设计', '理论课', 4),
('操作系统原理', '操作系统的基本概念、进程管理、内存管理、文件系统等', '理论课', 3),
('Web开发实践', 'HTML、CSS、JavaScript、前端框架等Web开发技术', '实践课', 2),
('数据库实验', '数据库设计、SQL编程、数据库管理等实验', '实验课', 1),
('计算机组成原理', '计算机硬件系统的组成、工作原理等', '理论课', 3),
('人工智能导论', '人工智能的基本概念、机器学习、深度学习等', '理论课', 3);

-- 插入课程管理相关菜单
INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`) VALUES
('课程管理', 'course', 0, 'MENU', '/course', '', 'Reading', 4, 1, '课程管理模块');

-- 获取课程管理菜单ID
SET @course_menu_id = LAST_INSERT_ID();

INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`) VALUES
('开课实例管理', 'course-management', @course_menu_id, 'MENU', '/course/management', 'CourseManagement', 'Edit', 1, 1, '开课实例管理页面'),
('课程模板管理', 'course-template-management', @course_menu_id, 'MENU', '/course/template', 'CourseTemplateManagement', 'Document', 2, 1, '课程模板管理页面'),
('申请开课', 'course-application', @course_menu_id, 'MENU', '/course/application', 'CourseApplication', 'Plus', 3, 1, '申请开课页面'),
('开课申请审批', 'course-application-review', @course_menu_id, 'MENU', '/course/application-review', 'CourseApplicationReview', 'Check', 4, 1, '开课申请审批页面');

-- 插入按钮权限
INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`) VALUES
('新增课程', 'course:add', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'course-management') t), 'BUTTON', '', '', '', 1, 1, '新增课程按钮'),
('编辑课程', 'course:edit', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'course-management') t), 'BUTTON', '', '', '', 2, 1, '编辑课程按钮'),
('删除课程', 'course:delete', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'course-management') t), 'BUTTON', '', '', '', 3, 1, '删除课程按钮'),
('查看课程', 'course:view', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'course-management') t), 'BUTTON', '', '', '', 4, 1, '查看课程按钮'),
('新增模板', 'template:add', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'course-template-management') t), 'BUTTON', '', '', '', 1, 1, '新增模板按钮'),
('编辑模板', 'template:edit', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'course-template-management') t), 'BUTTON', '', '', '', 2, 1, '编辑模板按钮'),
('删除模板', 'template:delete', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'course-template-management') t), 'BUTTON', '', '', '', 3, 1, '删除模板按钮'),
('提交申请', 'application:add', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'course-application') t), 'BUTTON', '', '', '', 1, 1, '提交申请按钮'),
('编辑申请', 'application:edit', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'course-application') t), 'BUTTON', '', '', '', 2, 1, '编辑申请按钮'),
('审批申请', 'application:review', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'course-application-review') t), 'BUTTON', '', '', '', 1, 1, '审批申请按钮');

-- 为管理员角色分配所有课程管理权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT 1, id FROM `sys_menu` WHERE `menu_code` IN (
  'course', 'course-management', 'course-template-management', 'course-application', 'course-application-review',
  'course:add', 'course:edit', 'course:delete', 'course:view',
  'template:add', 'template:edit', 'template:delete',
  'application:add', 'application:edit', 'application:review'
);

-- 为教师角色分配相应权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT 3, id FROM `sys_menu` WHERE `menu_code` IN (
  'course', 'course-management', 'course-template-management', 'course-application',
  'course:view', 'application:add', 'application:edit'
);

-- 添加学生角色（如果不存在）
INSERT IGNORE INTO `sys_role` (`role_name`, `role_code`, `description`, `status`) VALUES
('学生', 'STUDENT', '学生角色，拥有选课等权限', 1);

-- 为学生角色分配基本权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT (SELECT id FROM sys_role WHERE role_code = 'STUDENT'), id FROM `sys_menu` WHERE `menu_code` IN (
  'course', 'course-management', 'course:view'
); 