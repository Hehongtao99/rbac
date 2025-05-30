-- 修复课程表中默认的课时消耗值
SET NAMES utf8mb4;

-- 修改表结构，将默认的课时消耗改为1
ALTER TABLE `sys_schedule` 
MODIFY COLUMN `reduced_hours` int(11) DEFAULT 1 COMMENT '减少的课时数';

-- 将现有记录中reducedHours为2的记录改为1
UPDATE `sys_schedule` 
SET `reduced_hours` = 1 
WHERE `reduced_hours` = 2; 