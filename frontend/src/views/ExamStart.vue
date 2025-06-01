<template>
  <div class="exam-start">
    <div class="header">
      <h2>开始考试</h2>
    </div>

    <div class="content">
      <el-card class="exam-card">
        <template #header>
          <div class="card-header">
            <span>选择题库进行考试</span>
          </div>
        </template>

        <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
          <el-form-item label="选择题库" prop="bankId">
            <el-select 
              v-model="form.bankId" 
              placeholder="请选择题库" 
              style="width: 100%"
              @change="handleBankChange"
            >
              <el-option 
                v-for="bank in questionBanks" 
                :key="bank.id" 
                :label="bank.name" 
                :value="bank.id"
              >
                <div class="bank-option">
                  <span>{{ bank.name }}</span>
                  <span class="bank-desc">{{ bank.description }}</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="考试标题" prop="title">
            <el-input 
              v-model="form.title" 
              placeholder="请输入考试标题"
            />
          </el-form-item>

          <el-form-item label="考试时长" prop="duration">
            <el-input-number 
              v-model="form.duration" 
              :min="10" 
              :max="300" 
              :step="10"
              controls-position="right"
            />
            <span style="margin-left: 10px;">分钟</span>
          </el-form-item>

          <div v-if="selectedBank" class="bank-info">
            <h4>题库信息</h4>
            <p><strong>题库名称：</strong>{{ selectedBank.name }}</p>
            <p><strong>题库描述：</strong>{{ selectedBank.description }}</p>
            <p><strong>题目数量：</strong>{{ selectedBank.questionCount }} 道</p>
          </div>

          <el-form-item>
            <el-button 
              type="primary" 
              @click="handleStartExam" 
              :loading="loading"
              size="large"
            >
              开始考试
            </el-button>
            <el-button @click="handleReset" size="large">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getQuestionBankList } from '@/api/questionBank'
import { startExam } from '@/api/exam'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const questionBanks = ref([])
const formRef = ref()

// 表单数据
const form = reactive({
  bankId: null,
  title: '',
  duration: 60
})

// 表单验证规则
const rules = {
  bankId: [
    { required: true, message: '请选择题库', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入考试标题', trigger: 'blur' }
  ],
  duration: [
    { required: true, message: '请设置考试时长', trigger: 'blur' },
    { type: 'number', min: 10, max: 300, message: '考试时长应在10-300分钟之间', trigger: 'blur' }
  ]
}

// 计算属性
const selectedBank = computed(() => {
  return questionBanks.value.find(bank => bank.id === form.bankId)
})

// 获取题库列表
const fetchQuestionBanks = async () => {
  try {
    const response = await getQuestionBankList({
      current: 1,
      size: 100,
      status: 1
    })
    questionBanks.value = response.data.records || []
  } catch (error) {
    ElMessage.error('获取题库列表失败')
  }
}

// 题库选择变化
const handleBankChange = (bankId) => {
  const bank = questionBanks.value.find(b => b.id === bankId)
  if (bank) {
    form.title = `${bank.name} - 考试`
  }
}

// 开始考试
const handleStartExam = async () => {
  try {
    await formRef.value.validate()
    
    loading.value = true
    const response = await startExam(form)
    
    ElMessage.success('考试已开始')
    // 跳转到考试页面
    router.push(`/exam/${response.data.id}`)
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    loading.value = false
  }
}

// 重置表单
const handleReset = () => {
  formRef.value?.resetFields()
  form.bankId = null
  form.title = ''
  form.duration = 60
}

// 初始化
onMounted(() => {
  fetchQuestionBanks()
})
</script>

<style scoped>
.exam-start {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

.content {
  max-width: 800px;
  margin: 0 auto;
}

.exam-card {
  border-radius: 8px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.bank-option {
  display: flex;
  flex-direction: column;
}

.bank-desc {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.bank-info {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 6px;
  margin: 20px 0;
}

.bank-info h4 {
  margin: 0 0 10px 0;
  color: #409eff;
}

.bank-info p {
  margin: 5px 0;
  color: #606266;
}
</style> 