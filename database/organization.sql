-- 组织管理表
CREATE TABLE IF NOT EXISTS `organization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '组织名称',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父级ID',
  `level` int(11) NOT NULL COMMENT '级别：1省 2市 3区 4街道',
  `code` varchar(50) DEFAULT NULL COMMENT '组织编码',
  `full_name` varchar(200) DEFAULT NULL COMMENT '组织全称',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `image_urls` text DEFAULT NULL COMMENT '图片URLs，JSON格式存储多张图片',
  `description` text DEFAULT NULL COMMENT '描述信息',
  `sort` int(11) DEFAULT 0 COMMENT '排序',
  `is_enabled` tinyint(1) DEFAULT 1 COMMENT '是否启用：1启用 0禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_level` (`level`),
  KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织管理表';

-- 插入示例数据
INSERT INTO `organization` (`name`, `parent_id`, `level`, `code`, `full_name`, `sort`, `is_enabled`) VALUES
('北京市', NULL, 1, '110000', '北京市', 1, 1),
('上海市', NULL, 1, '310000', '上海市', 2, 1),
('广东省', NULL, 1, '440000', '广东省', 3, 1);

INSERT INTO `organization` (`name`, `parent_id`, `level`, `code`, `full_name`, `sort`, `is_enabled`) VALUES
('东城区', 1, 2, '110101', '北京市东城区', 1, 1),
('西城区', 1, 2, '110102', '北京市西城区', 2, 1),
('黄浦区', 2, 2, '310101', '上海市黄浦区', 1, 1),
('徐汇区', 2, 2, '310104', '上海市徐汇区', 2, 1),
('广州市', 3, 2, '440100', '广东省广州市', 1, 1),
('深圳市', 3, 2, '440300', '广东省深圳市', 2, 1);

INSERT INTO `organization` (`name`, `parent_id`, `level`, `code`, `full_name`, `sort`, `is_enabled`) VALUES
('东华门街道', 4, 3, '110101001', '北京市东城区东华门街道', 1, 1),
('景山街道', 4, 3, '110101002', '北京市东城区景山街道', 2, 1),
('什刹海街道', 5, 3, '110102001', '北京市西城区什刹海街道', 1, 1),
('西长安街街道', 5, 3, '110102002', '北京市西城区西长安街街道', 2, 1);

INSERT INTO `organization` (`name`, `parent_id`, `level`, `code`, `full_name`, `longitude`, `latitude`, `image_urls`, `description`, `sort`, `is_enabled`) VALUES
('多福巷社区', 11, 4, '110101001001', '北京市东城区东华门街道多福巷社区', 116.403414, 39.918695, '["https://example.com/image1.jpg"]', '多福巷社区位于东华门街道核心区域，历史悠久，文化底蕴深厚。', 1, 1),
('黄图岗社区', 11, 4, '110101001002', '北京市东城区东华门街道黄图岗社区', 116.404521, 39.919832, '["https://example.com/image2.jpg", "https://example.com/image2-2.jpg"]', '黄图岗社区紧邻故宫，是著名的文化旅游区域。', 2, 1),
('吉安所社区', 12, 4, '110101002001', '北京市东城区景山街道吉安所社区', 116.395833, 39.922500, '["https://example.com/image3.jpg"]', '吉安所社区环境优美，居民生活便利。', 1, 1); 