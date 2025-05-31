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

-- 检查并添加班级课程课时记录ID字段到课程表
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sys_schedule'
    AND COLUMN_NAME = 'class_course_hours_id'
);

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE `sys_schedule` ADD COLUMN `class_course_hours_id` bigint DEFAULT NULL COMMENT ''班级课程课时记录ID'' AFTER `class_name`',
    'SELECT ''Column class_course_hours_id already exists'' as message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加索引
SET @index_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sys_schedule'
    AND INDEX_NAME = 'idx_class_course_hours_id'
);

SET @sql = IF(@index_exists = 0,
    'ALTER TABLE `sys_schedule` ADD INDEX `idx_class_course_hours_id` (`class_course_hours_id`)',
    'SELECT ''Index idx_class_course_hours_id already exists'' as message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt; 