package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.QuestionDTO;
import com.example.entity.Question;
import com.example.entity.QuestionBank;
import com.example.entity.User;
import com.example.mapper.QuestionBankMapper;
import com.example.mapper.QuestionMapper;
import com.example.mapper.UserMapper;
import com.example.service.QuestionService;
import com.example.vo.QuestionVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {
    
    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private QuestionBankMapper questionBankMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Page<QuestionVO> getQuestionList(Integer current, Integer size, Long bankId, Integer type, String title) {
        Page<Question> page = new Page<>(current, size);
        
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        if (bankId != null) {
            wrapper.eq(Question::getBankId, bankId);
        }
        if (type != null) {
            wrapper.eq(Question::getType, type);
        }
        if (StringUtils.hasText(title)) {
            wrapper.like(Question::getTitle, title);
        }
        wrapper.orderByDesc(Question::getCreateTime);
        
        Page<Question> questionPage = questionMapper.selectPage(page, wrapper);
        
        Page<QuestionVO> voPage = new Page<>();
        BeanUtils.copyProperties(questionPage, voPage);
        
        voPage.setRecords(questionPage.getRecords().stream().map(this::convertToVO).toList());
        
        return voPage;
    }
    
    @Override
    public QuestionVO getQuestionById(Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }
        return convertToVO(question);
    }
    
    @Override
    public void createQuestion(QuestionDTO questionDTO, Long userId) {
        Question question = new Question();
        BeanUtils.copyProperties(questionDTO, question);
        
        // 将选项列表转换为JSON字符串
        if (questionDTO.getOptions() != null && !questionDTO.getOptions().isEmpty()) {
            try {
                question.setOptions(objectMapper.writeValueAsString(questionDTO.getOptions()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("选项格式错误");
            }
        }
        
        question.setCreateUserId(userId);
        question.setStatus(1);
        questionMapper.insert(question);
    }
    
    @Override
    public void updateQuestion(QuestionDTO questionDTO) {
        Question question = questionMapper.selectById(questionDTO.getId());
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }
        
        BeanUtils.copyProperties(questionDTO, question);
        
        // 将选项列表转换为JSON字符串
        if (questionDTO.getOptions() != null && !questionDTO.getOptions().isEmpty()) {
            try {
                question.setOptions(objectMapper.writeValueAsString(questionDTO.getOptions()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("选项格式错误");
            }
        }
        
        questionMapper.updateById(question);
    }
    
    @Override
    public void deleteQuestion(Long id) {
        questionMapper.deleteById(id);
    }
    
    @Override
    public void batchDeleteQuestion(Long[] ids) {
        questionMapper.deleteBatchIds(Arrays.asList(ids));
    }
    
    private QuestionVO convertToVO(Question question) {
        QuestionVO vo = new QuestionVO();
        BeanUtils.copyProperties(question, vo);
        
        // 获取题库名称
        QuestionBank questionBank = questionBankMapper.selectById(question.getBankId());
        if (questionBank != null) {
            vo.setBankName(questionBank.getName());
        }
        
        // 获取创建用户名
        User user = userMapper.selectById(question.getCreateUserId());
        if (user != null) {
            vo.setCreateUserName(user.getNickname());
        }
        
        // 转换题目类型名称
        vo.setTypeName(getTypeName(question.getType()));
        
        // 转换难度名称
        vo.setDifficultyName(getDifficultyName(question.getDifficulty()));
        
        // 将JSON字符串转换为选项列表
        if (StringUtils.hasText(question.getOptions())) {
            try {
                List<String> options = objectMapper.readValue(question.getOptions(), new TypeReference<List<String>>() {});
                vo.setOptions(options);
            } catch (JsonProcessingException e) {
                // 如果解析失败，设置为空列表
                vo.setOptions(List.of());
            }
        }
        
        return vo;
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
} 