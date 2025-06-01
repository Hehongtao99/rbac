<template>
  <div class="exam-result">
    <div class="result-header">
      <div class="result-info">
        <h2>考试结果</h2>
        <div class="exam-meta">
          <span>{{ examData?.title }}</span>
          <span>题库：{{ examData?.bankName }}</span>
        </div>
      </div>
      <div class="header-actions">
        <el-button @click="goToHome" type="info" size="small" style="margin-right: 15px;">
          <el-icon><House /></el-icon>
          返回首页
        </el-button>
        <div class="score-display">
          <el-statistic 
            :value="examData?.userScore || 0" 
            :precision="0"
            title="得分"
            suffix="分"
            :value-style="{ 
              color: getScoreColor(examData?.userScore, examData?.totalScore),
              fontSize: '36px',
              fontWeight: 'bold'
            }"
          />
          <div class="total-score">总分：{{ examData?.totalScore }}分</div>
          <div class="score-rate">
            得分率：{{ getScoreRate(examData?.userScore, examData?.totalScore) }}%
          </div>
        </div>
      </div>
    </div>

    <div class="result-summary">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="summary-card">
            <el-statistic 
              :value="examData?.questions?.length || 0" 
              title="总题数"
              suffix="道"
            />
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="summary-card">
            <el-statistic 
              :value="correctCount" 
              title="答对题数"
              suffix="道"
              :value-style="{ color: '#67c23a' }"
            />
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="summary-card">
            <el-statistic 
              :value="wrongCount" 
              title="答错题数"
              suffix="道"
              :value-style="{ color: '#f56c6c' }"
            />
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="summary-card">
            <el-statistic 
              :value="getExamDuration()" 
              title="用时"
              suffix="分钟"
              :value-style="{ color: '#409eff' }"
            />
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="result-content">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>答题详情</span>
            <div class="filter-buttons">
              <el-button 
                :type="filterType === 'all' ? 'primary' : ''"
                @click="filterType = 'all'"
                size="small"
              >
                全部
              </el-button>
              <el-button 
                :type="filterType === 'correct' ? 'success' : ''"
                @click="filterType = 'correct'"
                size="small"
              >
                答对
              </el-button>
              <el-button 
                :type="filterType === 'wrong' ? 'danger' : ''"
                @click="filterType = 'wrong'"
                size="small"
              >
                答错
              </el-button>
            </div>
          </div>
        </template>

        <div class="questions-list">
          <div 
            v-for="(question, index) in filteredQuestions" 
            :key="question.id"
            class="question-item"
          >
            <el-card class="question-card">
              <template #header>
                <div class="question-header">
                  <div class="question-info">
                    <span class="question-number">第{{ question.questionOrder }}题</span>
                    <el-tag :type="getTypeTagType(question.type)">
                      {{ question.typeName }}
                    </el-tag>
                    <el-tag :type="getDifficultyTagType(question.difficulty)">
                      {{ question.difficultyName }}
                    </el-tag>
                    <span class="question-score">{{ question.score }}分</span>
                  </div>
                  <div class="question-result">
                    <el-tag 
                      :type="question.isCorrect === 1 ? 'success' : 'danger'"
                      size="large"
                    >
                      {{ question.isCorrect === 1 ? '正确' : '错误' }}
                    </el-tag>
                    <span class="user-score">
                      得分：{{ question.userScore || 0 }}分
                    </span>
                  </div>
                </div>
              </template>

              <div class="question-content">
                <h4>{{ question.title }}</h4>
                <div class="question-text" v-html="question.content"></div>

                <!-- 选择题选项 -->
                <div v-if="question.options && question.options.length > 0 && question.type !== 3" class="options">
                  <div 
                    v-for="(option, optIndex) in question.options" 
                    :key="optIndex" 
                    :class="[
                      'option-item',
                      { 'correct-option': isCorrectOption(question, option) },
                      { 'user-option': isUserOption(question, option) },
                      { 'wrong-user-option': isWrongUserOption(question, option) }
                    ]"
                  >
                    <span class="option-label">{{ String.fromCharCode(65 + optIndex) }}.</span>
                    <span class="option-text">{{ option }}</span>
                    <span v-if="isCorrectOption(question, option)" class="option-mark correct">
                      <el-icon><Check /></el-icon>
                    </span>
                    <span v-if="isWrongUserOption(question, option)" class="option-mark wrong">
                      <el-icon><Close /></el-icon>
                    </span>
                  </div>
                </div>

                <!-- 判断题选项 -->
                <div v-if="question.type === 3" class="options">
                  <div 
                    :class="[
                      'option-item',
                      { 'correct-option': isCorrectOption(question, '正确') },
                      { 'user-option': isUserOption(question, '正确') },
                      { 'wrong-user-option': isWrongUserOption(question, '正确') }
                    ]"
                  >
                    <span class="option-label">A.</span>
                    <span class="option-text">正确</span>
                    <span v-if="isCorrectOption(question, '正确')" class="option-mark correct">
                      <el-icon><Check /></el-icon>
                    </span>
                    <span v-if="isWrongUserOption(question, '正确')" class="option-mark wrong">
                      <el-icon><Close /></el-icon>
                    </span>
                  </div>
                  <div 
                    :class="[
                      'option-item',
                      { 'correct-option': isCorrectOption(question, '错误') },
                      { 'user-option': isUserOption(question, '错误') },
                      { 'wrong-user-option': isWrongUserOption(question, '错误') }
                    ]"
                  >
                    <span class="option-label">B.</span>
                    <span class="option-text">错误</span>
                    <span v-if="isCorrectOption(question, '错误')" class="option-mark correct">
                      <el-icon><Check /></el-icon>
                    </span>
                    <span v-if="isWrongUserOption(question, '错误')" class="option-mark wrong">
                      <el-icon><Close /></el-icon>
                    </span>
                  </div>
                </div>

                <div class="answer-section">
                  <div class="answer-row">
                    <strong>正确答案：</strong>
                    <span class="correct-answer">{{ getFormattedAnswer(question, question.answer) }}</span>
                  </div>
                  <div class="answer-row">
                    <strong>您的答案：</strong>
                    <span :class="[
                      'user-answer',
                      question.isCorrect === 1 ? 'correct' : 'wrong'
                    ]">
                      {{ getFormattedAnswer(question, question.userAnswer) }}
                    </span>
                  </div>
                  <div v-if="question.analysis" class="answer-row">
                    <strong>题目解析：</strong>
                    <span class="analysis">{{ question.analysis }}</span>
                  </div>
                  <!-- 错题操作 -->
                  <div v-if="question.isCorrect === 0" class="wrong-question-actions">
                    <el-button 
                      type="danger" 
                      size="small" 
                      @click="showAddToWrongBookDialog(question)"
                      :icon="Plus"
                    >
                      加入错题本
                    </el-button>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </el-card>
    </div>

    <div class="result-actions">
      <el-button @click="goBack" size="large">返回</el-button>
      <el-button @click="goToExamList" type="primary" size="large">
        查看考试记录
      </el-button>
      <el-button @click="startNewExam" type="success" size="large">
        重新考试
      </el-button>
      <el-button 
        @click="showBatchAddToWrongBookDialog" 
        type="danger" 
        size="large"
        :disabled="wrongCount === 0"
      >
        <el-icon><Plus /></el-icon>
        一键加入错题本
      </el-button>
    </div>

    <!-- 添加到错题本对话框 -->
    <el-dialog v-model="addToWrongBookVisible" title="添加到错题本" width="600px">
      <div class="add-wrong-book-content">
        <div class="question-preview">
          <h4>题目预览</h4>
          <div class="question-info">
            <p><strong>{{ selectedQuestion?.title }}</strong></p>
            <div v-html="selectedQuestion?.content"></div>
          </div>
        </div>
        
        <el-form :model="wrongBookForm" label-width="100px">
          <el-form-item label="选择错题本">
            <el-select 
              v-model="wrongBookForm.bookId" 
              placeholder="请选择错题本"
              style="width: 100%"
            >
              <el-option
                v-for="book in wrongQuestionBooks"
                :key="book.id"
                :label="`${book.name} (${book.questionCount}题)`"
                :value="book.id"
              />
            </el-select>
            <el-button 
              type="text" 
              @click="showCreateBookDialog = true"
              style="margin-left: 10px;"
            >
              新建错题本
            </el-button>
          </el-form-item>
          
          <el-form-item label="错误答案">
            <el-input 
              v-model="wrongBookForm.wrongAnswer" 
              type="textarea" 
              :rows="2"
              placeholder="您的错误答案"
              readonly
            />
          </el-form-item>
          
          <el-form-item label="加入原因">
            <el-input 
              v-model="wrongBookForm.addReason" 
              type="textarea" 
              :rows="3"
              placeholder="请输入加入错题本的原因（可选）"
            />
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <el-button @click="addToWrongBookVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddToWrongBook" :loading="addingToWrongBook">
          确认添加
        </el-button>
      </template>
    </el-dialog>

    <!-- 新建错题本对话框 -->
    <el-dialog v-model="showCreateBookDialog" title="新建错题本" width="500px">
      <el-form :model="createBookForm" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="createBookForm.name" placeholder="请输入错题本名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input 
            v-model="createBookForm.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入错题本描述（可选）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showCreateBookDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreateWrongBook" :loading="creatingBook">
          创建
        </el-button>
      </template>
    </el-dialog>

    <!-- 批量加入错题本对话框 -->
    <el-dialog v-model="batchAddToWrongBookVisible" title="一键加入错题本" width="600px">
      <div class="batch-add-content">
        <div class="wrong-questions-preview">
          <h4>将要加入的错题（共{{ wrongQuestions.length }}道）</h4>
          <div class="questions-summary">
            <el-tag 
              v-for="(question, index) in wrongQuestions.slice(0, 5)" 
              :key="question.questionId"
              type="danger"
              style="margin: 2px;"
            >
              第{{ question.questionOrder }}题
            </el-tag>
            <span v-if="wrongQuestions.length > 5" class="more-indicator">
              ...还有{{ wrongQuestions.length - 5 }}道题
            </span>
          </div>
        </div>
        
        <el-form :model="batchWrongBookForm" label-width="100px" style="margin-top: 20px;">
          <el-form-item label="选择错题本">
            <el-select 
              v-model="batchWrongBookForm.bookId" 
              placeholder="请选择错题本"
              style="width: 100%"
            >
              <el-option
                v-for="book in wrongQuestionBooks"
                :key="book.id"
                :label="`${book.name} (${book.questionCount}题)`"
                :value="book.id"
              />
            </el-select>
            <el-button 
              type="text" 
              @click="showCreateBookDialog = true"
              style="margin-left: 10px;"
            >
              新建错题本
            </el-button>
          </el-form-item>
          
          <el-form-item label="加入原因">
            <el-input 
              v-model="batchWrongBookForm.addReason" 
              type="textarea" 
              :rows="3"
              placeholder="请输入加入错题本的原因（可选）"
            />
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <el-button @click="batchAddToWrongBookVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchAddToWrongBook" :loading="batchAddingToWrongBook">
          确认加入（{{ wrongQuestions.length }}道题）
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { getExamResult } from '@/api/exam'
import { getUserWrongQuestionBooks, addWrongQuestion, createWrongQuestionBook } from '@/api/wrongQuestionBook'
import { Plus, House } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 响应式数据
const examData = ref(null)
const filterType = ref('all')

// 错题本相关数据
const wrongQuestionBooks = ref([])
const selectedQuestion = ref(null)
const addToWrongBookVisible = ref(false)
const showCreateBookDialog = ref(false)
const addingToWrongBook = ref(false)
const creatingBook = ref(false)

// 批量加入错题本相关数据
const batchAddToWrongBookVisible = ref(false)
const batchAddingToWrongBook = ref(false)

// 表单数据
const wrongBookForm = ref({
  bookId: null,
  questionId: null,
  wrongAnswer: '',
  addReason: ''
})

const createBookForm = ref({
  name: '',
  description: ''
})

const batchWrongBookForm = ref({
  bookId: null,
  addReason: ''
})

// 计算属性
const correctCount = computed(() => {
  if (!examData.value?.questions) return 0
  return examData.value.questions.filter(q => q.isCorrect === 1).length
})

const wrongCount = computed(() => {
  if (!examData.value?.questions) return 0
  return examData.value.questions.filter(q => q.isCorrect === 0).length
})

const filteredQuestions = computed(() => {
  if (!examData.value?.questions) return []
  
  switch (filterType.value) {
    case 'correct':
      return examData.value.questions.filter(q => q.isCorrect === 1)
    case 'wrong':
      return examData.value.questions.filter(q => q.isCorrect === 0)
    default:
      return examData.value.questions
  }
})

// 错题列表
const wrongQuestions = computed(() => {
  if (!examData.value?.questions) return []
  return examData.value.questions.filter(q => q.isCorrect === 0)
})

// 获取考试结果
const fetchExamResult = async () => {
  try {
    const examId = route.params.id
    const response = await getExamResult(examId)
    examData.value = response.data
  } catch (error) {
    ElMessage.error('获取考试结果失败')
    router.push('/exam-list')
  }
}

// 获取得分颜色
const getScoreColor = (userScore, totalScore) => {
  if (!userScore || !totalScore) return '#909399'
  const rate = (userScore / totalScore) * 100
  if (rate >= 90) return '#67c23a'
  if (rate >= 80) return '#e6a23c'
  if (rate >= 60) return '#f56c6c'
  return '#909399'
}

// 获取得分率
const getScoreRate = (userScore, totalScore) => {
  if (!userScore || !totalScore) return 0
  return Math.round((userScore / totalScore) * 100)
}

// 获取考试用时
const getExamDuration = () => {
  if (!examData.value?.startTime || !examData.value?.endTime) return 0
  const start = new Date(examData.value.startTime).getTime()
  const end = new Date(examData.value.endTime).getTime()
  return Math.round((end - start) / (1000 * 60))
}

// 判断是否为正确选项
const isCorrectOption = (question, option) => {
  if (!question.answer) return false
  
  // 对于选择题，需要将答案内容与选项内容进行匹配
  if ([1, 2].includes(question.type)) {
    const correctAnswers = question.answer.split('、')
    return correctAnswers.includes(option)
  }
  
  // 对于判断题，需要标准化比较
  if (question.type === 3) {
    const normalizedCorrect = normalizeJudgmentAnswer(question.answer)
    const normalizedOption = normalizeJudgmentAnswer(option)
    return normalizedCorrect === normalizedOption
  }
  
  return question.answer === option
}

// 判断是否为用户选择的选项
const isUserOption = (question, option) => {
  if (!question.userAnswer) return false
  
  // 对于选择题，需要将用户答案内容与选项内容进行匹配
  if ([1, 2].includes(question.type)) {
    const userAnswers = question.userAnswer.split('、')
    return userAnswers.includes(option)
  }
  
  // 对于判断题，需要标准化比较
  if (question.type === 3) {
    const normalizedUser = normalizeJudgmentAnswer(question.userAnswer)
    const normalizedOption = normalizeJudgmentAnswer(option)
    return normalizedUser === normalizedOption
  }
  
  return question.userAnswer === option
}

// 判断是否为用户错误选择的选项
const isWrongUserOption = (question, option) => {
  return isUserOption(question, option) && !isCorrectOption(question, option)
}

// 获取格式化的答案显示（将选项内容转换为选项标签）
const getFormattedAnswer = (question, answer) => {
  if (!answer) {
    return '未作答'
  }
  
  // 对于选择题，将答案内容转换为选项标签
  if ([1, 2].includes(question.type) && question.options && question.options.length > 0) {
    // 处理多种分隔符
    const answers = answer.split(/[,，、;；]/).map(ans => ans.trim()).filter(ans => ans)
    const labels = []
    
    for (const ans of answers) {
      // 首先尝试精确匹配
      let index = question.options.findIndex(opt => opt.trim() === ans.trim())
      
      // 如果精确匹配失败，尝试包含匹配（用于长文本选项）
      if (index === -1) {
        index = question.options.findIndex(opt => 
          opt.trim().includes(ans.trim()) || ans.trim().includes(opt.trim())
        )
      }
      
      if (index !== -1) {
        const label = String.fromCharCode(65 + index)
        if (!labels.includes(label)) {
          labels.push(label)
        }
      } else {
        // 如果找不到匹配的选项，保留原始答案
        labels.push(ans)
      }
    }
    
    return labels.length > 0 ? labels.join('、') : answer
  }
  
  // 对于判断题，标准化显示
  if (question.type === 3) {
    return normalizeJudgmentAnswer(answer)
  }
  
  return answer
}

// 标准化判断题答案显示
const normalizeJudgmentAnswer = (answer) => {
  if (!answer) return '未作答'
  
  switch (answer.trim()) {
    case '对':
    case '正确':
    case 'T':
    case 'True':
    case 'true':
    case '是':
      return '正确'
    case '错':
    case '错误':
    case 'F':
    case 'False':
    case 'false':
    case '否':
      return '错误'
    default:
      return answer
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

// 返回
const goBack = () => {
  router.go(-1)
}

// 查看考试记录
const goToExamList = () => {
  router.push('/exam-list')
}

// 重新考试
const startNewExam = () => {
  router.push('/exam-start')
}

// 显示添加到错题本对话框
const showAddToWrongBookDialog = async (question) => {
  selectedQuestion.value = question
  wrongBookForm.value = {
    bookId: null,
    questionId: question.questionId,
    wrongAnswer: question.userAnswer || '未作答',
    addReason: ''
  }
  
  // 获取错题本列表
  await fetchWrongQuestionBooks()
  addToWrongBookVisible.value = true
}

// 获取错题本列表
const fetchWrongQuestionBooks = async () => {
  try {
    const response = await getUserWrongQuestionBooks({
      current: 1,
      size: 100
    })
    wrongQuestionBooks.value = response.data.records || []
  } catch (error) {
    ElMessage.error('获取错题本列表失败')
  }
}

// 添加到错题本
const handleAddToWrongBook = async () => {
  if (!wrongBookForm.value.bookId) {
    ElMessage.warning('请选择错题本')
    return
  }
  
  try {
    addingToWrongBook.value = true
    await addWrongQuestion(wrongBookForm.value)
    ElMessage.success('添加到错题本成功')
    addToWrongBookVisible.value = false
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '添加失败')
  } finally {
    addingToWrongBook.value = false
  }
}

// 创建错题本
const handleCreateWrongBook = async () => {
  if (!createBookForm.value.name.trim()) {
    ElMessage.warning('请输入错题本名称')
    return
  }
  
  try {
    creatingBook.value = true
    const response = await createWrongQuestionBook(createBookForm.value)
    ElMessage.success('创建错题本成功')
    
    // 刷新错题本列表
    await fetchWrongQuestionBooks()
    
    // 自动选择新创建的错题本
    wrongBookForm.value.bookId = response.data.id
    
    // 重置表单并关闭对话框
    createBookForm.value = { name: '', description: '' }
    showCreateBookDialog.value = false
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '创建失败')
  } finally {
    creatingBook.value = false
  }
}

// 返回首页
const goToHome = () => {
  router.push('/')
}

// 显示批量加入错题本对话框
const showBatchAddToWrongBookDialog = async () => {
  if (wrongQuestions.value.length === 0) {
    ElMessage.info('没有错题需要加入错题本')
    return
  }
  
  // 获取错题本列表
  await fetchWrongQuestionBooks()
  batchWrongBookForm.value = {
    bookId: null,
    addReason: `${examData.value?.title || '考试'} - 错题整理`
  }
  batchAddToWrongBookVisible.value = true
}

// 处理批量加入错题本
const handleBatchAddToWrongBook = async () => {
  if (!batchWrongBookForm.value.bookId) {
    ElMessage.warning('请选择错题本')
    return
  }
  
  try {
    batchAddingToWrongBook.value = true
    
    let successCount = 0
    let failCount = 0
    const errors = []
    
    // 逐个添加错题
    for (const question of wrongQuestions.value) {
      try {
        await addWrongQuestion({
          bookId: batchWrongBookForm.value.bookId,
          questionId: question.questionId,
          wrongAnswer: question.userAnswer || '未作答',
          addReason: batchWrongBookForm.value.addReason
        })
        successCount++
      } catch (error) {
        failCount++
        const errorMsg = error.response?.data?.message || '添加失败'
        if (errorMsg.includes('已在错题本中')) {
          // 题目已存在，不算错误
          successCount++
          failCount--
        } else {
          errors.push(`第${question.questionOrder}题: ${errorMsg}`)
        }
      }
    }
    
    // 显示结果
    if (failCount === 0) {
      ElMessage.success(`成功加入${successCount}道错题到错题本`)
    } else {
      ElMessage.warning(`成功加入${successCount}道题，${failCount}道题加入失败`)
      if (errors.length > 0) {
        console.error('批量加入错题失败详情:', errors)
      }
    }
    
    batchAddToWrongBookVisible.value = false
  } catch (error) {
    ElMessage.error('批量加入错题本失败')
  } finally {
    batchAddingToWrongBook.value = false
  }
}

// 处理批量加入错题本的逻辑
const handleBatchAddToWrongBookLogic = async () => {
  // 这个方法已经被上面的 handleBatchAddToWrongBook 替代
}

// 初始化
onMounted(() => {
  fetchExamResult()
})
</script>

<style scoped>
.exam-result {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.result-header {
  background: white;
  padding: 30px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-info h2 {
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

.score-display {
  text-align: center;
}

.total-score {
  margin-top: 10px;
  color: #606266;
  font-size: 14px;
}

.score-rate {
  color: #909399;
  font-size: 12px;
}

.result-summary {
  margin-bottom: 20px;
}

.summary-card {
  text-align: center;
}

.result-content {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-buttons {
  display: flex;
  gap: 10px;
}

.questions-list {
  max-height: 800px;
  overflow-y: auto;
}

.question-item {
  margin-bottom: 20px;
}

.question-card {
  border: 1px solid #e4e7ed;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-info {
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

.question-result {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-score {
  font-weight: bold;
  color: #606266;
}

.question-content h4 {
  margin: 0 0 15px 0;
  color: #303133;
}

.question-text {
  margin-bottom: 20px;
  line-height: 1.6;
  color: #606266;
}

.options {
  margin: 20px 0;
}

.option-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  padding: 10px;
  border-radius: 6px;
  border: 1px solid transparent;
  position: relative;
}

.option-item.correct-option {
  background: #f0f9ff;
  border-color: #67c23a;
}

.option-item.user-option {
  background: #fdf6ec;
  border-color: #e6a23c;
}

.option-item.wrong-user-option {
  background: #fef0f0;
  border-color: #f56c6c;
}

.option-label {
  font-weight: bold;
  margin-right: 8px;
  color: #409eff;
  min-width: 20px;
}

.option-text {
  flex: 1;
  line-height: 1.5;
}

.option-mark {
  margin-left: 10px;
  font-size: 16px;
}

.option-mark.correct {
  color: #67c23a;
}

.option-mark.wrong {
  color: #f56c6c;
}

.answer-section {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 6px;
  margin-top: 20px;
}

.answer-row {
  margin-bottom: 10px;
  line-height: 1.6;
}

.answer-row:last-child {
  margin-bottom: 0;
}

.correct-answer {
  color: #67c23a;
  font-weight: bold;
}

.user-answer.correct {
  color: #67c23a;
  font-weight: bold;
}

.user-answer.wrong {
  color: #f56c6c;
  font-weight: bold;
}

.analysis {
  color: #606266;
  font-style: italic;
}

.wrong-question-actions {
  margin-top: 10px;
  text-align: right;
}

.result-actions {
  text-align: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
}

.result-actions .el-button {
  margin: 0 10px;
}

.add-wrong-book-content {
  padding: 20px;
}

.question-preview {
  margin-bottom: 20px;
}

.question-info {
  margin-bottom: 10px;
}

.add-wrong-book-content .el-form {
  margin-top: 20px;
}

.add-wrong-book-content .el-form .el-form-item {
  margin-bottom: 10px;
}

.add-wrong-book-content .el-form .el-form-item .el-input {
  width: 100%;
}

.add-wrong-book-content .el-form .el-form-item .el-select {
  width: 100%;
}

.add-wrong-book-content .el-form .el-form-item .el-button {
  margin-left: 10px;
}

.batch-add-content {
  padding: 20px;
}

.wrong-questions-preview {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 20px;
}

.wrong-questions-preview h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.questions-summary {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 5px;
}

.more-indicator {
  color: #909399;
  font-size: 12px;
  margin-left: 10px;
}
</style> 