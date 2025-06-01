<template>
  <div class="wrong-question-book">
    <div class="header">
      <h2>错题本管理</h2>
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>
        新建错题本
      </el-button>
    </div>

    <div class="content">
      <el-card>
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="name" label="错题本名称" min-width="200">
            <template #default="scope">
              <div class="book-name">
                <el-icon><Document /></el-icon>
                <span>{{ scope.row.name }}</span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="description" label="描述" min-width="250" show-overflow-tooltip />
          
          <el-table-column prop="questionCount" label="题目数量" width="120">
            <template #default="scope">
              <el-tag type="info">{{ scope.row.questionCount }}题</el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="scope">
              {{ formatDateTime(scope.row.createTime) }}
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="300" fixed="right">
            <template #default="scope">
              <el-button 
                type="primary" 
                size="small" 
                @click="viewQuestions(scope.row)"
                :disabled="scope.row.questionCount === 0"
              >
                查看题目
              </el-button>
              <el-button 
                type="success" 
                size="small" 
                @click="startPractice(scope.row)"
                :disabled="scope.row.questionCount === 0"
              >
                开始练习
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="deleteBook(scope.row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 新建错题本对话框 -->
    <el-dialog v-model="showCreateDialog" title="新建错题本" width="500px">
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="createForm.name" placeholder="请输入错题本名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input 
            v-model="createForm.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入错题本描述（可选）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate" :loading="creating">
          创建
        </el-button>
      </template>
    </el-dialog>

    <!-- 题目列表对话框 -->
    <el-dialog v-model="showQuestionsDialog" :title="`${selectedBook?.name} - 题目列表`" width="80%">
      <div class="questions-content">
        <div class="questions-header">
          <el-input
            v-model="questionSearch"
            placeholder="搜索题目..."
            style="width: 300px;"
            clearable
          />
          <div class="filter-buttons">
            <el-button-group>
              <el-button 
                :type="masteryFilter === 'all' ? 'primary' : ''"
                @click="masteryFilter = 'all'"
              >
                全部
              </el-button>
              <el-button 
                :type="masteryFilter === 0 ? 'primary' : ''"
                @click="masteryFilter = 0"
              >
                未掌握
              </el-button>
              <el-button 
                :type="masteryFilter === 1 ? 'primary' : ''"
                @click="masteryFilter = 1"
              >
                部分掌握
              </el-button>
              <el-button 
                :type="masteryFilter === 2 ? 'primary' : ''"
                @click="masteryFilter = 2"
              >
                已掌握
              </el-button>
            </el-button-group>
          </div>
        </div>

        <div class="questions-list">
          <div 
            v-for="question in filteredQuestions" 
            :key="question.id"
            class="question-item"
          >
            <el-card>
              <div class="question-header">
                <div class="question-info">
                  <span class="question-number">第{{ question.questionOrder }}题</span>
                  <el-tag :type="getTypeTagType(question.type)">{{ question.typeName }}</el-tag>
                  <el-tag :type="getDifficultyTagType(question.difficulty)">{{ question.difficultyName }}</el-tag>
                  <span class="question-score">{{ question.score }}分</span>
                </div>
                <div class="question-actions">
                  <el-select 
                    :model-value="question.masteryLevel"
                    @change="(value) => updateMastery(question, value)"
                    size="small"
                    style="width: 120px;"
                  >
                    <el-option label="未掌握" :value="0" />
                    <el-option label="部分掌握" :value="1" />
                    <el-option label="已掌握" :value="2" />
                  </el-select>
                  <el-button 
                    type="danger" 
                    size="small" 
                    @click="removeQuestion(question)"
                  >
                    移除
                  </el-button>
                </div>
              </div>

              <div class="question-content">
                <h4>{{ question.title }}</h4>
                <div class="question-text" v-html="question.content"></div>

                <!-- 选择题选项 -->
                <div v-if="question.options && question.options.length > 0" class="question-options">
                  <div 
                    v-for="(option, index) in question.options" 
                    :key="index"
                    class="option-item"
                  >
                    <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
                    <span class="option-text">{{ option }}</span>
                  </div>
                </div>

                <!-- 判断题选项 -->
                <div v-if="question.type === 3" class="question-options">
                  <div class="option-item">
                    <span class="option-label">A.</span>
                    <span class="option-text">正确</span>
                  </div>
                  <div class="option-item">
                    <span class="option-label">B.</span>
                    <span class="option-text">错误</span>
                  </div>
                </div>

                <div class="answer-section">
                  <div class="answer-row">
                    <strong>正确答案：</strong>
                    <span class="correct-answer">{{ getFormattedAnswer(question, question.correctAnswer) }}</span>
                  </div>
                  <div class="answer-row">
                    <strong>您的错误答案：</strong>
                    <span class="wrong-answer">{{ getFormattedAnswer(question, question.wrongAnswer) }}</span>
                  </div>
                  <div v-if="question.addReason" class="answer-row">
                    <strong>加入原因：</strong>
                    <span class="add-reason">{{ question.addReason }}</span>
                  </div>
                  <div v-if="question.analysis" class="answer-row">
                    <strong>题目解析：</strong>
                    <span class="analysis">{{ question.analysis }}</span>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </div>

        <!-- 题目分页 -->
        <div class="questions-pagination">
          <el-pagination
            v-model:current-page="questionPagination.current"
            v-model:page-size="questionPagination.size"
            :page-sizes="[5, 10, 20]"
            :total="questionPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleQuestionSizeChange"
            @current-change="handleQuestionCurrentChange"
          />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { Plus, Document } from '@element-plus/icons-vue'
import { 
  getUserWrongQuestionBooks, 
  createWrongQuestionBook, 
  deleteWrongQuestionBook,
  getWrongQuestions,
  removeWrongQuestion,
  updateMasteryLevel
} from '@/api/wrongQuestionBook'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const creating = ref(false)
const tableData = ref([])
const showCreateDialog = ref(false)
const showQuestionsDialog = ref(false)
const selectedBook = ref(null)
const questionSearch = ref('')
const masteryFilter = ref('all')
const questions = ref([])

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const questionPagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表单数据
const createForm = reactive({
  name: '',
  description: ''
})

const createFormRef = ref()

// 表单验证规则
const createRules = {
  name: [
    { required: true, message: '请输入错题本名称', trigger: 'blur' }
  ]
}

// 计算属性
const filteredQuestions = computed(() => {
  let filtered = questions.value

  // 按掌握程度筛选
  if (masteryFilter.value !== 'all') {
    filtered = filtered.filter(q => q.masteryLevel === masteryFilter.value)
  }

  // 按搜索关键词筛选
  if (questionSearch.value.trim()) {
    const keyword = questionSearch.value.trim().toLowerCase()
    filtered = filtered.filter(q => 
      q.title.toLowerCase().includes(keyword) ||
      q.content.toLowerCase().includes(keyword)
    )
  }

  return filtered
})

// 获取错题本列表
const fetchData = async () => {
  try {
    loading.value = true
    const response = await getUserWrongQuestionBooks({
      current: pagination.current,
      size: pagination.size
    })
    
    tableData.value = response.data.records || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取错题本列表失败')
  } finally {
    loading.value = false
  }
}

// 获取错题列表
const fetchQuestions = async () => {
  if (!selectedBook.value) return
  
  try {
    const response = await getWrongQuestions(selectedBook.value.id, {
      current: questionPagination.current,
      size: questionPagination.size
    })
    
    questions.value = response.data.records || []
    questionPagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取题目列表失败')
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  fetchData()
}

// 当前页变化
const handleCurrentChange = (current) => {
  pagination.current = current
  fetchData()
}

// 题目分页大小变化
const handleQuestionSizeChange = (size) => {
  questionPagination.size = size
  questionPagination.current = 1
  fetchQuestions()
}

// 题目当前页变化
const handleQuestionCurrentChange = (current) => {
  questionPagination.current = current
  fetchQuestions()
}

// 创建错题本
const handleCreate = async () => {
  try {
    await createFormRef.value.validate()
    creating.value = true
    
    await createWrongQuestionBook(createForm)
    ElMessage.success('创建成功')
    
    // 重置表单
    Object.assign(createForm, { name: '', description: '' })
    showCreateDialog.value = false
    
    // 刷新列表
    fetchData()
  } catch (error) {
    if (error.response) {
      ElMessage.error(error.response.data.message || '创建失败')
    }
  } finally {
    creating.value = false
  }
}

// 查看题目
const viewQuestions = async (book) => {
  selectedBook.value = book
  questionPagination.current = 1
  showQuestionsDialog.value = true
  await fetchQuestions()
}

// 开始练习
const startPractice = (book) => {
  // TODO: 实现错题练习功能
  ElMessage.info('错题练习功能开发中...')
}

// 删除错题本
const deleteBook = async (book) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除错题本"${book.name}"吗？此操作将同时删除其中的所有题目。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteWrongQuestionBook(book.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 移除题目
const removeQuestion = async (question) => {
  try {
    await ElMessageBox.confirm(
      '确定要从错题本中移除这道题目吗？',
      '确认移除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await removeWrongQuestion(selectedBook.value.id, question.questionId)
    ElMessage.success('移除成功')
    
    // 刷新题目列表和错题本列表
    fetchQuestions()
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('移除失败')
    }
  }
}

// 更新掌握程度
const updateMastery = async (question, masteryLevel) => {
  try {
    await updateMasteryLevel(selectedBook.value.id, question.questionId, masteryLevel)
    question.masteryLevel = masteryLevel
    ElMessage.success('更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
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

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
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

// 监听筛选条件变化
watch([masteryFilter, questionSearch], () => {
  // 重置分页
  questionPagination.current = 1
})

// 初始化
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.wrong-question-book {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.content {
  background: white;
  border-radius: 8px;
}

.book-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.questions-content {
  max-height: 70vh;
  overflow-y: auto;
}

.questions-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 6px;
}

.filter-buttons {
  display: flex;
  gap: 10px;
}

.questions-list {
  margin-bottom: 20px;
}

.question-item {
  margin-bottom: 15px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
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

.question-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.question-content h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.question-text {
  margin-bottom: 15px;
  line-height: 1.6;
  color: #606266;
}

.question-options {
  margin: 15px 0;
}

.option-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 8px;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 4px;
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

.answer-section {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 6px;
  margin-top: 15px;
}

.answer-row {
  margin-bottom: 8px;
}

.answer-row:last-child {
  margin-bottom: 0;
}

.correct-answer {
  color: #67c23a;
  font-weight: bold;
}

.wrong-answer {
  color: #f56c6c;
  font-weight: bold;
}

.add-reason {
  color: #909399;
  font-style: italic;
}

.analysis {
  color: #606266;
  line-height: 1.6;
}

.questions-pagination {
  text-align: center;
  padding: 20px;
  border-top: 1px solid #e4e7ed;
}
</style> 