-- 修复课程表结构，添加缺失的字段
SET NAMES utf8mb4;

-- 删除现有的课程表（如果存在）
DROP TABLE IF EXISTS `sys_course`;

-- 重新创建课程表，包含所有必需的字段
CREATE TABLE `sys_course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `application_id` bigint NULL DEFAULT NULL COMMENT '关联的申请ID',
  `template_id` bigint NULL DEFAULT NULL COMMENT '课程模板ID',
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
  `course_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '课程描述',
  `course_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '课程类型',
  `credits` int NULL DEFAULT 1 COMMENT '学分',
  `max_students` int NOT NULL COMMENT '最大学生数',
  `current_students` int DEFAULT 0 COMMENT '当前学生数',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '教师姓名',
  `course_hours` int NOT NULL DEFAULT 16 COMMENT '课程学时',
  `academic_year` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学年',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学期',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `allow_application` tinyint NOT NULL DEFAULT 1 COMMENT '是否允许申请：0-不允许，1-允许',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_course_name` (`course_name`),
  INDEX `idx_template_id`(`template_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_academic_year`(`academic_year` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程信息表' ROW_FORMAT = Dynamic;

-- 删除并重新创建课程模板表，简化字段结构
DROP TABLE IF EXISTS `sys_course_template`;
CREATE TABLE `sys_course_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `course_hours` int NOT NULL DEFAULT 16 COMMENT '课程学时',
  `academic_year` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学年',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学期',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_academic_year`(`academic_year` ASC) USING BTREE,
  INDEX `idx_semester`(`semester` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程模板表' ROW_FORMAT = Dynamic;

-- 插入简化的课程模板示例数据
INSERT INTO `sys_course_template` (`template_name`, `description`, `course_hours`, `academic_year`, `semester`) VALUES
('Java程序设计', 'Java面向对象程序设计，包含基础语法、面向对象编程、集合框架等内容', 48, '2024-2025', '第一学期'),
('数据库系统原理', '数据库系统的基本概念、关系模型、SQL语言、数据库设计等', 40, '2024-2025', '第一学期'),
('计算机网络', '计算机网络的基本概念、网络协议、网络安全等', 36, '2024-2025', '第二学期'),
('软件工程', '软件开发生命周期、需求分析、系统设计、测试等', 32, '2024-2025', '第二学期'),
('数据结构与算法', '线性表、树、图等数据结构的实现与算法设计', 48, '2024-2025', '第一学期'),
('操作系统原理', '操作系统的基本概念、进程管理、内存管理、文件系统等', 40, '2024-2025', '第二学期'),
('Web开发实践', 'HTML、CSS、JavaScript、前端框架等Web开发技术', 32, '2024-2025', '第一学期'),
('数据库实验', '数据库设计、SQL编程、数据库管理等实验', 16, '2024-2025', '第一学期'),
('计算机组成原理', '计算机硬件系统的组成、工作原理等', 40, '2024-2025', '第一学期'),
('人工智能导论', '人工智能的基本概念、机器学习、深度学习等', 36, '2024-2025', '第二学期');

-- 检查并修复开课申请表结构
-- 如果需要修复开课申请表的课程学时字段
ALTER TABLE `sys_course_application` 
MODIFY COLUMN `course_hours` int NOT NULL DEFAULT 16 COMMENT '课程学时';

-- 插入一些示例课程数据
INSERT INTO `sys_course` (`course_name`, `course_description`, `max_students`, `current_students`, `teacher_id`, `teacher_name`, `course_hours`, `academic_year`, `semester`, `status`, `allow_application`) VALUES
('Java程序设计基础', 'Java面向对象程序设计，包含基础语法、面向对象编程、集合框架等内容', 50, 0, 1, '张教授', 48, '2024-2025', '第一学期', 1, 1),
('数据库系统原理', '数据库系统的基本概念、关系模型、SQL语言、数据库设计等', 45, 0, 2, '李教授', 40, '2024-2025', '第一学期', 1, 1),
('计算机网络', '计算机网络的基本概念、网络协议、网络安全等', 40, 0, 3, '王教授', 36, '2024-2025', '第一学期', 1, 1),
('软件工程', '软件开发生命周期、需求分析、系统设计、测试等', 35, 0, 4, '刘教授', 32, '2024-2025', '第二学期', 1, 1),
('数据结构与算法', '线性表、树、图等数据结构的实现与算法设计', 45, 0, 1, '张教授', 48, '2024-2025', '第二学期', 1, 1),
('操作系统原理', '操作系统的基本概念、进程管理、内存管理、文件系统等', 40, 0, 2, '李教授', 40, '2024-2025', '第二学期', 1, 1),
('Web开发实践', 'HTML、CSS、JavaScript、前端框架等Web开发技术', 30, 0, 5, '陈教授', 32, '2024-2025', '第一学期', 1, 1),
('数据库实验', '数据库设计、SQL编程、数据库管理等实验', 25, 0, 2, '李教授', 16, '2024-2025', '第一学期', 1, 1),
('计算机组成原理', '计算机硬件系统的组成、工作原理等', 40, 0, 3, '王教授', 36, '2024-2025', '第一学期', 1, 1),
('人工智能导论', '人工智能的基本概念、机器学习、深度学习等', 35, 0, 4, '刘教授', 32, '2024-2025', '第二学期', 1, 1);

-- 更新课程模板数据，添加学时信息
UPDATE `sys_course_template` SET `course_hours` = 48 WHERE `template_name` = 'Java程序设计';
UPDATE `sys_course_template` SET `course_hours` = 40 WHERE `template_name` = '数据库系统原理';
UPDATE `sys_course_template` SET `course_hours` = 36 WHERE `template_name` = '计算机网络';
UPDATE `sys_course_template` SET `course_hours` = 32 WHERE `template_name` = '软件工程';
UPDATE `sys_course_template` SET `course_hours` = 48 WHERE `template_name` = '数据结构与算法';
UPDATE `sys_course_template` SET `course_hours` = 40 WHERE `template_name` = '操作系统原理';
UPDATE `sys_course_template` SET `course_hours` = 32 WHERE `template_name` = 'Web开发实践';
UPDATE `sys_course_template` SET `course_hours` = 16 WHERE `template_name` = '数据库实验';
UPDATE `sys_course_template` SET `course_hours` = 36 WHERE `template_name` = '计算机组成原理';
UPDATE `sys_course_template` SET `course_hours` = 32 WHERE `template_name` = '人工智能导论'; 