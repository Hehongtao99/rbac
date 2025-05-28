<template>
  <div class="advertisement-add-form">
    <el-row :gutter="24" class="form-row">
      <!-- 左侧区域树 -->
      <el-col :span="7">
        <el-card class="tree-card" shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><LocationInformation /></el-icon>
              <span>选择区域</span>
            </div>
          </template>
          <el-tree
            ref="regionTreeRef"
            :data="regionTree"
            :props="treeProps"
            node-key="id"
            :highlight-current="true"
            :expand-on-click-node="false"
            @node-click="handleNodeClick"
            class="region-tree"
          >
            <template #default="{ node, data }">
              <span class="tree-node">
                <el-icon class="node-icon" :class="getNodeIconClass(node)">
                  <component :is="getNodeIcon(node)" />
                </el-icon>
                <span class="node-text">{{ data.name }}</span>
                <el-tag v-if="data.regionType" :type="getRegionTypeTag(data.regionType)" size="small" class="node-tag">
                  {{ data.regionType }}
                </el-tag>
              </span>
            </template>
          </el-tree>
        </el-card>
      </el-col>

      <!-- 右侧内容区域 -->
      <el-col :span="17">
        <div class="content-area">
          <!-- 选中区域信息 -->
          <el-card v-if="selectedRegion" class="info-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><Location /></el-icon>
                <span>{{ selectedRegion.name }} - 区域信息</span>
              </div>
            </template>
            <el-row :gutter="16">
              <el-col :span="6">
                <div class="info-item">
                  <label>区域名称</label>
                  <div class="info-value">{{ selectedRegion.name }}</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="info-item">
                  <label>区域类型</label>
                  <div class="info-value">
                    <el-tag :type="getRegionTypeTag(selectedRegion.regionType)" size="small">
                      {{ selectedRegion.regionType || '未知' }}
                    </el-tag>
                  </div>
                </div>
              </el-col>
              <el-col :span="6" v-if="selectedRegion.longitude">
                <div class="info-item">
                  <label>经纬度</label>
                  <div class="info-value">{{ selectedRegion.longitude }}, {{ selectedRegion.latitude }}</div>
                </div>
              </el-col>
              <el-col :span="6" v-if="selectedRegion.regionImage">
                <div class="info-item">
                  <label>区域图片</label>
                  <div class="info-value">
                    <el-image 
                      :src="getImageUrl(selectedRegion.regionImage)" 
                      class="region-image"
                      fit="cover"
                      :preview-src-list="[getImageUrl(selectedRegion.regionImage)]"
                    />
                  </div>
                </div>
              </el-col>
            </el-row>
          </el-card>

          <!-- 存量广告位 -->
          <el-card v-if="positionOptions.length > 0" class="info-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><Grid /></el-icon>
                <span>存量广告位 ({{ positionOptions.length }}个)</span>
                <el-button 
                  size="small" 
                  type="primary" 
                  :icon="Refresh" 
                  @click="refreshPositions"
                  class="refresh-btn"
                >
                  刷新状态
                </el-button>
              </div>
            </template>
            <el-table 
              :data="positionOptions" 
              class="position-table"
              @row-click="selectPosition"
              :row-class-name="getRowClassName"
            >
              <el-table-column prop="code" label="编号" width="100" />
              <el-table-column prop="positionName" label="广告位置" show-overflow-tooltip />
              <el-table-column prop="area" label="面积(㎡)" width="100" align="center" />
              <el-table-column prop="applicationStatusText" label="申请状态" width="100" align="center">
                <template #default="scope">
                  <el-tag :type="getApplicationStatusTag(scope.row.applicationStatus)" size="small">
                    {{ scope.row.applicationStatusText }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80" align="center">
                <template #default="scope">
                  <el-icon v-if="selectedPosition && selectedPosition.id === scope.row.id" class="selected-icon">
                    <Check />
                  </el-icon>
                  <el-button 
                    v-else-if="scope.row.applicationStatus === 'AVAILABLE' || !scope.row.applicationStatus"
                    size="small" 
                    type="primary" 
                    @click.stop="selectPosition(scope.row)"
                  >
                    选择
                  </el-button>
                  <span v-else class="unavailable-text">不可选</span>
                </template>
              </el-table-column>
            </el-table>
          </el-card>

          <!-- 选中广告位详情 -->
          <el-card v-if="selectedPosition" class="info-card selected-card" shadow="always">
            <template #header>
              <div class="card-header">
                <el-icon><Star /></el-icon>
                <span>已选广告位 - {{ selectedPosition.code }}</span>
              </div>
            </template>
            <el-row :gutter="16">
              <el-col :span="12">
                <div class="detail-item">
                  <el-icon><Flag /></el-icon>
                  <div>
                    <label>编号</label>
                    <div class="detail-value">{{ selectedPosition.code }}</div>
                  </div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="detail-item">
                  <el-icon><Expand /></el-icon>
                  <div>
                    <label>面积</label>
                    <div class="detail-value">{{ selectedPosition.area }}㎡</div>
                  </div>
                </div>
              </el-col>
            </el-row>
            <el-divider />
            <el-row :gutter="16">
              <el-col :span="8">
                <div class="detail-item">
                  <el-icon><Compass /></el-icon>
                  <div>
                    <label>坐标</label>
                    <div class="detail-value">{{ selectedPosition.longitude }}, {{ selectedPosition.latitude }}</div>
                  </div>
                </div>
              </el-col>
              <el-col :span="16" v-if="selectedPosition.positionImage">
                <div class="detail-item">
                  <el-icon><Picture /></el-icon>
                  <div>
                    <label>实图</label>
                    <div class="detail-value">
                      <el-image 
                        :src="getImageUrl(selectedPosition.positionImage)" 
                        class="position-image"
                        fit="cover"
                        :preview-src-list="[getImageUrl(selectedPosition.positionImage)]"
                      />
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </el-card>

          <!-- 申请表单 -->
          <el-form v-if="selectedPosition" :model="form" ref="formRef" label-width="120px">
            <!-- 街道信息展示 -->
            <el-card class="form-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <el-icon><LocationInformation /></el-icon>
                  <span>街道信息</span>
                </div>
              </template>
              <el-row :gutter="20">
                <el-col :span="8">
                  <div class="info-item">
                    <label>街道名称</label>
                    <div class="info-value">{{ selectedRegion.name }}</div>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="info-item">
                    <label>完整名称</label>
                    <div class="info-value">{{ selectedRegion.fullName || '-' }}</div>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="info-item">
                    <label>街道编码</label>
                    <div class="info-value">{{ selectedRegion.code || '-' }}</div>
                  </div>
                </el-col>
              </el-row>
              <el-row :gutter="20" v-if="selectedRegion.longitude && selectedRegion.latitude">
                <el-col :span="6">
                  <div class="info-item">
                    <label>经度</label>
                    <div class="info-value">{{ selectedRegion.longitude }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="info-item">
                    <label>纬度</label>
                    <div class="info-value">{{ selectedRegion.latitude }}</div>
                  </div>
                </el-col>
                <el-col :span="12" v-if="selectedRegion.description">
                  <div class="info-item">
                    <label>描述</label>
                    <div class="info-value">{{ selectedRegion.description }}</div>
                  </div>
                </el-col>
              </el-row>
              <!-- 街道图片展示 -->
              <el-row v-if="selectedRegion.regionImage" :gutter="20">
                <el-col :span="24">
                  <div class="info-item">
                    <label>街道图片</label>
                    <div class="info-value">
                      <el-image 
                        :src="getImageUrl(selectedRegion.regionImage)" 
                        class="street-image"
                        fit="cover"
                        :preview-src-list="[getImageUrl(selectedRegion.regionImage)]"
                      />
                    </div>
                  </div>
                </el-col>
              </el-row>
              <!-- 广告位图片展示 -->
              <el-row v-if="selectedPosition && selectedPosition.positionImages && selectedPosition.positionImages.length > 0" :gutter="20">
                <el-col :span="24">
                  <div class="info-item">
                    <label>广告位实景图片</label>
                    <div class="info-value">
                      <div class="position-images-grid">
                        <div v-for="(image, index) in selectedPosition.positionImages" :key="index" class="image-wrapper">
                          <el-image 
                            :src="getImageUrl(image)" 
                            class="position-grid-image"
                            fit="cover"
                            :preview-src-list="selectedPosition.positionImages.map(img => getImageUrl(img))"
                            @error="handleImageError"
                            @load="handleImageLoad"
                          >
                            <template #error>
                              <div class="image-error">
                                <el-icon><Picture /></el-icon>
                                <div>加载失败</div>
                                <div class="error-url">{{ getImageUrl(image) }}</div>
                              </div>
                            </template>
                          </el-image>
                        </div>
                      </div>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </el-card>
            
            <!-- 申请信息 -->
            <el-card class="form-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <el-icon><Edit /></el-icon>
                  <span>申请信息</span>
                </div>
              </template>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="广告设置类型" prop="adSettingType" required>
                    <el-select v-model="form.adSettingType" placeholder="请选择设置类型" style="width: 100%">
                      <el-option label="大型立柱广告" value="大型立柱广告" />
                      <el-option label="楼体广告" value="楼体广告" />
                      <el-option label="候车亭广告" value="候车亭广告" />
                      <el-option label="LED显示屏" value="LED显示屏" />
                      <el-option label="横幅广告" value="横幅广告" />
                      <el-option label="其他" value="其他" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="广告性质" prop="adNature" required>
                    <el-select v-model="form.adNature" placeholder="请选择广告性质" style="width: 100%">
                      <el-option label="商业广告" value="商业广告" />
                      <el-option label="公益广告" value="公益广告" />
                      <el-option label="旅游宣传" value="旅游宣传" />
                      <el-option label="政府公告" value="政府公告" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="总面积(㎡)" prop="totalArea" required>
                    <el-input-number 
                      v-model="form.totalArea" 
                      :precision="2" 
                      :min="0" 
                      placeholder="请输入总面积"
                      style="width: 100%"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="规格" prop="specification">
                    <el-input v-model="form.specification" placeholder="如：4m×5m" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="数量(块)" prop="quantity" required>
                    <el-input-number 
                      v-model="form.quantity" 
                      :min="1" 
                      placeholder="请输入数量"
                      style="width: 100%"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <!-- 预留空间 -->
                </el-col>
              </el-row>
            </el-card>

            <!-- 申请材料 -->
            <el-card class="form-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <el-icon><Document /></el-icon>
                  <span>申请材料（可选）</span>
                </div>
              </template>
              
              <div class="upload-grid">
                <div class="upload-item" v-for="(item, key) in uploadItems" :key="key">
                  <div class="upload-header">
                    <el-icon><Document /></el-icon>
                    <span>{{ item.label }}（可选）</span>
                    <el-tag v-if="form[key]" type="success" size="small">已上传</el-tag>
                  </div>
                  <el-upload
                    :action="uploadAction"
                    :headers="uploadHeaders"
                    :on-success="(response) => handleUploadSuccess(response, key)"
                    :on-error="(error) => handleUploadError(error, key)"
                    :before-upload="beforeUpload"
                    :show-file-list="false"
                    accept=".pdf,.doc,.docx,.jpg,.jpeg,.png"
                    class="upload-component"
                  >
                    <el-button type="primary" :icon="Upload" size="small">
                      {{ form[key] ? '重新上传' : '上传文件' }}
                    </el-button>
                  </el-upload>
                  <div v-if="form[key]" class="uploaded-file">
                    <el-icon><DocumentChecked /></el-icon>
                    <span>{{ getFileName(form[key], key) }}</span>
                    <el-button size="small" text type="danger" @click="removeFile(key)">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                </div>
              </div>
            </el-card>
          </el-form>

          <!-- 操作按钮 -->
          <div v-if="selectedPosition" class="form-actions">
            <el-button @click="handleCancel" size="large">取消</el-button>
            <el-button type="primary" @click="handleSubmit" :loading="submitting" size="large">
              <el-icon><Check /></el-icon>
              提交申请
            </el-button>
          </div>

          <!-- 空状态 -->
          <el-empty v-if="!selectedRegion" description="请从左侧选择区域" />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Upload, Document, DocumentChecked, Delete, Location, LocationInformation, 
  Grid, Star, Flag, Expand, Collection, Compass, Picture, Edit, Check, Refresh 
} from '@element-plus/icons-vue'
import { getOrganizationTree } from '../api/organization'
import { getPositionsByRegionId } from '../api/advertisement'
import { saveApplication } from '../api/advertisement'

export default {
  name: 'AdvertisementAddForm',
  components: {
    Upload, Document, DocumentChecked, Delete, Location, LocationInformation,
    Grid, Star, Flag, Expand, Collection, Compass, Picture, Edit, Check, Refresh
  },
  emits: ['success', 'cancel'],
  setup(props, { emit }) {
    const formRef = ref()
    const regionTreeRef = ref()
    const submitting = ref(false)
    const regionTree = ref([])
    const positionOptions = ref([])
    const selectedRegion = ref(null)
    const selectedPosition = ref(null)
    const refreshTimer = ref(null) // 定时刷新定时器

    // 上传配置
    const uploadAction = '/api/admin/upload'
    const uploadHeaders = {
      'Authorization': `Bearer ${localStorage.getItem('token') || ''}`
    }

    const form = reactive({
      positionId: null,
      adSettingType: '',
      adNature: '',
      totalArea: null,
      specification: '',
      quantity: 1,
      applicationFormUrl: '',
      safetyCommitmentUrl: '',
      siteAgreementUrl: '',
      effectDrawingUrl: '',
      structureDesignUrl: '',
      originalDrawingUrl: '',
      propertyCertificateUrl: '',
      applicationFormUrl_originalName: '',
      safetyCommitmentUrl_originalName: '',
      siteAgreementUrl_originalName: '',
      effectDrawingUrl_originalName: '',
      structureDesignUrl_originalName: '',
      originalDrawingUrl_originalName: '',
      propertyCertificateUrl_originalName: ''
    })

    // 上传项目配置
    const uploadItems = {
      applicationFormUrl: { label: '申请表' },
      safetyCommitmentUrl: { label: '安全承诺书' },
      siteAgreementUrl: { label: '场地协议' },
      effectDrawingUrl: { label: '实景效果图' },
      structureDesignUrl: { label: '结构设计图' },
      originalDrawingUrl: { label: '建筑物原貌图' },
      propertyCertificateUrl: { label: '产权证书' }
    }

    const treeProps = {
      children: 'children',
      label: 'name'
    }

    // 获取节点图标
    const getNodeIcon = (node) => {
      return node.isLeaf ? 'Location' : 'LocationInformation'
    }

    // 获取节点图标样式
    const getNodeIconClass = (node) => {
      return node.isLeaf ? 'leaf-icon' : 'folder-icon'
    }

    // 获取区域类型标签样式
    const getRegionTypeTag = (type) => {
      const typeMap = {
        '省': 'danger',
        '市': 'warning',
        '街道': 'success'
      }
      return typeMap[type] || ''
    }

    // 获取广告性质标签样式
    const getAdNatureTag = (nature) => {
      const natureMap = {
        '商业广告': 'primary',
        '公益广告': 'success',
        '旅游宣传': 'warning',
        '政府公告': 'danger'
      }
      return natureMap[nature] || ''
    }

    // 获取申请状态标签样式
    const getApplicationStatusTag = (status) => {
      const statusMap = {
        'AVAILABLE': 'success',
        'APPLIED': 'warning',
        'APPROVED': 'info',
        'REJECTED': 'danger'
      }
      return statusMap[status] || 'success'
    }

    // 获取表格行样式
    const getRowClassName = ({ row }) => {
      return selectedPosition.value && selectedPosition.value.id === row.id ? 'selected-row' : ''
    }

    // 获取图片完整URL
    const getImageUrl = (imagePath) => {
      console.log('getImageUrl 输入:', imagePath)
      if (!imagePath) {
        console.log('getImageUrl 输出: 空路径')
        return ''
      }
      if (imagePath.startsWith('http')) {
        console.log('getImageUrl 输出: 已是完整URL -', imagePath)
        return imagePath
      }
      const fullUrl = `http://localhost:8080${imagePath}`
      console.log('getImageUrl 输出: 拼接后的URL -', fullUrl)
      return fullUrl
    }

    // 获取文件名
    const getFileName = (url, field) => {
      if (!url) return ''
      
      // 优先使用存储的原始文件名
      const originalNameKey = field + '_originalName'
      if (form[originalNameKey]) {
        return form[originalNameKey]
      }
      
      // 如果没有原始文件名，从URL中提取
      return url.split('/').pop()
    }

    // 解析图片数组
    const parseImages = (imageStr) => {
      console.log('parseImages 输入:', imageStr, '类型:', typeof imageStr)
      
      if (!imageStr) {
        console.log('parseImages 输出: 空数组 (输入为空)')
        return []
      }
      
      // 如果是字符串，尝试解析JSON，如果失败则作为单个图片URL处理
      if (typeof imageStr === 'string') {
        // 如果以 [ 开头，说明是JSON数组格式
        if (imageStr.trim().startsWith('[')) {
          try {
            const parsed = JSON.parse(imageStr)
            // 处理路径中的转义字符
            const cleanedPaths = parsed.map(path => {
              if (typeof path === 'string') {
                return path.replace(/\\\//g, '/')  // 将 \/ 替换为 /
              }
              return path
            })
            console.log('parseImages 输出: JSON解析成功 (已清理路径) -', cleanedPaths)
            return cleanedPaths
          } catch (error) {
            console.warn('解析图片JSON失败:', error)
            console.log('parseImages 输出: 空数组 (JSON解析失败)')
            return []
          }
        } else {
          // 单个图片URL，也需要处理转义字符
          const cleanedPath = imageStr.replace(/\\\//g, '/')
          const result = [cleanedPath]
          console.log('parseImages 输出: 单个URL转数组 (已清理路径) -', result)
          return result
        }
      }
      
      // 如果已经是数组，直接返回
      if (Array.isArray(imageStr)) {
        console.log('parseImages 输出: 已是数组 -', imageStr)
        return imageStr
      }
      
      console.log('parseImages 输出: 空数组 (未知类型)')
      return []
    }

    // 加载组织树
    const loadRegionTree = async () => {
      try {
        const response = await getOrganizationTree()
        if (response.code === 200) {
          regionTree.value = response.data
        }
      } catch (error) {
        console.error('加载区域树失败', error)
      }
    }

    // 处理树节点点击
    const handleNodeClick = async (data, node) => {
      selectedRegion.value = data
      
      // 加载该区域的广告位
      try {
        const response = await getPositionsByRegionId(data.id)
        if (response.code === 200) {
          // 处理图片数组显示
          positionOptions.value = response.data.map(item => {
            const positionImages = parseImages(item.positionImage)
            console.log('广告位图片数据:', {
              id: item.id,
              code: item.code,
              originalImage: item.positionImage,
              parsedImages: positionImages
            })
            return {
              ...item,
              positionImages: positionImages
            }
          })
        }
      } catch (error) {
        ElMessage.error('加载广告位失败')
        positionOptions.value = []
      }
      
      // 清空已选择的广告位
      selectedPosition.value = null
      form.positionId = null
      
      // 启动定时刷新
      startAutoRefresh()
    }

    // 选择广告位
    const selectPosition = (position) => {
      // 检查广告位是否可申请
      if (position.applicationStatus && position.applicationStatus !== 'AVAILABLE') {
        ElMessage.warning('该广告位不可申请')
        return
      }
      
      selectedPosition.value = position
      form.positionId = position.id
      console.log('选中的广告位:', {
        id: position.id,
        code: position.code,
        applicationStatus: position.applicationStatus,
        positionImages: position.positionImages,
        hasImages: position.positionImages && position.positionImages.length > 0
      })
    }

    // 文件上传前校验
    const beforeUpload = (file) => {
      console.log('准备上传文件:', {
        name: file.name,
        size: file.size,
        type: file.type,
        uploadAction: uploadAction,
        headers: uploadHeaders
      })
      
      const isLt10M = file.size / 1024 / 1024 < 10
      if (!isLt10M) {
        ElMessage.error('文件大小不能超过 10MB!')
        return false
      }
      return true
    }

    // 文件上传成功处理
    const handleUploadSuccess = (response, field) => {
      console.log('上传响应:', response)
      if (response.code === 200) {
        // 新的返回格式包含url和originalName
        if (typeof response.data === 'object' && response.data.url) {
          form[field] = response.data.url
          // 存储原始文件名用于显示
          form[field + '_originalName'] = response.data.originalName
        } else {
          // 兼容旧格式
          form[field] = response.data
        }
        ElMessage.success('文件上传成功')
      } else {
        ElMessage.error(response.message || '文件上传失败')
      }
    }

    // 文件上传错误处理
    const handleUploadError = (error, field) => {
      console.error('上传错误:', error)
      ElMessage.error('文件上传失败，请重试')
    }

    // 删除文件
    const removeFile = (field) => {
      form[field] = ''
      // 同时清除原始文件名
      const originalNameKey = field + '_originalName'
      if (form[originalNameKey]) {
        form[originalNameKey] = ''
      }
    }

    // 表单校验
    const validateForm = () => {
      if (!form.positionId) {
        ElMessage.warning('请选择广告位')
        return false
      }
      if (!form.adSettingType) {
        ElMessage.warning('请选择户外广告设置类型')
        return false
      }
      if (!form.adNature) {
        ElMessage.warning('请选择广告性质')
        return false
      }
      if (!form.totalArea) {
        ElMessage.warning('请输入总面积')
        return false
      }
      if (!form.quantity) {
        ElMessage.warning('请输入数量')
        return false
      }
      
      // 移除文件必需校验，文件上传变为可选
      // const requiredFiles = Object.keys(uploadItems)
      // for (let field of requiredFiles) {
      //   if (!form[field]) {
      //     ElMessage.warning('请上传所有必需的申请材料')
      //     return false
      //   }
      // }
      
      return true
    }

    // 提交表单
    const handleSubmit = async () => {
      if (!validateForm()) {
        return
      }

      submitting.value = true
      try {
        const response = await saveApplication(form)
        if (response.code === 200) {
          emit('success')
        } else {
          ElMessage.error(response.message || '提交失败')
        }
      } catch (error) {
        ElMessage.error('提交失败')
      } finally {
        submitting.value = false
      }
    }

    // 取消
    const handleCancel = () => {
      resetForm()
      emit('cancel')
    }

    // 重置表单
    const resetForm = () => {
      // 停止定时刷新
      stopAutoRefresh()
      
      // 重置所有表单字段
      Object.keys(form).forEach(key => {
        if (key.includes('_originalName')) {
          form[key] = ''
        } else if (key === 'quantity') {
          form[key] = 1
        } else if (typeof form[key] === 'number') {
          form[key] = null
        } else {
          form[key] = ''
        }
      })
      
      // 清空选择状态
      selectedRegion.value = null
      selectedPosition.value = null
      positionOptions.value = []
      
      // 重置树选择
      if (regionTreeRef.value) {
        regionTreeRef.value.setCurrentKey(null)
      }
    }

    // 图片加载错误处理
    const handleImageError = (event) => {
      console.error('图片加载失败:', event.target.src)
      console.error('原始图片数据:', selectedPosition.value?.positionImages)
      console.error('当前选中的广告位:', selectedPosition.value)
    }

    // 图片加载成功处理
    const handleImageLoad = (event) => {
      console.log('图片加载成功:', event.target.src)
    }

    // 刷新广告位状态（供父组件调用）
    const refreshPositions = async () => {
      if (selectedRegion.value) {
        console.log('刷新广告位状态，当前区域:', selectedRegion.value.name)
        try {
          const response = await getPositionsByRegionId(selectedRegion.value.id)
          if (response.code === 200) {
            // 处理图片数组显示
            const newPositions = response.data.map(item => {
              const positionImages = parseImages(item.positionImage)
              return {
                ...item,
                positionImages: positionImages
              }
            })
            
            positionOptions.value = newPositions
            
            // 如果当前选中的广告位状态发生变化，需要更新或清空选择
            if (selectedPosition.value) {
              const updatedPosition = newPositions.find(p => p.id === selectedPosition.value.id)
              if (updatedPosition) {
                // 检查状态是否变为不可申请
                if (updatedPosition.applicationStatus && updatedPosition.applicationStatus !== 'AVAILABLE') {
                  // 状态变为不可申请，清空选择
                  selectedPosition.value = null
                  form.positionId = null
                  ElMessage.info('所选广告位状态已变更，请重新选择')
                } else {
                  // 更新选中的广告位信息
                  selectedPosition.value = updatedPosition
                }
              } else {
                // 广告位不存在了，清空选择
                selectedPosition.value = null
                form.positionId = null
                ElMessage.info('所选广告位已不存在，请重新选择')
              }
            }
            
            console.log('广告位状态刷新完成，共', newPositions.length, '个广告位')
          }
        } catch (error) {
          console.error('刷新广告位失败:', error)
          ElMessage.error('刷新广告位状态失败')
        }
      }
    }

    // 启动定时刷新
    const startAutoRefresh = () => {
      // 清除现有定时器
      if (refreshTimer.value) {
        clearInterval(refreshTimer.value)
      }
      
      // 设置新的定时器，每30秒刷新一次
      refreshTimer.value = setInterval(() => {
        if (selectedRegion.value) {
          console.log('定时刷新广告位状态')
          refreshPositions()
        }
      }, 30000) // 30秒
    }

    // 停止定时刷新
    const stopAutoRefresh = () => {
      if (refreshTimer.value) {
        clearInterval(refreshTimer.value)
        refreshTimer.value = null
      }
    }

    onMounted(() => {
      loadRegionTree()
      // 移除自动重置，让父组件控制重置时机
      // resetForm() // 组件挂载时重置表单
    })

    onUnmounted(() => {
      // 组件销毁时清理定时器
      stopAutoRefresh()
    })

    return {
      formRef,
      regionTreeRef,
      submitting,
      regionTree,
      positionOptions,
      selectedRegion,
      selectedPosition,
      form,
      uploadItems,
      treeProps,
      uploadAction,
      uploadHeaders,
      getNodeIcon,
      getNodeIconClass,
      getRegionTypeTag,
      getAdNatureTag,
      getApplicationStatusTag,
      getRowClassName,
      getImageUrl,
      getFileName,
      handleNodeClick,
      selectPosition,
      beforeUpload,
      handleUploadSuccess,
      handleUploadError,
      removeFile,
      handleSubmit,
      handleCancel,
      resetForm,
      handleImageError,
      handleImageLoad,
      refreshPositions,
      startAutoRefresh,
      stopAutoRefresh
    }
  }
}
</script>

<style scoped>
.advertisement-add-form {
  padding: 20px;
  background: #f5f7fa;
}

.form-row {
  min-height: 600px;
}

/* 左侧树形卡片 */
.tree-card {
  height: 600px;
  border-radius: 8px;
}

.tree-card :deep(.el-card__body) {
  padding: 0;
  height: calc(100% - 57px);
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.refresh-btn {
  margin-left: auto;
}

.region-tree {
  height: 100%;
  overflow-y: auto;
  padding: 16px;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.node-icon {
  font-size: 16px;
}

.leaf-icon {
  color: #67c23a;
}

.folder-icon {
  color: #409eff;
}

.node-text {
  flex: 1;
  font-size: 14px;
}

.node-tag {
  margin-left: auto;
}

/* 右侧内容区域 */
.content-area {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 600px;
  overflow-y: auto;
  padding-right: 8px;
}

/* 信息卡片 */
.info-card {
  border-radius: 8px;
  transition: all 0.3s;
}

.selected-card {
  border: 2px solid #409eff;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
}

.info-item {
  text-align: center;
}

.info-item label {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.info-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.region-image {
  width: 60px;
  height: 45px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
}

/* 广告位表格 */
.position-table {
  margin-top: 8px;
}

.position-table :deep(.selected-row) {
  background-color: #ecf5ff !important;
}

.position-table :deep(.el-table__row) {
  cursor: pointer;
}

.position-table :deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

.selected-icon {
  color: #67c23a;
  font-size: 16px;
}

.select-text {
  color: #909399;
  font-size: 12px;
}

.unavailable-text {
  color: #c0c4cc;
  font-size: 12px;
}

/* 详情项目 */
.detail-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 8px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 4px;
}

.detail-item .el-icon {
  color: #409eff;
  margin-top: 2px;
}

.detail-item label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.detail-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.position-image {
  width: 120px;
  height: 80px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
}

/* 表单卡片 */
.form-card {
  border-radius: 8px;
}

/* 街道图片样式 */
.street-image {
  width: 200px;
  height: 150px;
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  cursor: pointer;
  transition: transform 0.3s;
}

.street-image:hover {
  transform: scale(1.05);
}

/* 广告位图片网格样式 */
.position-images-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.image-wrapper {
  position: relative;
}

.position-grid-image {
  width: 120px;
  height: 90px;
  border-radius: 6px;
  border: 1px solid #dcdfe6;
  cursor: pointer;
  transition: transform 0.3s;
}

.position-grid-image:hover {
  transform: scale(1.05);
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 90px;
  background: #f5f7fa;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  color: #909399;
  font-size: 12px;
  text-align: center;
}

.image-error .el-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.error-url {
  font-size: 10px;
  color: #c0c4cc;
  margin-top: 4px;
  word-break: break-all;
  max-width: 110px;
}

/* 上传网格 */
.upload-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.upload-item {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 16px;
  background: #fafbfc;
  transition: all 0.3s;
}

.upload-item:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.upload-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  font-weight: 500;
  color: #303133;
}

.upload-component {
  margin-bottom: 8px;
}

.uploaded-file {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  background: #f0f9ff;
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
}

.uploaded-file .el-icon:first-child {
  color: #67c23a;
}

.uploaded-file span {
  flex: 1;
}

/* 操作按钮 */
.form-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 树形组件样式优化 */
:deep(.el-tree-node__content) {
  height: 36px;
  line-height: 36px;
  border-radius: 4px;
  margin: 2px 0;
}

:deep(.el-tree-node__content:hover) {
  background-color: #f0f9ff;
}

:deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content) {
  background-color: #e6f7ff;
  color: #1890ff;
  font-weight: 600;
}

/* 滚动条样式 */
.content-area::-webkit-scrollbar,
.region-tree::-webkit-scrollbar {
  width: 6px;
}

.content-area::-webkit-scrollbar-track,
.region-tree::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.content-area::-webkit-scrollbar-thumb,
.region-tree::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.content-area::-webkit-scrollbar-thumb:hover,
.region-tree::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style> 