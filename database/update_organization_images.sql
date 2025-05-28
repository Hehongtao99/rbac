-- 更新组织表，支持多张图片
-- 1. 添加新的多图片字段
ALTER TABLE `organization` ADD COLUMN `image_urls` text DEFAULT NULL COMMENT '图片URLs，JSON格式存储多张图片' AFTER `latitude`;

-- 2. 迁移现有数据（如果有单张图片数据）
UPDATE `organization` 
SET `image_urls` = CONCAT('["', `image_url`, '"]') 
WHERE `image_url` IS NOT NULL AND `image_url` != '';

-- 3. 删除旧的单张图片字段（可选，建议先备份数据）
-- ALTER TABLE `organization` DROP COLUMN `image_url`;

-- 注意：如果已经有数据，请先备份，然后根据实际情况执行迁移 