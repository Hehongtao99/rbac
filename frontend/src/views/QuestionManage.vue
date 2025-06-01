<template>
  <div class="question-manage">
    <div class="header">
      <h2>题目管理{{ bankName ? ` - ${bankName}` : '' }}</h2>
      <div class="header-buttons">
        <el-button type="success" @click="showAiGenerateDialog">AI提取题目</el-button>
        <el-button type="primary" @click="showAddDialog">新增题目</el-button>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="所属题库" v-if="!bankId">
          <el-select v-model="searchForm.bankId" placeholder="请选择题库" clearable @change="handleBankChange">
            <el-option 
              v-for="bank in questionBanks" 
              :key="bank.id" 
              :label="bank.name" 
              :value="bank.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="题目标题">
          <el-input v-model="searchForm.title" placeholder="请输入题目标题" clearable />
        </el-form-item>
        <el-form-item label="题目类型">
          <el-select v-model="searchForm.type" placeholder="请选择题目类型" clearable>
            <el-option label="单选题" :value="1" />
            <el-option label="多选题" :value="2" />
            <el-option label="判断题" :value="3" />
            <el-option label="问答题" :value="4" />
            <el-option label="填空题" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格区域 -->
    <div class="table-area">
      <el-table :data="tableData" v-loading="loading" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="bankName" label="所属题库" width="120" v-if="!bankId" />
        <el-table-column prop="title" label="题目标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="typeName" label="题目类型" width="100" />
        <el-table-column prop="difficultyName" label="难度" width="80" />
        <el-table-column prop="score" label="分值" width="80" />
        <el-table-column prop="createUserName" label="创建人" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 批量操作 -->
      <div class="batch-actions">
        <el-button type="danger" @click="handleBatchDelete" :disabled="!selectedRows.length">
          批量删除
        </el-button>
      </div>

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
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="所属题库" prop="bankId">
          <el-select v-model="form.bankId" placeholder="请选择题库" :disabled="!!bankId">
            <el-option 
              v-for="bank in questionBanks" 
              :key="bank.id" 
              :label="bank.name" 
              :value="bank.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="题目标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入题目标题" />
        </el-form-item>
        <el-form-item label="题目内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="请输入题目内容"
          />
        </el-form-item>
        <el-form-item label="题目类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择题目类型" @change="handleTypeChange">
            <el-option label="单选题" :value="1" />
            <el-option label="多选题" :value="2" />
            <el-option label="判断题" :value="3" />
            <el-option label="问答题" :value="4" />
            <el-option label="填空题" :value="5" />
          </el-select>
        </el-form-item>
        
        <!-- 选择题选项 -->
        <el-form-item v-if="[1, 2].includes(form.type)" label="选项" prop="options">
          <div class="options-container">
            <div v-for="(option, index) in form.options" :key="index" class="option-item">
              <el-input
                v-model="form.options[index]"
                :placeholder="`选项 ${String.fromCharCode(65 + index)}`"
              />
              <el-button
                type="danger"
                size="small"
                @click="removeOption(index)"
                :disabled="form.options.length <= 2"
              >
                删除
              </el-button>
            </div>
            <el-button type="primary" size="small" @click="addOption">添加选项</el-button>
          </div>
        </el-form-item>

        <!-- 判断题选项 -->
        <el-form-item v-if="form.type === 3" label="选项">
          <el-radio-group v-model="form.answer">
            <el-radio label="正确">正确</el-radio>
            <el-radio label="错误">错误</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 答案 -->
        <el-form-item v-if="form.type === 1" label="正确答案" prop="answer">
          <el-radio-group v-model="form.answer">
            <el-radio 
              v-for="(option, index) in form.options" 
              :key="index" 
              :label="option"
              :disabled="!option.trim()"
            >
              {{ String.fromCharCode(65 + index) }}. {{ option }}
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 多选题答案 -->
        <el-form-item v-if="form.type === 2" label="正确答案" prop="answer">
          <el-checkbox-group v-model="form.multipleAnswers">
            <el-checkbox 
              v-for="(option, index) in form.options" 
              :key="index" 
              :label="option"
              :disabled="!option.trim()"
            >
              {{ String.fromCharCode(65 + index) }}. {{ option }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <!-- 其他题型答案 -->
        <el-form-item v-if="![1, 2, 3].includes(form.type)" label="正确答案" prop="answer">
          <el-input
            v-model="form.answer"
            type="textarea"
            :rows="2"
            placeholder="请输入正确答案"
          />
        </el-form-item>

        <el-form-item label="题目解析">
          <el-input
            v-model="form.analysis"
            type="textarea"
            :rows="3"
            placeholder="请输入题目解析（可选）"
          />
        </el-form-item>
        <el-form-item label="难度等级" prop="difficulty">
          <el-select v-model="form.difficulty" placeholder="请选择难度等级">
            <el-option label="简单" :value="1" />
            <el-option label="中等" :value="2" />
            <el-option label="困难" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="分值" prop="score">
          <el-input-number v-model="form.score" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- AI生成对话框 -->
    <el-dialog
      v-model="aiDialogVisible"
      title="AI智能题目提取"
      width="600px"
      @close="resetAiForm"
    >
      <el-form :model="aiForm" :rules="aiRules" ref="aiFormRef" label-width="100px">
        <el-form-item label="选择题库" prop="bankId">
          <el-select v-model="aiForm.bankId" placeholder="请选择题库" :disabled="!!bankId" style="width: 100%">
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
            v-model="aiForm.content"
            type="textarea"
            :rows="8"
            placeholder="请输入包含题目和答案的内容，例如：&#10;&#10;1. 以下哪个是Java的关键字？&#10;A. class  B. Class  C. CLASS  D. clazz&#10;答案：A&#10;&#10;2. Java是面向对象的编程语言吗？&#10;答案：是&#10;&#10;AI会自动识别题目类型、选项和答案..."
            maxlength="20000"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="aiDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAiPreview" :loading="aiPreviewLoading">
            <span v-if="!aiPreviewLoading">预览提取结果</span>
            <span v-else>AI正在处理中，请耐心等待...</span>
          </el-button>
          <el-button type="success" @click="handleAiGenerateAndAdd" :loading="aiLoading">
            <span v-if="!aiLoading">直接提取并添加</span>
            <span v-else>AI正在处理中，请耐心等待...</span>
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- AI预览对话框 -->
    <el-dialog
      v-model="aiPreviewDialogVisible"
      title="AI提取题目预览"
      width="80%"
      :close-on-click-modal="false"
    >
      <div v-if="aiPreviewQuestions.length > 0" class="preview-content">
        <div v-for="(question, index) in aiPreviewQuestions" :key="index" class="question-item">
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

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="aiPreviewDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAiConfirmAdd" :loading="aiLoading">
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
import { useRoute, useRouter } from 'vue-router'
import {
  getQuestionList,
  createQuestion,
  updateQuestion,
  deleteQuestion,
  batchDeleteQuestion
} from '@/api/question'
import { getQuestionBankList } from '@/api/questionBank'
import { extractAndAddQuestions, extractQuestionsPreview, extractQuestionsStream, addStreamQuestions } from '@/api/aiQuestion'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const selectedRows = ref([])
const questionBanks = ref([])

// AI生成相关
const aiDialogVisible = ref(false)
const aiFormRef = ref()
const aiPreviewDialogVisible = ref(false)
const aiPreviewQuestions = ref([])
const aiLoading = ref(false)
const aiPreviewLoading = ref(false)

// 题库信息（可能来自路由参数）
const bankId = ref(route.query.bankId)
const bankName = ref(route.query.bankName || '')

// 搜索表单
const searchForm = reactive({
  bankId: bankId.value,
  title: '',
  type: null
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表单数据
const form = reactive({
  id: null,
  bankId: bankId.value,
  title: '',
  content: '',
  type: null,
  options: ['', ''],
  answer: '',
  multipleAnswers: [],
  analysis: '',
  difficulty: 1,
  score: 1,
  status: 1
})

// 表单验证规则
const rules = {
  bankId: [
    { required: true, message: '请选择题库', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入题目标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入题目内容', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择题目类型', trigger: 'change' }
  ],
  answer: [
    { required: true, message: '请输入正确答案', trigger: 'blur' }
  ],
  difficulty: [
    { required: true, message: '请选择难度等级', trigger: 'change' }
  ],
  score: [
    { required: true, message: '请输入分值', trigger: 'blur' }
  ]
}

// AI表单数据
const aiForm = reactive({
  bankId: bankId.value,
  content: ''
})

// AI表单验证规则
const aiRules = {
  bankId: [
    { required: true, message: '请选择题库', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入题目内容', trigger: 'blur' },
    { min: 20, message: '内容至少20个字符', trigger: 'blur' }
  ]
}

// 获取题库列表
const fetchQuestionBanks = async () => {
  try {
    const response = await getQuestionBankList({ current: 1, size: 1000 })
    questionBanks.value = response.data.records
  } catch (error) {
    ElMessage.error('获取题库列表失败')
  }
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      bankId: searchForm.bankId || undefined,
      type: searchForm.type || undefined,
      title: searchForm.title || undefined
    }
    const response = await getQuestionList(params)
    tableData.value = response.data.records
    pagination.total = response.data.total
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 题库选择变化
const handleBankChange = (selectedBankId) => {
  const selectedBank = questionBanks.value.find(bank => bank.id === selectedBankId)
  if (selectedBank) {
    bankName.value = selectedBank.name
  } else {
    bankName.value = ''
  }
  pagination.current = 1
  fetchData()
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.title = ''
  searchForm.type = null
  if (!bankId.value) {
    searchForm.bankId = null
    bankName.value = ''
  }
  pagination.current = 1
  fetchData()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  fetchData()
}

// 当前页改变
const handleCurrentChange = (current) => {
  pagination.current = current
  fetchData()
}

// 显示新增对话框
const showAddDialog = () => {
  if (!bankId.value && questionBanks.value.length === 0) {
    ElMessage.warning('请先创建题库')
    return
  }
  dialogTitle.value = '新增题目'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑题目'
  
  // 处理多选题答案
  let multipleAnswers = []
  if (row.type === 2 && row.answer) {
    // 将答案字符串分割为数组
    multipleAnswers = row.answer.split('、').filter(answer => answer.trim())
  }
  
  Object.assign(form, {
    ...row,
    options: row.options || ['', ''],
    multipleAnswers: multipleAnswers
  })
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个题目吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteQuestion(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (!selectedRows.value.length) {
    ElMessage.warning('请选择要删除的题目')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个题目吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const ids = selectedRows.value.map(row => row.id)
    await batchDeleteQuestion(ids)
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 题目类型改变
const handleTypeChange = (type) => {
  if (type === 3) {
    // 判断题
    form.options = []
    form.answer = '正确'
    form.multipleAnswers = []
  } else if ([1, 2].includes(type)) {
    // 选择题
    form.options = ['', '']
    form.answer = ''
    form.multipleAnswers = []
  } else {
    // 问答题、填空题
    form.options = []
    form.answer = ''
    form.multipleAnswers = []
  }
}

// 添加选项
const addOption = () => {
  form.options.push('')
}

// 删除选项
const removeOption = (index) => {
  if (form.options.length > 2) {
    form.options.splice(index, 1)
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    // 处理选择题选项验证
    if ([1, 2].includes(form.type)) {
      const validOptions = form.options.filter(option => option.trim())
      if (validOptions.length < 2) {
        ElMessage.error('选择题至少需要2个选项')
        return
      }
      form.options = validOptions
    }
    
    // 处理多选题答案
    if (form.type === 2) {
      if (form.multipleAnswers.length === 0) {
        ElMessage.error('多选题至少需要选择一个正确答案')
        return
      }
      // 将多选答案数组转换为字符串
      form.answer = form.multipleAnswers.join('、')
    }
    
    // 验证单选题答案
    if (form.type === 1 && !form.answer) {
      ElMessage.error('请选择正确答案')
      return
    }
    
    if (form.id) {
      await updateQuestion(form)
      ElMessage.success('更新成功')
    } else {
      await createQuestion(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 对话框关闭
const handleDialogClose = () => {
  resetForm()
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: null,
    bankId: bankId.value || (questionBanks.value.length > 0 ? questionBanks.value[0].id : null),
    title: '',
    content: '',
    type: null,
    options: ['', ''],
    answer: '',
    multipleAnswers: [],
    analysis: '',
    difficulty: 1,
    score: 1,
    status: 1
  })
  formRef.value?.clearValidate()
}

// 选择变化处理
const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

// 显示AI生成对话框
const showAiGenerateDialog = () => {
  if (!bankId.value && questionBanks.value.length === 0) {
    ElMessage.warning('请先创建题库')
    return
  }
  aiForm.bankId = bankId.value || (questionBanks.value.length > 0 ? questionBanks.value[0].id : null)
  aiDialogVisible.value = true
}

// AI预览生成
const handleAiPreview = async () => {
  if (!aiFormRef.value) return
  
  try {
    await aiFormRef.value.validate()
    aiPreviewLoading.value = true
    
    const response = await extractQuestionsPreview(aiForm)
    aiPreviewQuestions.value = response.data
    aiPreviewDialogVisible.value = true
    
    if (response.data.length > 0) {
      ElMessage.success(`成功提取到${response.data.length}道题目`)
    } else {
      ElMessage.warning('未能提取到题目，请检查输入内容格式')
    }
  } catch (error) {
    console.error('提取题目失败:', error)
    if (error.response && error.response.data && error.response.data.message) {
      const errorMsg = error.response.data.message
      if (errorMsg.includes('超时') || errorMsg.includes('timeout')) {
        ElMessage.error('AI服务响应超时，请稍后重试')
      } else if (errorMsg.includes('连接') || errorMsg.includes('connection')) {
        ElMessage.error('网络连接异常，请检查网络后重试')
      } else if (errorMsg.includes('解析') || errorMsg.includes('JSON') || errorMsg.includes('token')) {
        ElMessage.error('内容过长导致解析失败，建议使用"AI智能题目提取"页面进行分段处理')
      } else {
        ElMessage.error(errorMsg)
      }
    } else if (error.message) {
      if (error.message.includes('解析') || error.message.includes('JSON') || error.message.includes('token')) {
        ElMessage.error('内容过长导致解析失败，建议使用"AI智能题目提取"页面进行分段处理')
      } else {
        ElMessage.error(error.message)
      }
    } else {
      ElMessage.error('提取题目失败，请稍后重试')
    }
  } finally {
    aiPreviewLoading.value = false
  }
}

// AI直接生成并添加
const handleAiGenerateAndAdd = async () => {
  if (!aiFormRef.value) return
  
  try {
    await aiFormRef.value.validate()
    
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
      aiLoading.value = true
      
      const response = await extractAndAddQuestions(aiForm)
      const questionIds = response.data
      
      ElMessage.success(`成功提取并添加了${questionIds.length}道题目`)
      aiDialogVisible.value = false
      resetAiForm()
      fetchData()
    }
  } catch (error) {
    console.error('提取并添加题目失败:', error)
    if (error !== 'cancel') {
      if (error.response && error.response.data && error.response.data.message) {
        const errorMsg = error.response.data.message
        if (errorMsg.includes('超时') || errorMsg.includes('timeout')) {
          ElMessage.error('AI服务响应超时，请稍后重试')
        } else if (errorMsg.includes('连接') || errorMsg.includes('connection')) {
          ElMessage.error('网络连接异常，请检查网络后重试')
        } else if (errorMsg.includes('解析') || errorMsg.includes('JSON') || errorMsg.includes('token')) {
          ElMessage.error('内容过长导致解析失败，建议使用"AI智能题目提取"页面进行分段处理')
        } else {
          ElMessage.error(errorMsg)
        }
      } else if (error.message) {
        if (error.message.includes('解析') || error.message.includes('JSON') || error.message.includes('token')) {
          ElMessage.error('内容过长导致解析失败，建议使用"AI智能题目提取"页面进行分段处理')
        } else {
          ElMessage.error(error.message)
        }
      } else {
        ElMessage.error('操作失败，请稍后重试')
      }
    }
  } finally {
    aiLoading.value = false
  }
}

// 确认添加预览的题目
const handleAiConfirmAdd = async () => {
  try {
    aiLoading.value = true
    
    const response = await extractAndAddQuestions(aiForm)
    const questionIds = response.data
    
    ElMessage.success(`成功添加了${questionIds.length}道题目到题库`)
    aiPreviewDialogVisible.value = false
    aiDialogVisible.value = false
    resetAiForm()
    fetchData()
  } catch (error) {
    console.error('添加题目失败:', error)
    if (error.response && error.response.data && error.response.data.message) {
      const errorMsg = error.response.data.message
      if (errorMsg.includes('超时') || errorMsg.includes('timeout')) {
        ElMessage.error('AI服务响应超时，请稍后重试')
      } else if (errorMsg.includes('连接') || errorMsg.includes('connection')) {
        ElMessage.error('网络连接异常，请检查网络后重试')
      } else if (errorMsg.includes('解析') || errorMsg.includes('JSON') || errorMsg.includes('token')) {
        ElMessage.error('内容过长导致解析失败，建议使用"AI智能题目提取"页面进行分段处理')
      } else {
        ElMessage.error(errorMsg)
      }
    } else if (error.message) {
      if (error.message.includes('解析') || error.message.includes('JSON') || error.message.includes('token')) {
        ElMessage.error('内容过长导致解析失败，建议使用"AI智能题目提取"页面进行分段处理')
      } else {
        ElMessage.error(error.message)
      }
    } else {
      ElMessage.error('添加题目失败，请稍后重试')
    }
  } finally {
    aiLoading.value = false
  }
}

// 重置AI表单
const resetAiForm = () => {
  aiForm.content = ''
  aiPreviewQuestions.value = []
  aiFormRef.value?.clearValidate()
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

// 初始化
onMounted(async () => {
  await fetchQuestionBanks()
  fetchData()
})
</script>

<style scoped>
.question-manage {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.search-area {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.search-form {
  margin: 0;
}

.table-area {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.batch-actions {
  margin-top: 10px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.options-container {
  width: 100%;
}

.option-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  gap: 10px;
}

.option-item .el-input {
  flex: 1;
}

.dialog-footer {
  text-align: right;
}

.preview-content {
  max-height: 600px;
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
  align-items: center;
  gap: 10px;
}

.question-number {
  font-weight: bold;
  color: #409eff;
}

.question-score {
  margin-left: auto;
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
</style> 