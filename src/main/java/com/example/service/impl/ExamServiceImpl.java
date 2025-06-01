package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.ExamAnswerDTO;
import com.example.dto.ExamStartDTO;
import com.example.entity.*;
import com.example.mapper.*;
import com.example.service.ExamService;
import com.example.vo.ExamQuestionVO;
import com.example.vo.ExamVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExamServiceImpl implements ExamService {
    
    @Autowired
    private ExamMapper examMapper;
    
    @Autowired
    private ExamQuestionMapper examQuestionMapper;
    
    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private QuestionBankMapper questionBankMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    @Transactional
    public ExamVO startExam(ExamStartDTO examStartDTO, Long userId) {
        // 检查题库是否存在
        QuestionBank questionBank = questionBankMapper.selectById(examStartDTO.getBankId());
        if (questionBank == null) {
            throw new RuntimeException("题库不存在");
        }
        
        // 获取题库中的所有启用题目，按类型排序
        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<>();
        questionWrapper.eq(Question::getBankId, examStartDTO.getBankId())
                .eq(Question::getStatus, 1)
                .orderByAsc(Question::getType)
                .orderByAsc(Question::getId);
        List<Question> questions = questionMapper.selectList(questionWrapper);
        
        if (questions.isEmpty()) {
            throw new RuntimeException("该题库暂无可用题目");
        }
        
        // 创建考试记录
        Exam exam = new Exam();
        exam.setBankId(examStartDTO.getBankId());
        exam.setUserId(userId);
        exam.setTitle(examStartDTO.getTitle());
        exam.setDuration(examStartDTO.getDuration());
        exam.setStartTime(LocalDateTime.now());
        exam.setStatus(0); // 进行中
        
        // 计算总分
        int totalScore = questions.stream().mapToInt(Question::getScore).sum();
        exam.setTotalScore(totalScore);
        
        examMapper.insert(exam);
        
        // 创建考试题目记录
        List<ExamQuestion> examQuestions = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            ExamQuestion examQuestion = new ExamQuestion();
            examQuestion.setExamId(exam.getId());
            examQuestion.setQuestionId(question.getId());
            examQuestion.setQuestionOrder(i + 1);
            examQuestion.setScore(question.getScore());
            examQuestions.add(examQuestion);
        }
        
        // 批量插入考试题目
        examQuestions.forEach(examQuestionMapper::insert);
        
        // 返回考试详情
        return getExamDetail(exam.getId(), userId);
    }
    
    @Override
    public ExamVO getExamDetail(Long examId, Long userId) {
        log.info("获取考试详情，examId: {}, userId: {}", examId, userId);
        
        // 获取考试信息
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            log.error("考试不存在，examId: {}", examId);
            throw new RuntimeException("考试不存在，请检查考试ID是否正确");
        }
        
        if (!exam.getUserId().equals(userId)) {
            log.error("用户无权限访问考试，examId: {}, userId: {}, examUserId: {}", examId, userId, exam.getUserId());
            throw new RuntimeException("您无权限访问此考试");
        }
        
        log.info("考试信息获取成功，考试状态: {}", exam.getStatus());
        
        ExamVO examVO = new ExamVO();
        BeanUtils.copyProperties(exam, examVO);
        
        // 获取题库名称
        QuestionBank questionBank = questionBankMapper.selectById(exam.getBankId());
        if (questionBank != null) {
            examVO.setBankName(questionBank.getName());
        }
        
        // 获取用户名称
        User user = userMapper.selectById(exam.getUserId());
        if (user != null) {
            examVO.setUserName(user.getNickname());
        }
        
        // 设置状态名称
        examVO.setStatusName(getStatusName(exam.getStatus()));
        
        // 获取考试题目
        LambdaQueryWrapper<ExamQuestion> examQuestionWrapper = new LambdaQueryWrapper<>();
        examQuestionWrapper.eq(ExamQuestion::getExamId, examId)
                .orderByAsc(ExamQuestion::getQuestionOrder);
        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(examQuestionWrapper);
        
        log.info("获取到考试题目数量: {}", examQuestions.size());
        
        List<ExamQuestionVO> questionVOs = new ArrayList<>();
        for (ExamQuestion examQuestion : examQuestions) {
            Question question = questionMapper.selectById(examQuestion.getQuestionId());
            if (question != null) {
                ExamQuestionVO questionVO = convertToExamQuestionVO(examQuestion, question, exam.getStatus());
                questionVOs.add(questionVO);
            } else {
                log.warn("题目不存在，questionId: {}", examQuestion.getQuestionId());
            }
        }
        
        examVO.setQuestions(questionVOs);
        log.info("考试详情获取完成，题目数量: {}", questionVOs.size());
        return examVO;
    }
    
    @Override
    @Transactional
    public ExamVO submitExam(ExamAnswerDTO examAnswerDTO, Long userId) {
        // 获取考试信息
        Exam exam = examMapper.selectById(examAnswerDTO.getExamId());
        if (exam == null || !exam.getUserId().equals(userId)) {
            throw new RuntimeException("考试不存在或无权限访问");
        }
        
        if (exam.getStatus() != 0) {
            throw new RuntimeException("考试已结束，无法提交");
        }
        
        // 更新考试题目答案
        int totalUserScore = 0;
        for (ExamAnswerDTO.QuestionAnswer answer : examAnswerDTO.getAnswers()) {
            LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ExamQuestion::getExamId, examAnswerDTO.getExamId())
                    .eq(ExamQuestion::getQuestionId, answer.getQuestionId());
            ExamQuestion examQuestion = examQuestionMapper.selectOne(wrapper);
            
            if (examQuestion != null) {
                examQuestion.setUserAnswer(answer.getAnswer());
                
                // 判断答案是否正确并计算得分
                Question question = questionMapper.selectById(answer.getQuestionId());
                if (question != null) {
                    boolean isCorrect = checkAnswer(question, answer.getAnswer());
                    examQuestion.setIsCorrect(isCorrect ? 1 : 0);
                    examQuestion.setUserScore(isCorrect ? examQuestion.getScore() : 0);
                    totalUserScore += examQuestion.getUserScore();
                }
                
                examQuestionMapper.updateById(examQuestion);
            }
        }
        
        // 更新考试状态
        exam.setStatus(1); // 已完成
        exam.setEndTime(LocalDateTime.now());
        exam.setUserScore(totalUserScore);
        examMapper.updateById(exam);
        
        return getExamResult(exam.getId(), userId);
    }
    
    @Override
    public Page<ExamVO> getUserExamList(Long userId, Integer current, Integer size) {
        Page<Exam> page = new Page<>(current, size);
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Exam::getUserId, userId)
                .orderByDesc(Exam::getCreateTime);
        
        Page<Exam> examPage = examMapper.selectPage(page, wrapper);
        
        Page<ExamVO> examVOPage = new Page<>();
        BeanUtils.copyProperties(examPage, examVOPage);
        
        List<ExamVO> examVOs = examPage.getRecords().stream().map(exam -> {
            ExamVO examVO = new ExamVO();
            BeanUtils.copyProperties(exam, examVO);
            
            // 获取题库名称
            QuestionBank questionBank = questionBankMapper.selectById(exam.getBankId());
            if (questionBank != null) {
                examVO.setBankName(questionBank.getName());
            }
            
            // 设置状态名称
            examVO.setStatusName(getStatusName(exam.getStatus()));
            
            return examVO;
        }).collect(Collectors.toList());
        
        examVOPage.setRecords(examVOs);
        return examVOPage;
    }
    
    @Override
    public ExamVO getExamResult(Long examId, Long userId) {
        return getExamDetail(examId, userId);
    }
    
    private ExamQuestionVO convertToExamQuestionVO(ExamQuestion examQuestion, Question question, Integer examStatus) {
        ExamQuestionVO questionVO = new ExamQuestionVO();
        BeanUtils.copyProperties(examQuestion, questionVO);
        
        // 复制题目信息
        questionVO.setTitle(question.getTitle());
        questionVO.setContent(question.getContent());
        questionVO.setType(question.getType());
        questionVO.setTypeName(getTypeName(question.getType()));
        questionVO.setDifficulty(question.getDifficulty());
        questionVO.setDifficultyName(getDifficultyName(question.getDifficulty()));
        
        // 处理选项
        if (StringUtils.hasText(question.getOptions())) {
            try {
                List<String> options = objectMapper.readValue(question.getOptions(), new TypeReference<List<String>>() {});
                questionVO.setOptions(options);
            } catch (Exception e) {
                questionVO.setOptions(new ArrayList<>());
            }
        }
        
        // 考试结束后显示答案和解析
        if (examStatus != 0) {
            questionVO.setAnswer(question.getAnswer());
            questionVO.setAnalysis(question.getAnalysis());
        }
        
        return questionVO;
    }
    
    private boolean checkAnswer(Question question, String userAnswer) {
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return false;
        }
        
        String correctAnswer = question.getAnswer();
        if (correctAnswer == null) {
            return false;
        }
        
        // 根据题目类型判断答案
        switch (question.getType()) {
            case 1: // 单选题
                return correctAnswer.trim().equals(userAnswer.trim());
            case 3: // 判断题
                // 判断题答案格式兼容性处理
                String normalizedCorrect = normalizeJudgmentAnswer(correctAnswer.trim());
                String normalizedUser = normalizeJudgmentAnswer(userAnswer.trim());
                return normalizedCorrect.equals(normalizedUser);
            case 2: // 多选题
                log.info("多选题答案判断 - 题目ID: {}, 正确答案: [{}], 用户答案: [{}]", 
                    question.getId(), correctAnswer, userAnswer);
                
                // 多选题答案可能是用逗号或顿号分隔的
                String[] correctAnswers = correctAnswer.split("[,，、;；]");
                String[] userAnswers = userAnswer.split("[,，、;；]");
                
                log.info("多选题答案分割后 - 正确答案数组: {}, 用户答案数组: {}", 
                    Arrays.toString(correctAnswers), Arrays.toString(userAnswers));
                
                // 去除空白并转换为Set进行比较，忽略顺序
                Set<String> correctSet = Arrays.stream(correctAnswers)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
                    
                Set<String> userSet = Arrays.stream(userAnswers)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
                
                log.info("多选题答案集合 - 正确答案集合: {}, 用户答案集合: {}", correctSet, userSet);
                
                // 比较两个集合是否相等（忽略顺序）
                boolean isEqual = correctSet.equals(userSet);
                log.info("多选题答案判断结果: {}", isEqual);
                
                return isEqual;
            case 4: // 问答题
            case 5: // 填空题
                // 问答题和填空题可以进行模糊匹配
                return correctAnswer.trim().contains(userAnswer.trim()) || 
                       userAnswer.trim().contains(correctAnswer.trim());
            default:
                return false;
        }
    }
    
    /**
     * 标准化判断题答案格式
     */
    private String normalizeJudgmentAnswer(String answer) {
        if (answer == null) return "";
        
        // 将各种可能的答案格式统一化
        switch (answer) {
            case "对":
            case "正确":
            case "T":
            case "True":
            case "true":
            case "是":
                return "正确";
            case "错":
            case "错误":
            case "F":
            case "False":
            case "false":
            case "否":
                return "错误";
            default:
                return answer;
        }
    }
    
    private String getTypeName(Integer type) {
        if (type == null) return "";
        return switch (type) {
            case 1 -> "单选题";
            case 2 -> "多选题";
            case 3 -> "判断题";
            case 4 -> "问答题";
            case 5 -> "填空题";
            default -> "未知类型";
        };
    }
    
    private String getDifficultyName(Integer difficulty) {
        if (difficulty == null) return "";
        return switch (difficulty) {
            case 1 -> "简单";
            case 2 -> "中等";
            case 3 -> "困难";
            default -> "未知难度";
        };
    }
    
    private String getStatusName(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 0 -> "进行中";
            case 1 -> "已完成";
            case 2 -> "已超时";
            default -> "未知状态";
        };
    }
} 