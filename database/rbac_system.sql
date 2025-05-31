/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : rbac_system

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 31/05/2025 22:00:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_class_course_hours
-- ----------------------------
DROP TABLE IF EXISTS `sys_class_course_hours`;
CREATE TABLE `sys_class_course_hours`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `application_id` bigint NOT NULL COMMENT '课程申请ID',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '班级名称',
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '教师姓名',
  `academic_year` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学年',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学期',
  `total_hours` int NOT NULL COMMENT '总课时',
  `used_hours` int NOT NULL DEFAULT 0 COMMENT '已使用课时',
  `remaining_hours` int NOT NULL COMMENT '剩余课时',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_application_class`(`application_id` ASC, `class_id` ASC) USING BTREE,
  INDEX `idx_application_id`(`application_id` ASC) USING BTREE,
  INDEX `idx_class_id`(`class_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_academic_year`(`academic_year` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '班级课程课时管理表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_class_course_hours
-- ----------------------------
INSERT INTO `sys_class_course_hours` VALUES (1, 2, 14, '软工2021级1班', '数据结构与算法', 4, '2', '2024-2025', '第一学期', 48, 2, 46, 1, '2025-05-31 12:39:14', '2025-05-31 12:39:14', 0);
INSERT INTO `sys_class_course_hours` VALUES (2, 2, 15, '软工2021级2班', '数据结构与算法', 4, '2', '2024-2025', '第一学期', 48, 0, 48, 1, '2025-05-31 12:39:25', '2025-05-31 12:39:25', 0);
INSERT INTO `sys_class_course_hours` VALUES (3, 5, 15, '软工2021级2班', 'Web开发实践', 4, '2', '2024-2025', '第一学期', 32, 1, 31, 1, '2025-05-31 15:59:33', '2025-05-31 15:59:33', 0);
INSERT INTO `sys_class_course_hours` VALUES (4, 5, 14, '软工2021级1班', 'Web开发实践', 4, '2', '2024-2025', '第一学期', 32, 1, 31, 1, '2025-05-31 17:15:04', '2025-05-31 17:15:04', 0);
INSERT INTO `sys_class_course_hours` VALUES (5, 8, 14, '软工2021级1班', 'AFDSAFDSA', 4, '2', '2023-2024', '第一学期', 16, 2, 14, 1, '2025-05-31 18:26:49', '2025-05-31 18:27:43', 0);
INSERT INTO `sys_class_course_hours` VALUES (6, 8, 15, '软工2021级2班', 'AFDSAFDSA', 4, '2', '2023-2024', '第一学期', 16, 1, 15, 1, '2025-05-31 18:27:32', '2025-05-31 18:27:32', 0);

-- ----------------------------
-- Table structure for sys_course
-- ----------------------------
DROP TABLE IF EXISTS `sys_course`;
CREATE TABLE `sys_course`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `application_id` bigint NULL DEFAULT NULL COMMENT '关联的申请ID',
  `template_id` bigint NULL DEFAULT NULL COMMENT '课程模板ID',
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
  `course_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '课程描述',
  `course_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '课程类型',
  `credits` int NULL DEFAULT 1 COMMENT '学分',
  `max_students` int NOT NULL COMMENT '最大学生数',
  `current_students` int NULL DEFAULT 0 COMMENT '当前学生数',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '教师姓名',
  `course_hours` int NOT NULL COMMENT '课程学时',
  `academic_year` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学年',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学期',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `allow_application` tinyint NOT NULL DEFAULT 1 COMMENT '是否允许申请：0-不允许，1-允许',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_course_name`(`course_name` ASC) USING BTREE,
  INDEX `idx_template_id`(`template_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_academic_year`(`academic_year` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_course
-- ----------------------------
INSERT INTO `sys_course` VALUES (1, NULL, NULL, 'Java程序设计基础', 'Java面向对象程序设计，包含基础语法、面向对象编程、集合框架等内容', NULL, 1, 50, 0, 1, '张教授', 48, '2024-2025', '第一学期', 1, 1, '2025-05-30 08:05:24', '2025-05-30 08:05:24', 0);
INSERT INTO `sys_course` VALUES (2, NULL, NULL, '数据库系统原理', '数据库系统的基本概念、关系模型、SQL语言、数据库设计等', NULL, 1, 45, 0, 2, '李教授', 40, '2024-2025', '第一学期', 1, 1, '2025-05-30 08:05:24', '2025-05-30 08:05:24', 0);
INSERT INTO `sys_course` VALUES (3, NULL, NULL, '计算机网络', '计算机网络的基本概念、网络协议、网络安全等', NULL, 1, 40, 0, 3, '王教授', 36, '2024-2025', '第一学期', 1, 1, '2025-05-30 08:05:24', '2025-05-30 08:05:24', 0);
INSERT INTO `sys_course` VALUES (4, NULL, NULL, '软件工程', '软件开发生命周期、需求分析、系统设计、测试等', NULL, 1, 35, 0, 4, '刘教授', 28, '2024-2025', '第二学期', 1, 1, '2025-05-30 08:05:24', '2025-05-30 08:05:24', 0);
INSERT INTO `sys_course` VALUES (5, NULL, NULL, '数据结构与算法', '线性表、树、图等数据结构的实现与算法设计', NULL, 1, 45, 0, 1, '张教授', 48, '2024-2025', '第二学期', 1, 1, '2025-05-30 08:05:24', '2025-05-30 08:05:24', 0);
INSERT INTO `sys_course` VALUES (6, NULL, NULL, '操作系统原理', '操作系统的基本概念、进程管理、内存管理、文件系统等', NULL, 1, 40, 0, 2, '李教授', 40, '2024-2025', '第二学期', 1, 1, '2025-05-30 08:05:24', '2025-05-30 08:05:24', 0);
INSERT INTO `sys_course` VALUES (7, NULL, NULL, 'Web开发实践', 'HTML、CSS、JavaScript、前端框架等Web开发技术', NULL, 1, 30, 0, 5, '陈教授', 26, '2024-2025', '第一学期', 1, 1, '2025-05-30 08:05:24', '2025-05-30 08:05:24', 0);
INSERT INTO `sys_course` VALUES (8, NULL, NULL, '数据库实验', '数据库设计、SQL编程、数据库管理等实验', NULL, 1, 25, 0, 2, '李教授', 16, '2024-2025', '第一学期', 1, 1, '2025-05-30 08:05:24', '2025-05-30 08:05:24', 0);
INSERT INTO `sys_course` VALUES (9, NULL, NULL, '计算机组成原理', '计算机硬件系统的组成、工作原理等', NULL, 1, 40, 0, 3, '王教授', 36, '2024-2025', '第一学期', 1, 1, '2025-05-30 08:05:24', '2025-05-30 08:05:24', 0);
INSERT INTO `sys_course` VALUES (10, NULL, NULL, '人工智能导论', '人工智能的基本概念、机器学习、深度学习等', NULL, 1, 35, 0, 4, '刘教授', 30, '2024-2025', '第二学期', 1, 1, '2025-05-30 08:05:24', '2025-05-30 08:05:24', 0);

-- ----------------------------
-- Table structure for sys_course_application
-- ----------------------------
DROP TABLE IF EXISTS `sys_course_application`;
CREATE TABLE `sys_course_application`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `template_id` bigint NOT NULL COMMENT '课程模板ID',
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
  `teacher_id` bigint NOT NULL COMMENT '申请教师ID',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请教师姓名',
  `academic_year` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学年',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学期',
  `max_students` int NOT NULL COMMENT '计划人数',
  `course_hours` int NOT NULL DEFAULT 16 COMMENT '课程学时',
  `remaining_hours` int NOT NULL DEFAULT 0 COMMENT '剩余课时',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请理由',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-待审核，1-已通过，2-已拒绝',
  `apply_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `reviewer_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核人姓名',
  `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '审核意见',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_template_id`(`template_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_academic_year`(`academic_year` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '开课申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_course_application
-- ----------------------------
INSERT INTO `sys_course_application` VALUES (8, 31, 'AFDSAFDSA', 4, '2', '2023-2024', '第一学期', 50, 16, 16, '发多少阿凡达书法大赛发烧', 1, '2025-05-31 17:46:58', '2025-05-31 17:47:06', 11, '1', 'FDSAFDASFDSAFDAS', '2025-05-31 17:46:58', '2025-05-31 17:47:06', 0);
INSERT INTO `sys_course_application` VALUES (9, 32, 'FFFFFFFFFFFFFFFFF', 4, '2', '2024-2025', '第一学期', 50, 16, 16, '发大水范德萨范德萨发', 1, '2025-05-31 18:20:46', '2025-05-31 18:20:54', 11, '1', '范德萨啊啊啊啊啊啊啊啊啊啊', '2025-05-31 18:20:46', '2025-05-31 18:20:54', 0);

-- ----------------------------
-- Table structure for sys_course_template
-- ----------------------------
DROP TABLE IF EXISTS `sys_course_template`;
CREATE TABLE `sys_course_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '描述',
  `course_hours` int NOT NULL DEFAULT 16 COMMENT '课程学时',
  `academic_year` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学年',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学期',
  `max_students` int NOT NULL COMMENT '计划人数',
  `college_id` bigint NULL DEFAULT NULL COMMENT '学院ID',
  `major_id` bigint NULL DEFAULT NULL COMMENT '专业ID',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_academic_year`(`academic_year` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_semester`(`semester` ASC) USING BTREE,
  INDEX `idx_college_id`(`college_id` ASC) USING BTREE,
  INDEX `idx_major_id`(`major_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程模板表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_course_template
-- ----------------------------
INSERT INTO `sys_course_template` VALUES (1, 'Java程序设计', 'Java面向对象程序设计，包含基础语法、面向对象编程、集合框架等内容', 48, '2024-2025', '第二学期', 50, 1, 4, 1, '2025-05-30 08:14:36', '2025-05-31 10:43:55', 0);
INSERT INTO `sys_course_template` VALUES (2, '数据库系统原理', '数据库系统的基本概念、关系模型、SQL语言、数据库设计等', 40, '2024-2025', '第一学期', 50, 1, 4, 1, '2025-05-30 08:14:36', '2025-05-31 10:43:55', 0);
INSERT INTO `sys_course_template` VALUES (3, '计算机网络', '计算机网络的基本概念、网络协议、网络安全等', 36, '2024-2025', '第一学期', 50, 1, 4, 1, '2025-05-30 08:14:36', '2025-05-31 10:43:55', 0);
INSERT INTO `sys_course_template` VALUES (4, '软件工程', '软件开发生命周期、需求分析、系统设计、测试等', 32, '2024-2025', '第一学期', 50, 1, 5, 1, '2025-05-30 08:14:36', '2025-05-31 10:43:55', 0);
INSERT INTO `sys_course_template` VALUES (5, '数据结构与算法', '线性表、树、图等数据结构的实现与算法设计', 48, '2024-2025', '第一学期', 50, 1, 4, 1, '2025-05-30 08:14:36', '2025-05-31 10:43:56', 0);
INSERT INTO `sys_course_template` VALUES (6, '操作系统原理', '操作系统的基本概念、进程管理、内存管理、文件系统等', 40, '2024-2025', '第一学期', 50, NULL, NULL, 1, '2025-05-30 08:14:36', '2025-05-30 08:14:36', 0);
INSERT INTO `sys_course_template` VALUES (7, 'Web开发实践', 'HTML、CSS、JavaScript、前端框架等Web开发技术', 32, '2024-2025', '第一学期', 50, 1, 5, 1, '2025-05-30 08:14:36', '2025-05-31 10:43:56', 0);
INSERT INTO `sys_course_template` VALUES (8, '数据库实验', '数据库设计、SQL编程、数据库管理等实验', 16, '2024-2025', '第一学期', 50, NULL, NULL, 1, '2025-05-30 08:14:36', '2025-05-30 08:14:36', 0);
INSERT INTO `sys_course_template` VALUES (9, '计算机组成原理', '计算机硬件系统的组成、工作原理等', 40, '2024-2025', '第一学期', 50, 1, 4, 1, '2025-05-30 08:14:36', '2025-05-31 10:43:56', 0);
INSERT INTO `sys_course_template` VALUES (10, '人工智能导论', '人工智能的基本概念、机器学习、深度学习等', 36, '2024-2025', '第一学期', 50, NULL, NULL, 1, '2025-05-30 08:14:36', '2025-05-30 08:14:36', 0);
INSERT INTO `sys_course_template` VALUES (11, 'Java程序设计', 'Java面向对象程序设计，包含基础语法、面向对象编程、集合框架等内容', 48, '2024-2025', '第一学期', 50, 1, 4, 1, '2025-05-30 11:59:14', '2025-05-31 10:43:55', 0);
INSERT INTO `sys_course_template` VALUES (12, '数据库系统原理', '数据库系统的基本概念、关系模型、SQL语言、数据库设计等', 40, '2024-2025', '第一学期', 50, 1, 4, 1, '2025-05-30 11:59:14', '2025-05-31 10:43:55', 0);
INSERT INTO `sys_course_template` VALUES (13, '计算机网络', '计算机网络的基本概念、网络协议、网络安全等', 36, '2024-2025', '第二学期', 45, 1, 4, 1, '2025-05-30 11:59:14', '2025-05-31 10:43:55', 0);
INSERT INTO `sys_course_template` VALUES (14, '软件工程', '软件开发生命周期、需求分析、系统设计、测试等', 32, '2024-2025', '第二学期', 40, 1, 5, 1, '2025-05-30 11:59:14', '2025-05-31 10:43:55', 0);
INSERT INTO `sys_course_template` VALUES (15, '数据结构与算法', '线性表、树、图等数据结构的实现与算法设计', 48, '2024-2025', '第一学期', 50, 1, 4, 1, '2025-05-30 11:59:14', '2025-05-31 10:43:56', 0);
INSERT INTO `sys_course_template` VALUES (16, '操作系统原理', '操作系统的基本概念、进程管理、内存管理、文件系统等', 40, '2024-2025', '第二学期', 45, NULL, NULL, 1, '2025-05-30 11:59:14', '2025-05-30 11:59:14', 0);
INSERT INTO `sys_course_template` VALUES (17, 'Web开发实践', 'HTML、CSS、JavaScript、前端框架等Web开发技术', 32, '2024-2025', '第一学期', 40, 1, 5, 1, '2025-05-30 11:59:14', '2025-05-31 10:43:56', 0);
INSERT INTO `sys_course_template` VALUES (18, '数据库实验', '数据库设计、SQL编程、数据库管理等实验', 16, '2024-2025', '第一学期', 30, NULL, NULL, 1, '2025-05-30 11:59:14', '2025-05-30 11:59:14', 0);
INSERT INTO `sys_course_template` VALUES (19, '计算机组成原理', '计算机硬件系统的组成、工作原理等', 40, '2024-2025', '第一学期', 50, 1, 4, 1, '2025-05-30 11:59:14', '2025-05-31 10:43:56', 0);
INSERT INTO `sys_course_template` VALUES (20, '人工智能导论', '人工智能的基本概念、机器学习、深度学习等', 36, '2024-2025', '第二学期', 45, NULL, NULL, 1, '2025-05-30 11:59:14', '2025-05-30 11:59:14', 0);
INSERT INTO `sys_course_template` VALUES (21, 'Java程序设计', 'Java面向对象程序设计，包含基础语法、面向对象编程、集合框架等内容', 48, '2024-2025', '第一学期', 50, 1, 4, 1, '2025-05-30 12:04:00', '2025-05-31 10:43:55', 0);
INSERT INTO `sys_course_template` VALUES (22, '数据库系统原理', '数据库系统的基本概念、关系模型、SQL语言、数据库设计等', 40, '2024-2025', '第一学期', 50, 3, 9, 1, '2025-05-30 12:04:00', '2025-05-31 18:49:08', 0);
INSERT INTO `sys_course_template` VALUES (23, '计算机网络', '计算机网络的基本概念、网络协议、网络安全等', 36, '2024-2025', '第二学期', 45, 1, 4, 1, '2025-05-30 12:04:00', '2025-05-31 10:43:55', 0);
INSERT INTO `sys_course_template` VALUES (24, '软件工程', '软件开发生命周期、需求分析、系统设计、测试等', 32, '2024-2025', '第二学期', 40, 1, 5, 1, '2025-05-30 12:04:00', '2025-05-31 10:43:55', 0);
INSERT INTO `sys_course_template` VALUES (25, '数据结构与算法', '线性表、树、图等数据结构的实现与算法设计', 48, '2024-2025', '第一学期', 50, 1, 4, 1, '2025-05-30 12:04:00', '2025-05-31 10:43:56', 0);
INSERT INTO `sys_course_template` VALUES (26, '操作系统原理', '操作系统的基本概念、进程管理、内存管理、文件系统等', 40, '2024-2025', '第二学期', 45, NULL, NULL, 1, '2025-05-30 12:04:00', '2025-05-30 12:04:00', 0);
INSERT INTO `sys_course_template` VALUES (27, 'Web开发实践', 'HTML、CSS、JavaScript、前端框架等Web开发技术', 32, '2024-2025', '第一学期', 40, 1, 5, 1, '2025-05-30 12:04:00', '2025-05-31 10:43:56', 0);
INSERT INTO `sys_course_template` VALUES (28, '数据库实验', '数据库设计、SQL编程、数据库管理等实验', 16, '2024-2025', '第一学期', 30, NULL, NULL, 1, '2025-05-30 12:04:00', '2025-05-30 12:04:00', 0);
INSERT INTO `sys_course_template` VALUES (29, '计算机组成原理', '计算机硬件系统的组成、工作原理等', 40, '2024-2025', '第一学期', 50, 1, 4, 1, '2025-05-30 12:04:00', '2025-05-31 10:43:56', 0);
INSERT INTO `sys_course_template` VALUES (30, '人工智能导论', '人工智能的基本概念、机器学习、深度学习等', 36, '2024-2025', '第二学期', 45, NULL, NULL, 0, '2025-05-30 12:04:00', '2025-05-30 12:04:00', 0);
INSERT INTO `sys_course_template` VALUES (31, 'AFDSAFDSA', 'FDSAFDASFDSAF', 16, '2023-2024', '第一学期', 50, 2, 7, 1, '2025-05-30 22:19:36', '2025-05-31 18:48:57', 0);
INSERT INTO `sys_course_template` VALUES (32, 'FFFFFFFFFFFFFFFFF', 'FFFFFFFFFFFFFFFFFFFFF', 16, '2024-2025', '第一学期', 50, 1, 5, 1, '2025-05-31 18:14:56', '2025-05-31 18:48:45', 0);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `menu_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单编码',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID，0表示顶级菜单',
  `menu_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单类型：MENU-菜单，BUTTON-按钮',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `menu_code`(`menu_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 119 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 'system', 0, 'MENU', '/system', '', 'Setting', 1, 1, '系统管理模块', '2025-05-25 13:48:49', '2025-05-25 13:48:49', 0);
INSERT INTO `sys_menu` VALUES (2, '用户管理', 'user-management', 1, 'MENU', '/system/user', 'UserManagement', 'User', 1, 1, '用户管理页面', '2025-05-25 13:48:49', '2025-05-25 13:48:49', 0);
INSERT INTO `sys_menu` VALUES (3, '角色管理', 'role-management', 1, 'MENU', '/system/role', 'RoleManagement', 'UserFilled', 2, 1, '角色管理页面', '2025-05-25 13:48:49', '2025-05-25 13:48:49', 0);
INSERT INTO `sys_menu` VALUES (4, '新增用户', 'user:add', 2, 'BUTTON', '', '', '', 1, 1, '新增用户按钮', '2025-05-25 13:48:49', '2025-05-25 13:48:49', 0);
INSERT INTO `sys_menu` VALUES (5, '编辑用户', 'user:edit', 2, 'BUTTON', '', '', '', 2, 1, '编辑用户按钮', '2025-05-25 13:48:49', '2025-05-25 13:48:49', 0);
INSERT INTO `sys_menu` VALUES (6, '删除用户', 'user:delete', 2, 'BUTTON', '', '', '', 3, 1, '删除用户按钮', '2025-05-25 13:48:49', '2025-05-25 13:48:49', 0);
INSERT INTO `sys_menu` VALUES (7, '分配角色', 'user:assign-role', 2, 'BUTTON', '', '', '', 4, 1, '分配角色按钮', '2025-05-25 13:48:49', '2025-05-25 13:48:49', 0);
INSERT INTO `sys_menu` VALUES (8, '新增角色', 'role:add', 3, 'BUTTON', '', '', '', 1, 1, '新增角色按钮', '2025-05-25 13:48:49', '2025-05-25 13:48:49', 0);
INSERT INTO `sys_menu` VALUES (9, '编辑角色', 'role:edit', 3, 'BUTTON', '', '', '', 2, 1, '编辑角色按钮', '2025-05-25 13:48:49', '2025-05-25 13:48:49', 0);
INSERT INTO `sys_menu` VALUES (10, '删除角色', 'role:delete', 3, 'BUTTON', '', '', '', 3, 1, '删除角色按钮', '2025-05-25 13:48:49', '2025-05-25 13:48:49', 0);
INSERT INTO `sys_menu` VALUES (11, '分配权限', 'role:assign-menu', 3, 'BUTTON', '', '', '', 4, 1, '分配权限按钮', '2025-05-25 13:48:49', '2025-05-25 13:48:49', 0);
INSERT INTO `sys_menu` VALUES (12, '个人中心', 'profile', 0, 'MENU', '/profile', 'Profile', 'Avatar', 2, 1, '个人中心页面', '2025-05-25 13:48:49', '2025-05-25 13:48:49', 0);
INSERT INTO `sys_menu` VALUES (13, '修改密码', 'change-password', 12, 'BUTTON', '', '', '', 1, 1, '修改密码按钮', '2025-05-25 13:48:49', '2025-05-25 13:48:49', 0);
INSERT INTO `sys_menu` VALUES (14, '新增组织', 'org:add', 14, 'BUTTON', '', '', '', 1, 1, '新增组织按钮', '2025-05-25 14:41:36', '2025-05-25 14:41:36', 0);
INSERT INTO `sys_menu` VALUES (15, '编辑组织', 'org:edit', 14, 'BUTTON', '', '', '', 2, 1, '编辑组织按钮', '2025-05-25 14:41:36', '2025-05-25 14:41:36', 0);
INSERT INTO `sys_menu` VALUES (16, '删除组织', 'org:delete', 14, 'BUTTON', '', '', '', 3, 1, '删除组织按钮', '2025-05-25 14:41:36', '2025-05-25 14:41:36', 0);
INSERT INTO `sys_menu` VALUES (17, '组织管理', 'organization-management', 1, 'MENU', '/system/organization', 'OrganizationManagement', 'OfficeBuilding', 3, 1, '组织管理页面', '2025-05-25 14:43:02', '2025-05-25 14:43:02', 0);
INSERT INTO `sys_menu` VALUES (18, '教师管理', 'teacher-management', 1, 'MENU', '/system/teacher', 'TeacherManagement', 'Avatar', 4, 1, '教师管理页面', '2025-05-26 06:14:52', '2025-05-26 06:14:52', 0);
INSERT INTO `sys_menu` VALUES (19, '学生管理', 'student-management', 1, 'MENU', '/system/student', 'StudentManagement', 'User', 5, 1, '学生管理页面', '2025-05-26 06:14:52', '2025-05-26 06:14:52', 0);
INSERT INTO `sys_menu` VALUES (22, '新增教师', 'teacher:add', 18, 'BUTTON', '', '', '', 1, 1, '新增教师按钮', '2025-05-26 06:21:57', '2025-05-26 06:21:57', 0);
INSERT INTO `sys_menu` VALUES (23, '编辑教师', 'teacher:edit', 18, 'BUTTON', '', '', '', 2, 1, '编辑教师按钮', '2025-05-26 06:21:57', '2025-05-26 06:21:57', 0);
INSERT INTO `sys_menu` VALUES (24, '删除教师', 'teacher:delete', 18, 'BUTTON', '', '', '', 3, 1, '删除教师按钮', '2025-05-26 06:21:57', '2025-05-26 06:21:57', 0);
INSERT INTO `sys_menu` VALUES (25, '新增学生', 'student:add', 19, 'BUTTON', '', '', '', 1, 1, '新增学生按钮', '2025-05-26 06:21:57', '2025-05-26 06:21:57', 0);
INSERT INTO `sys_menu` VALUES (26, '编辑学生', 'student:edit', 19, 'BUTTON', '', '', '', 2, 1, '编辑学生按钮', '2025-05-26 06:21:57', '2025-05-26 06:21:57', 0);
INSERT INTO `sys_menu` VALUES (27, '删除学生', 'student:delete', 19, 'BUTTON', '', '', '', 3, 1, '删除学生按钮', '2025-05-26 06:21:57', '2025-05-26 06:21:57', 0);
INSERT INTO `sys_menu` VALUES (29, '通知管理', 'notice', 0, 'MENU', '/notice', '', 'Bell', 3, 1, '通知管理模块', '2025-05-26 08:22:57', '2025-05-26 08:22:57', 0);
INSERT INTO `sys_menu` VALUES (30, '通知管理', 'notice-management', 29, 'MENU', '/notice/management', 'NoticeManagement', 'Edit', 1, 1, '通知管理页面', '2025-05-26 08:22:57', '2025-05-26 08:22:57', 0);
INSERT INTO `sys_menu` VALUES (31, '通知公告', 'notice-view', 29, 'MENU', '/notice/view', 'NoticeView', 'View', 2, 1, '通知公告查看页面', '2025-05-26 08:22:57', '2025-05-26 08:22:57', 0);
INSERT INTO `sys_menu` VALUES (32, '新增通知', 'notice:add', 30, 'BUTTON', '', '', '', 1, 1, '新增通知按钮', '2025-05-26 08:22:57', '2025-05-26 08:22:57', 0);
INSERT INTO `sys_menu` VALUES (33, '编辑通知', 'notice:edit', 30, 'BUTTON', '', '', '', 2, 1, '编辑通知按钮', '2025-05-26 08:22:57', '2025-05-26 08:22:57', 0);
INSERT INTO `sys_menu` VALUES (34, '删除通知', 'notice:delete', 30, 'BUTTON', '', '', '', 3, 1, '删除通知按钮', '2025-05-26 08:22:57', '2025-05-26 08:22:57', 0);
INSERT INTO `sys_menu` VALUES (35, '发布通知', 'notice:publish', 30, 'BUTTON', '', '', '', 4, 1, '发布通知按钮', '2025-05-26 08:22:57', '2025-05-26 08:22:57', 0);
INSERT INTO `sys_menu` VALUES (36, '下线通知', 'notice:offline', 30, 'BUTTON', '', '', '', 5, 1, '下线通知按钮', '2025-05-26 08:22:57', '2025-05-26 08:22:57', 0);
INSERT INTO `sys_menu` VALUES (37, '课程管理', 'course', 0, 'MENU', '/course', '', 'Reading', 4, 1, '课程管理模块', '2025-05-26 17:02:34', '2025-05-26 17:02:34', 0);
INSERT INTO `sys_menu` VALUES (38, '开课实例管理', 'course-management', 37, 'MENU', '/course/management', 'CourseManagement', 'Edit', 1, 1, '管理已通过审批的开课实例', '2025-05-26 17:02:34', '2025-05-30 02:43:32', 0);
INSERT INTO `sys_menu` VALUES (39, '新增课程', 'course:add', 38, 'BUTTON', '', '', '', 1, 1, '新增课程按钮', '2025-05-26 17:02:34', '2025-05-26 17:02:34', 0);
INSERT INTO `sys_menu` VALUES (40, '编辑课程', 'course:edit', 38, 'BUTTON', '', '', '', 2, 1, '编辑课程按钮', '2025-05-26 17:02:34', '2025-05-26 17:02:34', 0);
INSERT INTO `sys_menu` VALUES (41, '删除课程', 'course:delete', 38, 'BUTTON', '', '', '', 3, 1, '删除课程按钮', '2025-05-26 17:02:34', '2025-05-26 17:02:34', 0);
INSERT INTO `sys_menu` VALUES (42, '查看课程', 'course:view', 38, 'BUTTON', '', '', '', 4, 1, '查看课程按钮', '2025-05-26 17:02:34', '2025-05-26 17:02:34', 0);
INSERT INTO `sys_menu` VALUES (47, '提交申请', 'course-application:submit', 44, 'BUTTON', '', '', '', 1, 1, '提交申请按钮', '2025-05-26 17:15:37', '2025-05-26 17:15:37', 0);
INSERT INTO `sys_menu` VALUES (48, '查看申请', 'course-application:view', 44, 'BUTTON', '', '', '', 2, 1, '查看申请按钮', '2025-05-26 17:15:37', '2025-05-26 17:15:37', 0);
INSERT INTO `sys_menu` VALUES (49, '审批通过', 'course-application:approve', 45, 'BUTTON', '', '', '', 1, 1, '审批通过按钮', '2025-05-26 17:15:37', '2025-05-26 17:15:37', 0);
INSERT INTO `sys_menu` VALUES (50, '审批拒绝', 'course-application:reject', 45, 'BUTTON', '', '', '', 2, 1, '审批拒绝按钮', '2025-05-26 17:15:37', '2025-05-26 17:15:37', 0);
INSERT INTO `sys_menu` VALUES (51, '查看详情', 'course-application:detail', 45, 'BUTTON', '', '', '', 3, 1, '查看详情按钮', '2025-05-26 17:15:37', '2025-05-26 17:15:37', 0);
INSERT INTO `sys_menu` VALUES (65, '开课申请审批', 'course-application-review', 37, 'MENU', '/course/application-review', 'CourseApplicationReview', 'DocumentChecked', 3, 1, '管理员审批开课申请页面', '2025-05-30 01:50:46', '2025-05-30 01:50:46', 0);
INSERT INTO `sys_menu` VALUES (66, '申请开课', 'course-application', 37, 'MENU', '/course/application', 'CourseApplication', 'Document', 2, 1, '教师申请开课页面', '2025-05-30 01:55:58', '2025-05-30 01:55:58', 0);
INSERT INTO `sys_menu` VALUES (69, '课程模板管理', 'course-template', 37, 'MENU', '/course/template', 'CourseTemplateManagement', 'Notebook', 1, 1, '管理课程模板，供教师申请开课', '2025-05-30 02:43:32', '2025-05-30 02:43:32', 0);
INSERT INTO `sys_menu` VALUES (102, '课程表管理', 'schedule-management', 37, 'MENU', '/course/schedule', '/course/schedule', 'Calendar', 50, 1, '教师课程表管理', '2025-05-30 12:36:39', '2025-05-30 12:36:39', 0);
INSERT INTO `sys_menu` VALUES (103, '我的申请', 'my-applications', 37, 'MENU', '/course/my-applications', '/course/my-applications', 'Document', 40, 1, '教师开课申请查看与管理', '2025-05-30 12:46:04', '2025-05-30 12:46:04', 0);
INSERT INTO `sys_menu` VALUES (108, '所有课程表', 'all-schedules', 37, 'MENU', '/course/all-schedules', 'AllSchedules', 'Calendar', 60, 1, '管理员查看所有教师课程表', '2025-05-30 15:05:49', '2025-05-30 15:05:49', 0);
INSERT INTO `sys_menu` VALUES (109, '学生选课', 'student-course-selection', 0, 'MENU', '/student-course-selection', '', 'School', 5, 1, '学生选课模块', '2025-05-31 12:47:24', '2025-05-31 12:47:24', 0);
INSERT INTO `sys_menu` VALUES (110, '选课管理', 'course-selection-management', 109, 'MENU', '/student-course-selection/management', 'CourseSelection', 'Edit', 1, 1, '学生选课管理页面', '2025-05-31 12:47:24', '2025-05-31 12:47:24', 0);
INSERT INTO `sys_menu` VALUES (111, '查看可选课程', 'course-selection:view-available', 110, 'BUTTON', '', '', '', 1, 1, '查看可选课程按钮', '2025-05-31 12:47:24', '2025-05-31 12:47:24', 0);
INSERT INTO `sys_menu` VALUES (112, '选择课程', 'course-selection:select', 110, 'BUTTON', '', '', '', 2, 1, '选择课程按钮', '2025-05-31 12:47:24', '2025-05-31 12:47:24', 0);
INSERT INTO `sys_menu` VALUES (113, '退选课程', 'course-selection:drop', 110, 'BUTTON', '', '', '', 3, 1, '退选课程按钮', '2025-05-31 12:47:24', '2025-05-31 12:47:24', 0);
INSERT INTO `sys_menu` VALUES (114, '查看已选课程', 'course-selection:view-selected', 110, 'BUTTON', '', '', '', 4, 1, '查看已选课程按钮', '2025-05-31 12:47:24', '2025-05-31 12:47:24', 0);
INSERT INTO `sys_menu` VALUES (115, '查看我的课程表', 'course-selection:view-schedule', 110, 'BUTTON', '', '', '', 5, 1, '查看我的课程表按钮', '2025-05-31 12:47:24', '2025-05-31 12:47:24', 0);

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES (1, '系统维护通知', 1, 'admin', '<p>系统将于本周六晚上10点至次日上午8点进行维护升级，期间可能无法正常访问，请各位同学提前做好相关安排。</p><p>维护内容包括：</p><ul><li>服务器硬件升级</li><li>系统安全补丁更新</li><li>数据库优化</li></ul><p>如有紧急事务，请联系管理员</p>', 1, '2025-05-26 08:22:57', '2025-05-26 08:22:57', '2025-05-26 16:26:07', 0);
INSERT INTO `sys_notice` VALUES (2, '期末考试安排通知', 1, 'admin', '<p>各位同学：</p><p>期末考试即将开始，现将相关安排通知如下：</p><p><strong>考试时间：</strong>下周一至周五</p><p><strong>考试地点：</strong>各自班级教室</p><p><strong>注意事项：</strong></p><ol><li>请提前30分钟到达考场</li><li>携带学生证和身份证</li><li>不得携带电子设备</li></ol><p>预祝各位同学考试顺利！</p>', 1, '2025-05-26 08:22:57', '2025-05-26 08:22:57', '2025-05-26 08:22:57', 0);
INSERT INTO `sys_notice` VALUES (3, '图书馆开放时间调整', 1, 'admin', '<p>因馆内设施维修，图书馆开放时间临时调整为：</p><p><strong>周一至周五：</strong>8:00-17:00</p><p><strong>周末：</strong>暂停开放</p><p>恢复正常开放时间另行通知，给大家带来的不便敬请谅解。</p>', 1, '2025-05-26 08:22:57', '2025-05-26 08:22:57', '2025-05-26 08:22:57', 0);
INSERT INTO `sys_notice` VALUES (4, '1', 1, '2', '1', 1, '2025-05-26 16:53:56', '2025-05-26 16:53:43', '2025-05-26 16:53:43', 0);

-- ----------------------------
-- Table structure for sys_organization
-- ----------------------------
DROP TABLE IF EXISTS `sys_organization`;
CREATE TABLE `sys_organization`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `org_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组织名称',
  `org_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组织编码',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父级组织ID',
  `org_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组织类型：COLLEGE-学院，MAJOR-专业，CLASS-班级',
  `org_level` int NOT NULL COMMENT '组织级别：1-学院，2-专业，3-班级',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_org_name`(`org_name` ASC) USING BTREE,
  UNIQUE INDEX `uk_org_code`(`org_code` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_org_level`(`org_level` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '组织架构表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_organization
-- ----------------------------
INSERT INTO `sys_organization` VALUES (1, '计算机学院', 'CS_COLLEGE', 0, 'COLLEGE', 1, 1, '张三', '13800138001', 'zhangsan@university.edu', '计算机科学与技术学院', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');
INSERT INTO `sys_organization` VALUES (2, '商学院', 'BUS_COLLEGE', 0, 'COLLEGE', 1, 2, '李四', '13800138002', 'lisi@university.edu', '工商管理学院', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');
INSERT INTO `sys_organization` VALUES (3, '外国语学院', 'FL_COLLEGE', 0, 'COLLEGE', 1, 3, '王五', '13800138003', 'wangwu@university.edu', '外国语学院', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');
INSERT INTO `sys_organization` VALUES (4, '计算机科学与技术', 'CS_MAJOR', 1, 'MAJOR', 2, 1, '赵六', '13800138011', 'zhaoliu@university.edu', '计算机科学与技术专业', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');
INSERT INTO `sys_organization` VALUES (5, '软件工程', 'SE_MAJOR', 1, 'MAJOR', 2, 2, '钱七', '13800138012', 'qianqi@university.edu', '软件工程专业', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');
INSERT INTO `sys_organization` VALUES (6, '网络工程', 'NE_MAJOR', 1, 'MAJOR', 2, 3, '孙八', '13800138013', 'sunba@university.edu', '网络工程专业', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');
INSERT INTO `sys_organization` VALUES (7, '工商管理', 'BA_MAJOR', 2, 'MAJOR', 2, 1, '周九', '13800138021', 'zhoujiu@university.edu', '工商管理专业', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');
INSERT INTO `sys_organization` VALUES (8, '市场营销', 'MK_MAJOR', 2, 'MAJOR', 2, 2, '吴十', '13800138022', 'wushi@university.edu', '市场营销专业', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');
INSERT INTO `sys_organization` VALUES (9, '英语', 'EN_MAJOR', 3, 'MAJOR', 2, 1, '郑十一', '13800138031', 'zhengshiyi@university.edu', '英语专业', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');
INSERT INTO `sys_organization` VALUES (10, '日语', 'JP_MAJOR', 3, 'MAJOR', 2, 2, '王十二', '13800138032', 'wangshier@university.edu', '日语专业', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');
INSERT INTO `sys_organization` VALUES (11, '计科2021级1班', 'CS_2021_1', 4, 'CLASS', 3, 1, '冯十三', '13800138041', 'fengshisan@university.edu', '计算机科学与技术2021级1班啊啊', 1, '2025-05-25 14:41:36', '2025-05-31 18:04:58');
INSERT INTO `sys_organization` VALUES (12, '计科2021级2班', 'CS_2021_2', 4, 'CLASS', 3, 2, '陈十四', '13800138042', 'chenshisi@university.edu', '计算机科学与技术2021级2班', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');
INSERT INTO `sys_organization` VALUES (13, '计科2022级1班', 'CS_2022_1', 4, 'CLASS', 3, 3, '褚十五', '13800138043', 'chushiwu@university.edu', '计算机科学与技术2022级1班', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');
INSERT INTO `sys_organization` VALUES (14, '软工2021级1班', 'SE_2021_1', 5, 'CLASS', 3, 1, '魏十六', '13800138051', 'weishiliu@university.edu', '软件工程2021级1班', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');
INSERT INTO `sys_organization` VALUES (15, '软工2021级2班', 'SE_2021_2', 5, 'CLASS', 3, 2, '姜十七', '13800138052', 'jiangshiqi@university.edu', '软件工程2021级2班', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');
INSERT INTO `sys_organization` VALUES (16, '工商2021级1班', 'BA_2021_1', 7, 'CLASS', 3, 1, '袁十八', '13800138061', 'yuanshiba@university.edu', '工商管理2021级1班', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');
INSERT INTO `sys_organization` VALUES (17, '工商2022级1班', 'BA_2022_1', 7, 'CLASS', 3, 2, '柳十九', '13800138062', 'liushijiu@university.edu', '工商管理2022级1班', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');
INSERT INTO `sys_organization` VALUES (18, '英语2021级1班', 'EN_2021_1', 9, 'CLASS', 3, 1, '分二十', '13800138071', 'fenershi@university.edu', '英语2021级1班', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');
INSERT INTO `sys_organization` VALUES (19, '英语2022级1班', 'EN_2022_1', 9, 'CLASS', 3, 2, '鲁二一', '13800138072', 'lueeryi@university.edu', '英语2022级1班', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '管理员', 'ADMIN', '系统管理员，拥有所有权限', 1, '2025-05-25 13:39:05', '2025-05-25 13:39:05', 0);
INSERT INTO `sys_role` VALUES (2, '学生', 'STUDENT', '学生角色，拥有基本权限', 1, '2025-05-25 13:39:05', '2025-05-26 06:14:52', 0);
INSERT INTO `sys_role` VALUES (3, '教师', 'TEACHER', '教师角色，拥有教学相关权限', 1, '2025-05-25 14:52:36', '2025-05-25 14:52:36', 0);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_menu_id`(`menu_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 701 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 1, 1, '2025-05-25 13:49:00', 1);
INSERT INTO `sys_role_menu` VALUES (2, 1, 2, '2025-05-25 13:49:00', 1);
INSERT INTO `sys_role_menu` VALUES (3, 1, 3, '2025-05-25 13:49:00', 1);
INSERT INTO `sys_role_menu` VALUES (4, 1, 4, '2025-05-25 13:49:00', 1);
INSERT INTO `sys_role_menu` VALUES (5, 1, 5, '2025-05-25 13:49:00', 1);
INSERT INTO `sys_role_menu` VALUES (6, 1, 6, '2025-05-25 13:49:00', 1);
INSERT INTO `sys_role_menu` VALUES (7, 1, 7, '2025-05-25 13:49:00', 1);
INSERT INTO `sys_role_menu` VALUES (8, 1, 8, '2025-05-25 13:49:00', 1);
INSERT INTO `sys_role_menu` VALUES (9, 1, 9, '2025-05-25 13:49:00', 1);
INSERT INTO `sys_role_menu` VALUES (10, 1, 10, '2025-05-25 13:49:00', 1);
INSERT INTO `sys_role_menu` VALUES (11, 1, 11, '2025-05-25 13:49:00', 1);
INSERT INTO `sys_role_menu` VALUES (12, 1, 12, '2025-05-25 13:49:00', 1);
INSERT INTO `sys_role_menu` VALUES (13, 1, 13, '2025-05-25 13:49:00', 1);
INSERT INTO `sys_role_menu` VALUES (14, 2, 12, '2025-05-25 13:49:00', 1);
INSERT INTO `sys_role_menu` VALUES (15, 2, 13, '2025-05-25 13:49:00', 1);
INSERT INTO `sys_role_menu` VALUES (16, 2, 4, '2025-05-25 21:54:31', 1);
INSERT INTO `sys_role_menu` VALUES (17, 2, 12, '2025-05-25 21:54:31', 1);
INSERT INTO `sys_role_menu` VALUES (18, 2, 13, '2025-05-25 21:54:31', 1);
INSERT INTO `sys_role_menu` VALUES (19, 2, 1, '2025-05-25 21:54:31', 1);
INSERT INTO `sys_role_menu` VALUES (20, 2, 2, '2025-05-25 21:54:31', 1);
INSERT INTO `sys_role_menu` VALUES (21, 1, 4, '2025-05-25 21:54:51', 1);
INSERT INTO `sys_role_menu` VALUES (22, 1, 5, '2025-05-25 21:54:51', 1);
INSERT INTO `sys_role_menu` VALUES (23, 1, 3, '2025-05-25 21:54:51', 1);
INSERT INTO `sys_role_menu` VALUES (24, 1, 8, '2025-05-25 21:54:51', 1);
INSERT INTO `sys_role_menu` VALUES (25, 1, 9, '2025-05-25 21:54:51', 1);
INSERT INTO `sys_role_menu` VALUES (26, 1, 10, '2025-05-25 21:54:51', 1);
INSERT INTO `sys_role_menu` VALUES (27, 1, 11, '2025-05-25 21:54:51', 1);
INSERT INTO `sys_role_menu` VALUES (28, 1, 12, '2025-05-25 21:54:51', 1);
INSERT INTO `sys_role_menu` VALUES (29, 1, 13, '2025-05-25 21:54:51', 1);
INSERT INTO `sys_role_menu` VALUES (30, 1, 1, '2025-05-25 21:54:51', 1);
INSERT INTO `sys_role_menu` VALUES (31, 1, 2, '2025-05-25 21:54:51', 1);
INSERT INTO `sys_role_menu` VALUES (32, 1, 1, '2025-05-25 21:55:03', 1);
INSERT INTO `sys_role_menu` VALUES (33, 1, 2, '2025-05-25 21:55:03', 1);
INSERT INTO `sys_role_menu` VALUES (34, 1, 4, '2025-05-25 21:55:03', 1);
INSERT INTO `sys_role_menu` VALUES (35, 1, 5, '2025-05-25 21:55:03', 1);
INSERT INTO `sys_role_menu` VALUES (36, 1, 6, '2025-05-25 21:55:03', 1);
INSERT INTO `sys_role_menu` VALUES (37, 1, 7, '2025-05-25 21:55:03', 1);
INSERT INTO `sys_role_menu` VALUES (38, 1, 3, '2025-05-25 21:55:03', 1);
INSERT INTO `sys_role_menu` VALUES (39, 1, 8, '2025-05-25 21:55:03', 1);
INSERT INTO `sys_role_menu` VALUES (40, 1, 9, '2025-05-25 21:55:03', 1);
INSERT INTO `sys_role_menu` VALUES (41, 1, 10, '2025-05-25 21:55:03', 1);
INSERT INTO `sys_role_menu` VALUES (42, 1, 11, '2025-05-25 21:55:03', 1);
INSERT INTO `sys_role_menu` VALUES (43, 1, 12, '2025-05-25 21:55:03', 1);
INSERT INTO `sys_role_menu` VALUES (44, 1, 13, '2025-05-25 21:55:03', 1);
INSERT INTO `sys_role_menu` VALUES (45, 2, 1, '2025-05-25 21:55:08', 1);
INSERT INTO `sys_role_menu` VALUES (46, 2, 2, '2025-05-25 21:55:08', 1);
INSERT INTO `sys_role_menu` VALUES (47, 2, 4, '2025-05-25 21:55:08', 1);
INSERT INTO `sys_role_menu` VALUES (48, 2, 5, '2025-05-25 21:55:08', 1);
INSERT INTO `sys_role_menu` VALUES (49, 2, 6, '2025-05-25 21:55:08', 1);
INSERT INTO `sys_role_menu` VALUES (50, 2, 7, '2025-05-25 21:55:08', 1);
INSERT INTO `sys_role_menu` VALUES (51, 2, 3, '2025-05-25 21:55:08', 1);
INSERT INTO `sys_role_menu` VALUES (52, 2, 8, '2025-05-25 21:55:08', 1);
INSERT INTO `sys_role_menu` VALUES (53, 2, 9, '2025-05-25 21:55:08', 1);
INSERT INTO `sys_role_menu` VALUES (54, 2, 10, '2025-05-25 21:55:08', 1);
INSERT INTO `sys_role_menu` VALUES (55, 2, 11, '2025-05-25 21:55:08', 1);
INSERT INTO `sys_role_menu` VALUES (56, 2, 12, '2025-05-25 21:55:08', 1);
INSERT INTO `sys_role_menu` VALUES (57, 2, 13, '2025-05-25 21:55:08', 1);
INSERT INTO `sys_role_menu` VALUES (58, 1, 5, '2025-05-25 21:55:28', 1);
INSERT INTO `sys_role_menu` VALUES (59, 1, 12, '2025-05-25 21:55:28', 1);
INSERT INTO `sys_role_menu` VALUES (60, 1, 13, '2025-05-25 21:55:28', 1);
INSERT INTO `sys_role_menu` VALUES (61, 1, 1, '2025-05-25 21:55:28', 1);
INSERT INTO `sys_role_menu` VALUES (62, 1, 2, '2025-05-25 21:55:28', 1);
INSERT INTO `sys_role_menu` VALUES (63, 2, 2, '2025-05-25 21:55:38', 1);
INSERT INTO `sys_role_menu` VALUES (64, 2, 4, '2025-05-25 21:55:38', 1);
INSERT INTO `sys_role_menu` VALUES (65, 2, 5, '2025-05-25 21:55:38', 1);
INSERT INTO `sys_role_menu` VALUES (66, 2, 6, '2025-05-25 21:55:38', 1);
INSERT INTO `sys_role_menu` VALUES (67, 2, 7, '2025-05-25 21:55:38', 1);
INSERT INTO `sys_role_menu` VALUES (68, 2, 12, '2025-05-25 21:55:38', 1);
INSERT INTO `sys_role_menu` VALUES (69, 2, 13, '2025-05-25 21:55:38', 1);
INSERT INTO `sys_role_menu` VALUES (70, 2, 1, '2025-05-25 21:55:38', 1);
INSERT INTO `sys_role_menu` VALUES (71, 2, 12, '2025-05-25 21:59:25', 1);
INSERT INTO `sys_role_menu` VALUES (72, 2, 13, '2025-05-25 21:59:25', 1);
INSERT INTO `sys_role_menu` VALUES (73, 2, 4, '2025-05-25 22:03:25', 1);
INSERT INTO `sys_role_menu` VALUES (74, 2, 12, '2025-05-25 22:03:25', 1);
INSERT INTO `sys_role_menu` VALUES (75, 2, 13, '2025-05-25 22:03:25', 1);
INSERT INTO `sys_role_menu` VALUES (76, 2, 1, '2025-05-25 22:03:25', 1);
INSERT INTO `sys_role_menu` VALUES (77, 2, 2, '2025-05-25 22:03:25', 1);
INSERT INTO `sys_role_menu` VALUES (78, 2, 4, '2025-05-25 22:03:39', 1);
INSERT INTO `sys_role_menu` VALUES (79, 2, 5, '2025-05-25 22:03:39', 1);
INSERT INTO `sys_role_menu` VALUES (80, 2, 12, '2025-05-25 22:03:39', 1);
INSERT INTO `sys_role_menu` VALUES (81, 2, 13, '2025-05-25 22:03:39', 1);
INSERT INTO `sys_role_menu` VALUES (82, 2, 1, '2025-05-25 22:03:39', 1);
INSERT INTO `sys_role_menu` VALUES (83, 2, 2, '2025-05-25 22:03:39', 1);
INSERT INTO `sys_role_menu` VALUES (84, 1, 4, '2025-05-25 22:03:53', 1);
INSERT INTO `sys_role_menu` VALUES (85, 1, 5, '2025-05-25 22:03:53', 1);
INSERT INTO `sys_role_menu` VALUES (86, 1, 12, '2025-05-25 22:03:53', 1);
INSERT INTO `sys_role_menu` VALUES (87, 1, 13, '2025-05-25 22:03:53', 1);
INSERT INTO `sys_role_menu` VALUES (88, 1, 1, '2025-05-25 22:03:53', 1);
INSERT INTO `sys_role_menu` VALUES (89, 1, 2, '2025-05-25 22:03:53', 1);
INSERT INTO `sys_role_menu` VALUES (90, 2, 2, '2025-05-25 22:04:03', 1);
INSERT INTO `sys_role_menu` VALUES (91, 2, 4, '2025-05-25 22:04:03', 1);
INSERT INTO `sys_role_menu` VALUES (92, 2, 5, '2025-05-25 22:04:03', 1);
INSERT INTO `sys_role_menu` VALUES (93, 2, 6, '2025-05-25 22:04:03', 1);
INSERT INTO `sys_role_menu` VALUES (94, 2, 7, '2025-05-25 22:04:03', 1);
INSERT INTO `sys_role_menu` VALUES (95, 2, 12, '2025-05-25 22:04:03', 1);
INSERT INTO `sys_role_menu` VALUES (96, 2, 13, '2025-05-25 22:04:03', 1);
INSERT INTO `sys_role_menu` VALUES (97, 2, 1, '2025-05-25 22:04:04', 1);
INSERT INTO `sys_role_menu` VALUES (98, 1, 1, '2025-05-25 22:04:22', 1);
INSERT INTO `sys_role_menu` VALUES (99, 1, 2, '2025-05-25 22:04:22', 1);
INSERT INTO `sys_role_menu` VALUES (100, 1, 4, '2025-05-25 22:04:22', 1);
INSERT INTO `sys_role_menu` VALUES (101, 1, 5, '2025-05-25 22:04:22', 1);
INSERT INTO `sys_role_menu` VALUES (102, 1, 6, '2025-05-25 22:04:22', 1);
INSERT INTO `sys_role_menu` VALUES (103, 1, 7, '2025-05-25 22:04:22', 1);
INSERT INTO `sys_role_menu` VALUES (104, 1, 3, '2025-05-25 22:04:22', 1);
INSERT INTO `sys_role_menu` VALUES (105, 1, 8, '2025-05-25 22:04:22', 1);
INSERT INTO `sys_role_menu` VALUES (106, 1, 9, '2025-05-25 22:04:22', 1);
INSERT INTO `sys_role_menu` VALUES (107, 1, 10, '2025-05-25 22:04:22', 1);
INSERT INTO `sys_role_menu` VALUES (108, 1, 11, '2025-05-25 22:04:22', 1);
INSERT INTO `sys_role_menu` VALUES (109, 1, 12, '2025-05-25 22:04:22', 1);
INSERT INTO `sys_role_menu` VALUES (110, 1, 13, '2025-05-25 22:04:22', 1);
INSERT INTO `sys_role_menu` VALUES (111, 2, 4, '2025-05-25 22:04:37', 1);
INSERT INTO `sys_role_menu` VALUES (112, 2, 12, '2025-05-25 22:04:37', 1);
INSERT INTO `sys_role_menu` VALUES (113, 2, 13, '2025-05-25 22:04:37', 1);
INSERT INTO `sys_role_menu` VALUES (114, 2, 1, '2025-05-25 22:04:37', 1);
INSERT INTO `sys_role_menu` VALUES (115, 2, 2, '2025-05-25 22:04:37', 1);
INSERT INTO `sys_role_menu` VALUES (116, 2, 4, '2025-05-25 22:04:53', 1);
INSERT INTO `sys_role_menu` VALUES (117, 2, 5, '2025-05-25 22:04:53', 1);
INSERT INTO `sys_role_menu` VALUES (118, 2, 12, '2025-05-25 22:04:53', 1);
INSERT INTO `sys_role_menu` VALUES (119, 2, 13, '2025-05-25 22:04:53', 1);
INSERT INTO `sys_role_menu` VALUES (120, 2, 1, '2025-05-25 22:04:53', 1);
INSERT INTO `sys_role_menu` VALUES (121, 2, 2, '2025-05-25 22:04:53', 1);
INSERT INTO `sys_role_menu` VALUES (122, 2, 1, '2025-05-25 22:05:03', 1);
INSERT INTO `sys_role_menu` VALUES (123, 2, 2, '2025-05-25 22:05:03', 1);
INSERT INTO `sys_role_menu` VALUES (124, 2, 4, '2025-05-25 22:05:03', 1);
INSERT INTO `sys_role_menu` VALUES (125, 2, 5, '2025-05-25 22:05:03', 1);
INSERT INTO `sys_role_menu` VALUES (126, 2, 6, '2025-05-25 22:05:03', 1);
INSERT INTO `sys_role_menu` VALUES (127, 2, 7, '2025-05-25 22:05:03', 1);
INSERT INTO `sys_role_menu` VALUES (128, 2, 3, '2025-05-25 22:05:03', 1);
INSERT INTO `sys_role_menu` VALUES (129, 2, 8, '2025-05-25 22:05:03', 1);
INSERT INTO `sys_role_menu` VALUES (130, 2, 9, '2025-05-25 22:05:03', 1);
INSERT INTO `sys_role_menu` VALUES (131, 2, 10, '2025-05-25 22:05:03', 1);
INSERT INTO `sys_role_menu` VALUES (132, 2, 11, '2025-05-25 22:05:03', 1);
INSERT INTO `sys_role_menu` VALUES (133, 2, 12, '2025-05-25 22:05:03', 1);
INSERT INTO `sys_role_menu` VALUES (134, 2, 13, '2025-05-25 22:05:03', 1);
INSERT INTO `sys_role_menu` VALUES (135, 2, 4, '2025-05-25 22:05:13', 1);
INSERT INTO `sys_role_menu` VALUES (136, 2, 12, '2025-05-25 22:05:13', 1);
INSERT INTO `sys_role_menu` VALUES (137, 2, 13, '2025-05-25 22:05:13', 1);
INSERT INTO `sys_role_menu` VALUES (138, 2, 1, '2025-05-25 22:05:13', 1);
INSERT INTO `sys_role_menu` VALUES (139, 2, 2, '2025-05-25 22:05:13', 1);
INSERT INTO `sys_role_menu` VALUES (140, 2, 5, '2025-05-25 22:05:22', 1);
INSERT INTO `sys_role_menu` VALUES (141, 2, 12, '2025-05-25 22:05:22', 1);
INSERT INTO `sys_role_menu` VALUES (142, 2, 13, '2025-05-25 22:05:22', 1);
INSERT INTO `sys_role_menu` VALUES (143, 2, 1, '2025-05-25 22:05:22', 1);
INSERT INTO `sys_role_menu` VALUES (144, 2, 2, '2025-05-25 22:05:22', 1);
INSERT INTO `sys_role_menu` VALUES (145, 2, 4, '2025-05-25 22:08:33', 1);
INSERT INTO `sys_role_menu` VALUES (146, 2, 5, '2025-05-25 22:08:33', 1);
INSERT INTO `sys_role_menu` VALUES (147, 2, 12, '2025-05-25 22:08:33', 1);
INSERT INTO `sys_role_menu` VALUES (148, 2, 13, '2025-05-25 22:08:33', 1);
INSERT INTO `sys_role_menu` VALUES (149, 2, 1, '2025-05-25 22:08:33', 1);
INSERT INTO `sys_role_menu` VALUES (150, 2, 2, '2025-05-25 22:08:33', 1);
INSERT INTO `sys_role_menu` VALUES (151, 2, 12, '2025-05-25 22:11:25', 1);
INSERT INTO `sys_role_menu` VALUES (152, 2, 13, '2025-05-25 22:11:25', 1);
INSERT INTO `sys_role_menu` VALUES (153, 2, 4, '2025-05-25 22:11:32', 1);
INSERT INTO `sys_role_menu` VALUES (154, 2, 12, '2025-05-25 22:11:32', 1);
INSERT INTO `sys_role_menu` VALUES (155, 2, 13, '2025-05-25 22:11:32', 1);
INSERT INTO `sys_role_menu` VALUES (156, 2, 1, '2025-05-25 22:11:32', 1);
INSERT INTO `sys_role_menu` VALUES (157, 2, 2, '2025-05-25 22:11:32', 1);
INSERT INTO `sys_role_menu` VALUES (158, 2, 4, '2025-05-25 22:11:40', 1);
INSERT INTO `sys_role_menu` VALUES (159, 2, 5, '2025-05-25 22:11:40', 1);
INSERT INTO `sys_role_menu` VALUES (160, 2, 12, '2025-05-25 22:11:40', 1);
INSERT INTO `sys_role_menu` VALUES (161, 2, 13, '2025-05-25 22:11:40', 1);
INSERT INTO `sys_role_menu` VALUES (162, 2, 1, '2025-05-25 22:11:40', 1);
INSERT INTO `sys_role_menu` VALUES (163, 2, 2, '2025-05-25 22:11:40', 1);
INSERT INTO `sys_role_menu` VALUES (164, 1, 14, '2025-05-25 14:41:36', 1);
INSERT INTO `sys_role_menu` VALUES (165, 1, 15, '2025-05-25 14:41:36', 1);
INSERT INTO `sys_role_menu` VALUES (166, 1, 16, '2025-05-25 14:41:36', 1);
INSERT INTO `sys_role_menu` VALUES (167, 2, 4, '2025-05-25 22:51:03', 1);
INSERT INTO `sys_role_menu` VALUES (168, 2, 5, '2025-05-25 22:51:03', 1);
INSERT INTO `sys_role_menu` VALUES (169, 2, 17, '2025-05-25 22:51:03', 1);
INSERT INTO `sys_role_menu` VALUES (170, 2, 12, '2025-05-25 22:51:03', 1);
INSERT INTO `sys_role_menu` VALUES (171, 2, 13, '2025-05-25 22:51:03', 1);
INSERT INTO `sys_role_menu` VALUES (172, 2, 1, '2025-05-25 22:51:03', 1);
INSERT INTO `sys_role_menu` VALUES (173, 2, 2, '2025-05-25 22:51:03', 1);
INSERT INTO `sys_role_menu` VALUES (177, 2, 4, '2025-05-25 22:55:31', 1);
INSERT INTO `sys_role_menu` VALUES (178, 2, 5, '2025-05-25 22:55:31', 1);
INSERT INTO `sys_role_menu` VALUES (179, 2, 17, '2025-05-25 22:55:31', 1);
INSERT INTO `sys_role_menu` VALUES (180, 2, 12, '2025-05-25 22:55:31', 1);
INSERT INTO `sys_role_menu` VALUES (181, 2, 13, '2025-05-25 22:55:31', 1);
INSERT INTO `sys_role_menu` VALUES (182, 2, 1, '2025-05-25 22:55:31', 1);
INSERT INTO `sys_role_menu` VALUES (183, 2, 2, '2025-05-25 22:55:31', 1);
INSERT INTO `sys_role_menu` VALUES (184, 2, 4, '2025-05-25 22:57:56', 1);
INSERT INTO `sys_role_menu` VALUES (185, 2, 5, '2025-05-25 22:57:56', 1);
INSERT INTO `sys_role_menu` VALUES (186, 2, 12, '2025-05-25 22:57:56', 1);
INSERT INTO `sys_role_menu` VALUES (187, 2, 13, '2025-05-25 22:57:56', 1);
INSERT INTO `sys_role_menu` VALUES (188, 2, 1, '2025-05-25 22:57:56', 1);
INSERT INTO `sys_role_menu` VALUES (189, 2, 2, '2025-05-25 22:57:56', 1);
INSERT INTO `sys_role_menu` VALUES (190, 2, 4, '2025-05-25 22:58:06', 1);
INSERT INTO `sys_role_menu` VALUES (191, 2, 5, '2025-05-25 22:58:06', 1);
INSERT INTO `sys_role_menu` VALUES (192, 2, 17, '2025-05-25 22:58:06', 1);
INSERT INTO `sys_role_menu` VALUES (193, 2, 12, '2025-05-25 22:58:06', 1);
INSERT INTO `sys_role_menu` VALUES (194, 2, 13, '2025-05-25 22:58:06', 1);
INSERT INTO `sys_role_menu` VALUES (195, 2, 1, '2025-05-25 22:58:06', 1);
INSERT INTO `sys_role_menu` VALUES (196, 2, 2, '2025-05-25 22:58:06', 1);
INSERT INTO `sys_role_menu` VALUES (197, 2, 4, '2025-05-25 22:58:12', 1);
INSERT INTO `sys_role_menu` VALUES (198, 2, 5, '2025-05-25 22:58:12', 1);
INSERT INTO `sys_role_menu` VALUES (199, 2, 12, '2025-05-25 22:58:13', 1);
INSERT INTO `sys_role_menu` VALUES (200, 2, 13, '2025-05-25 22:58:13', 1);
INSERT INTO `sys_role_menu` VALUES (201, 2, 1, '2025-05-25 22:58:13', 1);
INSERT INTO `sys_role_menu` VALUES (202, 2, 2, '2025-05-25 22:58:13', 1);
INSERT INTO `sys_role_menu` VALUES (203, 1, 19, '2025-05-26 06:21:57', 1);
INSERT INTO `sys_role_menu` VALUES (204, 1, 25, '2025-05-26 06:21:57', 1);
INSERT INTO `sys_role_menu` VALUES (205, 1, 27, '2025-05-26 06:21:57', 1);
INSERT INTO `sys_role_menu` VALUES (206, 1, 26, '2025-05-26 06:21:57', 1);
INSERT INTO `sys_role_menu` VALUES (207, 1, 18, '2025-05-26 06:21:57', 1);
INSERT INTO `sys_role_menu` VALUES (208, 1, 22, '2025-05-26 06:21:57', 1);
INSERT INTO `sys_role_menu` VALUES (209, 1, 24, '2025-05-26 06:21:57', 1);
INSERT INTO `sys_role_menu` VALUES (210, 1, 23, '2025-05-26 06:21:57', 1);
INSERT INTO `sys_role_menu` VALUES (218, 1, 1, '2025-05-26 15:10:50', 1);
INSERT INTO `sys_role_menu` VALUES (219, 1, 2, '2025-05-26 15:10:50', 1);
INSERT INTO `sys_role_menu` VALUES (220, 1, 4, '2025-05-26 15:10:50', 1);
INSERT INTO `sys_role_menu` VALUES (221, 1, 5, '2025-05-26 15:10:50', 1);
INSERT INTO `sys_role_menu` VALUES (222, 1, 6, '2025-05-26 15:10:50', 1);
INSERT INTO `sys_role_menu` VALUES (223, 1, 7, '2025-05-26 15:10:50', 1);
INSERT INTO `sys_role_menu` VALUES (224, 1, 3, '2025-05-26 15:10:50', 1);
INSERT INTO `sys_role_menu` VALUES (225, 1, 8, '2025-05-26 15:10:51', 1);
INSERT INTO `sys_role_menu` VALUES (226, 1, 9, '2025-05-26 15:10:51', 1);
INSERT INTO `sys_role_menu` VALUES (227, 1, 10, '2025-05-26 15:10:51', 1);
INSERT INTO `sys_role_menu` VALUES (228, 1, 11, '2025-05-26 15:10:51', 1);
INSERT INTO `sys_role_menu` VALUES (229, 1, 17, '2025-05-26 15:10:51', 1);
INSERT INTO `sys_role_menu` VALUES (230, 1, 18, '2025-05-26 15:10:51', 1);
INSERT INTO `sys_role_menu` VALUES (231, 1, 22, '2025-05-26 15:10:51', 1);
INSERT INTO `sys_role_menu` VALUES (232, 1, 23, '2025-05-26 15:10:51', 1);
INSERT INTO `sys_role_menu` VALUES (233, 1, 24, '2025-05-26 15:10:51', 1);
INSERT INTO `sys_role_menu` VALUES (234, 1, 19, '2025-05-26 15:10:51', 1);
INSERT INTO `sys_role_menu` VALUES (235, 1, 25, '2025-05-26 15:10:51', 1);
INSERT INTO `sys_role_menu` VALUES (236, 1, 26, '2025-05-26 15:10:51', 1);
INSERT INTO `sys_role_menu` VALUES (237, 1, 27, '2025-05-26 15:10:51', 1);
INSERT INTO `sys_role_menu` VALUES (238, 1, 12, '2025-05-26 15:10:51', 1);
INSERT INTO `sys_role_menu` VALUES (239, 1, 13, '2025-05-26 15:10:51', 1);
INSERT INTO `sys_role_menu` VALUES (240, 1, 29, '2025-05-26 08:22:57', 1);
INSERT INTO `sys_role_menu` VALUES (241, 1, 30, '2025-05-26 08:22:57', 1);
INSERT INTO `sys_role_menu` VALUES (242, 1, 32, '2025-05-26 08:22:57', 1);
INSERT INTO `sys_role_menu` VALUES (243, 1, 34, '2025-05-26 08:22:57', 1);
INSERT INTO `sys_role_menu` VALUES (244, 1, 33, '2025-05-26 08:22:57', 1);
INSERT INTO `sys_role_menu` VALUES (245, 1, 36, '2025-05-26 08:22:57', 1);
INSERT INTO `sys_role_menu` VALUES (246, 1, 35, '2025-05-26 08:22:57', 1);
INSERT INTO `sys_role_menu` VALUES (247, 2, 29, '2025-05-26 08:22:57', 1);
INSERT INTO `sys_role_menu` VALUES (248, 2, 31, '2025-05-26 08:22:57', 1);
INSERT INTO `sys_role_menu` VALUES (253, 2, 4, '2025-05-26 16:24:08', 1);
INSERT INTO `sys_role_menu` VALUES (254, 2, 5, '2025-05-26 16:24:08', 1);
INSERT INTO `sys_role_menu` VALUES (255, 2, 12, '2025-05-26 16:24:08', 1);
INSERT INTO `sys_role_menu` VALUES (256, 2, 13, '2025-05-26 16:24:08', 1);
INSERT INTO `sys_role_menu` VALUES (257, 2, 32, '2025-05-26 16:24:08', 1);
INSERT INTO `sys_role_menu` VALUES (258, 2, 31, '2025-05-26 16:24:08', 1);
INSERT INTO `sys_role_menu` VALUES (259, 2, 1, '2025-05-26 16:24:08', 1);
INSERT INTO `sys_role_menu` VALUES (260, 2, 2, '2025-05-26 16:24:08', 1);
INSERT INTO `sys_role_menu` VALUES (261, 2, 29, '2025-05-26 16:24:08', 1);
INSERT INTO `sys_role_menu` VALUES (262, 2, 30, '2025-05-26 16:24:08', 1);
INSERT INTO `sys_role_menu` VALUES (263, 2, 4, '2025-05-26 16:24:33', 1);
INSERT INTO `sys_role_menu` VALUES (264, 2, 5, '2025-05-26 16:24:33', 1);
INSERT INTO `sys_role_menu` VALUES (265, 2, 12, '2025-05-26 16:24:33', 1);
INSERT INTO `sys_role_menu` VALUES (266, 2, 13, '2025-05-26 16:24:33', 1);
INSERT INTO `sys_role_menu` VALUES (267, 2, 34, '2025-05-26 16:24:33', 1);
INSERT INTO `sys_role_menu` VALUES (268, 2, 31, '2025-05-26 16:24:33', 1);
INSERT INTO `sys_role_menu` VALUES (269, 2, 1, '2025-05-26 16:24:33', 1);
INSERT INTO `sys_role_menu` VALUES (270, 2, 2, '2025-05-26 16:24:33', 1);
INSERT INTO `sys_role_menu` VALUES (271, 2, 29, '2025-05-26 16:24:33', 1);
INSERT INTO `sys_role_menu` VALUES (272, 2, 30, '2025-05-26 16:24:33', 1);
INSERT INTO `sys_role_menu` VALUES (273, 2, 4, '2025-05-26 16:24:47', 1);
INSERT INTO `sys_role_menu` VALUES (274, 2, 5, '2025-05-26 16:24:47', 1);
INSERT INTO `sys_role_menu` VALUES (275, 2, 12, '2025-05-26 16:24:47', 1);
INSERT INTO `sys_role_menu` VALUES (276, 2, 13, '2025-05-26 16:24:47', 1);
INSERT INTO `sys_role_menu` VALUES (277, 2, 31, '2025-05-26 16:24:47', 1);
INSERT INTO `sys_role_menu` VALUES (278, 2, 1, '2025-05-26 16:24:47', 1);
INSERT INTO `sys_role_menu` VALUES (279, 2, 2, '2025-05-26 16:24:47', 1);
INSERT INTO `sys_role_menu` VALUES (280, 2, 29, '2025-05-26 16:24:47', 1);
INSERT INTO `sys_role_menu` VALUES (281, 2, 4, '2025-05-26 16:24:58', 1);
INSERT INTO `sys_role_menu` VALUES (282, 2, 5, '2025-05-26 16:24:58', 1);
INSERT INTO `sys_role_menu` VALUES (283, 2, 12, '2025-05-26 16:24:58', 1);
INSERT INTO `sys_role_menu` VALUES (284, 2, 13, '2025-05-26 16:24:58', 1);
INSERT INTO `sys_role_menu` VALUES (285, 2, 36, '2025-05-26 16:24:58', 1);
INSERT INTO `sys_role_menu` VALUES (286, 2, 31, '2025-05-26 16:24:58', 1);
INSERT INTO `sys_role_menu` VALUES (287, 2, 1, '2025-05-26 16:24:58', 1);
INSERT INTO `sys_role_menu` VALUES (288, 2, 2, '2025-05-26 16:24:58', 1);
INSERT INTO `sys_role_menu` VALUES (289, 2, 29, '2025-05-26 16:24:58', 1);
INSERT INTO `sys_role_menu` VALUES (290, 2, 30, '2025-05-26 16:24:58', 1);
INSERT INTO `sys_role_menu` VALUES (291, 2, 4, '2025-05-26 16:25:24', 1);
INSERT INTO `sys_role_menu` VALUES (292, 2, 5, '2025-05-26 16:25:24', 1);
INSERT INTO `sys_role_menu` VALUES (293, 2, 12, '2025-05-26 16:25:24', 1);
INSERT INTO `sys_role_menu` VALUES (294, 2, 13, '2025-05-26 16:25:24', 1);
INSERT INTO `sys_role_menu` VALUES (295, 2, 32, '2025-05-26 16:25:24', 1);
INSERT INTO `sys_role_menu` VALUES (296, 2, 31, '2025-05-26 16:25:24', 1);
INSERT INTO `sys_role_menu` VALUES (297, 2, 1, '2025-05-26 16:25:24', 1);
INSERT INTO `sys_role_menu` VALUES (298, 2, 2, '2025-05-26 16:25:24', 1);
INSERT INTO `sys_role_menu` VALUES (299, 2, 29, '2025-05-26 16:25:24', 1);
INSERT INTO `sys_role_menu` VALUES (300, 2, 30, '2025-05-26 16:25:24', 1);
INSERT INTO `sys_role_menu` VALUES (301, 2, 4, '2025-05-26 16:25:48', 1);
INSERT INTO `sys_role_menu` VALUES (302, 2, 5, '2025-05-26 16:25:48', 1);
INSERT INTO `sys_role_menu` VALUES (303, 2, 12, '2025-05-26 16:25:48', 1);
INSERT INTO `sys_role_menu` VALUES (304, 2, 13, '2025-05-26 16:25:48', 1);
INSERT INTO `sys_role_menu` VALUES (305, 2, 36, '2025-05-26 16:25:48', 1);
INSERT INTO `sys_role_menu` VALUES (306, 2, 31, '2025-05-26 16:25:48', 1);
INSERT INTO `sys_role_menu` VALUES (307, 2, 1, '2025-05-26 16:25:48', 1);
INSERT INTO `sys_role_menu` VALUES (308, 2, 2, '2025-05-26 16:25:48', 1);
INSERT INTO `sys_role_menu` VALUES (309, 2, 29, '2025-05-26 16:25:48', 1);
INSERT INTO `sys_role_menu` VALUES (310, 2, 30, '2025-05-26 16:25:48', 1);
INSERT INTO `sys_role_menu` VALUES (311, 2, 4, '2025-05-26 16:32:09', 1);
INSERT INTO `sys_role_menu` VALUES (312, 2, 5, '2025-05-26 16:32:09', 1);
INSERT INTO `sys_role_menu` VALUES (313, 2, 6, '2025-05-26 16:32:09', 1);
INSERT INTO `sys_role_menu` VALUES (314, 2, 12, '2025-05-26 16:32:09', 1);
INSERT INTO `sys_role_menu` VALUES (315, 2, 13, '2025-05-26 16:32:09', 1);
INSERT INTO `sys_role_menu` VALUES (316, 2, 36, '2025-05-26 16:32:09', 1);
INSERT INTO `sys_role_menu` VALUES (317, 2, 31, '2025-05-26 16:32:09', 1);
INSERT INTO `sys_role_menu` VALUES (318, 2, 1, '2025-05-26 16:32:09', 1);
INSERT INTO `sys_role_menu` VALUES (319, 2, 2, '2025-05-26 16:32:09', 1);
INSERT INTO `sys_role_menu` VALUES (320, 2, 29, '2025-05-26 16:32:09', 1);
INSERT INTO `sys_role_menu` VALUES (321, 2, 30, '2025-05-26 16:32:09', 1);
INSERT INTO `sys_role_menu` VALUES (322, 2, 4, '2025-05-26 16:53:23', 1);
INSERT INTO `sys_role_menu` VALUES (323, 2, 5, '2025-05-26 16:53:23', 1);
INSERT INTO `sys_role_menu` VALUES (324, 2, 6, '2025-05-26 16:53:23', 1);
INSERT INTO `sys_role_menu` VALUES (325, 2, 12, '2025-05-26 16:53:23', 1);
INSERT INTO `sys_role_menu` VALUES (326, 2, 13, '2025-05-26 16:53:23', 1);
INSERT INTO `sys_role_menu` VALUES (327, 2, 32, '2025-05-26 16:53:23', 1);
INSERT INTO `sys_role_menu` VALUES (328, 2, 31, '2025-05-26 16:53:23', 1);
INSERT INTO `sys_role_menu` VALUES (329, 2, 1, '2025-05-26 16:53:23', 1);
INSERT INTO `sys_role_menu` VALUES (330, 2, 2, '2025-05-26 16:53:23', 1);
INSERT INTO `sys_role_menu` VALUES (331, 2, 29, '2025-05-26 16:53:24', 1);
INSERT INTO `sys_role_menu` VALUES (332, 2, 30, '2025-05-26 16:53:24', 1);
INSERT INTO `sys_role_menu` VALUES (333, 1, 37, '2025-05-26 17:02:34', 1);
INSERT INTO `sys_role_menu` VALUES (334, 1, 38, '2025-05-26 17:02:34', 1);
INSERT INTO `sys_role_menu` VALUES (335, 1, 39, '2025-05-26 17:02:34', 1);
INSERT INTO `sys_role_menu` VALUES (336, 1, 41, '2025-05-26 17:02:34', 1);
INSERT INTO `sys_role_menu` VALUES (337, 1, 40, '2025-05-26 17:02:34', 1);
INSERT INTO `sys_role_menu` VALUES (338, 1, 42, '2025-05-26 17:02:34', 1);
INSERT INTO `sys_role_menu` VALUES (343, 1, 44, '2025-05-26 17:15:37', 1);
INSERT INTO `sys_role_menu` VALUES (344, 1, 45, '2025-05-26 17:15:37', 1);
INSERT INTO `sys_role_menu` VALUES (345, 1, 49, '2025-05-26 17:15:37', 1);
INSERT INTO `sys_role_menu` VALUES (346, 1, 51, '2025-05-26 17:15:37', 1);
INSERT INTO `sys_role_menu` VALUES (347, 1, 50, '2025-05-26 17:15:37', 1);
INSERT INTO `sys_role_menu` VALUES (348, 1, 47, '2025-05-26 17:15:37', 1);
INSERT INTO `sys_role_menu` VALUES (349, 1, 48, '2025-05-26 17:15:37', 1);
INSERT INTO `sys_role_menu` VALUES (350, 1, 46, '2025-05-26 17:15:37', 1);
INSERT INTO `sys_role_menu` VALUES (351, 1, 53, '2025-05-26 17:15:37', 1);
INSERT INTO `sys_role_menu` VALUES (352, 1, 52, '2025-05-26 17:15:37', 1);
INSERT INTO `sys_role_menu` VALUES (353, 1, 54, '2025-05-26 17:15:37', 1);
INSERT INTO `sys_role_menu` VALUES (354, 1, 43, '2025-05-26 17:15:37', 1);
INSERT INTO `sys_role_menu` VALUES (363, 1, 45, '2025-05-29 06:50:39', 1);
INSERT INTO `sys_role_menu` VALUES (365, 1, 65, '2025-05-30 01:51:10', 1);
INSERT INTO `sys_role_menu` VALUES (366, 2, 4, '2025-05-30 09:53:38', 1);
INSERT INTO `sys_role_menu` VALUES (367, 2, 5, '2025-05-30 09:53:38', 1);
INSERT INTO `sys_role_menu` VALUES (368, 2, 6, '2025-05-30 09:53:39', 1);
INSERT INTO `sys_role_menu` VALUES (369, 2, 12, '2025-05-30 09:53:39', 1);
INSERT INTO `sys_role_menu` VALUES (370, 2, 13, '2025-05-30 09:53:39', 1);
INSERT INTO `sys_role_menu` VALUES (371, 2, 31, '2025-05-30 09:53:39', 1);
INSERT INTO `sys_role_menu` VALUES (372, 2, 1, '2025-05-30 09:53:39', 1);
INSERT INTO `sys_role_menu` VALUES (373, 2, 2, '2025-05-30 09:53:39', 1);
INSERT INTO `sys_role_menu` VALUES (374, 2, 29, '2025-05-30 09:53:39', 1);
INSERT INTO `sys_role_menu` VALUES (375, 2, 12, '2025-05-30 09:53:56', 0);
INSERT INTO `sys_role_menu` VALUES (376, 2, 13, '2025-05-30 09:53:56', 0);
INSERT INTO `sys_role_menu` VALUES (377, 2, 31, '2025-05-30 09:53:56', 0);
INSERT INTO `sys_role_menu` VALUES (378, 2, 29, '2025-05-30 09:53:56', 0);
INSERT INTO `sys_role_menu` VALUES (380, 1, 1, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (381, 1, 2, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (382, 1, 4, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (383, 1, 5, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (384, 1, 6, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (385, 1, 7, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (386, 1, 3, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (387, 1, 8, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (388, 1, 9, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (389, 1, 10, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (390, 1, 11, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (391, 1, 17, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (392, 1, 18, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (393, 1, 22, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (394, 1, 23, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (395, 1, 24, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (396, 1, 19, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (397, 1, 25, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (398, 1, 26, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (399, 1, 27, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (400, 1, 12, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (401, 1, 13, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (402, 1, 30, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (403, 1, 32, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (404, 1, 33, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (405, 1, 34, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (406, 1, 35, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (407, 1, 36, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (408, 1, 38, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (409, 1, 39, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (410, 1, 40, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (411, 1, 41, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (412, 1, 42, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (413, 1, 65, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (414, 1, 29, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (415, 1, 37, '2025-05-30 10:01:15', 0);
INSERT INTO `sys_role_menu` VALUES (416, 1, 38, '2025-05-30 02:43:32', 0);
INSERT INTO `sys_role_menu` VALUES (417, 1, 69, '2025-05-30 02:43:32', 0);
INSERT INTO `sys_role_menu` VALUES (418, 1, 37, '2025-05-30 02:43:32', 0);
INSERT INTO `sys_role_menu` VALUES (419, 1, 70, '2025-05-30 03:15:53', 0);
INSERT INTO `sys_role_menu` VALUES (420, 1, 71, '2025-05-30 03:15:53', 0);
INSERT INTO `sys_role_menu` VALUES (425, 2, 70, '2025-05-30 03:15:53', 0);
INSERT INTO `sys_role_menu` VALUES (426, 2, 73, '2025-05-30 03:15:53', 0);
INSERT INTO `sys_role_menu` VALUES (428, 1, 75, '2025-05-30 03:38:17', 0);
INSERT INTO `sys_role_menu` VALUES (429, 1, 76, '2025-05-30 03:38:17', 0);
INSERT INTO `sys_role_menu` VALUES (430, 1, 79, '2025-05-30 03:38:17', 0);
INSERT INTO `sys_role_menu` VALUES (431, 1, 81, '2025-05-30 03:38:17', 0);
INSERT INTO `sys_role_menu` VALUES (432, 1, 80, '2025-05-30 03:38:17', 0);
INSERT INTO `sys_role_menu` VALUES (433, 1, 82, '2025-05-30 03:38:17', 0);
INSERT INTO `sys_role_menu` VALUES (434, 1, 78, '2025-05-30 03:38:17', 0);
INSERT INTO `sys_role_menu` VALUES (435, 1, 77, '2025-05-30 03:38:17', 0);
INSERT INTO `sys_role_menu` VALUES (436, 1, 83, '2025-05-30 03:38:17', 0);
INSERT INTO `sys_role_menu` VALUES (437, 1, 85, '2025-05-30 03:38:17', 0);
INSERT INTO `sys_role_menu` VALUES (438, 1, 84, '2025-05-30 03:38:17', 0);
INSERT INTO `sys_role_menu` VALUES (443, 2, 75, '2025-05-30 03:38:17', 0);
INSERT INTO `sys_role_menu` VALUES (444, 2, 78, '2025-05-30 03:38:17', 0);
INSERT INTO `sys_role_menu` VALUES (445, 2, 77, '2025-05-30 03:38:17', 0);
INSERT INTO `sys_role_menu` VALUES (446, 2, 83, '2025-05-30 03:38:17', 0);
INSERT INTO `sys_role_menu` VALUES (447, 2, 85, '2025-05-30 03:38:17', 0);
INSERT INTO `sys_role_menu` VALUES (448, 2, 84, '2025-05-30 03:38:17', 0);
INSERT INTO `sys_role_menu` VALUES (505, 1, 108, '2025-05-30 15:05:49', 0);
INSERT INTO `sys_role_menu` VALUES (659, 3, 12, '2025-05-31 18:08:21', 0);
INSERT INTO `sys_role_menu` VALUES (660, 3, 13, '2025-05-31 18:08:21', 0);
INSERT INTO `sys_role_menu` VALUES (661, 3, 31, '2025-05-31 18:08:21', 0);
INSERT INTO `sys_role_menu` VALUES (662, 3, 42, '2025-05-31 18:08:21', 0);
INSERT INTO `sys_role_menu` VALUES (663, 3, 66, '2025-05-31 18:08:21', 0);
INSERT INTO `sys_role_menu` VALUES (664, 3, 103, '2025-05-31 18:08:21', 0);
INSERT INTO `sys_role_menu` VALUES (665, 3, 102, '2025-05-31 18:08:21', 0);
INSERT INTO `sys_role_menu` VALUES (666, 3, 29, '2025-05-31 18:08:21', 0);
INSERT INTO `sys_role_menu` VALUES (667, 3, 37, '2025-05-31 18:08:21', 0);
INSERT INTO `sys_role_menu` VALUES (668, 3, 38, '2025-05-31 18:08:21', 0);
INSERT INTO `sys_role_menu` VALUES (669, 2, 110, '2025-05-31 12:07:53', 0);
INSERT INTO `sys_role_menu` VALUES (670, 2, 111, '2025-05-31 12:07:53', 0);
INSERT INTO `sys_role_menu` VALUES (671, 2, 112, '2025-05-31 12:07:53', 0);
INSERT INTO `sys_role_menu` VALUES (672, 2, 117, '2025-05-31 12:07:53', 0);
INSERT INTO `sys_role_menu` VALUES (673, 2, 113, '2025-05-31 12:07:53', 0);
INSERT INTO `sys_role_menu` VALUES (674, 2, 114, '2025-05-31 12:07:53', 0);
INSERT INTO `sys_role_menu` VALUES (675, 2, 115, '2025-05-31 12:07:53', 0);
INSERT INTO `sys_role_menu` VALUES (676, 2, 116, '2025-05-31 12:07:53', 0);
INSERT INTO `sys_role_menu` VALUES (677, 2, 118, '2025-05-31 12:07:53', 0);
INSERT INTO `sys_role_menu` VALUES (678, 1, 110, '2025-05-31 12:07:53', 0);
INSERT INTO `sys_role_menu` VALUES (679, 1, 111, '2025-05-31 12:07:53', 0);
INSERT INTO `sys_role_menu` VALUES (680, 1, 112, '2025-05-31 12:07:53', 0);
INSERT INTO `sys_role_menu` VALUES (681, 1, 117, '2025-05-31 12:07:53', 0);
INSERT INTO `sys_role_menu` VALUES (682, 1, 113, '2025-05-31 12:07:53', 0);
INSERT INTO `sys_role_menu` VALUES (683, 1, 114, '2025-05-31 12:07:53', 0);
INSERT INTO `sys_role_menu` VALUES (684, 1, 115, '2025-05-31 12:07:53', 0);
INSERT INTO `sys_role_menu` VALUES (685, 1, 116, '2025-05-31 12:07:53', 0);
INSERT INTO `sys_role_menu` VALUES (686, 1, 118, '2025-05-31 12:07:53', 0);
INSERT INTO `sys_role_menu` VALUES (687, 2, 109, '2025-05-31 12:47:24', 0);
INSERT INTO `sys_role_menu` VALUES (688, 2, 110, '2025-05-31 12:47:24', 0);
INSERT INTO `sys_role_menu` VALUES (689, 2, 111, '2025-05-31 12:47:24', 0);
INSERT INTO `sys_role_menu` VALUES (690, 2, 112, '2025-05-31 12:47:24', 0);
INSERT INTO `sys_role_menu` VALUES (691, 2, 113, '2025-05-31 12:47:24', 0);
INSERT INTO `sys_role_menu` VALUES (692, 2, 114, '2025-05-31 12:47:24', 0);
INSERT INTO `sys_role_menu` VALUES (693, 2, 115, '2025-05-31 12:47:24', 0);
INSERT INTO `sys_role_menu` VALUES (694, 1, 109, '2025-05-31 12:47:25', 0);
INSERT INTO `sys_role_menu` VALUES (695, 1, 110, '2025-05-31 12:47:25', 0);
INSERT INTO `sys_role_menu` VALUES (696, 1, 111, '2025-05-31 12:47:25', 0);
INSERT INTO `sys_role_menu` VALUES (697, 1, 112, '2025-05-31 12:47:25', 0);
INSERT INTO `sys_role_menu` VALUES (698, 1, 113, '2025-05-31 12:47:25', 0);
INSERT INTO `sys_role_menu` VALUES (699, 1, 114, '2025-05-31 12:47:25', 0);
INSERT INTO `sys_role_menu` VALUES (700, 1, 115, '2025-05-31 12:47:25', 0);

-- ----------------------------
-- Table structure for sys_schedule
-- ----------------------------
DROP TABLE IF EXISTS `sys_schedule`;
CREATE TABLE `sys_schedule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师姓名',
  `class_id` bigint NULL DEFAULT NULL COMMENT '班级ID',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '班级名称',
  `class_course_hours_id` bigint NULL DEFAULT NULL COMMENT '班级课程课时记录ID',
  `academic_year` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学年',
  `week_number` int NOT NULL COMMENT '第几周 (1-20)',
  `day_of_week` int NOT NULL COMMENT '星期几 (1-7, 1为周一)',
  `time_slot` int NOT NULL COMMENT '时间段 (1-6)',
  `time_range` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '时间范围文字描述',
  `reduced_hours` int NULL DEFAULT 2 COMMENT '减少的课时数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NULL DEFAULT 0 COMMENT '是否删除 (0-否, 1-是)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_teacher_academic`(`teacher_id` ASC, `academic_year` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_time_schedule`(`week_number` ASC, `day_of_week` ASC, `time_slot` ASC) USING BTREE,
  INDEX `idx_class_id`(`class_id` ASC) USING BTREE,
  INDEX `idx_class_course_hours_id`(`class_course_hours_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_schedule
-- ----------------------------
INSERT INTO `sys_schedule` VALUES (1, 7, 'Web开发实践', 5, '张老师', NULL, NULL, NULL, '2024-2025', 1, 1, 1, '08:00-09:40', 2, '2025-05-30 21:13:22', '2025-05-30 13:17:08', 1);
INSERT INTO `sys_schedule` VALUES (2, 7, 'Web开发实践', 5, '张老师', NULL, NULL, NULL, '2024-2025', 1, 1, 1, '08:00-09:40', 2, '2025-05-30 21:16:43', '2025-05-30 21:17:16', 1);
INSERT INTO `sys_schedule` VALUES (3, 7, 'Web开发实践', 5, '张老师', NULL, NULL, NULL, '2024-2025', 1, 1, 1, '08:00-09:40', 2, '2025-05-30 21:17:20', '2025-05-30 21:19:43', 1);
INSERT INTO `sys_schedule` VALUES (4, 7, 'Web开发实践', 5, '张老师', NULL, NULL, NULL, '2024-2025', 1, 1, 1, '08:00-09:40', 2, '2025-05-30 21:19:46', '2025-05-30 21:20:11', 1);
INSERT INTO `sys_schedule` VALUES (5, 7, 'Web开发实践', 5, '张老师', NULL, NULL, NULL, '2024-2025', 1, 1, 1, '08:00-09:40', 2, '2025-05-30 21:20:14', '2025-05-30 21:20:14', 0);
INSERT INTO `sys_schedule` VALUES (6, 7, 'Web开发实践', 5, '张老师', NULL, NULL, NULL, '2024-2025', 1, 2, 1, '08:00-09:40', 2, '2025-05-30 21:25:21', '2025-05-30 21:25:21', 0);
INSERT INTO `sys_schedule` VALUES (7, 7, 'Web开发实践', 5, '张老师', NULL, NULL, NULL, '2024-2025', 1, 3, 1, '08:00-09:40', 2, '2025-05-30 21:28:22', '2025-05-30 21:28:22', 0);
INSERT INTO `sys_schedule` VALUES (8, 4, '软件工程', 4, '2', NULL, NULL, NULL, '2024-2025', 1, 1, 1, '08:00-09:40', 2, '2025-05-30 22:15:02', '2025-05-31 11:43:15', 1);
INSERT INTO `sys_schedule` VALUES (9, 4, '软件工程', 4, '2', NULL, NULL, NULL, '2024-2025', 1, 2, 2, '10:00-11:40', 2, '2025-05-30 22:15:39', '2025-05-31 11:43:16', 1);
INSERT INTO `sys_schedule` VALUES (10, 10, '人工智能导论', 4, '2', NULL, NULL, NULL, '2024-2025', 1, 4, 1, '08:00-09:40', 2, '2025-05-30 22:16:13', '2025-05-31 11:43:20', 1);
INSERT INTO `sys_schedule` VALUES (11, 2, '数据结构与算法', 4, '2', NULL, NULL, NULL, '2024-2025', 1, 4, 3, '16:30-18:10', 2, '2025-05-30 22:24:19', '2025-05-31 11:43:23', 1);
INSERT INTO `sys_schedule` VALUES (12, 2, '数据结构与算法', 4, '2', NULL, NULL, NULL, '2024-2025', 1, 5, 1, '08:00-09:40', 2, '2025-05-30 22:24:27', '2025-05-31 11:43:21', 1);
INSERT INTO `sys_schedule` VALUES (13, 2, '数据结构与算法', 4, '2', NULL, NULL, NULL, '2024-2025', 1, 1, 3, '16:30-18:10', 2, '2025-05-30 22:32:18', '2025-05-31 11:43:18', 1);
INSERT INTO `sys_schedule` VALUES (14, 2, '数据结构与算法', 4, '2', NULL, NULL, NULL, '2024-2025', 1, 5, 3, '16:30-18:10', 1, '2025-05-30 22:38:59', '2025-05-31 11:43:25', 1);
INSERT INTO `sys_schedule` VALUES (15, 2, '数据结构与算法', 4, '2', NULL, NULL, NULL, '2024-2025', 1, 4, 4, '18:20-20:00', 1, '2025-05-30 23:02:07', '2025-05-31 11:43:26', 1);
INSERT INTO `sys_schedule` VALUES (16, 2, '数据结构与算法', 4, '2', 14, '软工2021级1班', NULL, '2024-2025', 1, 1, 1, '08:00-09:40', 1, '2025-05-31 11:43:33', '2025-05-31 11:50:49', 1);
INSERT INTO `sys_schedule` VALUES (17, 2, '数据结构与算法', 4, '2', 14, '软工2021级1班', NULL, '2024-2025', 1, 3, 1, '08:00-09:40', 1, '2025-05-31 11:43:35', '2025-05-31 11:50:51', 1);
INSERT INTO `sys_schedule` VALUES (18, 2, '数据结构与算法', 4, '2', 15, '软工2021级2班', NULL, '2024-2025', 1, 2, 1, '08:00-09:40', 1, '2025-05-31 11:44:09', '2025-05-31 11:50:46', 1);
INSERT INTO `sys_schedule` VALUES (19, 2, '数据结构与算法', 4, '2', 14, '软工2021级1班', NULL, '2024-2025', 1, 2, 1, '08:00-09:40', 1, '2025-05-31 12:10:10', '2025-05-31 12:35:13', 1);
INSERT INTO `sys_schedule` VALUES (20, 2, '数据结构与算法', 4, '2', 14, '软工2021级1班', NULL, '2024-2025', 1, 3, 1, '08:00-09:40', 1, '2025-05-31 12:10:46', '2025-05-31 12:35:16', 1);
INSERT INTO `sys_schedule` VALUES (21, 2, '数据结构与算法', 4, '2', 14, '软工2021级1班', NULL, '2024-2025', 1, 4, 1, '08:00-09:40', 1, '2025-05-31 12:10:49', '2025-05-31 12:35:27', 1);
INSERT INTO `sys_schedule` VALUES (22, 2, '数据结构与算法', 4, '2', 14, '软工2021级1班', NULL, '2024-2025', 1, 5, 1, '08:00-09:40', 1, '2025-05-31 12:10:53', '2025-05-31 12:36:51', 1);
INSERT INTO `sys_schedule` VALUES (23, 2, '数据结构与算法', 4, '2', 15, '软工2021级2班', NULL, '2024-2025', 1, 2, 2, '10:00-11:40', 1, '2025-05-31 12:10:59', '2025-05-31 12:39:22', 1);
INSERT INTO `sys_schedule` VALUES (24, 2, '数据结构与算法', 4, '2', 14, '软工2021级1班', 1, '2024-2025', 1, 2, 2, '08:00-09:40', 1, '2025-05-31 12:39:14', '2025-05-31 12:39:14', 0);
INSERT INTO `sys_schedule` VALUES (25, 2, '数据结构与算法', 4, '2', 14, '软工2021级1班', 1, '2024-2025', 1, 3, 3, '10:00-11:40', 1, '2025-05-31 12:39:18', '2025-05-31 12:39:18', 0);
INSERT INTO `sys_schedule` VALUES (26, 2, '数据结构与算法', 4, '2', 15, '软工2021级2班', 2, '2024-2025', 1, 2, 1, '08:00-09:40', 1, '2025-05-31 12:39:25', '2025-05-31 12:53:50', 1);
INSERT INTO `sys_schedule` VALUES (27, 5, 'Web开发实践', 4, '2', 15, '软工2021级2班', 3, '2024-2025', 1, 3, 1, '08:00-09:40', 1, '2025-05-31 15:59:33', '2025-05-31 15:59:33', 0);
INSERT INTO `sys_schedule` VALUES (28, 5, 'Web开发实践', 4, '2', 14, '软工2021级1班', 4, '2024-2025', 1, 2, 1, '08:00-09:40', 1, '2025-05-31 17:15:04', '2025-05-31 17:15:04', 0);
INSERT INTO `sys_schedule` VALUES (29, 8, 'AFDSAFDSA', 4, '2', 14, '软工2021级1班', 5, '2023-2024', 1, 1, 6, '20:50-22:30', 1, '2025-05-31 18:26:49', '2025-05-31 18:26:49', 0);
INSERT INTO `sys_schedule` VALUES (30, 8, 'AFDSAFDSA', 4, '2', 15, '软工2021级2班', 6, '2023-2024', 1, 4, 2, '10:00-11:40', 1, '2025-05-31 18:27:32', '2025-05-31 18:27:32', 0);
INSERT INTO `sys_schedule` VALUES (31, 8, 'AFDSAFDSA', 4, '2', 14, '软工2021级1班', 5, '2023-2024', 1, 2, 4, '16:30-18:10', 1, '2025-05-31 18:27:43', '2025-05-31 18:27:43', 0);

-- ----------------------------
-- Table structure for sys_student
-- ----------------------------
DROP TABLE IF EXISTS `sys_student`;
CREATE TABLE `sys_student`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '关联用户ID',
  `student_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学生姓名',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '未知' COMMENT '性别',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专业',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '班级',
  `grade` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '年级',
  `education_system` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学制',
  `enrollment_date` datetime NULL DEFAULT NULL COMMENT '入学时间',
  `graduation_date` datetime NULL DEFAULT NULL COMMENT '毕业时间',
  `current_year` int NULL DEFAULT NULL COMMENT '当前年级(1-5)',
  `current_semester` int NULL DEFAULT NULL COMMENT '当前学期(1-上学期，2-下学期)',
  `current_academic_year` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '当前学年学期',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_student
-- ----------------------------
INSERT INTO `sys_student` VALUES (6, 16, 'S2025001', '4', NULL, '4', '4', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2025-05-31 21:25:14', '2025-05-31 21:28:54', 1);
INSERT INTO `sys_student` VALUES (10, 12, 'S2025001', '3', NULL, '3', '3', NULL, NULL, '2021', '4年', NULL, NULL, 2, 1, '大二上学期', 1, '2025-05-31 21:34:00', '2025-05-31 21:55:38', 0);

-- ----------------------------
-- Table structure for sys_student_course
-- ----------------------------
DROP TABLE IF EXISTS `sys_student_course`;
CREATE TABLE `sys_student_course`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `student_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学号',
  `student_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学生姓名',
  `course_application_id` bigint NOT NULL COMMENT '课程申请ID',
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '教师姓名',
  `academic_year` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学年',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学期',
  `course_hours` int NOT NULL COMMENT '课程学时',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-已选课，1-已退课',
  `enroll_time` datetime NULL DEFAULT NULL COMMENT '选课时间',
  `withdraw_time` datetime NULL DEFAULT NULL COMMENT '退课时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_student_course`(`student_id` ASC, `course_application_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_course_application_id`(`course_application_id` ASC) USING BTREE,
  INDEX `idx_academic_year`(`academic_year` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生选课表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_student_course
-- ----------------------------

-- ----------------------------
-- Table structure for sys_student_course_selection
-- ----------------------------
DROP TABLE IF EXISTS `sys_student_course_selection`;
CREATE TABLE `sys_student_course_selection`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '选课记录ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `student_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学号',
  `student_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学生姓名',
  `course_application_id` bigint NOT NULL COMMENT '开课申请ID',
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '教师姓名',
  `academic_year` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学年',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学期',
  `course_hours` int NOT NULL COMMENT '课程学时',
  `selection_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-已选课，0-已退课',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_student_course`(`student_id` ASC, `course_application_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_course_application_id`(`course_application_id` ASC) USING BTREE,
  INDEX `idx_academic_year`(`academic_year` ASC) USING BTREE,
  INDEX `idx_semester`(`semester` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生选课关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_student_course_selection
-- ----------------------------

-- ----------------------------
-- Table structure for sys_teacher
-- ----------------------------
DROP TABLE IF EXISTS `sys_teacher`;
CREATE TABLE `sys_teacher`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '教师ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '关联用户ID',
  `teacher_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '教师工号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '教师姓名',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '未知' COMMENT '性别',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `department` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属部门',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '职称',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教师信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_teacher
-- ----------------------------
INSERT INTO `sys_teacher` VALUES (6, 16, 'T001', '4', NULL, '4', '4', NULL, NULL, 1, '2025-05-31 21:25:22', '2025-05-31 21:28:54', 0);
INSERT INTO `sys_teacher` VALUES (7, 12, 'T002', '3', NULL, '3', '3', NULL, NULL, 1, '2025-05-31 21:29:08', '2025-05-31 21:34:00', 1);

-- ----------------------------
-- Table structure for sys_time_slot
-- ----------------------------
DROP TABLE IF EXISTS `sys_time_slot`;
CREATE TABLE `sys_time_slot`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '时间段ID',
  `slot_number` int NOT NULL COMMENT '时间段序号(1-11)',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `slot_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '时间段名称',
  `is_active` tinyint NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_slot_number`(`slot_number` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '时间段定义表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_time_slot
-- ----------------------------
INSERT INTO `sys_time_slot` VALUES (1, 1, '08:20:00', '09:05:00', '第1节', 1);
INSERT INTO `sys_time_slot` VALUES (2, 2, '09:15:00', '10:00:00', '第2节', 1);
INSERT INTO `sys_time_slot` VALUES (3, 3, '10:25:00', '11:10:00', '第3节', 1);
INSERT INTO `sys_time_slot` VALUES (4, 4, '11:20:00', '12:05:00', '第4节', 1);
INSERT INTO `sys_time_slot` VALUES (5, 5, '14:00:00', '14:45:00', '第5节', 1);
INSERT INTO `sys_time_slot` VALUES (6, 6, '14:55:00', '15:40:00', '第6节', 1);
INSERT INTO `sys_time_slot` VALUES (7, 7, '16:00:00', '16:45:00', '第7节', 1);
INSERT INTO `sys_time_slot` VALUES (8, 8, '16:55:00', '17:40:00', '第8节', 1);
INSERT INTO `sys_time_slot` VALUES (9, 9, '18:50:00', '19:35:00', '第9节', 1);
INSERT INTO `sys_time_slot` VALUES (10, 10, '19:45:00', '20:30:00', '第10节', 1);
INSERT INTO `sys_time_slot` VALUES (11, 11, '20:45:00', '21:30:00', '第11节', 1);

-- ----------------------------
-- Table structure for sys_time_slot_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_time_slot_config`;
CREATE TABLE `sys_time_slot_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `time_slot` int NOT NULL COMMENT '时间段 (1-6)',
  `slot_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '时间段名称',
  `start_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '开始时间',
  `end_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '结束时间',
  `period` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '时间段分类 (上午/下午/晚上)',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NULL DEFAULT 0 COMMENT '是否删除 (0-否, 1-是)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_time_slot`(`time_slot` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '时间段配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_time_slot_config
-- ----------------------------
INSERT INTO `sys_time_slot_config` VALUES (1, 1, '第一大节', '08:00', '09:40', '上午', '上午第一大节（8:00-8:45, 8:55-9:40）', '2025-05-30 12:36:27', '2025-05-30 12:36:27', 0);
INSERT INTO `sys_time_slot_config` VALUES (2, 2, '第二大节', '10:00', '11:40', '上午', '上午第二大节（10:00-10:45, 10:55-11:40）', '2025-05-30 12:36:27', '2025-05-30 12:36:27', 0);
INSERT INTO `sys_time_slot_config` VALUES (3, 3, '第三大节', '16:30', '18:10', '下午', '下午第一大节（16:30-17:15, 17:25-18:10）', '2025-05-30 12:36:27', '2025-05-30 12:36:27', 0);
INSERT INTO `sys_time_slot_config` VALUES (4, 4, '第四大节', '18:20', '20:00', '下午', '下午第二大节（18:20-19:05, 19:15-20:00）', '2025-05-30 12:36:27', '2025-05-30 12:36:27', 0);
INSERT INTO `sys_time_slot_config` VALUES (5, 5, '第五大节', '19:00', '20:40', '晚上', '晚上第一大节（19:00-19:45, 19:55-20:40）', '2025-05-30 12:36:27', '2025-05-30 12:36:27', 0);
INSERT INTO `sys_time_slot_config` VALUES (6, 6, '第六大节', '20:50', '22:30', '晚上', '晚上第二大节（20:50-21:35, 21:45-22:30）', '2025-05-30 12:36:27', '2025-05-30 12:36:27', 0);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '昵称',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '系统管理员', 'admin@example.com', '13800138000', 0, '2025-05-25 13:39:05', '2025-05-31 20:58:05', 0);
INSERT INTO `sys_user` VALUES (2, 'user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '普通用户', 'user@example.com', '13800138001', 0, '2025-05-25 13:39:05', '2025-05-31 20:57:58', 0);
INSERT INTO `sys_user` VALUES (3, '111', '$2a$10$w1ZWt.KcB2WZoUKHrzQMauMB2I/wYjTwV70aMl8r.DADfnOdFxIsm', 'hht', '2237701658@qq.com', '13384403671', 0, '2025-05-25 21:42:25', '2025-05-31 20:58:17', 0);
INSERT INTO `sys_user` VALUES (4, '2', '$2a$10$sXijLXqNmeCh6CSV5DVaAexVHjK7rVeqTvLHlvCGu4Jsq4o7xoM4S', '2', '2237701658@qq.com', '13384403671', 1, '2025-05-25 21:51:43', '2025-05-25 21:51:43', 0);
INSERT INTO `sys_user` VALUES (5, 'teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '张老师', 'teacher1@university.edu', '13900139001', 0, '2025-05-25 14:52:36', '2025-05-31 20:58:09', 0);
INSERT INTO `sys_user` VALUES (6, 'teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '李老师', 'teacher2@university.edu', '13900139002', 0, '2025-05-25 14:52:36', '2025-05-31 20:58:07', 0);
INSERT INTO `sys_user` VALUES (7, 'student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '张三', 'student1@university.edu', '13700137001', 0, '2025-05-25 15:07:33', '2025-05-31 20:58:10', 0);
INSERT INTO `sys_user` VALUES (8, 'student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '李四', 'student2@university.edu', '13700137002', 0, '2025-05-25 15:07:33', '2025-05-31 20:58:02', 0);
INSERT INTO `sys_user` VALUES (9, 'student3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '王五', 'student3@university.edu', '13700137003', 0, '2025-05-25 15:07:33', '2025-05-31 20:58:00', 0);
INSERT INTO `sys_user` VALUES (11, '1', '$2a$10$uiw1lYJJyBDDfSNezVvvju6R8weIYvtfkmuzKpbFp2dnum3N0hz.S', '1', '1', '1', 1, '2025-05-28 10:16:06', '2025-05-28 10:16:06', 0);
INSERT INTO `sys_user` VALUES (12, '3', '$2a$10$Da1bJsKHn3SLm7ZwJm3fqOIvRyYkQSpYTgWmj/UWPnyuyKPY1YPuC', '3', '3', '3', 1, '2025-05-30 09:52:58', '2025-05-30 09:52:58', 0);
INSERT INTO `sys_user` VALUES (16, '4', '$2a$10$QStW8U0WJTZ4AT.Cso3XLOhhpM4D4HlD2ukpy/ooASR8H1mdkbnE2', '4', '4', '4', 1, '2025-05-31 19:04:53', '2025-05-31 19:04:53', 0);

-- ----------------------------
-- Table structure for sys_user_organization
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_organization`;
CREATE TABLE `sys_user_organization`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `organization_id` bigint NOT NULL COMMENT '组织ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_org`(`user_id` ASC, `organization_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_organization_id`(`organization_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户组织关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_organization
-- ----------------------------
INSERT INTO `sys_user_organization` VALUES (4, 7, 11, '2025-05-25 15:12:01', '2025-05-25 15:12:01');
INSERT INTO `sys_user_organization` VALUES (5, 8, 12, '2025-05-25 15:12:01', '2025-05-25 15:12:01');
INSERT INTO `sys_user_organization` VALUES (6, 9, 14, '2025-05-25 15:12:01', '2025-05-25 15:12:01');
INSERT INTO `sys_user_organization` VALUES (10, 5, 11, '2025-05-25 23:30:13', '2025-05-25 23:30:13');
INSERT INTO `sys_user_organization` VALUES (11, 5, 12, '2025-05-25 23:30:13', '2025-05-25 23:30:13');
INSERT INTO `sys_user_organization` VALUES (14, 4, 15, '2025-05-31 11:41:37', '2025-05-31 11:41:37');
INSERT INTO `sys_user_organization` VALUES (15, 4, 14, '2025-05-31 11:41:37', '2025-05-31 11:41:37');
INSERT INTO `sys_user_organization` VALUES (26, 12, 1, '2025-05-31 21:34:39', '2025-05-31 21:34:39');
INSERT INTO `sys_user_organization` VALUES (27, 12, 5, '2025-05-31 21:34:39', '2025-05-31 21:34:39');
INSERT INTO `sys_user_organization` VALUES (28, 12, 14, '2025-05-31 21:34:39', '2025-05-31 21:34:39');
INSERT INTO `sys_user_organization` VALUES (29, 16, 1, '2025-05-31 21:35:04', '2025-05-31 21:35:04');
INSERT INTO `sys_user_organization` VALUES (30, 16, 4, '2025-05-31 21:35:04', '2025-05-31 21:35:04');
INSERT INTO `sys_user_organization` VALUES (31, 16, 11, '2025-05-31 21:35:04', '2025-05-31 21:35:04');
INSERT INTO `sys_user_organization` VALUES (32, 16, 12, '2025-05-31 21:35:04', '2025-05-31 21:35:04');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 59 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (4, 4, 2, '2025-05-25 21:51:43', 1);
INSERT INTO `sys_user_role` VALUES (10, 4, 2, '2025-05-25 23:12:21', 1);
INSERT INTO `sys_user_role` VALUES (11, 4, 1, '2025-05-25 23:12:31', 1);
INSERT INTO `sys_user_role` VALUES (12, 4, 2, '2025-05-25 23:12:55', 1);
INSERT INTO `sys_user_role` VALUES (13, 4, 3, '2025-05-25 23:16:29', 1);
INSERT INTO `sys_user_role` VALUES (14, 4, 2, '2025-05-25 23:16:34', 1);
INSERT INTO `sys_user_role` VALUES (17, 4, 2, '2025-05-25 23:43:24', 1);
INSERT INTO `sys_user_role` VALUES (18, 4, 2, '2025-05-26 14:28:25', 1);
INSERT INTO `sys_user_role` VALUES (23, 4, 3, '2025-05-26 15:05:57', 1);
INSERT INTO `sys_user_role` VALUES (24, 4, 2, '2025-05-26 15:06:12', 1);
INSERT INTO `sys_user_role` VALUES (25, 11, 1, '2025-05-29 11:56:02', 0);
INSERT INTO `sys_user_role` VALUES (26, 4, 3, '2025-05-30 09:52:47', 0);
INSERT INTO `sys_user_role` VALUES (27, 12, 2, '2025-05-30 09:52:58', 1);
INSERT INTO `sys_user_role` VALUES (34, 16, 2, '2025-05-31 19:04:53', 1);
INSERT INTO `sys_user_role` VALUES (35, 16, 3, NULL, NULL);
INSERT INTO `sys_user_role` VALUES (36, 16, 3, NULL, NULL);
INSERT INTO `sys_user_role` VALUES (37, 16, 3, NULL, NULL);
INSERT INTO `sys_user_role` VALUES (38, 16, 3, '2025-05-31 19:08:30', 1);
INSERT INTO `sys_user_role` VALUES (39, 16, 2, '2025-05-31 21:12:57', 1);
INSERT INTO `sys_user_role` VALUES (40, 16, 3, '2025-05-31 21:18:47', 1);
INSERT INTO `sys_user_role` VALUES (41, 16, 2, '2025-05-31 21:18:55', 1);
INSERT INTO `sys_user_role` VALUES (42, 16, 3, '2025-05-31 21:23:41', 1);
INSERT INTO `sys_user_role` VALUES (43, 16, 2, '2025-05-31 21:24:08', 1);
INSERT INTO `sys_user_role` VALUES (44, 16, 3, '2025-05-31 21:24:17', 1);
INSERT INTO `sys_user_role` VALUES (45, 16, 2, '2025-05-31 21:24:44', 1);
INSERT INTO `sys_user_role` VALUES (46, 16, 3, '2025-05-31 21:24:50', 1);
INSERT INTO `sys_user_role` VALUES (47, 16, 2, '2025-05-31 21:25:14', 1);
INSERT INTO `sys_user_role` VALUES (48, 16, 3, '2025-05-31 21:25:22', 1);
INSERT INTO `sys_user_role` VALUES (49, 16, 2, '2025-05-31 21:28:47', 1);
INSERT INTO `sys_user_role` VALUES (50, 16, 3, '2025-05-31 21:28:54', 0);
INSERT INTO `sys_user_role` VALUES (51, 12, 3, '2025-05-31 21:29:08', 1);
INSERT INTO `sys_user_role` VALUES (52, 12, 2, '2025-05-31 21:29:11', 1);
INSERT INTO `sys_user_role` VALUES (53, 12, 3, '2025-05-31 21:29:20', 1);
INSERT INTO `sys_user_role` VALUES (54, 12, 2, '2025-05-31 21:29:25', 1);
INSERT INTO `sys_user_role` VALUES (55, 12, 3, '2025-05-31 21:32:52', 1);
INSERT INTO `sys_user_role` VALUES (56, 12, 2, '2025-05-31 21:32:58', 1);
INSERT INTO `sys_user_role` VALUES (57, 12, 3, '2025-05-31 21:33:50', 1);
INSERT INTO `sys_user_role` VALUES (58, 12, 2, '2025-05-31 21:34:00', 0);

SET FOREIGN_KEY_CHECKS = 1;
