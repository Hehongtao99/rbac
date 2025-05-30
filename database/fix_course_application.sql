-- 修复课程申请表结构
SET NAMES utf8mb4;

-- 检查并添加缺失的字段
-- 添加课程学时字段（如果不存在）
ALTER TABLE `sys_course_application` 
ADD COLUMN `course_hours` int NOT NULL DEFAULT 16 COMMENT '课程学时' AFTER `max_students`;

-- 为现有记录设置默认的课程学时
UPDATE `sys_course_application` SET `course_hours` = 16 WHERE `course_hours` = 0 OR `course_hours` IS NULL;

-- 修改字段，移除默认值
ALTER TABLE `sys_course_application` 
ALTER COLUMN `course_hours` DROP DEFAULT;

-- 为课程学时字段添加索引
ALTER TABLE `sys_course_application` 
ADD INDEX `idx_course_hours`(`course_hours` ASC) USING BTREE;

SELECT 'Course application table structure fixed successfully!' as message; 