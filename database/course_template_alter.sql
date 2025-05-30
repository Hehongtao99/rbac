-- 课程模板表结构修改脚本
-- 为现有的 sys_course_template 表添加学期字段和计划人数字段

SET NAMES utf8mb4;

-- 为课程模板表添加学期字段
ALTER TABLE `sys_course_template` 
ADD COLUMN `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '第一学期' COMMENT '学期' AFTER `academic_year`;

-- 为课程模板表添加计划人数字段
ALTER TABLE `sys_course_template` 
ADD COLUMN `max_students` int NOT NULL DEFAULT 50 COMMENT '计划人数' AFTER `semester`;

-- 添加学期字段的索引
ALTER TABLE `sys_course_template` 
ADD INDEX `idx_semester`(`semester` ASC) USING BTREE;

-- 更新现有数据，为没有学期信息的记录设置默认学期
-- 这里可以根据实际情况调整学期分配逻辑
UPDATE `sys_course_template` SET `semester` = '第一学期' WHERE `semester` = '';
UPDATE `sys_course_template` SET `semester` = '第一学期' WHERE `semester` IS NULL;

-- 更新现有数据，为没有计划人数的记录设置默认值
UPDATE `sys_course_template` SET `max_students` = 50 WHERE `max_students` = 0;
UPDATE `sys_course_template` SET `max_students` = 50 WHERE `max_students` IS NULL;

-- 如果需要为不同课程设置不同学期，可以使用以下语句作为参考：
-- UPDATE `sys_course_template` SET `semester` = '第二学期' WHERE `template_name` IN ('计算机网络', '软件工程', '操作系统原理', '人工智能导论');

-- 移除默认值约束（可选）
ALTER TABLE `sys_course_template` 
ALTER COLUMN `semester` DROP DEFAULT;

ALTER TABLE `sys_course_template` 
ALTER COLUMN `max_students` DROP DEFAULT; 