-- 修复课程表中教师姓名显示问题
-- 将schedule表中的teacherName更新为正确的当前教师姓名

-- 更新课程表中的教师姓名，使用用户表中的nickname或username
UPDATE sys_schedule s
JOIN sys_user u ON s.teacher_id = u.id
SET s.teacher_name = CASE 
    WHEN u.nickname IS NOT NULL AND u.nickname != '' THEN u.nickname
    ELSE u.username
END
WHERE s.teacher_id IS NOT NULL;

-- 验证更新结果（可选）
-- SELECT s.id, s.course_name, s.teacher_id, s.teacher_name, u.username, u.nickname
-- FROM sys_schedule s
-- JOIN sys_user u ON s.teacher_id = u.id
-- ORDER BY s.id; 