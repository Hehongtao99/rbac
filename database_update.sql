-- 删除广告位表中的广告性质字段，因为现在广告性质在申请时决定
ALTER TABLE advertisement_position DROP COLUMN ad_nature;

-- 确保修改密码权限已分配给管理员角色
INSERT IGNORE INTO sys_role_menu (role_id, menu_id, create_time) 
SELECT 1, 13, NOW() 
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 13
); 