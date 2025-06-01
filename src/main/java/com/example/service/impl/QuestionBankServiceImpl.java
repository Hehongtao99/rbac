package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.QuestionBankDTO;
import com.example.entity.Question;
import com.example.entity.QuestionBank;
import com.example.entity.User;
import com.example.mapper.QuestionBankMapper;
import com.example.mapper.QuestionMapper;
import com.example.mapper.UserMapper;
import com.example.service.QuestionBankService;
import com.example.vo.QuestionBankVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class QuestionBankServiceImpl implements QuestionBankService {
    
    @Autowired
    private QuestionBankMapper questionBankMapper;
    
    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public Page<QuestionBankVO> getQuestionBankList(Integer current, Integer size, String name) {
        Page<QuestionBank> page = new Page<>(current, size);
        
        LambdaQueryWrapper<QuestionBank> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(QuestionBank::getName, name);
        }
        wrapper.orderByDesc(QuestionBank::getCreateTime);
        
        Page<QuestionBank> questionBankPage = questionBankMapper.selectPage(page, wrapper);
        
        Page<QuestionBankVO> voPage = new Page<>();
        BeanUtils.copyProperties(questionBankPage, voPage);
        
        voPage.setRecords(questionBankPage.getRecords().stream().map(this::convertToVO).toList());
        
        return voPage;
    }
    
    @Override
    public QuestionBankVO getQuestionBankById(Long id) {
        QuestionBank questionBank = questionBankMapper.selectById(id);
        if (questionBank == null) {
            throw new RuntimeException("题库不存在");
        }
        return convertToVO(questionBank);
    }
    
    @Override
    public void createQuestionBank(QuestionBankDTO questionBankDTO, Long userId) {
        QuestionBank questionBank = new QuestionBank();
        BeanUtils.copyProperties(questionBankDTO, questionBank);
        questionBank.setCreateUserId(userId);
        questionBank.setStatus(1);
        questionBankMapper.insert(questionBank);
    }
    
    @Override
    public void updateQuestionBank(QuestionBankDTO questionBankDTO) {
        QuestionBank questionBank = questionBankMapper.selectById(questionBankDTO.getId());
        if (questionBank == null) {
            throw new RuntimeException("题库不存在");
        }
        
        BeanUtils.copyProperties(questionBankDTO, questionBank);
        questionBankMapper.updateById(questionBank);
    }
    
    @Override
    public void deleteQuestionBank(Long id) {
        // 检查是否有题目
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getBankId, id);
        Long count = questionMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("该题库下还有题目，无法删除");
        }
        
        questionBankMapper.deleteById(id);
    }
    
    private QuestionBankVO convertToVO(QuestionBank questionBank) {
        QuestionBankVO vo = new QuestionBankVO();
        BeanUtils.copyProperties(questionBank, vo);
        
        // 获取创建用户名
        User user = userMapper.selectById(questionBank.getCreateUserId());
        if (user != null) {
            vo.setCreateUserName(user.getNickname());
        }
        
        // 获取题目数量
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getBankId, questionBank.getId());
        Long count = questionMapper.selectCount(wrapper);
        vo.setQuestionCount(count.intValue());
        
        return vo;
    }
} 