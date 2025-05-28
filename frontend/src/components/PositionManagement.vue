<template>
  <div class="position-management">
    <div class="header">
      <h4>
        <el-icon><Grid /></el-icon>
        {{ streetName }} - 广告位管理
      </h4>
      <el-button type="primary" size="small" @click="showAddDialog" icon="Plus">
        新增广告位
      </el-button>
    </div>

    <!-- 搜索条件 -->
    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="编号">
        <el-input v-model="searchForm.code" placeholder="请输入编号" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item label="位置">
        <el-input v-model="searchForm.positionName" placeholder="请输入位置" clearable style="width: 150px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="small" @click="searchData">搜索</el-button>
        <el-button size="small" @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 广告位列表 -->
    <el-table :data="tableData" border style="width: 100%" v-loading="loading" size="small">
      <el-table-column prop="code" label="编号" width="100" />
      <el-table-column prop="positionName" label="广告位置" show-overflow-tooltip />
      <el-table-column prop="area" label="面积(㎡)" width="100" align="center" />
      <el-table-column prop="longitude" label="经度" width="100" align="center" />
      <el-table-column prop="latitude" label="纬度" width="100" align="center" />
      <el-table-column label="实图" width="80" align="center">
        <template #default="scope">
          <el-image 
            v-if="scope.row.positionImages && scope.row.positionImages.length > 0"
            :src="getImageUrl(scope.row.positionImages[0])" 
            style="width: 50px; height: 35px; border-radius: 4px;"
            fit="cover"
            :preview-src-list="scope.row.positionImages.map(img => getImageUrl(img))"
          />
          <span v-else class="no-image">-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button size="small" type="primary" @click="editPosition(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deletePosition(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[5, 10, 20]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
        small
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="form" ref="formRef" label-width="100px">
        <el-form-item label="编号" prop="code" required>
          <el-input v-model="form.code" placeholder="请输入广告位编号" />
        </el-form-item>
        
        <el-form-item label="广告位置" prop="positionName" required>
          <el-input v-model="form.positionName" placeholder="请输入广告位置" />
        </el-form-item>
        
        <el-form-item label="面积(㎡)" prop="area" required>
          <el-input-number 
            v-model="form.area" 
            :precision="2" 
            :min="0" 
            style="width: 100%"
            placeholder="请输入面积"
          />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="经度" prop="longitude" required>
              <el-input-number 
                v-model="form.longitude" 
                :precision="6"
                style="width: 100%"
                placeholder="自动从街道获取"
                :disabled="true"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度" prop="latitude" required>
              <el-input-number 
                v-model="form.latitude" 
                :precision="6"
                style="width: 100%"
                placeholder="自动从街道获取"
                :disabled="true"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="实景图片" prop="positionImages">
          <div class="upload-container">
            <!-- 已上传图片列表 -->
            <div v-if="form.positionImages && form.positionImages.length > 0" class="uploaded-images">
              <div 
                v-for="(image, index) in form.positionImages" 
                :key="index" 
                class="image-item"
              >
                <el-image 
                  :src="getImageUrl(image)"
                  style="width: 120px; height: 90px; border-radius: 8px;"
                  fit="cover"
                  :preview-src-list="form.positionImages.map(img => getImageUrl(img))"
                />
                <div class="image-actions">
                  <el-button 
                    size="small" 
                    type="danger" 
                    @click="removeImage(index)" 
                    icon="Delete"
                    circle
                  />
                </div>
              </div>
            </div>
            
            <!-- 上传区域 -->
            <el-upload
              :action="''"
              :http-request="handleUpload"
              :show-file-list="false"
              :before-upload="beforeUpload"
              accept="image/*"
              class="image-uploader"
            >
              <div class="upload-area">
                <el-icon class="upload-icon"><Plus /></el-icon>
                <div class="upload-text">点击上传图片</div>
                <div class="upload-hint">支持jpg、png格式，单个文件不超过5MB</div>
              </div>
            </el-upload>
          </div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Grid, Plus, Delete } from '@element-plus/icons-vue'
import { 
  getPositionsPage, 
  savePosition, 
  updatePosition, 
  deletePosition as deletePositionApi,
  uploadFile 
} from '../api/advertisement'
import { getOrganizationById } from '../api/organization'

export default {
  name: 'PositionManagement',
  components: { Grid, Plus, Delete },
  props: {
    regionId: {
      type: Number,
      required: true
    },
    streetName: {
      type: String,
      default: '未知街道'
    }
  },
  setup(props) {
    const formRef = ref()
    const loading = ref(false)
    const submitting = ref(false)
    const tableData = ref([])
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const dialogTitle = ref('新增广告位')
    const streetInfo = ref(null)

    const searchForm = reactive({
      code: '',
      positionName: ''
    })

    const pagination = reactive({
      page: 1,
      size: 5,
      total: 0
    })

    const form = reactive({
      id: null,
      code: '',
      positionName: '',
      area: null,
      longitude: null,
      latitude: null,
      positionImages: [],
      regionId: props.regionId
    })

    // 获取图片完整URL
    const getImageUrl = (imagePath) => {
      if (!imagePath) return ''
      if (imagePath.startsWith('http')) return imagePath
      return `http://localhost:8080${imagePath}`
    }

    // 处理图片数据
    const parseImages = (imageData) => {
      if (!imageData) return []
      if (typeof imageData === 'string') {
        try {
          return JSON.parse(imageData)
        } catch {
          return [imageData] // 兼容旧的单图片格式
        }
      }
      return Array.isArray(imageData) ? imageData : []
    }

    // 格式化图片数据用于保存
    const formatImages = (images) => {
      if (!images || images.length === 0) return null
      return JSON.stringify(images)
    }

    // 获取街道信息
    const loadStreetInfo = async () => {
      if (props.regionId) {
        try {
          const response = await getOrganizationById(props.regionId)
          if (response.code === 200) {
            streetInfo.value = response.data
          }
        } catch (error) {
          console.error('获取街道信息失败:', error)
        }
      }
    }

    // 加载数据
    const loadData = async () => {
      loading.value = true
      try {
        const params = {
          page: pagination.page,
          size: pagination.size,
          regionId: props.regionId,
          ...searchForm
        }
        
        const response = await getPositionsPage(params)
        if (response.code === 200) {
          // 处理图片数组显示
          tableData.value = response.data.records.map(item => ({
            ...item,
            positionImages: parseImages(item.positionImage)
          }))
          pagination.total = response.data.total
        }
      } catch (error) {
        ElMessage.error('加载数据失败')
      } finally {
        loading.value = false
      }
    }

    // 搜索数据
    const searchData = () => {
      pagination.page = 1
      loadData()
    }

    // 重置搜索
    const resetSearch = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      searchData()
    }

    // 显示新增对话框
    const showAddDialog = async () => {
      resetForm()
      form.regionId = props.regionId
      
      // 自动填充街道的经纬度
      if (streetInfo.value && streetInfo.value.longitude && streetInfo.value.latitude) {
        form.longitude = streetInfo.value.longitude
        form.latitude = streetInfo.value.latitude
      }
      
      dialogTitle.value = '新增广告位'
      isEdit.value = false
      dialogVisible.value = true
    }

    // 编辑广告位
    const editPosition = (row) => {
      Object.assign(form, {
        ...row,
        positionImages: parseImages(row.positionImage)
      })
      dialogTitle.value = '编辑广告位'
      isEdit.value = true
      dialogVisible.value = true
    }

    // 删除广告位
    const deletePosition = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除广告位 "${row.code}" 吗？`,
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        await deletePositionApi(row.id)
        ElMessage.success('删除成功')
        loadData()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }

    // 文件上传前校验
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

    // 自定义上传
    const handleUpload = async (options) => {
      try {
        const response = await uploadFile(options.file)
        form.positionImages.push(response.data)
        ElMessage.success('图片上传成功')
      } catch (error) {
        ElMessage.error('图片上传失败')
      }
    }

    // 删除图片
    const removeImage = (index) => {
      form.positionImages.splice(index, 1)
    }

    // 提交表单
    const submitForm = async () => {
      // 表单校验
      if (!form.code || !form.positionName || !form.area) {
        ElMessage.warning('请填写所有必填项')
        return
      }

      submitting.value = true
      try {
        const submitData = {
          ...form,
          positionImage: formatImages(form.positionImages)
        }
        delete submitData.positionImages
        
        if (isEdit.value) {
          await updatePosition(submitData)
          ElMessage.success('更新成功')
        } else {
          await savePosition(submitData)
          ElMessage.success('添加成功')
        }
        
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      } finally {
        submitting.value = false
      }
    }

    // 重置表单
    const resetForm = () => {
      Object.assign(form, {
        id: null,
        code: '',
        positionName: '',
        area: null,
        longitude: null,
        latitude: null,
        positionImages: [],
        regionId: props.regionId
      })
    }

    // 监听regionId变化
    watch(() => props.regionId, (newRegionId) => {
      if (newRegionId) {
        form.regionId = newRegionId
        loadStreetInfo()
        loadData()
      }
    })

    onMounted(() => {
      if (props.regionId) {
        loadStreetInfo()
        loadData()
      }
    })

    return {
      formRef,
      loading,
      submitting,
      tableData,
      searchForm,
      pagination,
      dialogVisible,
      isEdit,
      dialogTitle,
      form,
      getImageUrl,
      loadData,
      searchData,
      resetSearch,
      showAddDialog,
      editPosition,
      deletePosition,
      beforeUpload,
      handleUpload,
      removeImage,
      submitForm,
      resetForm
    }
  }
}
</script>

<style scoped>
.position-management {
  background: white;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e4e7ed;
}

.header h4 {
  margin: 0;
  font-size: 16px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-form {
  margin-bottom: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.pagination {
  margin-top: 16px;
  text-align: right;
}

.no-image {
  color: #c0c4cc;
  font-size: 12px;
}

.table-images {
  position: relative;
  display: inline-block;
}

.image-count {
  position: absolute;
  bottom: 2px;
  right: 2px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  font-size: 10px;
  padding: 1px 4px;
  border-radius: 2px;
}

/* 上传组件样式 */
.upload-container {
  width: 100%;
}

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

.image-item .el-image {
  width: 100%;
  height: 100%;
  cursor: pointer;
  transition: transform 0.3s;
}

.image-item:hover .el-image {
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

.image-uploader .upload-area {
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  width: 150px;
  height: 100px;
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

.dialog-footer {
  text-align: right;
}
</style> 