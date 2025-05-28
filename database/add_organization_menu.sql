-- 添加组织管理菜单
INSERT INTO sys_menu (menu_name, menu_code, parent_id, menu_type, path, component, icon, sort, description) VALUES
('组织管理', 'organization-management', 1, 'MENU', '/system/organization', 'OrganizationManagement', 'OfficeBuilding', 3, '组织架构管理页面');

-- 获取组织管理菜单ID
SET @org_menu_id = LAST_INSERT_ID();

-- 添加组织管理相关按钮权限
INSERT INTO sys_menu (menu_name, menu_code, parent_id, menu_type, path, component, icon, sort, description) VALUES
('新增组织', 'organization:add', @org_menu_id, 'BUTTON', '', '', '', 1, '新增组织按钮'),
('编辑组织', 'organization:edit', @org_menu_id, 'BUTTON', '', '', '', 2, '编辑组织按钮'),
('删除组织', 'organization:delete', @org_menu_id, 'BUTTON', '', '', '', 3, '删除组织按钮');

-- 给管理员角色分配组织管理权限
INSERT INTO sys_role_menu (role_id, menu_id) 
SELECT 1, id FROM sys_menu WHERE menu_code IN ('organization-management', 'organization:add', 'organization:edit', 'organization:delete') AND deleted = 0; 