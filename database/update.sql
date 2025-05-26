-- 数据库更新SQL - 修改用户权限为学生并添加教师管理和学生管理

-- 1. 将USER角色改为STUDENT角色
UPDATE sys_role SET role_name = '学生', role_code = 'STUDENT', description = '学生角色，拥有基本权限' WHERE role_code = 'USER';

-- 2. 新增教师管理菜单
INSERT INTO sys_menu (menu_name, menu_code, parent_id, menu_type, path, component, icon, sort, status, description) 
VALUES ('教师管理', 'teacher-management', 1, 'MENU', '/system/teacher', 'TeacherManagement', 'Avatar', 4, 1, '教师管理页面');

-- 3. 新增学生管理菜单  
INSERT INTO sys_menu (menu_name, menu_code, parent_id, menu_type, path, component, icon, sort, status, description)
VALUES ('学生管理', 'student-management', 1, 'MENU', '/system/student', 'StudentManagement', 'User', 5, 1, '学生管理页面');

-- 4. 获取教师管理菜单ID并新增相关按钮权限
SET @teacher_menu_id = (SELECT id FROM sys_menu WHERE menu_code = 'teacher-management');

INSERT INTO sys_menu (menu_name, menu_code, parent_id, menu_type, path, component, icon, sort, status, description)
VALUES ('新增教师', 'teacher:add', @teacher_menu_id, 'BUTTON', '', '', '', 1, 1, '新增教师按钮');

INSERT INTO sys_menu (menu_name, menu_code, parent_id, menu_type, path, component, icon, sort, status, description)
VALUES ('编辑教师', 'teacher:edit', @teacher_menu_id, 'BUTTON', '', '', '', 2, 1, '编辑教师按钮');

INSERT INTO sys_menu (menu_name, menu_code, parent_id, menu_type, path, component, icon, sort, status, description)
VALUES ('删除教师', 'teacher:delete', @teacher_menu_id, 'BUTTON', '', '', '', 3, 1, '删除教师按钮');

-- 5. 获取学生管理菜单ID并新增相关按钮权限
SET @student_menu_id = (SELECT id FROM sys_menu WHERE menu_code = 'student-management');

INSERT INTO sys_menu (menu_name, menu_code, parent_id, menu_type, path, component, icon, sort, status, description)
VALUES ('新增学生', 'student:add', @student_menu_id, 'BUTTON', '', '', '', 1, 1, '新增学生按钮');

INSERT INTO sys_menu (menu_name, menu_code, parent_id, menu_type, path, component, icon, sort, status, description)
VALUES ('编辑学生', 'student:edit', @student_menu_id, 'BUTTON', '', '', '', 2, 1, '编辑学生按钮');

INSERT INTO sys_menu (menu_name, menu_code, parent_id, menu_type, path, component, icon, sort, status, description)
VALUES ('删除学生', 'student:delete', @student_menu_id, 'BUTTON', '', '', '', 3, 1, '删除学生按钮');

-- 6. 为管理员角色分配新的菜单权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE menu_code IN ('teacher-management', 'student-management', 'teacher:add', 'teacher:edit', 'teacher:delete', 'student:add', 'student:edit', 'student:delete');

-- 7. 创建教师表
CREATE TABLE IF NOT EXISTS sys_teacher (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '教师ID',
    teacher_no VARCHAR(20) NOT NULL COMMENT '教师工号',
    name VARCHAR(50) NOT NULL COMMENT '教师姓名',
    gender VARCHAR(10) DEFAULT '未知' COMMENT '性别',
    phone VARCHAR(20) COMMENT '手机号码',
    email VARCHAR(100) COMMENT '邮箱',
    department VARCHAR(100) COMMENT '所属部门',
    title VARCHAR(50) COMMENT '职称',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_teacher_no (teacher_no)
) ENGINE=InnoDB COMMENT='教师信息表';

-- 8. 创建学生表
CREATE TABLE IF NOT EXISTS sys_student (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '学生ID',
    student_no VARCHAR(20) NOT NULL COMMENT '学号',
    name VARCHAR(50) NOT NULL COMMENT '学生姓名',
    gender VARCHAR(10) DEFAULT '未知' COMMENT '性别',
    phone VARCHAR(20) COMMENT '手机号码',
    email VARCHAR(100) COMMENT '邮箱',
    major VARCHAR(100) COMMENT '专业',
    class_name VARCHAR(100) COMMENT '班级',
    grade VARCHAR(20) COMMENT '年级',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_student_no (student_no)
) ENGINE=InnoDB COMMENT='学生信息表';

-- 9. 插入示例教师数据
INSERT INTO sys_teacher (teacher_no, name, gender, phone, email, department, title) VALUES
('T001', '张教授', '男', '13900000001', 'zhang@university.edu', '计算机学院', '教授'),
('T002', '李老师', '女', '13900000002', 'li@university.edu', '计算机学院', '副教授'),
('T003', '王讲师', '男', '13900000003', 'wang@university.edu', '计算机学院', '讲师');

-- 10. 插入示例学生数据
INSERT INTO sys_student (student_no, name, gender, phone, email, major, class_name, grade) VALUES
('S2021001', '张三', '男', '13700000001', 'zhangsan@student.edu', '计算机科学与技术', '计科2021级1班', '2021'),
('S2021002', '李四', '女', '13700000002', 'lisi@student.edu', '计算机科学与技术', '计科2021级1班', '2021'),
('S2021003', '王五', '男', '13700000003', 'wangwu@student.edu', '软件工程', '软工2021级1班', '2021'); 