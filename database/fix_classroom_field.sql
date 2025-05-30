-- 修复课程表classroom字段的默认值问题
-- 解决前端移除教室字段后的数据库兼容性问题

-- 方案1：为classroom字段添加默认值
ALTER TABLE `sys_schedule` 
MODIFY COLUMN `classroom` varchar(50) NOT NULL DEFAULT '待定' COMMENT '教室';

-- 方案2：如果希望classroom字段可以为空，则修改为可空字段
-- ALTER TABLE `sys_schedule` 
-- MODIFY COLUMN `classroom` varchar(50) DEFAULT '待定' COMMENT '教室';

-- 更新现有记录中的空教室为默认值（可选）
UPDATE `sys_schedule` 
SET `classroom` = '待定' 
WHERE `classroom` IS NULL OR `classroom` = '';

-- 验证修改结果
-- SELECT COLUMN_NAME, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_COMMENT 
-- FROM INFORMATION_SCHEMA.COLUMNS 
-- WHERE TABLE_NAME = 'sys_schedule' AND COLUMN_NAME = 'classroom'; 