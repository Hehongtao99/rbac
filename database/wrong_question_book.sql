-- 错题本表
CREATE TABLE `wrong_question_book` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '错题本名称',
  `description` varchar(500) DEFAULT NULL COMMENT '错题本描述',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `question_count` int DEFAULT 0 COMMENT '题目数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='错题本表';

-- 错题本题目关联表
CREATE TABLE `wrong_question_book_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_id` bigint NOT NULL COMMENT '错题本ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `wrong_answer` text COMMENT '错误答案',
  `correct_answer` text COMMENT '正确答案',
  `add_reason` varchar(200) DEFAULT NULL COMMENT '加入原因',
  `mastery_level` tinyint DEFAULT 0 COMMENT '掌握程度：0-未掌握 1-部分掌握 2-已掌握',
  `practice_count` int DEFAULT 0 COMMENT '练习次数',
  `last_practice_time` datetime DEFAULT NULL COMMENT '最后练习时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_book_question` (`book_id`, `question_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_mastery_level` (`mastery_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='错题本题目关联表';

-- 错题本练习记录表
CREATE TABLE `wrong_question_practice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_id` bigint NOT NULL COMMENT '错题本ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) NOT NULL COMMENT '练习标题',
  `total_count` int NOT NULL COMMENT '总题数',
  `correct_count` int DEFAULT 0 COMMENT '正确题数',
  `wrong_count` int DEFAULT 0 COMMENT '错误题数',
  `score` int DEFAULT 0 COMMENT '得分',
  `total_score` int DEFAULT 0 COMMENT '总分',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `duration` int DEFAULT NULL COMMENT '用时（分钟）',
  `status` tinyint DEFAULT 0 COMMENT '状态：0-进行中 1-已完成',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='错题本练习记录表';

-- 错题本练习题目表
CREATE TABLE `wrong_question_practice_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `practice_id` bigint NOT NULL COMMENT '练习记录ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `user_answer` text COMMENT '用户答案',
  `is_correct` tinyint DEFAULT 0 COMMENT '是否正确：0-错误 1-正确',
  `score` int DEFAULT 0 COMMENT '题目分值',
  `user_score` int DEFAULT 0 COMMENT '用户得分',
  `question_order` int DEFAULT 0 COMMENT '题目顺序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`),
  KEY `idx_practice_id` (`practice_id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_question_order` (`question_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='错题本练习题目表';

-- 插入默认错题本
INSERT INTO `wrong_question_book` (`name`, `description`, `user_id`) VALUES 
('我的错题本', '默认错题本，收集所有做错的题目', 1);