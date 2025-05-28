<template>
  <div class="organization-management">
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h2><el-icon><OfficeBuilding /></el-icon>组织架构管理</h2>
          <p>管理省市区街道四级组织架构，支持街道级别的详细信息配置</p>
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
                  v-if="data.level < 4 && hasPermission('organization:add')" 
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
          <template v-if="selectedNode.level === 4">
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
            
            <div class="detail-item" v-if="selectedNode.imageUrls && getImageList(selectedNode.imageUrls).length > 0">
              <label>图片：</label>
              <div class="images-preview">
                <div class="image-grid">
                  <el-image
                    v-for="(image, index) in getImageList(selectedNode.imageUrls)"
                    :key="index"
                    :src="getImageUrl(image)"
                    :preview-src-list="getImageList(selectedNode.imageUrls).map(img => getImageUrl(img))"
                    :initial-index="index"
                    fit="cover"
                    class="preview-image"
                  />
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
      width="600px"
      @close="resetForm"
    >
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

                  <!-- 街道级别显示经纬度和图片字段 -->
          <template v-if="form.level === 4">
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
            
            <el-form-item label="图片" prop="imageUrls">
              <div class="upload-container">
                <!-- 已上传图片展示 -->
                <div v-if="getImageList(form.imageUrls).length > 0" class="uploaded-images">
                  <div class="image-item" v-for="(image, index) in getImageList(form.imageUrls)" :key="index">
                    <el-image 
                      :src="getImageUrl(image)"
                      class="uploaded-image"
                      fit="cover"
                      @click="previewImages(index)"
                    />
                    <div class="image-actions">
                      <el-button size="small" type="primary" @click="previewImages(index)" icon="View" circle></el-button>
                      <el-button size="small" type="danger" @click="removeImage(index)" icon="Delete" circle></el-button>
                    </div>
                  </div>
                </div>
                
                <!-- 上传区域 -->
                <el-upload
                  ref="uploadRef"
                  :action="''"
                  :http-request="handleUpload"
                  :show-file-list="false"
                  :before-upload="beforeUpload"
                  accept="image/*"
                  multiple
                  class="image-uploader"
                >
                  <div class="upload-area">
                    <el-icon class="upload-icon"><Plus /></el-icon>
                    <div class="upload-text">点击上传图片</div>
                    <div class="upload-hint">支持多张图片上传</div>
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
          </template>
        
        <el-form-item label="状态" prop="isEnabled">
          <el-switch v-model="form.isEnabled" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
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
import { usePermissions } from '../stores/permission'

export default {
  name: 'OrganizationManagement',
  setup() {
    const treeRef = ref()
    const formRef = ref()
    const uploadRef = ref()
    
    const treeData = ref([])
    const selectedNode = ref(null)
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const uploading = ref(false)
    
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
      imageUrls: '',
      description: '',
      sort: 0,
      isEnabled: true
    })

    const dialogTitle = ref('添加组织')

    // 级别文本映射
    const getLevelText = (level) => {
      const levelMap = {
        1: '省',
        2: '市',
        3: '区',
        4: '街道'
      }
      return levelMap[level] || '-'
    }

    // 级别标签类型
    const getLevelTagType = (level) => {
      const tagTypeMap = {
        1: 'danger',
        2: 'warning', 
        3: 'primary',
        4: 'success'
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

    // 获取图片列表
    const getImageList = (imageUrls) => {
      if (!imageUrls) return []
      try {
        return JSON.parse(imageUrls)
      } catch {
        return []
      }
    }

    // 设置图片列表
    const setImageList = (images) => {
      form.imageUrls = JSON.stringify(images)
      // 实时更新详情信息
      if (selectedNode.value && selectedNode.value.id === form.id) {
        selectedNode.value.imageUrls = form.imageUrls
      }
    }

    // 自定义上传
    const handleUpload = async (options) => {
      try {
        uploading.value = true
        const response = await uploadFile(options.file)
        const currentImages = getImageList(form.imageUrls)
        currentImages.push(response.data)
        setImageList(currentImages)
        ElMessage.success('图片上传成功')
      } catch (error) {
        ElMessage.error('图片上传失败')
        console.error(error)
      } finally {
        uploading.value = false
      }
    }

    // 预览图片
    const previewImages = (index) => {
      const images = getImageList(form.imageUrls)
      if (images.length > 0) {
        // 使用Element Plus的图片预览功能
        const imageUrls = images.map(img => getImageUrl(img))
        // 这里可以触发图片预览，由于是在upload区域，我们直接点击图片即可预览
      }
    }

    // 删除图片
    const removeImage = (index) => {
      const currentImages = getImageList(form.imageUrls)
      currentImages.splice(index, 1)
      setImageList(currentImages)
      // 实时更新详情信息
      ElMessage.success('图片已删除')
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
    }

    // 添加根节点（省份）
    const addRootNode = () => {
      resetForm()
      form.level = 1
      form.parentId = null
      dialogTitle.value = '添加省份'
      isEdit.value = false
      dialogVisible.value = true
    }

    // 添加子节点
    const addChild = (parentData) => {
      resetForm()
      form.parentId = parentData.id
      form.level = parentData.level + 1
      
      const levelNames = ['', '省份', '城市', '区域', '街道']
      dialogTitle.value = `添加${levelNames[form.level]}`
      
      isEdit.value = false
      dialogVisible.value = true
    }

    // 编辑节点
    const editNode = (data) => {
      Object.assign(form, data)
      dialogTitle.value = '编辑组织'
      isEdit.value = true
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
        imageUrls: '',
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
      uploadRef,
      treeData,
      selectedNode,
      dialogVisible,
      isEdit,
      uploading,
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
      handleUpload,
      previewImages,
      removeImage,
      getImageList,
      hasPermission
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
  top: 4px;
  right: 4px;
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.3s;
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
</style> 