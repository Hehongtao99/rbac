-- 为课程申请表添加剩余课时字段
SET NAMES utf8mb4;

-- 添加剩余课时字段
ALTER TABLE `sys_course_application` 
ADD COLUMN `remaining_hours` int NOT NULL DEFAULT 0 COMMENT '剩余课时' AFTER `course_hours`;

-- 将现有记录的剩余课时设置为课程总课时
UPDATE `sys_course_application` 
SET `remaining_hours` = `course_hours` 
WHERE `remaining_hours` = 0; 