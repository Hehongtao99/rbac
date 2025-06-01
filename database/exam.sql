-- 考试表
CREATE TABLE `exam` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `bank_id` bigint NOT NULL COMMENT '题库ID',
  `user_id` bigint NOT NULL COMMENT '考试用户ID',
  `title` varchar(200) NOT NULL COMMENT '考试标题',
  `total_score` int NOT NULL DEFAULT '0' COMMENT '总分',
  `user_score` int DEFAULT NULL COMMENT '用户得分',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `duration` int NOT NULL DEFAULT '60' COMMENT '考试时长（分钟）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-进行中 1-已完成 2-已超时',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_bank_id` (`bank_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试表';

-- 考试题目表
CREATE TABLE `exam_question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `exam_id` bigint NOT NULL COMMENT '考试ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `question_order` int NOT NULL COMMENT '题目顺序',
  `user_answer` text COMMENT '用户答案',
  `is_correct` tinyint DEFAULT NULL COMMENT '是否正确：0-错误 1-正确',
  `score` int NOT NULL DEFAULT '0' COMMENT '题目分值',
  `user_score` int DEFAULT NULL COMMENT '用户得分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_question_order` (`question_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试题目表'; 