-- 简化的数据库结构修改脚本
-- 直接执行ALTER语句

SET NAMES utf8mb4;

-- 为课程模板表添加学期字段
ALTER TABLE `sys_course_template` 
ADD COLUMN `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '第一学期' COMMENT '学期' AFTER `academic_year`;

-- 添加学期字段的索引
ALTER TABLE `sys_course_template` 
ADD INDEX `idx_semester`(`semester` ASC) USING BTREE;

-- 确保开课申请表有课程学时字段（如果没有的话）
-- ALTER TABLE `sys_course_application` 
-- ADD COLUMN `course_hours` int NOT NULL DEFAULT 16 COMMENT '课程学时' AFTER `max_students`;

-- 确保课程实例表有课程学时字段（如果没有的话）
-- ALTER TABLE `sys_course` 
-- ADD COLUMN `course_hours` int NOT NULL DEFAULT 16 COMMENT '课程学时' AFTER `teacher_name`;

-- 更新现有课程模板数据，设置学期
UPDATE `sys_course_template` SET `semester` = '第一学期' WHERE `semester` = '';

-- 为部分课程设置为第二学期（根据需要调整）
UPDATE `sys_course_template` 
SET `semester` = '第二学期' 
WHERE `template_name` IN ('计算机网络', '软件工程', '操作系统原理', '人工智能导论');

-- 移除默认值约束
ALTER TABLE `sys_course_template` 
ALTER COLUMN `semester` DROP DEFAULT; 