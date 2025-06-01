package com.example.service.impl;

import com.example.dto.*;
import com.example.entity.Question;
import com.example.mapper.QuestionMapper;
import com.example.service.AiQuestionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class AiQuestionServiceImpl implements AiQuestionService {
    
    @Autowired
    private WebClient webClient;
    
    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // Gemini API配置
    private static final String API_URL = "https://chataiapi.com/v1/chat/completions";
    private static final String API_KEY = "sk-XroANN9PzwAQi0wEINqHgoXnUaTnSpafdpLmoVWEsZGAOOmi";
    private static final String MODEL = "gemini-2.0-flash-lite";
    
    @Override
    public List<Long> extractAndAddQuestions(AiQuestionRequest request, Long userId) {
        // 验证输入内容长度
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new RuntimeException("输入内容不能为空");
        }
        if (request.getContent().length() > 20000) {
            throw new RuntimeException("输入内容不能超过20000个字符");
        }
        
        // 提取题目
        List<AiExtractedQuestion> extractedQuestions = extractQuestions(request.getContent());
        
        // 保存到数据库
        List<Long> questionIds = new ArrayList<>();
        for (AiExtractedQuestion aiQuestion : extractedQuestions) {
            Question question = convertToQuestion(aiQuestion, request.getBankId(), userId);
            questionMapper.insert(question);
            questionIds.add(question.getId());
        }
        
        return questionIds;
    }
    
    @Override
    public List<AiExtractedQuestion> extractQuestions(String content) {
        // 验证输入内容
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("输入内容不能为空");
        }
        if (content.length() > 20000) {
            throw new RuntimeException("输入内容不能超过20000个字符");
        }
        
        try {
            String prompt = buildExtractionPrompt(content);
            String aiResponse = callGeminiApi(prompt);
            return parseAiResponse(aiResponse);
        } catch (Exception e) {
            log.error("AI提取题目失败", e);
            throw new RuntimeException("AI提取题目失败: " + e.getMessage());
        }
    }
    
    @Override
    public AiStreamResponse extractQuestionsStream(AiQuestionRequest request) {
        try {
            String content = request.getContent();
            Integer startPosition = request.getStartPosition() != null ? request.getStartPosition() : 0;
            
            // 验证输入
            if (content == null || content.trim().isEmpty()) {
                throw new RuntimeException("输入内容不能为空");
            }
            
            log.info("流式解析开始，总长度: {}, 开始位置: {}", content.length(), startPosition);
            
            // 分段处理：降低分段阈值，确保能够分段
            int maxChunkSize = 2000; // 降低到2000字符，更容易触发分段
            String contentToProcess;
            boolean hasMore = false;
            int nextPosition = startPosition;
            
            if (startPosition >= content.length()) {
                // 已经处理完所有内容
                AiStreamResponse response = new AiStreamResponse();
                response.setQuestions(new ArrayList<>());
                response.setHasMore(false);
                response.setNextPosition(content.length());
                response.setRemainingContent("");
                response.setProcessedContent(content);
                log.info("所有内容已处理完成");
                return response;
            }
            
            int endPosition = Math.min(startPosition + maxChunkSize, content.length());
            
            // 强制分段：如果剩余内容长度超过maxChunkSize，就必须分段
            int remainingLength = content.length() - startPosition;
            log.info("剩余内容长度: {}, maxChunkSize: {}", remainingLength, maxChunkSize);
            
            if (remainingLength > maxChunkSize) {
                // 寻找最近的题目分隔符
                String chunk = content.substring(startPosition, endPosition);
                int lastQuestionEnd = findLastQuestionEnd(chunk);
                
                // 如果找到合适的分割点且不是整个chunk，则使用该分割点
                if (lastQuestionEnd > 0 && lastQuestionEnd < chunk.length()) {
                    endPosition = startPosition + lastQuestionEnd;
                    log.info("找到题目分割点，调整结束位置到: {}", endPosition);
                } else {
                    // 如果找不到合适的分割点，强制在3/4位置分割
                    endPosition = startPosition + (chunk.length() * 3 / 4);
                    log.info("强制分割，结束位置: {}", endPosition);
                }
                
                hasMore = true;
                nextPosition = endPosition;
                log.info("分段处理，当前段: {} - {}, 还有更多内容: {}", startPosition, endPosition, hasMore);
            } else {
                // 这是最后一段
                hasMore = false;
                nextPosition = content.length();
                log.info("最后一段处理，范围: {} - {}, hasMore: {}", startPosition, endPosition, hasMore);
            }
            
            contentToProcess = content.substring(startPosition, endPosition);
            log.info("本次处理内容长度: {}, hasMore: {}, nextPosition: {}", contentToProcess.length(), hasMore, nextPosition);
            
            // 构建提示词
            String prompt = buildStreamExtractionPrompt(contentToProcess, startPosition > 0);
            String aiResponse = callGeminiApi(prompt);
            List<AiExtractedQuestion> questions = parseAiResponse(aiResponse);
            
            log.info("本次提取到 {} 道题目，hasMore: {}", questions.size(), hasMore);
            
            // 构建响应
            AiStreamResponse response = new AiStreamResponse();
            response.setQuestions(questions);
            response.setHasMore(hasMore);
            response.setNextPosition(nextPosition);
            response.setRemainingContent(hasMore ? content.substring(nextPosition) : "");
            response.setProcessedContent(content.substring(0, nextPosition));
            
            log.info("返回响应: hasMore={}, nextPosition={}, questionsCount={}", 
                response.getHasMore(), response.getNextPosition(), response.getQuestions().size());
            
            return response;
            
        } catch (Exception e) {
            log.error("AI流式提取题目失败", e);
            throw new RuntimeException("AI流式提取题目失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Long> addStreamQuestions(List<AiExtractedQuestion> questions, Long bankId, Long userId) {
        List<Long> questionIds = new ArrayList<>();
        for (AiExtractedQuestion aiQuestion : questions) {
            Question question = convertToQuestion(aiQuestion, bankId, userId);
            questionMapper.insert(question);
            questionIds.add(question.getId());
        }
        return questionIds;
    }
    
    private String buildExtractionPrompt(String content) {
        return String.format("""
            请从以下内容中提取出题目和答案，并按照指定的JSON格式返回。
            
            重要提示：
            1. 如果内容很长，请优先提取前面的题目，确保每道题目都是完整的
            2. 宁可少提取几道题目，也要保证JSON格式完整有效
            3. 如果担心输出长度超限，请减少提取的题目数量
            
            内容：
            %s
            
            请严格按照以下JSON格式返回，不要包含任何其他文字：
            [
              {
                "title": "题目标题（如果没有明确标题，可以用题目内容的前几个字作为标题）",
                "content": "完整的题目内容",
                "type": 1,
                "options": ["选项A", "选项B", "选项C", "选项D"],
                "answer": "正确答案",
                "analysis": "题目解析（如果原文中有解析的话）",
                "difficulty": 1,
                "score": 5
              }
            ]
            
            提取规则：
            1. 自动识别题目类型：
               - type=1: 单选题（只有一个正确答案的选择题）
               - type=2: 多选题（有多个正确答案的选择题）
               - type=3: 判断题（正确/错误类型的题目）
               - type=4: 问答题（开放性问题）
               - type=5: 填空题（需要填空的题目）
            
            2. 选项处理：
               - 单选题和多选题：提取所有选项到options数组
               - 判断题：options设为["正确", "错误"]
               - 问答题和填空题：不需要options字段或设为空数组
            
            3. 答案处理：
               - 选择题：填写正确选项的内容
               - 判断题：填写"正确"或"错误"
               - 问答题和填空题：填写标准答案
            
            4. 如果没有明确的题目解析，analysis字段可以为空字符串
            5. difficulty默认设为1（简单），score默认设为5分
            6. 请确保提取出的每道题目都是完整和准确的
            7. 确保JSON格式完整，以]结尾
            """, content);
    }
    
    private String callGeminiApi(String prompt) {
        try {
            GeminiRequest request = new GeminiRequest();
            request.setModel(MODEL);
            request.setTemperature(0.3); // 降低温度以提高准确性
            request.setMax_tokens(8192);
            request.setMessages(Arrays.asList(
                new GeminiRequest.Message("user", prompt)
            ));
            
            Mono<GeminiResponse> responseMono = webClient.post()
                    .uri(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .timeout(Duration.ofMinutes(10)); // 设置10分钟超时
            
            GeminiResponse response = responseMono.block(Duration.ofMinutes(11)); // 稍微长一点的阻塞超时
            
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                String content = response.getChoices().get(0).getMessage().getContent();
                if (content == null || content.trim().isEmpty()) {
                    throw new RuntimeException("AI返回内容为空");
                }
                return content;
            } else {
                throw new RuntimeException("AI API返回空响应");
            }
        } catch (Exception e) {
            log.error("调用Gemini API失败", e);
            if (e.getMessage().contains("timeout") || e.getMessage().contains("Timeout")) {
                throw new RuntimeException("AI服务响应超时，请稍后重试");
            } else if (e.getMessage().contains("connection") || e.getMessage().contains("Connection")) {
                throw new RuntimeException("无法连接到AI服务，请检查网络连接");
            } else {
                throw new RuntimeException("调用AI服务失败: " + e.getMessage());
            }
        }
    }
    
    private List<AiExtractedQuestion> parseAiResponse(String aiResponse) {
        try {
            // 清理响应内容，移除可能的markdown标记
            String cleanResponse = aiResponse.trim();
            if (cleanResponse.startsWith("```json")) {
                cleanResponse = cleanResponse.substring(7);
            }
            if (cleanResponse.endsWith("```")) {
                cleanResponse = cleanResponse.substring(0, cleanResponse.length() - 3);
            }
            cleanResponse = cleanResponse.trim();
            
            // 检查JSON是否完整，如果不完整则尝试修复
            cleanResponse = fixIncompleteJson(cleanResponse);
            
            // 解析JSON
            return objectMapper.readValue(cleanResponse, new TypeReference<List<AiExtractedQuestion>>() {});
        } catch (JsonProcessingException e) {
            log.error("解析AI响应失败: {}", aiResponse, e);
            
            // 尝试从不完整的JSON中提取部分数据
            List<AiExtractedQuestion> partialResults = extractPartialQuestions(aiResponse);
            if (!partialResults.isEmpty()) {
                log.warn("从不完整的响应中提取到{}道题目", partialResults.size());
                return partialResults;
            }
            
            throw new RuntimeException("解析AI响应失败: " + e.getMessage());
        }
    }
    
    /**
     * 修复不完整的JSON
     */
    private String fixIncompleteJson(String json) {
        if (!json.startsWith("[")) {
            return "[]";
        }
        
        // 检查JSON是否以]结尾
        if (!json.trim().endsWith("]")) {
            // 找到最后一个完整的对象
            int lastCompleteObject = findLastCompleteObject(json);
            if (lastCompleteObject > 0) {
                json = json.substring(0, lastCompleteObject) + "]";
            } else {
                return "[]";
            }
        }
        
        return json;
    }
    
    /**
     * 找到最后一个完整的JSON对象位置
     */
    private int findLastCompleteObject(String json) {
        int braceCount = 0;
        int lastCompletePos = -1;
        boolean inString = false;
        boolean escaped = false;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (escaped) {
                escaped = false;
                continue;
            }
            
            if (c == '\\') {
                escaped = true;
                continue;
            }
            
            if (c == '"') {
                inString = !inString;
                continue;
            }
            
            if (!inString) {
                if (c == '{') {
                    braceCount++;
                } else if (c == '}') {
                    braceCount--;
                    if (braceCount == 0) {
                        // 找到一个完整的对象
                        lastCompletePos = i + 1;
                    }
                }
            }
        }
        
        return lastCompletePos;
    }
    
    /**
     * 从不完整的响应中提取部分题目
     */
    private List<AiExtractedQuestion> extractPartialQuestions(String response) {
        List<AiExtractedQuestion> questions = new ArrayList<>();
        
        try {
            // 清理响应
            String cleanResponse = response.trim();
            if (cleanResponse.startsWith("```json")) {
                cleanResponse = cleanResponse.substring(7);
            }
            if (cleanResponse.endsWith("```")) {
                cleanResponse = cleanResponse.substring(0, cleanResponse.length() - 3);
            }
            cleanResponse = cleanResponse.trim();
            
            // 如果不是以[开头，直接返回空列表
            if (!cleanResponse.startsWith("[")) {
                return questions;
            }
            
            // 尝试逐个解析JSON对象
            int startPos = 1; // 跳过开头的[
            int braceCount = 0;
            boolean inString = false;
            boolean escaped = false;
            int objectStart = -1;
            
            for (int i = startPos; i < cleanResponse.length(); i++) {
                char c = cleanResponse.charAt(i);
                
                if (escaped) {
                    escaped = false;
                    continue;
                }
                
                if (c == '\\') {
                    escaped = true;
                    continue;
                }
                
                if (c == '"') {
                    inString = !inString;
                    continue;
                }
                
                if (!inString) {
                    if (c == '{') {
                        if (braceCount == 0) {
                            objectStart = i;
                        }
                        braceCount++;
                    } else if (c == '}') {
                        braceCount--;
                        if (braceCount == 0 && objectStart >= 0) {
                            // 找到一个完整的对象
                            String objectJson = cleanResponse.substring(objectStart, i + 1);
                            try {
                                AiExtractedQuestion question = objectMapper.readValue(objectJson, AiExtractedQuestion.class);
                                questions.add(question);
                            } catch (Exception e) {
                                log.warn("解析单个题目对象失败: {}", objectJson, e);
                            }
                            objectStart = -1;
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("提取部分题目失败", e);
        }
        
        return questions;
    }
    
    private Question convertToQuestion(AiExtractedQuestion aiQuestion, Long bankId, Long userId) {
        Question question = new Question();
        question.setBankId(bankId);
        question.setTitle(aiQuestion.getTitle());
        question.setContent(aiQuestion.getContent());
        question.setType(aiQuestion.getType());
        question.setAnswer(aiQuestion.getAnswer());
        question.setAnalysis(aiQuestion.getAnalysis());
        question.setDifficulty(aiQuestion.getDifficulty() != null ? aiQuestion.getDifficulty() : 1);
        question.setScore(aiQuestion.getScore() != null ? aiQuestion.getScore() : 5);
        question.setStatus(1);
        question.setCreateUserId(userId);
        
        // 处理选项
        if (aiQuestion.getOptions() != null && !aiQuestion.getOptions().isEmpty()) {
            try {
                question.setOptions(objectMapper.writeValueAsString(aiQuestion.getOptions()));
            } catch (JsonProcessingException e) {
                log.error("序列化选项失败", e);
                question.setOptions("[]");
            }
        }
        
        return question;
    }
    
    /**
     * 寻找最后一个完整题目的结束位置
     */
    private int findLastQuestionEnd(String content) {
        // 寻找答案标识符的最后出现位置
        String[] answerMarkers = {"答案：", "答案:", "正确答案：", "正确答案:", "解析："};
        int lastAnswerPos = -1;
        String foundMarker = null;
        
        for (String marker : answerMarkers) {
            int pos = content.lastIndexOf(marker);
            if (pos > lastAnswerPos) {
                lastAnswerPos = pos;
                foundMarker = marker;
            }
        }
        
        if (lastAnswerPos > 0) {
            log.debug("找到答案标识符 '{}' 在位置: {}", foundMarker, lastAnswerPos);
            
            // 从答案位置往后找到行尾或下一个题目开始
            int searchStart = lastAnswerPos + foundMarker.length();
            
            // 寻找下一行的开始
            int lineEnd = content.indexOf('\n', searchStart);
            if (lineEnd > 0) {
                // 检查下一行是否是新题目的开始（通常以数字开头）
                int nextLineStart = lineEnd + 1;
                if (nextLineStart < content.length()) {
                    String nextLine = content.substring(nextLineStart, Math.min(nextLineStart + 50, content.length()));
                    // 如果下一行以数字开头，说明是新题目
                    if (nextLine.matches("^\\s*\\d+[、.].*")) {
                        log.debug("找到下一题开始位置: {}", nextLineStart);
                        return nextLineStart;
                    }
                }
                // 否则返回当前行结束位置
                return lineEnd + 1;
            } else {
                // 没有找到换行符，返回内容结束位置
                return content.length();
            }
        }
        
        // 如果没有找到答案标识符，尝试寻找题目编号
        String[] questionMarkers = {"1、", "2、", "3、", "4、", "5、", "6、", "7、", "8、", "9、", "10、"};
        int lastQuestionPos = -1;
        
        for (String marker : questionMarkers) {
            int pos = content.lastIndexOf(marker);
            if (pos > lastQuestionPos) {
                lastQuestionPos = pos;
            }
        }
        
        if (lastQuestionPos > 0) {
            log.debug("找到题目编号在位置: {}", lastQuestionPos);
            return lastQuestionPos;
        }
        
        // 如果都找不到，返回内容的3/4位置，避免分割过小
        int fallbackPosition = content.length() * 3 / 4;
        log.debug("使用默认分割位置: {}", fallbackPosition);
        return fallbackPosition;
    }
    
    /**
     * 构建流式提取的提示词
     */
    private String buildStreamExtractionPrompt(String content, boolean isContinuation) {
        String continuationNote = isContinuation ? 
            "注意：这是内容的一部分，请提取其中完整的题目。如果开头或结尾有不完整的题目，请忽略。" : 
            "";
            
        return String.format("""
            请从以下内容中提取出所有完整的题目和答案，并按照指定的JSON格式返回。
            
            %s
            
            重要提示：
            1. 只提取完整的题目，忽略不完整的题目
            2. 宁可少提取几道题目，也要保证JSON格式完整有效
            3. 确保JSON格式完整，以]结尾
            4. 如果担心输出长度超限，请减少提取的题目数量
            
            内容：
            %s
            
            请严格按照以下JSON格式返回，不要包含任何其他文字：
            [
              {
                "title": "题目标题（如果没有明确标题，可以用题目内容的前几个字作为标题）",
                "content": "完整的题目内容",
                "type": 1,
                "options": ["选项A", "选项B", "选项C", "选项D"],
                "answer": "正确答案",
                "analysis": "题目解析（如果原文中有解析的话）",
                "difficulty": 1,
                "score": 5
              }
            ]
            
            提取规则：
            1. 只提取完整的题目，忽略不完整的题目
            2. 自动识别题目类型：
               - type=1: 单选题（只有一个正确答案的选择题）
               - type=2: 多选题（有多个正确答案的选择题）
               - type=3: 判断题（正确/错误类型的题目）
               - type=4: 问答题（开放性问题）
               - type=5: 填空题（需要填空的题目）
            
            3. 选项处理：
               - 单选题和多选题：提取所有选项到options数组
               - 判断题：options设为["正确", "错误"]
               - 问答题和填空题：不需要options字段或设为空数组
            
            4. 答案处理：
               - 选择题：填写正确选项的内容
               - 判断题：填写"正确"或"错误"
               - 问答题和填空题：填写标准答案
            
            5. 如果没有明确的题目解析，analysis字段可以为空字符串
            6. difficulty默认设为1（简单），score默认设为5分
            """, continuationNote, content);
    }
    
    @Override
    public SseEmitter extractQuestionsRealtimeStream(AiQuestionRequest request, Long userId) {
        log.info("创建SSE连接，用户ID: {}, 题库ID: {}, 内容长度: {}", 
            userId, request.getBankId(), request.getContent() != null ? request.getContent().length() : 0);
        
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30分钟超时
        
        // 设置超时和完成回调
        emitter.onTimeout(() -> {
            log.warn("SSE连接超时");
            emitter.complete();
        });
        
        emitter.onCompletion(() -> {
            log.info("SSE连接完成");
        });
        
        emitter.onError((ex) -> {
            log.error("SSE连接错误", ex);
        });
        
        // 异步处理，避免阻塞
        CompletableFuture.runAsync(() -> {
            try {
                log.info("开始异步处理实时提取");
                performRealtimeExtraction(request, userId, emitter);
            } catch (Exception e) {
                log.error("实时流式提取失败", e);
                try {
                    emitter.send(SseEmitter.event()
                        .name("error")
                        .data(objectMapper.writeValueAsString(AiStreamEvent.error("提取失败: " + e.getMessage()))));
                    emitter.complete();
                } catch (IOException ioException) {
                    log.error("发送错误事件失败", ioException);
                    emitter.completeWithError(ioException);
                }
            }
        });
        
        log.info("SSE Emitter已创建并返回");
        return emitter;
    }
    
    private void performRealtimeExtraction(AiQuestionRequest request, Long userId, SseEmitter emitter) throws IOException {
        String content = request.getContent();
        if (content == null || content.trim().isEmpty()) {
            emitter.send(SseEmitter.event()
                .name("error")
                .data(objectMapper.writeValueAsString(AiStreamEvent.error("输入内容不能为空"))));
            emitter.complete();
            return;
        }
        
        int totalLength = content.length();
        int currentPosition = 0;
        int totalQuestionCount = 0;
        List<AiExtractedQuestion> allQuestions = new ArrayList<>();
        
        log.info("开始实时流式提取，总长度: {}", totalLength);
        
        try {
            // 发送开始事件
            emitter.send(SseEmitter.event()
                .name("progress")
                .data(objectMapper.writeValueAsString(AiStreamEvent.progress("开始AI智能提取...", 0, totalLength))));
            
            // 分段处理
            int maxChunkSize = 3000; // 减小每段大小，提高响应速度
            
            while (currentPosition < totalLength) {
                try {
                    int endPosition = Math.min(currentPosition + maxChunkSize, totalLength);
                    
                    // 寻找合适的分割点
                    if (endPosition < totalLength) {
                        String chunk = content.substring(currentPosition, endPosition);
                        int lastQuestionEnd = findLastQuestionEnd(chunk);
                        if (lastQuestionEnd > 0 && lastQuestionEnd < chunk.length()) {
                            endPosition = currentPosition + lastQuestionEnd;
                        } else {
                            endPosition = currentPosition + (chunk.length() * 3 / 4);
                        }
                    }
                    
                    String contentToProcess = content.substring(currentPosition, endPosition);
                    
                    // 发送进度事件
                    emitter.send(SseEmitter.event()
                        .name("progress")
                        .data(objectMapper.writeValueAsString(AiStreamEvent.progress(
                            String.format("正在处理第 %d-%d 字符...", currentPosition + 1, endPosition), 
                            endPosition, 
                            totalLength))));
                    
                    log.info("处理段落: {} - {}", currentPosition, endPosition);
                    
                    // 调用AI提取
                    String prompt = buildRealtimeExtractionPrompt(contentToProcess, currentPosition > 0);
                    String aiResponse = callGeminiApiForRealtime(prompt);
                    List<AiExtractedQuestion> questions = parseAiResponseForRealtime(aiResponse);
                    
                    log.info("本段提取到 {} 道题目", questions.size());
                    
                    // 逐个发送题目
                    for (AiExtractedQuestion question : questions) {
                        totalQuestionCount++;
                        allQuestions.add(question);
                        
                        log.info("发送第 {} 道题目: {}", totalQuestionCount, question.getTitle());
                        
                        // 发送题目事件
                        emitter.send(SseEmitter.event()
                            .name("question")
                            .data(objectMapper.writeValueAsString(AiStreamEvent.question(question, totalQuestionCount, endPosition, totalLength))));
                        
                        // 稍微延迟，让用户能看到实时效果
                        Thread.sleep(300);
                    }
                    
                    currentPosition = endPosition;
                    
                    // 如果这段没有提取到题目，发送进度更新
                    if (questions.isEmpty()) {
                        emitter.send(SseEmitter.event()
                            .name("progress")
                            .data(objectMapper.writeValueAsString(AiStreamEvent.progress("本段未发现完整题目，继续处理...", endPosition, totalLength))));
                    }
                    
                    // 发送段落完成进度
                    emitter.send(SseEmitter.event()
                        .name("progress")
                        .data(objectMapper.writeValueAsString(AiStreamEvent.progress(
                            String.format("已完成 %d/%d 字符的处理", endPosition, totalLength), 
                            endPosition, 
                            totalLength))));
                    
                } catch (Exception e) {
                    log.error("处理段落失败，位置: {}", currentPosition, e);
                    emitter.send(SseEmitter.event()
                        .name("error")
                        .data(objectMapper.writeValueAsString(AiStreamEvent.error("处理失败: " + e.getMessage()))));
                    break;
                }
            }
            
            // 发送完成事件
            emitter.send(SseEmitter.event()
                .name("complete")
                .data(objectMapper.writeValueAsString(AiStreamEvent.complete(totalQuestionCount, 
                    String.format("提取完成！共提取到 %d 道题目", totalQuestionCount)))));
            
            log.info("实时流式提取完成，共提取 {} 道题目", totalQuestionCount);
            
        } catch (Exception e) {
            log.error("实时流式提取过程中发生错误", e);
            emitter.send(SseEmitter.event()
                .name("error")
                .data(objectMapper.writeValueAsString(AiStreamEvent.error("提取过程中发生错误: " + e.getMessage()))));
        } finally {
            emitter.complete();
        }
    }
    
    private String buildRealtimeExtractionPrompt(String content, boolean isContinuation) {
        String continuationNote = isContinuation ? 
            "注意：这是内容的一部分，请提取其中完整的题目。如果开头或结尾有不完整的题目，请忽略。" : 
            "";
            
        return String.format("""
            请从以下内容中提取出所有完整的题目和答案，并按照指定的JSON格式返回。
            
            %s
            
            重要提示：
            1. 只提取完整的题目，忽略不完整的题目
            2. 确保JSON格式完整有效
            3. 每道题目都要包含完整信息
            4. 优先保证质量而不是数量
            
            内容：
            %s
            
            请严格按照以下JSON格式返回，不要包含任何其他文字：
            [
              {
                "title": "题目标题",
                "content": "完整的题目内容",
                "type": 1,
                "options": ["选项A", "选项B", "选项C", "选项D"],
                "answer": "正确答案",
                "analysis": "题目解析",
                "difficulty": 1,
                "score": 5
              }
            ]
            
            题目类型说明：
            - type=1: 单选题
            - type=2: 多选题  
            - type=3: 判断题
            - type=4: 问答题
            - type=5: 填空题
            """, continuationNote, content);
    }
    
    private String callGeminiApiForRealtime(String prompt) {
        try {
            GeminiRequest request = new GeminiRequest();
            request.setModel(MODEL);
            request.setTemperature(0.3);
            request.setMax_tokens(4096); // 减少token数量，提高响应速度
            request.setMessages(Arrays.asList(
                new GeminiRequest.Message("user", prompt)
            ));
            
            Mono<GeminiResponse> responseMono = webClient.post()
                    .uri(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .timeout(Duration.ofMinutes(3)); // 缩短超时时间
            
            GeminiResponse response = responseMono.block(Duration.ofMinutes(4));
            
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                String content = response.getChoices().get(0).getMessage().getContent();
                if (content == null || content.trim().isEmpty()) {
                    throw new RuntimeException("AI返回内容为空");
                }
                return content;
            } else {
                throw new RuntimeException("AI API返回空响应");
            }
        } catch (Exception e) {
            log.error("调用Gemini API失败", e);
            throw new RuntimeException("AI服务调用失败: " + e.getMessage());
        }
    }
    
    private List<AiExtractedQuestion> parseAiResponseForRealtime(String aiResponse) {
        try {
            // 清理响应内容
            String cleanResponse = aiResponse.trim();
            if (cleanResponse.startsWith("```json")) {
                cleanResponse = cleanResponse.substring(7);
            }
            if (cleanResponse.endsWith("```")) {
                cleanResponse = cleanResponse.substring(0, cleanResponse.length() - 3);
            }
            cleanResponse = cleanResponse.trim();
            
            // 尝试解析JSON
            List<AiExtractedQuestion> questions = objectMapper.readValue(
                cleanResponse, 
                new TypeReference<List<AiExtractedQuestion>>() {}
            );
            
            // 验证题目完整性
            List<AiExtractedQuestion> validQuestions = new ArrayList<>();
            for (AiExtractedQuestion question : questions) {
                if (isValidQuestion(question)) {
                    validQuestions.add(question);
                }
            }
            
            return validQuestions;
            
        } catch (Exception e) {
            log.warn("解析AI响应失败，尝试修复: {}", e.getMessage());
            // 尝试修复和部分解析
            return extractPartialQuestions(aiResponse);
        }
    }
    
    private boolean isValidQuestion(AiExtractedQuestion question) {
        return question != null 
            && question.getContent() != null 
            && !question.getContent().trim().isEmpty()
            && question.getAnswer() != null 
            && !question.getAnswer().trim().isEmpty();
    }
} 