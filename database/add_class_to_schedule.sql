-- 为课程表添加班级字段
-- 执行此脚本前请确保已备份数据库

-- 添加班级ID字段
ALTER TABLE `sys_schedule` ADD COLUMN `class_id` bigint(20) DEFAULT NULL COMMENT '班级ID' AFTER `teacher_name`;

-- 添加班级名称字段
ALTER TABLE `sys_schedule` ADD COLUMN `class_name` varchar(100) DEFAULT NULL COMMENT '班级名称' AFTER `class_id`;

-- 为班级ID添加索引
ALTER TABLE `sys_schedule` ADD INDEX `idx_class_id` (`class_id`);

-- 验证字段是否已添加（可选，用于确认）
-- DESCRIBE `sys_schedule`; 