-- 删除广告位表中的广告性质字段，因为现在广告性质在申请时决定
ALTER TABLE advertisement_position DROP COLUMN ad_nature; 