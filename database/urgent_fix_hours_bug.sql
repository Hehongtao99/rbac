-- 紧急修复课时Bug脚本
SET NAMES utf8mb4;

-- 1. 查看当前课程表中的reducedHours分布
SELECT reduced_hours, COUNT(*) as count FROM sys_schedule GROUP BY reduced_hours;

-- 2. 强制将所有课程表记录的reducedHours设置为1
UPDATE sys_schedule SET reduced_hours = 1;

-- 3. 重新计算所有申请记录的剩余课时
-- 先备份当前状态
CREATE TEMPORARY TABLE temp_application_backup AS 
SELECT id, course_name, teacher_name, course_hours, remaining_hours 
FROM sys_course_application WHERE status = 1;

-- 重置所有通过申请的剩余课时为总课时
UPDATE sys_course_application 
SET remaining_hours = course_hours 
WHERE status = 1;

-- 根据实际课程表使用情况重新计算剩余课时
UPDATE sys_course_application ca 
SET remaining_hours = course_hours - (
    SELECT COALESCE(SUM(s.reduced_hours), 0) 
    FROM sys_schedule s 
    WHERE s.course_id = ca.id AND s.deleted = 0
)
WHERE ca.status = 1;

-- 4. 显示修复结果
SELECT 
    ca.id,
    ca.course_name,
    ca.teacher_name,
    ca.course_hours as total_hours,
    ca.remaining_hours,
    (SELECT COUNT(*) FROM sys_schedule s WHERE s.course_id = ca.id AND s.deleted = 0) as used_schedules,
    (SELECT SUM(s.reduced_hours) FROM sys_schedule s WHERE s.course_id = ca.id AND s.deleted = 0) as used_hours
FROM sys_course_application ca 
WHERE ca.status = 1
ORDER BY ca.id; 