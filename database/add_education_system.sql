-- 为学生表添加学制字段
ALTER TABLE sys_student ADD COLUMN education_system VARCHAR(20) COMMENT '学制' AFTER grade;

-- 添加入学时间字段
ALTER TABLE sys_student ADD COLUMN enrollment_date DATETIME COMMENT '入学时间' AFTER education_system;

-- 添加毕业时间字段
ALTER TABLE sys_student ADD COLUMN graduation_date DATETIME COMMENT '毕业时间' AFTER enrollment_date;

-- 添加当前年级字段
ALTER TABLE sys_student ADD COLUMN current_year INT COMMENT '当前年级(1-5)' AFTER graduation_date;

-- 添加当前学期字段
ALTER TABLE sys_student ADD COLUMN current_semester INT COMMENT '当前学期(1-上学期，2-下学期)' AFTER current_year;

-- 添加当前学年字段
ALTER TABLE sys_student ADD COLUMN current_academic_year VARCHAR(50) COMMENT '当前学年学期' AFTER current_semester;

-- 更新现有学生数据的学制字段，根据年级设置默认学制
UPDATE sys_student SET education_system = '4年' WHERE grade IN ('2020', '2021', '2022', '2023', '2024'); 