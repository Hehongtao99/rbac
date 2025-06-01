<template>
  <div class="ai-question-extract">
    <div class="header">
      <h2>AI智能题目提取</h2>
      <el-tag type="success" size="large">支持实时流式显示</el-tag>
    </div>

    <el-card class="extract-card">
      <template #header>
        <div class="card-header">
          <span>输入包含题目和答案的内容，AI自动提取并录入题库</span>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="选择题库" prop="bankId">
          <el-select v-model="form.bankId" placeholder="请选择题库" style="width: 100%">
            <el-option 
              v-for="bank in questionBanks" 
              :key="bank.id" 
              :label="bank.name" 
              :value="bank.id" 
            />
          </el-select>
        </el-form-item>

        <el-form-item label="题目内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="12"
            placeholder="请输入包含题目和答案的内容，例如：&#10;&#10;1. 以下哪个是Java的关键字？&#10;A. class&#10;B. Class&#10;C. CLASS&#10;D. clazz&#10;答案：A&#10;&#10;2. Java是面向对象的编程语言吗？&#10;答案：是&#10;&#10;AI会自动识别题目类型、选项和答案..."
            maxlength="50000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleRealtimeStreamExtract" :loading="realtimeLoading" size="large">
            <el-icon><VideoPlay /></el-icon>
            <span v-if="!realtimeLoading">实时流式提取</span>
            <span v-else>AI正在实时提取中...</span>
          </el-button>
          <el-button type="success" @click="handleStreamExtract" :loading="streamLoading">
            <span v-if="!streamLoading">分段智能提取</span>
            <span v-else>AI正在处理中，请耐心等待...</span>
          </el-button>
          <el-button type="info" @click="handleExtractAndAdd" :loading="extractLoading" v-if="!isStreaming && !isRealtimeStreaming">
            <span v-if="!extractLoading">直接提取并添加到题库</span>
            <span v-else>AI正在处理中，请耐心等待...</span>
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 实时流式解析结果显示区域 -->
    <el-card v-if="isRealtimeStreaming || realtimeQuestions.length > 0" class="realtime-stream-results">
      <template #header>
        <div class="stream-header">
          <span>
            <el-icon><VideoPlay /></el-icon>
            实时提取结果 (已提取 {{ realtimeCurrentCount }} 道题目)
          </span>
          <div class="stream-actions">
            <el-button 
              v-if="realtimeQuestions.length > 0 && !realtimeLoading" 
              type="success" 
              @click="handleAddAllRealtimeQuestions" 
              :loading="addAllLoading"
              size="small"
            >
              添加所有题目到题库
            </el-button>
          </div>
        </div>
      </template>

      <!-- 实时进度指示器 -->
      <div v-if="isRealtimeStreaming" class="realtime-progress-info">
        <el-progress 
          :percentage="realtimeProgressPercentage" 
          :status="realtimeLoading ? 'active' : 'success'"
          :stroke-width="8"
        />
        <p class="progress-text">
          {{ realtimeProgress }}
        </p>
        <div v-if="realtimeLoading" class="loading-animation">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>AI正在智能分析中，每提取到一个题目就会立即显示...</span>
        </div>
      </div>

      <!-- 实时题目列表 -->
      <div v-if="realtimeQuestions.length > 0" class="realtime-questions-list">
        <div 
          v-for="(question, index) in realtimeQuestions" 
          :key="index" 
          class="realtime-question-item"
        >
          <el-card class="question-card" shadow="hover">
            <template #header>
              <div class="question-header">
                <span class="question-number">第{{ index + 1 }}题</span>
                <el-tag :type="getTypeTagType(question.type)" size="small">{{ getTypeText(question.type) }}</el-tag>
                <el-tag :type="getDifficultyTagType(question.difficulty)" size="small">{{ getDifficultyText(question.difficulty) }}</el-tag>
                <span class="question-score">{{ question.score }}分</span>
                <el-button 
                  type="danger" 
                  size="small" 
                  @click="removeRealtimeQuestion(index)"
                  style="margin-left: auto;"
                >
                  删除
                </el-button>
              </div>
            </template>

            <div class="question-content">
              <h4>{{ question.title }}</h4>
              <p class="content-text">{{ question.content }}</p>

              <!-- 选择题选项 -->
              <div v-if="question.options && question.options.length > 0" class="options">
                <div v-for="(option, optIndex) in question.options" :key="optIndex" class="option-item">
                  <span class="option-label">{{ String.fromCharCode(65 + optIndex) }}.</span>
                  <span>{{ option }}</span>
                </div>
              </div>

              <div class="answer-section">
                <p><strong>正确答案：</strong>{{ question.answer }}</p>
                <p v-if="question.analysis"><strong>题目解析：</strong>{{ question.analysis }}</p>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!isRealtimeStreaming && realtimeQuestions.length === 0" class="no-questions">
        <el-empty description="暂未提取到题目" />
      </div>
    </el-card>

    <!-- 分段流式解析结果显示区域 -->
    <el-card v-if="isStreaming || extractedQuestions.length > 0" class="stream-results">
      <template #header>
        <div class="stream-header">
          <span>分段解析结果 (已提取 {{ extractedQuestions.length }} 道题目)</span>
          <div class="stream-actions">
            <el-button 
              v-if="hasMoreContent" 
              type="primary" 
              @click="handleContinueExtract" 
              :loading="streamLoading"
              size="small"
            >
              继续解析
            </el-button>
            <!-- 调试：强制显示继续解析按钮 -->
            <el-button 
              v-if="!hasMoreContent && extractedQuestions.length > 0 && processedLength < totalLength" 
              type="warning" 
              @click="handleContinueExtract" 
              :loading="streamLoading"
              size="small"
            >
              强制继续解析
            </el-button>
            <el-button 
              v-if="extractedQuestions.length > 0" 
              type="success" 
              @click="handleAddAllQuestions" 
              :loading="addAllLoading"
              size="small"
            >
              添加所有题目到题库
            </el-button>
          </div>
        </div>
      </template>

      <!-- 进度指示器 -->
      <div v-if="isStreaming" class="progress-info">
        <el-progress 
          :percentage="progressPercentage" 
          :status="streamLoading ? 'active' : 'success'"
          :stroke-width="8"
        />
        <p class="progress-text">
          已处理：{{ processedLength }} / {{ totalLength }} 字符
          <span v-if="hasMoreContent">（还有更多内容待处理）</span>
        </p>
        
        <!-- 调试信息 -->
        <el-collapse v-if="extractedQuestions.length > 0" class="debug-info">
          <el-collapse-item title="调试信息" name="debug">
            <div class="debug-content">
              <p><strong>当前位置：</strong>{{ currentPosition }}</p>
              <p><strong>总长度：</strong>{{ totalLength }}</p>
              <p><strong>已处理长度：</strong>{{ processedLength }}</p>
              <p><strong>还有更多内容：</strong>{{ hasMoreContent ? '是' : '否' }}</p>
              <p><strong>正在流式解析：</strong>{{ isStreaming ? '是' : '否' }}</p>
              <p><strong>已提取题目数：</strong>{{ extractedQuestions.length }}</p>
              <p><strong>进度百分比：</strong>{{ progressPercentage }}%</p>
              <p><strong>剩余字符：</strong>{{ totalLength - processedLength }}</p>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>

      <!-- 非流式状态下也显示调试信息 -->
      <div v-if="!isStreaming && extractedQuestions.length > 0" class="static-debug-info">
        <el-alert 
          :title="`调试信息: hasMoreContent=${hasMoreContent}, processedLength=${processedLength}, totalLength=${totalLength}`"
          type="info"
          :closable="false"
          show-icon
        />
      </div>

      <!-- 题目列表 -->
      <div class="questions-list">
        <div v-for="(question, index) in extractedQuestions" :key="index" class="question-item">
          <el-card class="question-card">
            <template #header>
              <div class="question-header">
                <span class="question-number">第{{ index + 1 }}题</span>
                <el-tag :type="getTypeTagType(question.type)">{{ getTypeText(question.type) }}</el-tag>
                <el-tag :type="getDifficultyTagType(question.difficulty)">{{ getDifficultyText(question.difficulty) }}</el-tag>
                <span class="question-score">{{ question.score }}分</span>
                <el-button 
                  type="danger" 
                  size="small" 
                  @click="removeQuestion(index)"
                  style="margin-left: auto;"
                >
                  删除
                </el-button>
              </div>
            </template>

            <div class="question-content">
              <h4>{{ question.title }}</h4>
              <p class="content-text">{{ question.content }}</p>

              <!-- 选择题选项 -->
              <div v-if="question.options && question.options.length > 0" class="options">
                <div v-for="(option, optIndex) in question.options" :key="optIndex" class="option-item">
                  <span class="option-label">{{ String.fromCharCode(65 + optIndex) }}.</span>
                  <span>{{ option }}</span>
                </div>
              </div>

              <div class="answer-section">
                <p><strong>正确答案：</strong>{{ question.answer }}</p>
                <p v-if="question.analysis"><strong>题目解析：</strong>{{ question.analysis }}</p>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!isStreaming && extractedQuestions.length === 0" class="no-questions">
        <el-empty description="暂未提取到题目" />
      </div>
    </el-card>

    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="AI提取题目预览"
      width="80%"
      :close-on-click-modal="false"
    >
      <div v-if="previewQuestions.length > 0" class="preview-content">
        <div class="preview-summary">
          <el-alert
            :title="`成功提取到 ${previewQuestions.length} 道题目`"
            type="success"
            :closable="false"
            show-icon
          />
        </div>
        
        <div v-for="(question, index) in previewQuestions" :key="index" class="question-item">
          <el-card class="question-card">
            <template #header>
              <div class="question-header">
                <span class="question-number">第{{ index + 1 }}题</span>
                <el-tag :type="getTypeTagType(question.type)">{{ getTypeText(question.type) }}</el-tag>
                <el-tag :type="getDifficultyTagType(question.difficulty)">{{ getDifficultyText(question.difficulty) }}</el-tag>
                <span class="question-score">{{ question.score }}分</span>
              </div>
            </template>

            <div class="question-content">
              <h4>{{ question.title }}</h4>
              <p class="content-text">{{ question.content }}</p>

              <!-- 选择题选项 -->
              <div v-if="question.options && question.options.length > 0" class="options">
                <div v-for="(option, optIndex) in question.options" :key="optIndex" class="option-item">
                  <span class="option-label">{{ String.fromCharCode(65 + optIndex) }}.</span>
                  <span>{{ option }}</span>
                </div>
              </div>

              <div class="answer-section">
                <p><strong>正确答案：</strong>{{ question.answer }}</p>
                <p v-if="question.analysis"><strong>题目解析：</strong>{{ question.analysis }}</p>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <div v-else class="no-questions">
        <el-empty description="未能提取到题目，请检查输入内容格式" />
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="previewDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleConfirmAdd" :loading="confirmLoading" :disabled="previewQuestions.length === 0">
            确认添加到题库
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { VideoPlay, Loading, Delete } from '@element-plus/icons-vue'
import { getQuestionBankList } from '@/api/questionBank'
import { extractAndAddQuestions, extractQuestionsPreview, extractQuestionsStream, addStreamQuestions, extractQuestionsRealtimeStream } from '@/api/aiQuestion'

// 响应式数据
const formRef = ref()
const questionBanks = ref([])
const previewDialogVisible = ref(false)
const previewQuestions = ref([])
const previewLoading = ref(false)
const extractLoading = ref(false)
const confirmLoading = ref(false)

// 流式解析相关
const isStreaming = ref(false)
const streamLoading = ref(false)
const addAllLoading = ref(false)
const extractedQuestions = ref([])
const hasMoreContent = ref(false)
const currentPosition = ref(0)
const totalLength = ref(0)
const processedLength = ref(0)

// 实时流式解析相关
const isRealtimeStreaming = ref(false)
const realtimeLoading = ref(false)
const realtimeQuestions = ref([])
const realtimeProgress = ref('')
const realtimeCurrentCount = ref(0)

// 表单数据
const form = reactive({
  bankId: null,
  content: ''
})

// 表单验证规则
const rules = {
  bankId: [
    { required: true, message: '请选择题库', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入题目内容', trigger: 'blur' },
    { min: 20, message: '内容至少20个字符', trigger: 'blur' }
  ]
}

// 计算进度百分比
const progressPercentage = computed(() => {
  if (totalLength.value === 0) return 0
  return Math.round((processedLength.value / totalLength.value) * 100)
})

// 实时流式进度百分比
const realtimeProgressPercentage = computed(() => {
  if (totalLength.value === 0) return 0
  return Math.round((processedLength.value / totalLength.value) * 100)
})

// 获取题库列表
const fetchQuestionBanks = async () => {
  try {
    const response = await getQuestionBankList({ current: 1, size: 1000 })
    questionBanks.value = response.data.records
  } catch (error) {
    ElMessage.error('获取题库列表失败')
  }
}

// 开始流式提取
const handleStreamExtract = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 检查内容长度
    const contentLength = form.content.length
    console.log('输入内容长度:', contentLength)
    
    if (contentLength < 2000) {
      ElMessage.warning('内容较短，可能不会触发分段解析。建议内容长度超过4000字符以测试流式解析功能。')
    }
    
    // 重置状态
    isStreaming.value = true
    streamLoading.value = true
    extractedQuestions.value = []
    currentPosition.value = 0
    totalLength.value = contentLength
    processedLength.value = 0
    hasMoreContent.value = true
    
    ElMessage.info('开始智能提取，AI将分段处理您的内容...')
    
    await performStreamExtraction()
    
  } catch (error) {
    console.error('流式提取失败:', error)
    handleStreamError(error)
  } finally {
    streamLoading.value = false
  }
}

// 执行流式提取
const performStreamExtraction = async () => {
  try {
    console.log('发送流式提取请求，参数:', {
      bankId: form.bankId,
      content: form.content.substring(0, 100) + '...',
      contentLength: form.content.length,
      startPosition: currentPosition.value
    })
    
    const response = await extractQuestionsStream({
      bankId: form.bankId,
      content: form.content,
      startPosition: currentPosition.value
    })
    
    console.log('收到流式解析响应:', response)
    
    const { questions, hasMore, nextPosition, processedContent } = response.data
    
    console.log('流式解析响应详情:', {
      questionsCount: questions ? questions.length : 0,
      hasMore,
      nextPosition,
      currentPosition: currentPosition.value,
      totalLength: totalLength.value,
      processedContentLength: processedContent ? processedContent.length : 0
    })
    
    // 添加新提取的题目
    if (questions && questions.length > 0) {
      extractedQuestions.value.push(...questions)
      ElMessage.success(`本次提取到 ${questions.length} 道题目`)
    } else {
      ElMessage.warning('本次未提取到题目，可能内容格式不符合要求')
    }
    
    // 更新状态 - 关键修复
    hasMoreContent.value = hasMore === true  // 确保布尔值
    currentPosition.value = nextPosition || currentPosition.value
    processedLength.value = processedContent ? processedContent.length : nextPosition || currentPosition.value
    
    console.log('更新后的状态:', {
      hasMoreContent: hasMoreContent.value,
      currentPosition: currentPosition.value,
      processedLength: processedLength.value,
      isStreaming: isStreaming.value
    })
    
    // 重要：只有在没有更多内容时才停止流式状态
    if (!hasMore) {
      isStreaming.value = false
      ElMessage.success(`提取完成！共提取到 ${extractedQuestions.value.length} 道题目`)
    } else {
      // 有更多内容时，保持流式状态但停止loading
      ElMessage.info(`已处理 ${processedLength.value}/${totalLength.value} 字符，点击"继续解析"处理剩余内容`)
    }
    
  } catch (error) {
    // 发生错误时也要停止流式状态
    isStreaming.value = false
    hasMoreContent.value = false
    throw error
  }
}

// 继续提取
const handleContinueExtract = async () => {
  if (!hasMoreContent.value) return
  
  streamLoading.value = true
  try {
    await performStreamExtraction()
  } catch (error) {
    console.error('继续提取失败:', error)
    handleStreamError(error)
  } finally {
    streamLoading.value = false
  }
}

// 处理流式提取错误
const handleStreamError = (error) => {
  if (error.response && error.response.data && error.response.data.message) {
    const errorMsg = error.response.data.message
    if (errorMsg.includes('超时') || errorMsg.includes('timeout')) {
      ElMessage.error('AI服务响应超时，请点击"继续解析"重试')
    } else if (errorMsg.includes('连接') || errorMsg.includes('connection')) {
      ElMessage.error('网络连接异常，请检查网络后重试')
    } else if (errorMsg.includes('解析') || errorMsg.includes('JSON') || errorMsg.includes('token')) {
      ElMessage.error('内容过长导致解析失败，建议使用"开始智能提取"进行分段处理')
    } else {
      ElMessage.error(errorMsg)
    }
  } else if (error.message) {
    if (error.message.includes('解析') || error.message.includes('JSON') || error.message.includes('token')) {
      ElMessage.error('内容过长导致解析失败，建议使用"开始智能提取"进行分段处理')
    } else {
      ElMessage.error(error.message)
    }
  } else {
    ElMessage.error('提取失败，请稍后重试')
  }
}

// 添加所有题目到题库
const handleAddAllQuestions = async () => {
  if (extractedQuestions.value.length === 0) {
    ElMessage.warning('没有可添加的题目')
    return
  }
  
  try {
    const result = await ElMessageBox.confirm(
      `确定要将 ${extractedQuestions.value.length} 道题目添加到题库吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    if (result === 'confirm') {
      addAllLoading.value = true
      
      const response = await addStreamQuestions({
        bankId: form.bankId,
        processedQuestions: extractedQuestions.value
      })
      
      const questionIds = response.data
      ElMessage.success(`成功添加了 ${questionIds.length} 道题目到题库`)
      
      // 重置状态
      handleReset()
    }
  } catch (error) {
    console.error('添加题目失败:', error)
    if (error !== 'cancel') {
      handleStreamError(error)
    }
  } finally {
    addAllLoading.value = false
  }
}

// 删除单个题目
const removeQuestion = (index) => {
  extractedQuestions.value.splice(index, 1)
  ElMessage.success('题目已删除')
}

// 预览提取结果
const handlePreview = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    previewLoading.value = true
    
    // 显示处理提示
    ElMessage.info('AI正在分析您的内容，请耐心等待...')
    
    const response = await extractQuestionsPreview(form)
    previewQuestions.value = response.data
    previewDialogVisible.value = true
    
    if (response.data.length > 0) {
      ElMessage.success(`成功提取到${response.data.length}道题目`)
    } else {
      ElMessage.warning('未能提取到题目，请检查输入内容格式')
    }
  } catch (error) {
    console.error('提取题目失败:', error)
    handleStreamError(error)
  } finally {
    previewLoading.value = false
  }
}

// 直接提取并添加
const handleExtractAndAdd = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    const result = await ElMessageBox.confirm(
      '确定要提取题目并直接添加到题库吗？',
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    if (result === 'confirm') {
      extractLoading.value = true
      
      // 显示处理提示
      ElMessage.info('AI正在分析您的内容，请耐心等待...')
      
      const response = await extractAndAddQuestions(form)
      const questionIds = response.data
      
      ElMessage.success(`成功提取并添加了${questionIds.length}道题目`)
      handleReset()
    }
  } catch (error) {
    console.error('提取并添加题目失败:', error)
    if (error !== 'cancel') {
      handleStreamError(error)
    }
  } finally {
    extractLoading.value = false
  }
}

// 确认添加预览的题目
const handleConfirmAdd = async () => {
  try {
    confirmLoading.value = true
    
    // 直接使用已提取的题目数据，不再调用AI
    const response = await addStreamQuestions({
      bankId: form.bankId,
      processedQuestions: previewQuestions.value
    })
    
    const questionIds = response.data
    ElMessage.success(`成功添加了${questionIds.length}道题目到题库`)
    previewDialogVisible.value = false
    handleReset()
  } catch (error) {
    console.error('添加题目失败:', error)
    handleStreamError(error)
  } finally {
    confirmLoading.value = false
  }
}

// 重置表单
const handleReset = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  form.bankId = null
  form.content = ''
  previewQuestions.value = []
  
  // 重置流式状态
  isStreaming.value = false
  extractedQuestions.value = []
  hasMoreContent.value = false
  currentPosition.value = 0
  totalLength.value = 0
  processedLength.value = 0
  
  // 重置实时流式状态
  isRealtimeStreaming.value = false
  realtimeQuestions.value = []
  realtimeCurrentCount.value = 0
  realtimeProgress.value = ''
}

// 获取题目类型文本
const getTypeText = (type) => {
  const typeMap = {
    1: '单选题',
    2: '多选题',
    3: '判断题',
    4: '问答题',
    5: '填空题'
  }
  return typeMap[type] || '未知'
}

// 获取题目类型标签颜色
const getTypeTagType = (type) => {
  const typeMap = {
    1: 'primary',
    2: 'success',
    3: 'warning',
    4: 'info',
    5: 'danger'
  }
  return typeMap[type] || ''
}

// 获取难度文本
const getDifficultyText = (difficulty) => {
  const difficultyMap = {
    1: '简单',
    2: '中等',
    3: '困难'
  }
  return difficultyMap[difficulty] || '未知'
}

// 获取难度标签颜色
const getDifficultyTagType = (difficulty) => {
  const difficultyMap = {
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return difficultyMap[difficulty] || ''
}

// 开始实时流式提取
const handleRealtimeStreamExtract = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 重置状态
    isRealtimeStreaming.value = true
    realtimeLoading.value = true
    realtimeQuestions.value = []
    realtimeCurrentCount.value = 0
    realtimeProgress.value = '准备开始AI智能提取...'
    totalLength.value = form.content.length
    processedLength.value = 0
    
    console.log('开始实时流式提取，内容长度:', totalLength.value)
    ElMessage.info('开始实时AI提取，每提取到一个题目就会立即显示')
    
    await extractQuestionsRealtimeStream(
      {
        bankId: form.bankId,
        content: form.content
      },
      // onMessage 回调
      (event) => {
        console.log('收到SSE事件:', event)
        handleRealtimeStreamEvent(event)
      },
      // onError 回调
      (error) => {
        console.error('实时提取错误:', error)
        ElMessage.error('实时提取失败: ' + error)
        isRealtimeStreaming.value = false
        realtimeLoading.value = false
      },
      // onComplete 回调
      () => {
        console.log('实时提取完成')
        realtimeLoading.value = false
        isRealtimeStreaming.value = false
        ElMessage.success(`实时提取完成！共提取到 ${realtimeCurrentCount.value} 道题目`)
      }
    )
    
  } catch (error) {
    console.error('实时流式提取失败:', error)
    ElMessage.error('实时提取失败: ' + (error.message || error))
    isRealtimeStreaming.value = false
    realtimeLoading.value = false
  }
}

// 处理实时流式事件
const handleRealtimeStreamEvent = (event) => {
  console.log('处理实时事件:', event.type, event)
  
  switch (event.type) {
    case 'question':
      // 新题目提取完成
      console.log('收到新题目:', event.question)
      realtimeQuestions.value.push(event.question)
      realtimeCurrentCount.value = event.currentCount
      processedLength.value = event.totalProcessed
      realtimeProgress.value = `已提取第 ${event.currentCount} 道题目: ${event.question.title || event.question.content.substring(0, 20)}...`
      
      // 显示题目提取成功的消息
      ElMessage.success(`提取到第 ${event.currentCount} 道题目`)
      
      // 滚动到最新题目
      setTimeout(() => {
        const questionElements = document.querySelectorAll('.realtime-question-item')
        if (questionElements.length > 0) {
          questionElements[questionElements.length - 1].scrollIntoView({ 
            behavior: 'smooth', 
            block: 'nearest' 
          })
        }
      }, 100)
      break
      
    case 'progress':
      // 进度更新
      console.log('进度更新:', event.message)
      realtimeProgress.value = event.message
      if (event.totalProcessed) {
        processedLength.value = event.totalProcessed
      }
      break
      
    case 'complete':
      // 提取完成
      console.log('提取完成:', event.message)
      isRealtimeStreaming.value = false
      realtimeProgress.value = event.message
      break
      
    case 'error':
      // 错误处理
      console.error('提取错误:', event.error)
      ElMessage.error('提取过程中出现错误: ' + event.error)
      isRealtimeStreaming.value = false
      realtimeLoading.value = false
      break
      
    default:
      console.warn('未知事件类型:', event.type)
  }
}

// 添加所有实时提取的题目到题库
const handleAddAllRealtimeQuestions = async () => {
  if (realtimeQuestions.value.length === 0) {
    ElMessage.warning('没有可添加的题目')
    return
  }
  
  try {
    const result = await ElMessageBox.confirm(
      `确定要将 ${realtimeQuestions.value.length} 道题目添加到题库吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    if (result === 'confirm') {
      addAllLoading.value = true
      
      const response = await addStreamQuestions({
        bankId: form.bankId,
        processedQuestions: realtimeQuestions.value
      })
      
      const questionIds = response.data
      ElMessage.success(`成功添加了 ${questionIds.length} 道题目到题库`)
      
      // 重置状态
      handleReset()
    }
  } catch (error) {
    console.error('添加题目失败:', error)
    if (error !== 'cancel') {
      ElMessage.error('添加题目失败: ' + error.message)
    }
  } finally {
    addAllLoading.value = false
  }
}

// 删除实时提取的单个题目
const removeRealtimeQuestion = (index) => {
  realtimeQuestions.value.splice(index, 1)
  realtimeCurrentCount.value = realtimeQuestions.value.length
  ElMessage.success('题目已删除')
}

// 页面加载时获取数据
onMounted(() => {
  fetchQuestionBanks()
})
</script>

<style scoped>
.ai-question-extract {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

.header h2 {
  color: #303133;
  margin: 0;
}

.extract-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stream-results {
  margin-bottom: 20px;
}

.stream-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stream-actions {
  display: flex;
  gap: 10px;
}

.progress-info {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.progress-text {
  margin-top: 10px;
  margin-bottom: 0;
  color: #606266;
  font-size: 14px;
}

.questions-list {
  max-height: 600px;
  overflow-y: auto;
}

.preview-content {
  max-height: 600px;
  overflow-y: auto;
}

.preview-summary {
  margin-bottom: 20px;
}

.question-item {
  margin-bottom: 20px;
}

.question-card {
  border: 1px solid #e4e7ed;
}

.question-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.question-number {
  font-weight: bold;
  color: #409eff;
}

.question-score {
  color: #f56c6c;
  font-weight: bold;
}

.question-content h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.content-text {
  margin: 10px 0;
  color: #606266;
  line-height: 1.6;
}

.options {
  margin: 15px 0;
}

.option-item {
  margin: 8px 0;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
}

.option-label {
  font-weight: bold;
  margin-right: 8px;
  color: #409eff;
  min-width: 20px;
}

.answer-section {
  margin-top: 15px;
  padding: 15px;
  background-color: #f0f9ff;
  border-radius: 4px;
  border-left: 4px solid #409eff;
}

.answer-section p {
  margin: 5px 0;
  color: #303133;
}

.no-questions {
  text-align: center;
  padding: 40px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.debug-info {
  margin-top: 10px;
}

.debug-content {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.realtime-stream-results {
  margin-bottom: 20px;
  border: 2px solid #67c23a;
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.15);
}

.realtime-stream-results .el-card__header {
  background: linear-gradient(135deg, #67c23a, #85ce61);
  color: white;
}

.realtime-progress-info {
  margin-bottom: 20px;
  padding: 15px;
  background: linear-gradient(135deg, #f0f9ff, #e0f2fe);
  border-radius: 8px;
  border-left: 4px solid #67c23a;
}

.loading-animation {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 15px;
  padding: 10px;
  background-color: rgba(103, 194, 58, 0.1);
  border-radius: 6px;
  font-size: 14px;
  color: #67c23a;
}

.loading-animation .el-icon {
  margin-right: 8px;
  font-size: 18px;
}

.realtime-questions-list {
  max-height: 600px;
  overflow-y: auto;
  padding-right: 10px;
}

.realtime-questions-list::-webkit-scrollbar {
  width: 6px;
}

.realtime-questions-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.realtime-questions-list::-webkit-scrollbar-thumb {
  background: #67c23a;
  border-radius: 3px;
}

.realtime-question-item {
  margin-bottom: 20px;
  animation: slideInUp 0.5s ease-out;
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.realtime-question-item .question-card {
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.realtime-question-item .question-card:hover {
  border-color: #67c23a;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(103, 194, 58, 0.15);
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  color: #303133;
}

.progress-text {
  margin: 10px 0;
  font-size: 14px;
  color: #606266;
  text-align: center;
}

.stream-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stream-header span {
  display: flex;
  align-items: center;
  font-weight: 600;
}

.stream-header .el-icon {
  margin-right: 8px;
  color: #67c23a;
}
</style> 