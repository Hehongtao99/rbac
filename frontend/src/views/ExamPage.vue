<template>
  <div class="exam-page">
    <!-- 考试头部信息 -->
    <div class="exam-header">
      <div class="exam-info">
        <h2>{{ examData?.title }}</h2>
        <div class="exam-meta">
          <span>题库：{{ examData?.bankName }}</span>
          <span>总分：{{ examData?.totalScore }}分</span>
          <span>题目数量：{{ examData?.questions?.length }}道</span>
        </div>
      </div>
      <div class="header-actions">
        <el-button @click="goToHome" type="info" size="small" style="margin-right: 15px;">
          <el-icon><House /></el-icon>
          返回首页
        </el-button>
        <div class="exam-timer">
          <el-statistic 
            :value="timeLeft" 
            format="mm:ss"
            title="剩余时间"
            :value-style="{ color: timeLeft < 300000 ? '#f56c6c' : '#409eff' }"
          />
        </div>
      </div>
    </div>

    <!-- 考试内容 -->
    <div class="exam-content" v-if="examData">
      <div class="question-navigation">
        <div class="nav-title">题目导航</div>
        <div class="nav-grid">
          <div 
            v-for="(question, index) in examData.questions" 
            :key="question.id"
            :class="[
              'nav-item',
              { 'active': currentQuestionIndex === index },
              { 'answered': isQuestionAnswered(question.questionId) },
              { 'current': currentQuestionIndex === index }
            ]"
            @click="goToQuestion(index)"
          >
            {{ index + 1 }}
          </div>
        </div>
        <div class="nav-legend">
          <div class="legend-item">
            <span class="legend-dot answered"></span>
            <span>已答</span>
          </div>
          <div class="legend-item">
            <span class="legend-dot current"></span>
            <span>当前</span>
          </div>
          <div class="legend-item">
            <span class="legend-dot"></span>
            <span>未答</span>
          </div>
        </div>
      </div>

      <div class="question-area">
        <div v-if="currentQuestion" class="question-container">
          <!-- 题目信息 -->
          <div class="question-header">
            <div class="question-number">
              第{{ currentQuestionIndex + 1 }}题 / 共{{ examData.questions.length }}题
            </div>
            <div class="question-meta">
              <el-tag :type="getTypeTagType(currentQuestion.type)">
                {{ currentQuestion.typeName }}
              </el-tag>
              <el-tag :type="getDifficultyTagType(currentQuestion.difficulty)">
                {{ currentQuestion.difficultyName }}
              </el-tag>
              <span class="question-score">{{ currentQuestion.score }}分</span>
            </div>
          </div>

          <!-- 题目内容 -->
          <div class="question-content">
            <h3>{{ currentQuestion.title }}</h3>
            <div class="question-text" v-html="currentQuestion.content"></div>

            <!-- 选择题选项 -->
            <div v-if="[1, 2, 3].includes(currentQuestion.type)" class="question-options">
              <!-- 单选题 -->
              <el-radio-group 
                v-if="currentQuestion.type === 1" 
                v-model="answers[currentQuestion.questionId]"
                @change="handleAnswerChange"
                class="vertical-options"
              >
                <el-radio 
                  v-for="(option, index) in currentQuestion.options" 
                  :key="index" 
                  :label="option"
                  class="option-item"
                >
                  <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
                  <span class="option-text">{{ option }}</span>
                </el-radio>
              </el-radio-group>

              <!-- 多选题 -->
              <el-checkbox-group 
                v-if="currentQuestion.type === 2" 
                v-model="multipleAnswers[currentQuestion.questionId]"
                @change="handleMultipleAnswerChange"
                class="vertical-options"
              >
                <el-checkbox 
                  v-for="(option, index) in currentQuestion.options" 
                  :key="index" 
                  :label="option"
                  class="option-item"
                >
                  <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
                  <span class="option-text">{{ option }}</span>
                </el-checkbox>
              </el-checkbox-group>

              <!-- 判断题 -->
              <el-radio-group 
                v-if="currentQuestion.type === 3" 
                v-model="answers[currentQuestion.questionId]"
                @change="handleAnswerChange"
                class="vertical-options"
              >
                <el-radio label="正确" class="option-item">
                  <span class="option-label">A.</span>
                  <span class="option-text">正确</span>
                </el-radio>
                <el-radio label="错误" class="option-item">
                  <span class="option-label">B.</span>
                  <span class="option-text">错误</span>
                </el-radio>
              </el-radio-group>
            </div>

            <!-- 问答题和填空题 -->
            <div v-if="[4, 5].includes(currentQuestion.type)" class="question-input">
              <el-input
                v-model="answers[currentQuestion.questionId]"
                type="textarea"
                :rows="currentQuestion.type === 4 ? 6 : 3"
                :placeholder="currentQuestion.type === 4 ? '请输入您的答案...' : '请填入答案...'"
                @input="handleAnswerChange"
              />
            </div>
          </div>

          <!-- 题目操作 -->
          <div class="question-actions">
            <el-button 
              @click="prevQuestion" 
              :disabled="currentQuestionIndex === 0"
            >
              上一题
            </el-button>
            <el-button 
              @click="nextQuestion" 
              :disabled="currentQuestionIndex === examData.questions.length - 1"
              type="primary"
            >
              下一题
            </el-button>
            <el-button 
              @click="showSubmitDialog" 
              type="success"
              style="margin-left: auto;"
            >
              提交考试
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 提交确认对话框 -->
    <el-dialog v-model="submitDialogVisible" title="提交考试" width="500px">
      <div class="submit-info">
        <p>您确定要提交考试吗？</p>
        <div class="answer-summary">
          <p>已答题目：{{ answeredCount }} / {{ examData?.questions?.length }}</p>
          <p>未答题目：{{ unansweredCount }}</p>
        </div>
        <p style="color: #f56c6c; margin-top: 15px;">
          <el-icon><Warning /></el-icon>
          提交后将无法修改答案！
        </p>
      </div>
      <template #footer>
        <el-button @click="submitDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitExam" :loading="submitting">
          确认提交
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { getExamDetail, submitExam } from '@/api/exam'
import { House } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 响应式数据
const examData = ref(null)
const currentQuestionIndex = ref(0)
const answers = reactive({})
const multipleAnswers = reactive({})
const timeLeft = ref(0)
const timer = ref(null)
const submitDialogVisible = ref(false)
const submitting = ref(false)

// 计算属性
const currentQuestion = computed(() => {
  return examData.value?.questions?.[currentQuestionIndex.value]
})

const answeredCount = computed(() => {
  if (!examData.value?.questions) return 0
  return examData.value.questions.filter(q => isQuestionAnswered(q.questionId)).length
})

const unansweredCount = computed(() => {
  if (!examData.value?.questions) return 0
  return examData.value.questions.length - answeredCount.value
})

// 获取考试详情
const fetchExamDetail = async () => {
  try {
    const examId = route.params.id
    console.log('获取考试详情，examId:', examId)
    
    if (!examId || examId === 'record') {
      ElMessage.error('考试ID无效，请重新选择考试')
      router.push('/exam-list')
      return
    }
    
    const response = await getExamDetail(examId)
    examData.value = response.data
    
    console.log('考试详情获取成功:', examData.value)
    
    // 初始化答案
    examData.value.questions.forEach(question => {
      if (question.userAnswer) {
        if (question.type === 2) {
          // 多选题
          multipleAnswers[question.questionId] = question.userAnswer.split('、')
        } else {
          answers[question.questionId] = question.userAnswer
        }
      } else {
        if (question.type === 2) {
          multipleAnswers[question.questionId] = []
        }
      }
    })
    
    // 启动计时器
    startTimer()
  } catch (error) {
    console.error('获取考试信息失败:', error)
    const errorMessage = error.response?.data?.message || error.message || '获取考试信息失败'
    ElMessage.error(errorMessage)
    router.push('/exam-list')
  }
}

// 启动计时器
const startTimer = () => {
  if (!examData.value) return
  
  const startTime = new Date(examData.value.startTime).getTime()
  const duration = examData.value.duration * 60 * 1000 // 转换为毫秒
  const endTime = startTime + duration
  
  const updateTimer = () => {
    const now = Date.now()
    const remaining = endTime - now
    
    if (remaining <= 0) {
      timeLeft.value = 0
      handleTimeUp()
    } else {
      timeLeft.value = remaining
    }
  }
  
  updateTimer()
  timer.value = setInterval(updateTimer, 1000)
}

// 时间到
const handleTimeUp = () => {
  clearInterval(timer.value)
  ElMessage.warning('考试时间已到，系统将自动提交')
  handleSubmitExam()
}

// 判断题目是否已答
const isQuestionAnswered = (questionId) => {
  const answer = answers[questionId]
  const multipleAnswer = multipleAnswers[questionId]
  
  if (multipleAnswer && Array.isArray(multipleAnswer)) {
    return multipleAnswer.length > 0
  }
  
  return answer && answer.trim() !== ''
}

// 跳转到指定题目
const goToQuestion = (index) => {
  currentQuestionIndex.value = index
}

// 上一题
const prevQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

// 下一题
const nextQuestion = () => {
  if (currentQuestionIndex.value < examData.value.questions.length - 1) {
    currentQuestionIndex.value++
  }
}

// 答案变化处理
const handleAnswerChange = () => {
  // 答案已自动绑定到 answers 对象
}

// 多选题答案变化处理
const handleMultipleAnswerChange = () => {
  const questionId = currentQuestion.value.questionId
  const selectedOptions = multipleAnswers[questionId] || []
  answers[questionId] = selectedOptions.join('、')
}

// 显示提交对话框
const showSubmitDialog = () => {
  submitDialogVisible.value = true
}

// 提交考试
const handleSubmitExam = async () => {
  try {
    submitting.value = true
    
    // 准备提交数据
    const submitData = {
      examId: examData.value.id,
      answers: []
    }
    
    // 收集所有答案
    examData.value.questions.forEach(question => {
      const answer = answers[question.questionId] || ''
      submitData.answers.push({
        questionId: question.questionId,
        answer: answer
      })
    })
    
    const response = await submitExam(submitData)
    
    ElMessage.success('考试提交成功')
    submitDialogVisible.value = false
    
    // 跳转到结果页面
    router.push(`/exam-result/${examData.value.id}`)
  } catch (error) {
    ElMessage.error('提交考试失败')
  } finally {
    submitting.value = false
  }
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

// 获取难度标签颜色
const getDifficultyTagType = (difficulty) => {
  const difficultyMap = {
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return difficultyMap[difficulty] || ''
}

// 页面离开前确认
const beforeUnload = (e) => {
  e.preventDefault()
  e.returnValue = '考试正在进行中，确定要离开吗？'
}

// 返回首页
const goToHome = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要离开考试页面返回首页吗？考试进度将会保存。',
      '确认离开',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    router.push('/dashboard')
  } catch (error) {
    // 用户取消，不做任何操作
  }
}

// 初始化
onMounted(() => {
  fetchExamDetail()
  window.addEventListener('beforeunload', beforeUnload)
})

// 清理
onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
  window.removeEventListener('beforeunload', beforeUnload)
})
</script>

<style scoped>
.exam-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.exam-header {
  background: white;
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.exam-info h2 {
  margin: 0 0 10px 0;
  color: #303133;
}

.exam-meta {
  display: flex;
  gap: 20px;
  color: #606266;
}

.header-actions {
  display: flex;
  align-items: center;
}

.exam-timer {
  text-align: center;
}

.exam-content {
  display: flex;
  height: calc(100vh - 120px);
}

.question-navigation {
  width: 280px;
  background: white;
  border-right: 1px solid #e4e7ed;
  padding: 20px;
  overflow-y: auto;
}

.nav-title {
  font-weight: bold;
  margin-bottom: 15px;
  color: #303133;
  font-size: 16px;
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(40px, 1fr));
  gap: 10px;
  margin-bottom: 20px;
  max-width: 100%;
}

.nav-item {
  width: 40px;
  height: 40px;
  border: 2px solid #dcdfe6;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  background: white;
  position: relative;
}

.nav-item:hover {
  border-color: #409eff;
  color: #409eff;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.nav-item.answered {
  background: linear-gradient(135deg, #67c23a, #85ce61);
  color: white;
  border-color: #67c23a;
  box-shadow: 0 2px 6px rgba(103, 194, 58, 0.3);
}

.nav-item.current {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: white;
  border-color: #409eff;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
  }
  50% {
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.5);
  }
  100% {
    box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
  }
}

.nav-legend {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: #606266;
  font-weight: 500;
}

.legend-dot {
  width: 16px;
  height: 16px;
  border: 2px solid #dcdfe6;
  border-radius: 4px;
  flex-shrink: 0;
}

.legend-dot.answered {
  background: linear-gradient(135deg, #67c23a, #85ce61);
  border-color: #67c23a;
}

.legend-dot.current {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  border-color: #409eff;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .question-navigation {
    width: 250px;
  }
  
  .nav-grid {
    grid-template-columns: repeat(4, 1fr);
  }
  
  .nav-item {
    width: 36px;
    height: 36px;
    font-size: 13px;
  }
}

@media (max-width: 768px) {
  .question-navigation {
    width: 200px;
    padding: 15px;
  }
  
  .nav-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
  }
  
  .nav-item {
    width: 32px;
    height: 32px;
    font-size: 12px;
  }
  
  .nav-title {
    font-size: 14px;
  }
}

.question-area {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.question-container {
  background: white;
  border-radius: 8px;
  padding: 30px;
  max-width: 900px;
  margin: 0 auto;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.question-number {
  font-size: 16px;
  font-weight: bold;
  color: #409eff;
}

.question-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.question-score {
  color: #f56c6c;
  font-weight: bold;
}

.question-content h3 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 18px;
}

.question-text {
  margin-bottom: 20px;
  line-height: 1.6;
  color: #606266;
}

.question-options {
  margin: 20px 0;
}

.vertical-options {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
}

.option-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15px;
  padding: 10px;
  border-radius: 6px;
  transition: background-color 0.3s;
  width: 100%;
  text-align: left;
}

.option-item:hover {
  background: #f5f7fa;
}

.option-item :deep(.el-radio),
.option-item :deep(.el-checkbox) {
  width: 100%;
  margin-right: 0;
  display: flex;
  align-items: flex-start;
}

.option-item :deep(.el-radio__input),
.option-item :deep(.el-checkbox__input) {
  margin-right: 8px;
  margin-top: 2px;
}

.option-item :deep(.el-radio__label),
.option-item :deep(.el-checkbox__label) {
  display: flex;
  align-items: flex-start;
  width: 100%;
  padding-left: 0;
}

.option-label {
  font-weight: bold;
  margin-right: 8px;
  color: #409eff;
  min-width: 20px;
  flex-shrink: 0;
}

.option-text {
  flex: 1;
  line-height: 1.5;
  text-align: left;
}

.question-input {
  margin: 20px 0;
}

.question-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.submit-info {
  text-align: center;
}

.answer-summary {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 6px;
  margin: 15px 0;
}

.answer-summary p {
  margin: 5px 0;
  color: #606266;
}
</style> 