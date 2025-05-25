-- 创建数据库
CREATE DATABASE IF NOT EXISTS rbac_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE rbac_system;

-- 创建用户表
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) NOT NULL COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建角色表
CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 创建用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 插入初始角色数据
INSERT INTO sys_role (role_name, role_code, description) VALUES
('管理员', 'ADMIN', '系统管理员，拥有所有权限'),
('用户', 'USER', '普通用户，拥有基本权限');

-- 插入初始用户数据（密码为123456的BCrypt加密结果）
INSERT INTO sys_user (username, password, nickname, email, phone) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '系统管理员', 'admin@example.com', '13800138000'),
('user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '普通用户', 'user@example.com', '13800138001');

-- 创建菜单表
CREATE TABLE sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '菜单ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    menu_code VARCHAR(50) NOT NULL UNIQUE COMMENT '菜单编码',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID，0表示顶级菜单',
    menu_type VARCHAR(10) NOT NULL COMMENT '菜单类型：MENU-菜单，BUTTON-按钮',
    path VARCHAR(200) COMMENT '路由路径',
    component VARCHAR(200) COMMENT '组件路径',
    icon VARCHAR(50) COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description VARCHAR(200) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

-- 创建角色菜单关联表
CREATE TABLE sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_role_id (role_id),
    INDEX idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- 插入菜单数据
INSERT INTO sys_menu (menu_name, menu_code, parent_id, menu_type, path, component, icon, sort, description) VALUES
-- 主菜单
('系统管理', 'system', 0, 'MENU', '/system', '', 'Setting', 1, '系统管理模块'),
('用户管理', 'user-management', 1, 'MENU', '/system/user', 'UserManagement', 'User', 1, '用户管理页面'),
('角色管理', 'role-management', 1, 'MENU', '/system/role', 'RoleManagement', 'UserFilled', 2, '角色管理页面'),

-- 用户管理按钮
('新增用户', 'user:add', 2, 'BUTTON', '', '', '', 1, '新增用户按钮'),
('编辑用户', 'user:edit', 2, 'BUTTON', '', '', '', 2, '编辑用户按钮'),
('删除用户', 'user:delete', 2, 'BUTTON', '', '', '', 3, '删除用户按钮'),
('分配角色', 'user:assign-role', 2, 'BUTTON', '', '', '', 4, '分配角色按钮'),

-- 角色管理按钮
('新增角色', 'role:add', 3, 'BUTTON', '', '', '', 1, '新增角色按钮'),
('编辑角色', 'role:edit', 3, 'BUTTON', '', '', '', 2, '编辑角色按钮'),
('删除角色', 'role:delete', 3, 'BUTTON', '', '', '', 3, '删除角色按钮'),
('分配权限', 'role:assign-menu', 3, 'BUTTON', '', '', '', 4, '分配权限按钮'),

-- 普通功能菜单
('个人中心', 'profile', 0, 'MENU', '/profile', 'Profile', 'Avatar', 2, '个人中心页面'),
('修改密码', 'change-password', 12, 'BUTTON', '', '', '', 1, '修改密码按钮');

-- 分配角色
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1), -- admin用户分配管理员角色
(2, 2); -- user用户分配普通用户角色

-- 分配管理员所有权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13);

-- 分配普通用户基本权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(2, 12), (2, 13); -- 只有个人中心和修改密码权限 