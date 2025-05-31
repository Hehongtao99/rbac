-- 班级课程课时管理表
-- 用于独立管理每个班级的课时，解决课时在不同班级间共享的问题

SET NAMES utf8mb4;

-- 创建班级课程课时表
CREATE TABLE IF NOT EXISTS `sys_class_course_hours` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `application_id` bigint NOT NULL COMMENT '课程申请ID',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `class_name` varchar(100) NOT NULL COMMENT '班级名称',
  `course_name` varchar(100) NOT NULL COMMENT '课程名称',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `teacher_name` varchar(50) NOT NULL COMMENT '教师姓名',
  `academic_year` varchar(20) NOT NULL COMMENT '学年',
  `semester` varchar(20) NOT NULL COMMENT '学期',
  `total_hours` int NOT NULL COMMENT '总课时',
  `used_hours` int NOT NULL DEFAULT 0 COMMENT '已使用课时',
  `remaining_hours` int NOT NULL COMMENT '剩余课时',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_application_class` (`application_id`, `class_id`),
  INDEX `idx_application_id` (`application_id` ASC) USING BTREE,
  INDEX `idx_class_id` (`class_id` ASC) USING BTREE,
  INDEX `idx_teacher_id` (`teacher_id` ASC) USING BTREE,
  INDEX `idx_academic_year` (`academic_year` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '班级课程课时管理表' ROW_FORMAT = Dynamic;

-- 修改课程表，添加班级课程课时记录ID字段
ALTER TABLE `sys_schedule` 
ADD COLUMN `class_course_hours_id` bigint DEFAULT NULL COMMENT '班级课程课时记录ID' AFTER `class_name`;

-- 为新字段添加索引
ALTER TABLE `sys_schedule` 
ADD INDEX `idx_class_course_hours_id` (`class_course_hours_id`);

-- 从现有数据迁移到新的班级课时管理表
-- 注意：这个脚本会为每个班级创建独立的课时记录
INSERT INTO `sys_class_course_hours` (
    `application_id`, `class_id`, `class_name`, `course_name`, 
    `teacher_id`, `teacher_name`, `academic_year`, `semester`,
    `total_hours`, `used_hours`, `remaining_hours`
)
SELECT DISTINCT
    s.course_id as application_id,
    s.class_id,
    s.class_name,
    s.course_name,
    s.teacher_id,
    s.teacher_name,
    s.academic_year,
    COALESCE(ca.semester, '第一学期') as semester,
    ca.course_hours as total_hours,
    COALESCE(SUM(s.reduced_hours), 0) as used_hours,
    ca.course_hours - COALESCE(SUM(s.reduced_hours), 0) as remaining_hours
FROM `sys_schedule` s
LEFT JOIN `sys_course_application` ca ON s.course_id = ca.id
WHERE s.class_id IS NOT NULL 
  AND s.deleted = 0
  AND ca.id IS NOT NULL
GROUP BY s.course_id, s.class_id, s.class_name, s.course_name, 
         s.teacher_id, s.teacher_name, s.academic_year, ca.semester, ca.course_hours
ON DUPLICATE KEY UPDATE
    `used_hours` = VALUES(`used_hours`),
    `remaining_hours` = VALUES(`remaining_hours`);

-- 为没有班级的课程申请创建默认记录（如果教师有分配班级的话）
-- 这部分需要根据实际的教师班级分配情况来处理
-- 暂时跳过，由业务逻辑在需要时创建

-- 更新现有课程表记录，关联到班级课程课时记录
UPDATE `sys_schedule` s
SET `class_course_hours_id` = (
    SELECT cch.id 
    FROM `sys_class_course_hours` cch 
    WHERE cch.application_id = s.course_id 
      AND cch.class_id = s.class_id
    LIMIT 1
)
WHERE s.class_id IS NOT NULL AND s.deleted = 0; 