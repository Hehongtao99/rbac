-- 完整的数据库结构修改脚本
-- 用于现有数据库的增量更新

SET NAMES utf8mb4;

-- =====================================
-- 1. 课程模板表结构修改
-- =====================================

-- 检查并添加学期字段（如果不存在）
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'sys_course_template' 
  AND COLUMN_NAME = 'semester';

SET @sql = IF(@col_exists = 0, 
  'ALTER TABLE `sys_course_template` ADD COLUMN `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT ''第一学期'' COMMENT ''学期'' AFTER `academic_year`;',
  'SELECT ''semester column already exists'' as message;'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加学期字段的索引（如果不存在）
SET @index_exists = 0;
SELECT COUNT(*) INTO @index_exists 
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'sys_course_template' 
  AND INDEX_NAME = 'idx_semester';

SET @sql = IF(@index_exists = 0, 
  'ALTER TABLE `sys_course_template` ADD INDEX `idx_semester`(`semester` ASC) USING BTREE;',
  'SELECT ''idx_semester index already exists'' as message;'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =====================================
-- 2. 更新现有课程模板数据
-- =====================================

-- 为现有记录设置默认学期
UPDATE `sys_course_template` 
SET `semester` = '第一学期' 
WHERE `semester` = '' OR `semester` IS NULL;

-- 根据课程特点设置学期（可根据实际情况调整）
UPDATE `sys_course_template` 
SET `semester` = '第二学期' 
WHERE `template_name` IN (
  '计算机网络', 
  '软件工程', 
  '操作系统原理', 
  '人工智能导论'
);

-- =====================================
-- 3. 检查开课申请表结构
-- =====================================

-- 确保开课申请表有课程学时字段
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'sys_course_application' 
  AND COLUMN_NAME = 'course_hours';

SET @sql = IF(@col_exists = 0, 
  'ALTER TABLE `sys_course_application` ADD COLUMN `course_hours` int NOT NULL DEFAULT 16 COMMENT ''课程学时'' AFTER `max_students`;',
  'SELECT ''course_hours column already exists in application table'' as message;'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =====================================
-- 4. 检查课程实例表结构
-- =====================================

-- 确保课程实例表有课程学时字段
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'sys_course' 
  AND COLUMN_NAME = 'course_hours';

SET @sql = IF(@col_exists = 0, 
  'ALTER TABLE `sys_course` ADD COLUMN `course_hours` int NOT NULL DEFAULT 16 COMMENT ''课程学时'' AFTER `teacher_name`;',
  'SELECT ''course_hours column already exists in course table'' as message;'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =====================================
-- 5. 插入示例数据（如果表为空）
-- =====================================

-- 检查课程模板表是否有数据
SET @record_count = 0;
SELECT COUNT(*) INTO @record_count FROM `sys_course_template`;

-- 如果表为空，插入示例数据
SET @sql = IF(@record_count = 0, 
  "INSERT INTO `sys_course_template` (`template_name`, `description`, `course_hours`, `academic_year`, `semester`) VALUES
   ('Java程序设计', 'Java面向对象程序设计，包含基础语法、面向对象编程、集合框架等内容', 48, '2024-2025', '第一学期'),
   ('数据库系统原理', '数据库系统的基本概念、关系模型、SQL语言、数据库设计等', 40, '2024-2025', '第一学期'),
   ('计算机网络', '计算机网络的基本概念、网络协议、网络安全等', 36, '2024-2025', '第二学期'),
   ('软件工程', '软件开发生命周期、需求分析、系统设计、测试等', 32, '2024-2025', '第二学期'),
   ('数据结构与算法', '线性表、树、图等数据结构的实现与算法设计', 48, '2024-2025', '第一学期'),
   ('操作系统原理', '操作系统的基本概念、进程管理、内存管理、文件系统等', 40, '2024-2025', '第二学期'),
   ('Web开发实践', 'HTML、CSS、JavaScript、前端框架等Web开发技术', 32, '2024-2025', '第一学期'),
   ('数据库实验', '数据库设计、SQL编程、数据库管理等实验', 16, '2024-2025', '第一学期'),
   ('计算机组成原理', '计算机硬件系统的组成、工作原理等', 40, '2024-2025', '第一学期'),
   ('人工智能导论', '人工智能的基本概念、机器学习、深度学习等', 36, '2024-2025', '第二学期');",
  'SELECT ''Template data already exists'' as message;'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =====================================
-- 完成信息
-- =====================================
SELECT 'Database structure update completed successfully!' as result; 