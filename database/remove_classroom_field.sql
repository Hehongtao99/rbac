-- 移除课程表中的教室字段
-- 执行此脚本前请确保已备份数据库

-- 删除sys_schedule表中的classroom字段
ALTER TABLE `sys_schedule` DROP COLUMN `classroom`;

-- 验证字段是否已删除（可选，用于确认）
-- DESCRIBE `sys_schedule`; 