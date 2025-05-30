-- 课程表表
DROP TABLE IF EXISTS `sys_schedule`;
CREATE TABLE `sys_schedule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `course_id` bigint(20) NOT NULL COMMENT '课程ID',
  `course_name` varchar(100) NOT NULL COMMENT '课程名称',
  `teacher_id` bigint(20) NOT NULL COMMENT '教师ID',
  `teacher_name` varchar(50) NOT NULL COMMENT '教师姓名',
  `academic_year` varchar(20) NOT NULL COMMENT '学年',
  `week_number` int(11) NOT NULL COMMENT '第几周 (1-20)',
  `day_of_week` int(11) NOT NULL COMMENT '星期几 (1-7, 1为周一)',
  `time_slot` int(11) NOT NULL COMMENT '时间段 (1-6)',
  `time_range` varchar(50) DEFAULT NULL COMMENT '时间范围文字描述',
  `classroom` varchar(50) NOT NULL COMMENT '教室',
  `reduced_hours` int(11) DEFAULT 2 COMMENT '减少的课时数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int(11) DEFAULT 0 COMMENT '是否删除 (0-否, 1-是)',
  PRIMARY KEY (`id`),
  KEY `idx_teacher_academic` (`teacher_id`, `academic_year`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_time_schedule` (`week_number`, `day_of_week`, `time_slot`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 时间段配置表
DROP TABLE IF EXISTS `sys_time_slot_config`;
CREATE TABLE `sys_time_slot_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `time_slot` int(11) NOT NULL COMMENT '时间段 (1-6)',
  `slot_name` varchar(50) NOT NULL COMMENT '时间段名称',
  `start_time` varchar(10) NOT NULL COMMENT '开始时间',
  `end_time` varchar(10) NOT NULL COMMENT '结束时间',
  `period` varchar(20) NOT NULL COMMENT '时间段分类 (上午/下午/晚上)',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int(11) DEFAULT 0 COMMENT '是否删除 (0-否, 1-是)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_time_slot` (`time_slot`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间段配置表';

-- 初始化时间段配置数据
INSERT INTO `sys_time_slot_config` (`time_slot`, `slot_name`, `start_time`, `end_time`, `period`, `description`) VALUES
(1, '第一大节', '08:00', '09:40', '上午', '上午第一大节（8:00-8:45, 8:55-9:40）'),
(2, '第二大节', '10:00', '11:40', '上午', '上午第二大节（10:00-10:45, 10:55-11:40）'),
(3, '第三大节', '16:30', '18:10', '下午', '下午第一大节（16:30-17:15, 17:25-18:10）'),
(4, '第四大节', '18:20', '20:00', '下午', '下午第二大节（18:20-19:05, 19:15-20:00）'),
(5, '第五大节', '19:00', '20:40', '晚上', '晚上第一大节（19:00-19:45, 19:55-20:40）'),
(6, '第六大节', '20:50', '22:30', '晚上', '晚上第二大节（20:50-21:35, 21:45-22:30）'); 