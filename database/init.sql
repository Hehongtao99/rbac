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

 Date: 26/05/2025 14:10:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

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
INSERT INTO `sys_organization` VALUES (11, '计科2021级1班', 'CS_2021_1', 4, 'CLASS', 3, 1, '冯十三', '13800138041', 'fengshisan@university.edu', '计算机科学与技术2021级1班', 1, '2025-05-25 14:41:36', '2025-05-25 14:41:36');
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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '管理员', 'ADMIN', '系统管理员，拥有所有权限', 1, '2025-05-25 13:39:05', '2025-05-25 13:39:05', 0);
INSERT INTO `sys_role` VALUES (2, '用户', 'USER', '普通用户，拥有基本权限', 1, '2025-05-25 13:39:05', '2025-05-25 13:39:05', 0);
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
) ENGINE = InnoDB AUTO_INCREMENT = 203 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色菜单关联表' ROW_FORMAT = Dynamic;

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
INSERT INTO `sys_role_menu` VALUES (98, 1, 1, '2025-05-25 22:04:22', 0);
INSERT INTO `sys_role_menu` VALUES (99, 1, 2, '2025-05-25 22:04:22', 0);
INSERT INTO `sys_role_menu` VALUES (100, 1, 4, '2025-05-25 22:04:22', 0);
INSERT INTO `sys_role_menu` VALUES (101, 1, 5, '2025-05-25 22:04:22', 0);
INSERT INTO `sys_role_menu` VALUES (102, 1, 6, '2025-05-25 22:04:22', 0);
INSERT INTO `sys_role_menu` VALUES (103, 1, 7, '2025-05-25 22:04:22', 0);
INSERT INTO `sys_role_menu` VALUES (104, 1, 3, '2025-05-25 22:04:22', 0);
INSERT INTO `sys_role_menu` VALUES (105, 1, 8, '2025-05-25 22:04:22', 0);
INSERT INTO `sys_role_menu` VALUES (106, 1, 9, '2025-05-25 22:04:22', 0);
INSERT INTO `sys_role_menu` VALUES (107, 1, 10, '2025-05-25 22:04:22', 0);
INSERT INTO `sys_role_menu` VALUES (108, 1, 11, '2025-05-25 22:04:22', 0);
INSERT INTO `sys_role_menu` VALUES (109, 1, 12, '2025-05-25 22:04:22', 0);
INSERT INTO `sys_role_menu` VALUES (110, 1, 13, '2025-05-25 22:04:22', 0);
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
INSERT INTO `sys_role_menu` VALUES (164, 1, 14, '2025-05-25 14:41:36', 0);
INSERT INTO `sys_role_menu` VALUES (165, 1, 15, '2025-05-25 14:41:36', 0);
INSERT INTO `sys_role_menu` VALUES (166, 1, 16, '2025-05-25 14:41:36', 0);
INSERT INTO `sys_role_menu` VALUES (167, 2, 4, '2025-05-25 22:51:03', 1);
INSERT INTO `sys_role_menu` VALUES (168, 2, 5, '2025-05-25 22:51:03', 1);
INSERT INTO `sys_role_menu` VALUES (169, 2, 17, '2025-05-25 22:51:03', 1);
INSERT INTO `sys_role_menu` VALUES (170, 2, 12, '2025-05-25 22:51:03', 1);
INSERT INTO `sys_role_menu` VALUES (171, 2, 13, '2025-05-25 22:51:03', 1);
INSERT INTO `sys_role_menu` VALUES (172, 2, 1, '2025-05-25 22:51:03', 1);
INSERT INTO `sys_role_menu` VALUES (173, 2, 2, '2025-05-25 22:51:03', 1);
INSERT INTO `sys_role_menu` VALUES (174, 3, 13, '2025-05-25 14:52:36', 0);
INSERT INTO `sys_role_menu` VALUES (175, 3, 12, '2025-05-25 14:52:36', 0);
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
INSERT INTO `sys_role_menu` VALUES (197, 2, 4, '2025-05-25 22:58:12', 0);
INSERT INTO `sys_role_menu` VALUES (198, 2, 5, '2025-05-25 22:58:12', 0);
INSERT INTO `sys_role_menu` VALUES (199, 2, 12, '2025-05-25 22:58:13', 0);
INSERT INTO `sys_role_menu` VALUES (200, 2, 13, '2025-05-25 22:58:13', 0);
INSERT INTO `sys_role_menu` VALUES (201, 2, 1, '2025-05-25 22:58:13', 0);
INSERT INTO `sys_role_menu` VALUES (202, 2, 2, '2025-05-25 22:58:13', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '系统管理员', 'admin@example.com', '13800138000', 1, '2025-05-25 13:39:05', '2025-05-25 13:39:05', 0);
INSERT INTO `sys_user` VALUES (2, 'user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '普通用户', 'user@example.com', '13800138001', 1, '2025-05-25 13:39:05', '2025-05-25 13:39:05', 0);
INSERT INTO `sys_user` VALUES (3, '111', '$2a$10$w1ZWt.KcB2WZoUKHrzQMauMB2I/wYjTwV70aMl8r.DADfnOdFxIsm', 'hht', '2237701658@qq.com', '13384403671', 1, '2025-05-25 21:42:25', '2025-05-25 21:42:25', 0);
INSERT INTO `sys_user` VALUES (4, '2', '$2a$10$sXijLXqNmeCh6CSV5DVaAexVHjK7rVeqTvLHlvCGu4Jsq4o7xoM4S', '2', '2237701658@qq.com', '13384403671', 1, '2025-05-25 21:51:43', '2025-05-25 21:51:43', 0);
INSERT INTO `sys_user` VALUES (5, 'teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '张老师', 'teacher1@university.edu', '13900139001', 1, '2025-05-25 14:52:36', '2025-05-25 14:52:36', 0);
INSERT INTO `sys_user` VALUES (6, 'teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '李老师', 'teacher2@university.edu', '13900139002', 1, '2025-05-25 14:52:36', '2025-05-25 14:52:36', 0);
INSERT INTO `sys_user` VALUES (7, 'student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '张三', 'student1@university.edu', '13700137001', 1, '2025-05-25 15:07:33', '2025-05-25 15:07:33', 0);
INSERT INTO `sys_user` VALUES (8, 'student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '李四', 'student2@university.edu', '13700137002', 1, '2025-05-25 15:07:33', '2025-05-25 15:07:33', 0);
INSERT INTO `sys_user` VALUES (9, 'student3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '王五', 'student3@university.edu', '13700137003', 1, '2025-05-25 15:07:33', '2025-05-25 15:07:33', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户组织关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_organization
-- ----------------------------
INSERT INTO `sys_user_organization` VALUES (4, 7, 11, '2025-05-25 15:12:01', '2025-05-25 15:12:01');
INSERT INTO `sys_user_organization` VALUES (5, 8, 12, '2025-05-25 15:12:01', '2025-05-25 15:12:01');
INSERT INTO `sys_user_organization` VALUES (6, 9, 14, '2025-05-25 15:12:01', '2025-05-25 15:12:01');
INSERT INTO `sys_user_organization` VALUES (10, 5, 11, '2025-05-25 23:30:13', '2025-05-25 23:30:13');
INSERT INTO `sys_user_organization` VALUES (11, 5, 12, '2025-05-25 23:30:13', '2025-05-25 23:30:13');
INSERT INTO `sys_user_organization` VALUES (12, 4, 15, '2025-05-25 23:43:14', '2025-05-25 23:43:14');

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
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1, '2025-05-25 13:39:05', 0);
INSERT INTO `sys_user_role` VALUES (2, 2, 2, '2025-05-25 13:39:05', 0);
INSERT INTO `sys_user_role` VALUES (3, 3, 1, '2025-05-25 21:42:25', 0);
INSERT INTO `sys_user_role` VALUES (4, 4, 2, '2025-05-25 21:51:43', 1);
INSERT INTO `sys_user_role` VALUES (5, 5, 3, '2025-05-25 14:52:36', 1);
INSERT INTO `sys_user_role` VALUES (6, 6, 3, '2025-05-25 14:52:36', 1);
INSERT INTO `sys_user_role` VALUES (7, 5, 3, '2025-05-25 15:09:27', 1);
INSERT INTO `sys_user_role` VALUES (8, 6, 3, '2025-05-25 15:09:27', 1);
INSERT INTO `sys_user_role` VALUES (10, 4, 2, '2025-05-25 23:12:21', 1);
INSERT INTO `sys_user_role` VALUES (11, 4, 3, '2025-05-25 23:12:31', 1);
INSERT INTO `sys_user_role` VALUES (12, 4, 2, '2025-05-25 23:12:55', 1);
INSERT INTO `sys_user_role` VALUES (13, 4, 3, '2025-05-25 23:16:29', 1);
INSERT INTO `sys_user_role` VALUES (14, 4, 2, '2025-05-25 23:16:34', 1);
INSERT INTO `sys_user_role` VALUES (15, 5, 3, '2025-05-25 23:17:27', 0);
INSERT INTO `sys_user_role` VALUES (16, 6, 3, '2025-05-25 23:17:33', 0);
INSERT INTO `sys_user_role` VALUES (17, 4, 2, '2025-05-25 23:43:24', 0);

SET FOREIGN_KEY_CHECKS = 1;
