package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.AddWrongQuestionDTO;
import com.example.dto.WrongQuestionBookDTO;
import com.example.entity.*;
import com.example.mapper.*;
import com.example.service.WrongQuestionBookService;
import com.example.vo.WrongQuestionBookVO;
import com.example.vo.WrongQuestionItemVO;
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
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WrongQuestionBookServiceImpl implements WrongQuestionBookService {
    
    @Autowired
    private WrongQuestionBookMapper wrongQuestionBookMapper;
    
    @Autowired
    private WrongQuestionBookItemMapper wrongQuestionBookItemMapper;
    
    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    @Transactional
    public WrongQuestionBookVO createWrongQuestionBook(WrongQuestionBookDTO dto, Long userId) {
        WrongQuestionBook book = new WrongQuestionBook();
        book.setName(dto.getName());
        book.setDescription(dto.getDescription());
        book.setUserId(userId);
        book.setQuestionCount(0);
        
        wrongQuestionBookMapper.insert(book);
        
        return getWrongQuestionBookDetail(book.getId(), userId);
    }
    
    @Override
    public Page<WrongQuestionBookVO> getUserWrongQuestionBooks(Long userId, Integer current, Integer size) {
        Page<WrongQuestionBook> page = new Page<>(current, size);
        LambdaQueryWrapper<WrongQuestionBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestionBook::getUserId, userId)
                .orderByDesc(WrongQuestionBook::getCreateTime);
        
        Page<WrongQuestionBook> bookPage = wrongQuestionBookMapper.selectPage(page, wrapper);
        
        Page<WrongQuestionBookVO> voPage = new Page<>();
        BeanUtils.copyProperties(bookPage, voPage);
        
        List<WrongQuestionBookVO> bookVOs = bookPage.getRecords().stream().map(book -> {
            WrongQuestionBookVO vo = new WrongQuestionBookVO();
            BeanUtils.copyProperties(book, vo);
            
            // 获取用户名称
            User user = userMapper.selectById(book.getUserId());
            if (user != null) {
                vo.setUserName(user.getNickname());
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(bookVOs);
        return voPage;
    }
    
    @Override
    public WrongQuestionBookVO getWrongQuestionBookDetail(Long bookId, Long userId) {
        WrongQuestionBook book = wrongQuestionBookMapper.selectById(bookId);
        if (book == null || !book.getUserId().equals(userId)) {
            throw new RuntimeException("错题本不存在或无权限访问");
        }
        
        WrongQuestionBookVO vo = new WrongQuestionBookVO();
        BeanUtils.copyProperties(book, vo);
        
        // 获取用户名称
        User user = userMapper.selectById(book.getUserId());
        if (user != null) {
            vo.setUserName(user.getNickname());
        }
        
        return vo;
    }
    
    @Override
    @Transactional
    public void addWrongQuestion(AddWrongQuestionDTO dto, Long userId) {
        // 检查错题本是否存在
        WrongQuestionBook book = wrongQuestionBookMapper.selectById(dto.getBookId());
        if (book == null || !book.getUserId().equals(userId)) {
            throw new RuntimeException("错题本不存在或无权限访问");
        }
        
        // 检查题目是否存在
        Question question = questionMapper.selectById(dto.getQuestionId());
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }
        
        // 检查是否已经在错题本中
        LambdaQueryWrapper<WrongQuestionBookItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestionBookItem::getBookId, dto.getBookId())
                .eq(WrongQuestionBookItem::getQuestionId, dto.getQuestionId())
                .eq(WrongQuestionBookItem::getUserId, userId);
        WrongQuestionBookItem existItem = wrongQuestionBookItemMapper.selectOne(wrapper);
        
        if (existItem != null) {
            throw new RuntimeException("该题目已在错题本中");
        }
        
        // 添加错题
        WrongQuestionBookItem item = new WrongQuestionBookItem();
        item.setBookId(dto.getBookId());
        item.setQuestionId(dto.getQuestionId());
        item.setUserId(userId);
        item.setWrongAnswer(dto.getWrongAnswer());
        item.setCorrectAnswer(question.getAnswer());
        item.setAddReason(dto.getAddReason());
        item.setMasteryLevel(0); // 默认未掌握
        item.setPracticeCount(0);
        
        wrongQuestionBookItemMapper.insert(item);
        
        // 更新错题本题目数量
        book.setQuestionCount(book.getQuestionCount() + 1);
        wrongQuestionBookMapper.updateById(book);
    }
    
    @Override
    public Page<WrongQuestionItemVO> getWrongQuestions(Long bookId, Long userId, Integer current, Integer size) {
        // 检查错题本权限
        WrongQuestionBook book = wrongQuestionBookMapper.selectById(bookId);
        if (book == null || !book.getUserId().equals(userId)) {
            throw new RuntimeException("错题本不存在或无权限访问");
        }
        
        Page<WrongQuestionBookItem> page = new Page<>(current, size);
        LambdaQueryWrapper<WrongQuestionBookItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestionBookItem::getBookId, bookId)
                .eq(WrongQuestionBookItem::getUserId, userId)
                .orderByDesc(WrongQuestionBookItem::getCreateTime);
        
        Page<WrongQuestionBookItem> itemPage = wrongQuestionBookItemMapper.selectPage(page, wrapper);
        
        Page<WrongQuestionItemVO> voPage = new Page<>();
        BeanUtils.copyProperties(itemPage, voPage);
        
        List<WrongQuestionItemVO> itemVOs = itemPage.getRecords().stream().map(item -> {
            WrongQuestionItemVO vo = new WrongQuestionItemVO();
            BeanUtils.copyProperties(item, vo);
            
            // 获取题目信息
            Question question = questionMapper.selectById(item.getQuestionId());
            if (question != null) {
                vo.setTitle(question.getTitle());
                vo.setContent(question.getContent());
                vo.setType(question.getType());
                vo.setTypeName(getTypeName(question.getType()));
                vo.setDifficulty(question.getDifficulty());
                vo.setDifficultyName(getDifficultyName(question.getDifficulty()));
                vo.setScore(question.getScore());
                vo.setAnalysis(question.getAnalysis());
                
                // 处理选项
                if (StringUtils.hasText(question.getOptions())) {
                    try {
                        List<String> options = objectMapper.readValue(question.getOptions(), new TypeReference<List<String>>() {});
                        vo.setOptions(options);
                    } catch (Exception e) {
                        vo.setOptions(new ArrayList<>());
                    }
                }
            }
            
            // 设置掌握程度名称
            vo.setMasteryLevelName(getMasteryLevelName(item.getMasteryLevel()));
            
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(itemVOs);
        return voPage;
    }
    
    @Override
    @Transactional
    public void removeWrongQuestion(Long bookId, Long questionId, Long userId) {
        // 检查错题本权限
        WrongQuestionBook book = wrongQuestionBookMapper.selectById(bookId);
        if (book == null || !book.getUserId().equals(userId)) {
            throw new RuntimeException("错题本不存在或无权限访问");
        }
        
        // 删除错题
        LambdaQueryWrapper<WrongQuestionBookItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestionBookItem::getBookId, bookId)
                .eq(WrongQuestionBookItem::getQuestionId, questionId)
                .eq(WrongQuestionBookItem::getUserId, userId);
        
        int deleted = wrongQuestionBookItemMapper.delete(wrapper);
        if (deleted > 0) {
            // 更新错题本题目数量
            book.setQuestionCount(Math.max(0, book.getQuestionCount() - 1));
            wrongQuestionBookMapper.updateById(book);
        }
    }
    
    @Override
    @Transactional
    public void updateMasteryLevel(Long bookId, Long questionId, Integer masteryLevel, Long userId) {
        LambdaQueryWrapper<WrongQuestionBookItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestionBookItem::getBookId, bookId)
                .eq(WrongQuestionBookItem::getQuestionId, questionId)
                .eq(WrongQuestionBookItem::getUserId, userId);
        
        WrongQuestionBookItem item = wrongQuestionBookItemMapper.selectOne(wrapper);
        if (item != null) {
            item.setMasteryLevel(masteryLevel);
            wrongQuestionBookItemMapper.updateById(item);
        }
    }
    
    @Override
    @Transactional
    public void deleteWrongQuestionBook(Long bookId, Long userId) {
        // 检查错题本权限
        WrongQuestionBook book = wrongQuestionBookMapper.selectById(bookId);
        if (book == null || !book.getUserId().equals(userId)) {
            throw new RuntimeException("错题本不存在或无权限访问");
        }
        
        // 删除错题本中的所有题目
        LambdaQueryWrapper<WrongQuestionBookItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(WrongQuestionBookItem::getBookId, bookId);
        wrongQuestionBookItemMapper.delete(itemWrapper);
        
        // 删除错题本
        wrongQuestionBookMapper.deleteById(bookId);
    }
    
    @Override
    public boolean isQuestionInBook(Long bookId, Long questionId, Long userId) {
        LambdaQueryWrapper<WrongQuestionBookItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestionBookItem::getBookId, bookId)
                .eq(WrongQuestionBookItem::getQuestionId, questionId)
                .eq(WrongQuestionBookItem::getUserId, userId);
        
        return wrongQuestionBookItemMapper.selectCount(wrapper) > 0;
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
    
    private String getMasteryLevelName(Integer masteryLevel) {
        if (masteryLevel == null) return "";
        return switch (masteryLevel) {
            case 0 -> "未掌握";
            case 1 -> "部分掌握";
            case 2 -> "已掌握";
            default -> "未知";
        };
    }
} 