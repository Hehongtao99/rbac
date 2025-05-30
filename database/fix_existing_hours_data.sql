-- 修复现有数据的课时相关问题
SET NAMES utf8mb4;

-- 1. 更新现有课程表记录的课时消耗为1
UPDATE `sys_schedule` 
SET `reduced_hours` = 1 
WHERE `reduced_hours` != 1;

-- 2. 重新计算申请记录的剩余课时
-- 先将所有申请记录的剩余课时重置为总课时
UPDATE `sys_course_application` 
SET `remaining_hours` = `course_hours`;

-- 3. 然后根据实际使用情况计算剩余课时
UPDATE `sys_course_application` ca 
SET `remaining_hours` = `course_hours` - (
    SELECT COALESCE(SUM(s.reduced_hours), 0) 
    FROM `sys_schedule` s 
    WHERE s.course_id = ca.id 
    AND s.deleted = 0
)
WHERE ca.status = 1; 