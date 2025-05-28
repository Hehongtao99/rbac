<template>
  <div class="organization-management">
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h2><el-icon><OfficeBuilding /></el-icon>组织架构管理</h2>
          <p>管理省市区街道三级组织架构，支持街道级别的详细信息配置</p>
        </div>
        <el-button 
          v-if="hasPermission('organization:add')"
          type="primary" 
          @click="addRootNode" 
          icon="Plus"
          size="large"
        >
          添加省份
        </el-button>
      </div>
    </div>

    <div class="content-container">
      <!-- 左侧树形结构 -->
      <div class="tree-container">
        <div class="tree-header">
          <h3><el-icon><List /></el-icon>组织架构树</h3>
        </div>
        <el-tree
          ref="treeRef"
          :data="treeData"
          :props="treeProps"
          node-key="id"
          :expand-on-click-node="false"
          :default-expand-all="false"
          @node-click="handleNodeClick"
          class="organization-tree"
        >
          <template #default="{ node, data }">
            <div class="tree-node">
              <span class="node-label">{{ node.label }}</span>
              <div class="node-actions">
                <el-button 
                  v-if="data.level < 3 && hasPermission('organization:add')" 
                  type="text" 
                  size="small" 
                  @click.stop="addChild(data)"
                  icon="Plus"
                >
                  添加
                </el-button>
                <el-button 
                  v-if="hasPermission('organization:edit')"
                  type="text" 
                  size="small" 
                  @click.stop="editNode(data)"
                  icon="Edit"
                >
                  编辑
                </el-button>
                <el-button 
                  v-if="hasPermission('organization:delete')"
                  type="text" 
                  size="small" 
                  @click.stop="deleteNode(data)"
                  icon="Delete"
                  style="color: #f56c6c"
                >
                  删除
                </el-button>
              </div>
            </div>
          </template>
        </el-tree>
      </div>

      <!-- 右侧详情预览 -->
      <div class="detail-container">
        <div class="detail-header">
          <h3><el-icon><Document /></el-icon>详情信息</h3>
        </div>
        <div v-if="selectedNode" class="detail-content">
          <div class="detail-title">
            <el-tag :type="getLevelTagType(selectedNode.level)" size="large">
              {{ getLevelText(selectedNode.level) }}
            </el-tag>
            <h4>{{ selectedNode.name }}</h4>
          </div>
          
          <div class="detail-item">
            <label>名称：</label>
            <span>{{ selectedNode.name }}</span>
          </div>
          
          <div class="detail-item">
            <label>全称：</label>
            <span>{{ selectedNode.fullName || '-' }}</span>
          </div>
          
          <div class="detail-item">
            <label>级别：</label>
            <span>{{ getLevelText(selectedNode.level) }}</span>
          </div>
          
          <div class="detail-item">
            <label>编码：</label>
            <span>{{ selectedNode.code || '-' }}</span>
          </div>

          <!-- 街道级别显示经纬度和图片 -->
          <template v-if="selectedNode.level === 3">
            <div class="detail-item">
              <label>经度：</label>
              <span>{{ selectedNode.longitude || '-' }}</span>
            </div>
            
            <div class="detail-item">
              <label>纬度：</label>
              <span>{{ selectedNode.latitude || '-' }}</span>
            </div>
            
            <div class="detail-item">
              <label>描述：</label>
              <p>{{ selectedNode.description || '-' }}</p>
            </div>
            
            <div class="detail-item" v-if="selectedNode.regionImage">
              <label>区域图片：</label>
              <div class="images-preview">
                <el-image
                  :src="getImageUrl(selectedNode.regionImage)"
                  :preview-src-list="[getImageUrl(selectedNode.regionImage)]"
                  fit="cover"
                  class="preview-image"
                />
              </div>
            </div>

            <!-- 广告位信息 -->
            <div class="detail-item">
              <label>广告位统计：</label>
              <div class="position-stats">
                <el-tag size="large" type="info">
                  总数: {{ positionStats.total }}
                </el-tag>
                <el-tag size="large" type="success">
                  可申请: {{ positionStats.available }}
                </el-tag>
                <el-tag size="large" type="warning">
                  已申请: {{ positionStats.applied }}
                </el-tag>
                <el-tag size="large" type="primary">
                  已通过: {{ positionStats.approved }}
                </el-tag>
              </div>
            </div>

            <div class="detail-item" v-if="positionList.length > 0">
              <label>广告位列表：</label>
              <div class="position-list">
                <div 
                  v-for="position in positionList" 
                  :key="position.id" 
                  class="position-card"
                >
                  <div class="position-header">
                    <span class="position-code">{{ position.code }}</span>
                    <el-tag 
                      :type="getPositionStatusType(position.applicationStatus)" 
                      size="small"
                    >
                      {{ position.applicationStatusText }}
                    </el-tag>
                  </div>
                  <div class="position-info">
                    <div class="position-name">{{ position.positionName }}</div>
                    <div class="position-area">面积: {{ position.area }}㎡</div>
                    <div class="position-coords">
                      经纬度: {{ position.longitude }}, {{ position.latitude }}
                    </div>
                  </div>
                  <div v-if="position.positionImages && position.positionImages.length > 0" class="position-images">
                    <el-image
                      v-for="(image, index) in position.positionImages.slice(0, 2)"
                      :key="index"
                      :src="getImageUrl(image)"
                      :preview-src-list="position.positionImages.map(img => getImageUrl(img))"
                      fit="cover"
                      class="position-thumb"
                    />
                    <span v-if="position.positionImages.length > 2" class="more-images">
                      +{{ position.positionImages.length - 2 }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </template>
          
          <div class="detail-item">
            <label>状态：</label>
            <el-tag :type="selectedNode.isEnabled ? 'success' : 'danger'">
              {{ selectedNode.isEnabled ? '启用' : '禁用' }}
            </el-tag>
          </div>
        </div>
        
        <div v-else class="no-selection">
          <el-empty description="请选择一个组织节点查看详情" />
        </div>
      </div>
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      :width="form.level === 3 ? '1000px' : '600px'"
      @close="resetForm"
    >
      <!-- 街道级别使用选项卡布局 -->
      <el-tabs v-if="form.level === 3" v-model="activeTab" type="card">
        <!-- 基本信息选项卡 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-form :model="form" ref="formRef" label-width="80px">
            <el-form-item label="名称" prop="name" required>
              <el-input v-model="form.name" placeholder="请输入名称" />
            </el-form-item>
            
            <el-form-item label="全称" prop="fullName">
              <el-input v-model="form.fullName" placeholder="请输入全称" />
            </el-form-item>
            
            <el-form-item label="编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入编码" />
            </el-form-item>
            
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="form.sort" :min="0" />
            </el-form-item>

            <el-form-item label="经度" prop="longitude">
              <el-input-number 
                v-model="form.longitude" 
                :precision="6"
                placeholder="请输入经度"
                style="width: 100%"
              />
            </el-form-item>
            
            <el-form-item label="纬度" prop="latitude">
              <el-input-number 
                v-model="form.latitude" 
                :precision="6"
                placeholder="请输入纬度"
                style="width: 100%"
              />
            </el-form-item>
            
            <el-form-item label="区域图片" prop="regionImage">
              <div class="upload-container">
                <!-- 已上传图片 -->
                <div v-if="form.regionImage" class="image-item uploaded-region-image">
                  <el-image 
                    :src="getImageUrl(form.regionImage)"
                    style="width: 200px; height: 150px; border-radius: 8px;"
                    fit="cover"
                    :preview-src-list="[getImageUrl(form.regionImage)]"
                    class="uploaded-image"
                  />
                  <div class="image-actions">
                    <el-button size="small" type="danger" @click="removeRegionImage" icon="Delete" circle />
                  </div>
                </div>
                
                <!-- 上传区域 -->
                <el-upload
                  v-if="!form.regionImage"
                  :action="''"
                  :http-request="handleRegionImageUpload"
                  :show-file-list="false"
                  :before-upload="beforeUpload"
                  accept="image/*"
                  class="image-uploader"
                >
                  <div class="upload-area">
                    <el-icon class="upload-icon"><Plus /></el-icon>
                    <div class="upload-text">点击上传区域图片</div>
                  </div>
                </el-upload>
              </div>
            </el-form-item>
            
            <el-form-item label="描述" prop="description">
              <el-input 
                v-model="form.description" 
                type="textarea" 
                :rows="4"
                placeholder="请输入街道的详细描述信息..."
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
        
            <el-form-item label="状态" prop="isEnabled">
              <el-switch v-model="form.isEnabled" />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 广告位管理选项卡 -->
        <el-tab-pane v-if="isEdit" label="广告位管理" name="positions">
          <PositionManagement 
            v-if="form.id" 
            :region-id="form.id" 
            :street-name="form.name"
          />
        </el-tab-pane>
      </el-tabs>

      <!-- 非街道级别使用普通表单 -->
      <el-form v-else :model="form" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name" required>
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        
        <el-form-item label="全称" prop="fullName">
          <el-input v-model="form.fullName" placeholder="请输入全称" />
        </el-form-item>
        
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入编码" />
        </el-form-item>
        
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        
        <el-form-item label="状态" prop="isEnabled">
          <el-switch v-model="form.isEnabled" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button v-if="activeTab === 'basic' || form.level !== 3" type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getOrganizationTree, 
  saveOrganization, 
  updateOrganization, 
  deleteOrganization,
  uploadFile 
} from '../api/organization'
import { getPositionsByRegionId } from '../api/advertisement'
import { usePermissions } from '../stores/permission'
import PositionManagement from '../components/PositionManagement.vue'

export default {
  name: 'OrganizationManagement',
  components: {
    PositionManagement
  },
  setup() {
    const treeRef = ref()
    const formRef = ref()
    
    const treeData = ref([])
    const selectedNode = ref(null)
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const uploading = ref(false)
    const activeTab = ref('basic')
    
    // 广告位相关数据
    const positionList = ref([])
    const positionStats = reactive({
      total: 0,
      available: 0,
      applied: 0,
      approved: 0
    })
    
    // 使用权限检查
    const { hasPermission } = usePermissions()
    
    const treeProps = {
      children: 'children',
      label: 'name'
    }

    const form = reactive({
      id: null,
      name: '',
      fullName: '',
      code: '',
      parentId: null,
      level: 1,
      longitude: null,
      latitude: null,
      regionImage: '',
      description: '',
      sort: 0,
      isEnabled: true
    })

    const dialogTitle = ref('添加组织')

    // 级别文本映射（修改为三级）
    const getLevelText = (level) => {
      const levelMap = {
        1: '省',
        2: '市', 
        3: '街道'
      }
      return levelMap[level] || '-'
    }

    // 级别标签类型
    const getLevelTagType = (level) => {
      const tagTypeMap = {
        1: 'danger',
        2: 'warning',
        3: 'success'
      }
      return tagTypeMap[level] || ''
    }

    // 获取图片完整URL
    const getImageUrl = (imagePath) => {
      if (!imagePath) return ''
      if (imagePath.startsWith('http')) return imagePath
      return `http://localhost:8080${imagePath}`
    }

    // 上传前校验
    const beforeUpload = (file) => {
      const isImage = file.type.startsWith('image/')
      const isLt5M = file.size / 1024 / 1024 < 5

      if (!isImage) {
        ElMessage.error('只能上传图片文件!')
        return false
      }
      if (!isLt5M) {
        ElMessage.error('图片大小不能超过 5MB!')
        return false
      }
      return true
    }

    // 自定义上传区域图片
    const handleRegionImageUpload = async (options) => {
      try {
        uploading.value = true
        const response = await uploadFile(options.file)
        form.regionImage = response.data
        ElMessage.success('图片上传成功')
        // 实时更新详情信息
        if (selectedNode.value && selectedNode.value.id === form.id) {
          selectedNode.value.regionImage = form.regionImage
        }
      } catch (error) {
        ElMessage.error('图片上传失败')
        console.error(error)
      } finally {
        uploading.value = false
      }
    }

    // 删除区域图片
    const removeRegionImage = () => {
      form.regionImage = ''
      // 实时更新详情信息
      if (selectedNode.value && selectedNode.value.id === form.id) {
        selectedNode.value.regionImage = ''
      }
    }

    // 加载组织树
    const loadTree = async () => {
      try {
        const response = await getOrganizationTree()
        treeData.value = response.data
      } catch (error) {
        ElMessage.error('加载组织树失败')
        console.error(error)
      }
    }

    // 节点点击事件
    const handleNodeClick = (data) => {
      selectedNode.value = data
      // 如果是街道级别，加载广告位信息
      if (data.level === 3) {
        loadPositionData(data.id)
      } else {
        // 清空广告位数据
        positionList.value = []
        resetPositionStats()
      }
    }

    // 加载广告位数据
    const loadPositionData = async (regionId) => {
      try {
        const response = await getPositionsByRegionId(regionId)
        if (response.code === 200) {
          // 处理图片数据
          positionList.value = response.data.map(position => ({
            ...position,
            positionImages: parseImages(position.positionImage)
          }))
          // 计算统计信息
          calculatePositionStats()
        }
      } catch (error) {
        console.error('加载广告位数据失败:', error)
        positionList.value = []
        resetPositionStats()
      }
    }

    // 处理图片数据
    const parseImages = (imageData) => {
      if (!imageData) return []
      if (typeof imageData === 'string') {
        try {
          // 处理转义字符
          const cleanData = imageData.replace(/\\/g, '')
          return JSON.parse(cleanData)
        } catch {
          return [imageData] // 兼容旧的单图片格式
        }
      }
      return Array.isArray(imageData) ? imageData : []
    }

    // 计算广告位统计信息
    const calculatePositionStats = () => {
      const total = positionList.value.length
      const available = positionList.value.filter(p => p.applicationStatus === 'AVAILABLE' || !p.applicationStatus).length
      const applied = positionList.value.filter(p => p.applicationStatus === 'APPLIED').length
      const approved = positionList.value.filter(p => p.applicationStatus === 'APPROVED').length
      
      Object.assign(positionStats, {
        total,
        available,
        applied,
        approved
      })
    }

    // 重置广告位统计
    const resetPositionStats = () => {
      Object.assign(positionStats, {
        total: 0,
        available: 0,
        applied: 0,
        approved: 0
      })
    }

    // 获取广告位状态标签类型
    const getPositionStatusType = (status) => {
      const statusTypeMap = {
        'AVAILABLE': 'success',
        'APPLIED': 'warning',
        'APPROVED': 'primary',
        'REJECTED': 'info'
      }
      return statusTypeMap[status] || 'success'
    }

    // 添加根节点（省份）
    const addRootNode = () => {
      resetForm()
      form.level = 1
      form.parentId = null
      dialogTitle.value = '添加省份'
      isEdit.value = false
      activeTab.value = 'basic'
      dialogVisible.value = true
    }

    // 添加子节点
    const addChild = (parentData) => {
      resetForm()
      form.parentId = parentData.id
      form.level = parentData.level + 1
      
      const levelNames = ['', '省份', '城市', '街道']
      dialogTitle.value = `添加${levelNames[form.level]}`
      
      isEdit.value = false
      activeTab.value = 'basic'
      dialogVisible.value = true
    }

    // 编辑节点
    const editNode = (data) => {
      Object.assign(form, data)
      dialogTitle.value = '编辑组织'
      isEdit.value = true
      activeTab.value = 'basic'
      dialogVisible.value = true
    }

    // 删除节点
    const deleteNode = async (data) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除 "${data.name}" 吗？`,
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        await deleteOrganization(data.id)
        ElMessage.success('删除成功')
        await loadTree()
        
        // 如果删除的是当前选中的节点，清空选中状态
        if (selectedNode.value && selectedNode.value.id === data.id) {
          selectedNode.value = null
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error.message || '删除失败')
        }
      }
    }

    // 提交表单
    const submitForm = async () => {
      try {
        if (isEdit.value) {
          await updateOrganization(form)
          ElMessage.success('更新成功')
        } else {
          await saveOrganization(form)
          ElMessage.success('添加成功')
        }
        
        dialogVisible.value = false
        await loadTree()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }

    // 实时更新详情信息
    const updateSelectedNodeField = (field, value) => {
      if (selectedNode.value && selectedNode.value.id === form.id) {
        selectedNode.value[field] = value
      }
    }

    // 设置实时更新监听
    watch(() => form.name, (newVal) => {
      updateSelectedNodeField('name', newVal)
    })
    
    watch(() => form.fullName, (newVal) => {
      updateSelectedNodeField('fullName', newVal)
    })
    
    watch(() => form.code, (newVal) => {
      updateSelectedNodeField('code', newVal)
    })
    
    watch(() => form.longitude, (newVal) => {
      updateSelectedNodeField('longitude', newVal)
    })
    
    watch(() => form.latitude, (newVal) => {
      updateSelectedNodeField('latitude', newVal)
    })
    
    watch(() => form.description, (newVal) => {
      updateSelectedNodeField('description', newVal)
    })
    
    watch(() => form.isEnabled, (newVal) => {
      updateSelectedNodeField('isEnabled', newVal)
    })

    // 重置表单
    const resetForm = () => {
      Object.assign(form, {
        id: null,
        name: '',
        fullName: '',
        code: '',
        parentId: null,
        level: 1,
        longitude: null,
        latitude: null,
        regionImage: '',
        description: '',
        sort: 0,
        isEnabled: true
      })
    }

    onMounted(() => {
      loadTree()
    })

    return {
      treeRef,
      formRef,
      treeData,
      selectedNode,
      dialogVisible,
      isEdit,
      uploading,
      activeTab,
      treeProps,
      form,
      dialogTitle,
      getLevelText,
      getLevelTagType,
      getImageUrl,
      handleNodeClick,
      addRootNode,
      addChild,
      editNode,
      deleteNode,
      submitForm,
      resetForm,
      beforeUpload,
      handleRegionImageUpload,
      removeRegionImage,
      hasPermission,
      positionList,
      positionStats,
      getPositionStatusType
    }
  }
}
</script>

<style scoped>
.organization-management {
  padding: 24px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  color: white;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-info h2 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-info p {
  margin: 0;
  opacity: 0.9;
  font-size: 16px;
}

.content-container {
  display: flex;
  gap: 24px;
  height: calc(100vh - 250px);
}

.tree-container {
  flex: 1;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.tree-header {
  background: #f8f9fa;
  padding: 16px 20px;
  border-bottom: 1px solid #e9ecef;
}

.tree-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #495057;
  display: flex;
  align-items: center;
  gap: 8px;
}

.organization-tree {
  background: white;
  padding: 16px;
  height: calc(100% - 60px);
  overflow-y: auto;
}

.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding-right: 10px;
}

.node-label {
  flex: 1;
}

.node-actions {
  display: flex;
  gap: 5px;
}

.detail-container {
  flex: 1;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.detail-header {
  background: #f8f9fa;
  padding: 16px 20px;
  border-bottom: 1px solid #e9ecef;
}

.detail-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #495057;
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-content {
  padding: 24px;
  height: calc(100% - 60px);
  overflow-y: auto;
}

.detail-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f2f5;
}

.detail-title h4 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.detail-item {
  margin-bottom: 15px;
  display: flex;
  align-items: flex-start;
}

.detail-item label {
  font-weight: 600;
  color: #606266;
  min-width: 80px;
  margin-right: 10px;
}

.detail-item span,
.detail-item p {
  color: #303133;
  margin: 0;
}

.image-preview {
  margin-top: 5px;
}

.no-selection {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.dialog-footer {
  text-align: right;
}

/* 上传组件样式 */
.upload-container {
  width: 100%;
}

/* 已上传图片展示 */
.uploaded-images {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.image-item {
  position: relative;
  width: 120px;
  height: 90px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 区域图片专用样式 */
.uploaded-region-image {
  width: 200px !important;
  height: 150px !important;
}

.image-item .uploaded-image {
  width: 100%;
  height: 100%;
  cursor: pointer;
  transition: transform 0.3s;
}

.image-item:hover .uploaded-image {
  transform: scale(1.05);
}

.image-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.3s;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 6px;
  padding: 4px;
}

.image-item:hover .image-actions {
  opacity: 1;
}

/* 上传区域 */
.image-uploader .upload-area {
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  width: 200px;
  height: 120px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c939d;
}

.image-uploader .upload-area:hover {
  border-color: #409EFF;
}

.upload-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
  margin-bottom: 4px;
}

.upload-hint {
  font-size: 12px;
  color: #b0b8c1;
}

/* 详情预览图片样式 */
.images-preview {
  width: 100%;
}

.image-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.preview-image {
  width: 120px;
  height: 90px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.3s;
}

.preview-image:hover {
  transform: scale(1.05);
}

/* 广告位统计样式 */
.position-stats {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

/* 广告位列表样式 */
.position-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  max-height: 400px;
  overflow-y: auto;
}

.position-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 12px;
  background: #fafafa;
  transition: all 0.3s;
  min-height: 140px;
  display: flex;
  flex-direction: column;
}

.position-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.15);
  transform: translateY(-2px);
}

.position-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.position-code {
  font-weight: 600;
  color: #409eff;
  font-size: 14px;
}

.position-info {
  flex: 1;
  margin-bottom: 8px;
}

.position-name {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.position-area,
.position-coords {
  font-size: 11px;
  color: #909399;
  margin-bottom: 2px;
}

.position-images {
  display: flex;
  gap: 6px;
  align-items: center;
  margin-top: auto;
}

.position-thumb {
  width: 35px;
  height: 26px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
  cursor: pointer;
  flex-shrink: 0;
}

.more-images {
  font-size: 11px;
  color: #909399;
  margin-left: 4px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .position-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .position-list {
    grid-template-columns: 1fr;
  }
  
  .content-container {
    flex-direction: column;
    height: auto;
  }
  
  .tree-container,
  .detail-container {
    flex: none;
  }
  
  .tree-container {
    height: 400px;
  }
}
</style> 