-- 如果表已存在，先删除负责人字段
ALTER TABLE `sys_organization` DROP COLUMN IF EXISTS `leader`;

-- 创建组织管理表
CREATE TABLE `sys_organization` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `org_name` varchar(50) NOT NULL COMMENT '组织名称',
  `org_code` varchar(20) NOT NULL COMMENT '组织编码',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父级组织ID',
  `org_type` varchar(20) NOT NULL COMMENT '组织类型：COLLEGE-学院，MAJOR-专业，CLASS-班级',
  `org_level` int NOT NULL COMMENT '组织级别：1-学院，2-专业，3-班级',
  `sort` int DEFAULT '0' COMMENT '排序',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_org_name` (`org_name`),
  UNIQUE KEY `uk_org_code` (`org_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_org_level` (`org_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组织架构表';

-- 插入组织管理菜单
INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`) 
VALUES ('组织管理', 'organization-management', 1, 'MENU', '/system/organization', 'OrganizationManagement', 'OfficeBuilding', 3, 1, '组织管理页面');

-- 获取组织管理菜单ID
SET @org_menu_id = LAST_INSERT_ID();

-- 插入组织管理按钮权限
INSERT INTO `sys_menu` (`menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `description`) VALUES
('新增组织', 'org:add', @org_menu_id, 'BUTTON', '', '', '', 1, 1, '新增组织按钮'),
('编辑组织', 'org:edit', @org_menu_id, 'BUTTON', '', '', '', 2, 1, '编辑组织按钮'),
('删除组织', 'org:delete', @org_menu_id, 'BUTTON', '', '', '', 3, 1, '删除组织按钮');

-- 为管理员角色分配组织管理权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT 1, `id` FROM `sys_menu` WHERE `path` = '/system/organization' OR `parent_id` = @org_menu_id;

-- 插入示例组织数据
INSERT INTO `sys_organization` (`org_name`, `org_code`, `parent_id`, `org_type`, `org_level`, `sort`, `phone`, `email`, `description`, `status`) VALUES
-- 学院
('计算机学院', 'CS_COLLEGE', 0, 'COLLEGE', 1, 1, '13800138001', 'cs@university.edu', '计算机科学与技术学院', 1),
('商学院', 'BUS_COLLEGE', 0, 'COLLEGE', 1, 2, '13800138002', 'business@university.edu', '工商管理学院', 1),
('外国语学院', 'FL_COLLEGE', 0, 'COLLEGE', 1, 3, '13800138003', 'foreign@university.edu', '外国语学院', 1);

-- 获取学院ID
SET @cs_college_id = (SELECT id FROM sys_organization WHERE org_code = 'CS_COLLEGE');
SET @bus_college_id = (SELECT id FROM sys_organization WHERE org_code = 'BUS_COLLEGE');
SET @fl_college_id = (SELECT id FROM sys_organization WHERE org_code = 'FL_COLLEGE');

-- 计算机学院的专业
INSERT INTO `sys_organization` (`org_name`, `org_code`, `parent_id`, `org_type`, `org_level`, `sort`, `phone`, `email`, `description`, `status`) VALUES
('计算机科学与技术', 'CS_MAJOR', @cs_college_id, 'MAJOR', 2, 1, '13800138011', 'cs.major@university.edu', '计算机科学与技术专业', 1),
('软件工程', 'SE_MAJOR', @cs_college_id, 'MAJOR', 2, 2, '13800138012', 'se.major@university.edu', '软件工程专业', 1),
('网络工程', 'NE_MAJOR', @cs_college_id, 'MAJOR', 2, 3, '13800138013', 'ne.major@university.edu', '网络工程专业', 1);

-- 商学院的专业
INSERT INTO `sys_organization` (`org_name`, `org_code`, `parent_id`, `org_type`, `org_level`, `sort`, `phone`, `email`, `description`, `status`) VALUES
('工商管理', 'BA_MAJOR', @bus_college_id, 'MAJOR', 2, 1, '13800138021', 'ba.major@university.edu', '工商管理专业', 1),
('市场营销', 'MK_MAJOR', @bus_college_id, 'MAJOR', 2, 2, '13800138022', 'mk.major@university.edu', '市场营销专业', 1);

-- 外国语学院的专业
INSERT INTO `sys_organization` (`org_name`, `org_code`, `parent_id`, `org_type`, `org_level`, `sort`, `phone`, `email`, `description`, `status`) VALUES
('英语', 'EN_MAJOR', @fl_college_id, 'MAJOR', 2, 1, '13800138031', 'en.major@university.edu', '英语专业', 1),
('日语', 'JP_MAJOR', @fl_college_id, 'MAJOR', 2, 2, '13800138032', 'jp.major@university.edu', '日语专业', 1);

-- 获取专业ID
SET @cs_major_id = (SELECT id FROM sys_organization WHERE org_code = 'CS_MAJOR');
SET @se_major_id = (SELECT id FROM sys_organization WHERE org_code = 'SE_MAJOR');
SET @ba_major_id = (SELECT id FROM sys_organization WHERE org_code = 'BA_MAJOR');
SET @en_major_id = (SELECT id FROM sys_organization WHERE org_code = 'EN_MAJOR');

-- 计算机科学与技术专业的班级
INSERT INTO `sys_organization` (`org_name`, `org_code`, `parent_id`, `org_type`, `org_level`, `sort`, `phone`, `email`, `description`, `status`) VALUES
('计科2021级1班', 'CS_2021_1', @cs_major_id, 'CLASS', 3, 1, '13800138041', 'cs2021-1@university.edu', '计算机科学与技术2021级1班', 1),
('计科2021级2班', 'CS_2021_2', @cs_major_id, 'CLASS', 3, 2, '13800138042', 'cs2021-2@university.edu', '计算机科学与技术2021级2班', 1),
('计科2022级1班', 'CS_2022_1', @cs_major_id, 'CLASS', 3, 3, '13800138043', 'cs2022-1@university.edu', '计算机科学与技术2022级1班', 1);

-- 软件工程专业的班级
INSERT INTO `sys_organization` (`org_name`, `org_code`, `parent_id`, `org_type`, `org_level`, `sort`, `phone`, `email`, `description`, `status`) VALUES
('软工2021级1班', 'SE_2021_1', @se_major_id, 'CLASS', 3, 1, '13800138051', 'se2021-1@university.edu', '软件工程2021级1班', 1),
('软工2021级2班', 'SE_2021_2', @se_major_id, 'CLASS', 3, 2, '13800138052', 'se2021-2@university.edu', '软件工程2021级2班', 1);

-- 工商管理专业的班级
INSERT INTO `sys_organization` (`org_name`, `org_code`, `parent_id`, `org_type`, `org_level`, `sort`, `phone`, `email`, `description`, `status`) VALUES
('工商2021级1班', 'BA_2021_1', @ba_major_id, 'CLASS', 3, 1, '13800138061', 'ba2021-1@university.edu', '工商管理2021级1班', 1),
('工商2022级1班', 'BA_2022_1', @ba_major_id, 'CLASS', 3, 2, '13800138062', 'ba2022-1@university.edu', '工商管理2022级1班', 1);

-- 英语专业的班级
INSERT INTO `sys_organization` (`org_name`, `org_code`, `parent_id`, `org_type`, `org_level`, `sort`, `phone`, `email`, `description`, `status`) VALUES
('英语2021级1班', 'EN_2021_1', @en_major_id, 'CLASS', 3, 1, '13800138071', 'en2021-1@university.edu', '英语2021级1班', 1),
('英语2022级1班', 'EN_2022_1', @en_major_id, 'CLASS', 3, 2, '13800138072', 'en2022-1@university.edu', '英语2022级1班', 1);

-- 添加教师角色
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`, `status`) VALUES
('教师', 'TEACHER', '教师角色，拥有教学相关权限', 1);

-- 获取教师角色ID
SET @teacher_role_id = (SELECT id FROM sys_role WHERE role_code = 'TEACHER');

-- 为教师角色分配基本权限（个人中心等）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT @teacher_role_id, `id` FROM `sys_menu` WHERE `menu_code` IN ('profile', 'change-password');

-- 添加教师用户示例
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `phone`, `status`) VALUES
('teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '张老师', 'teacher1@university.edu', '13900139001', 1),
('teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '李老师', 'teacher2@university.edu', '13900139002', 1);

-- 获取教师用户ID
SET @teacher1_id = (SELECT id FROM sys_user WHERE username = 'teacher1');
SET @teacher2_id = (SELECT id FROM sys_user WHERE username = 'teacher2');

-- 为教师用户分配教师角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(@teacher1_id, @teacher_role_id),
(@teacher2_id, @teacher_role_id); 