<template>
  <div class="advertisement-management">
    <el-card>
      <div slot="header" class="clearfix">
        <span>广告申请管理</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="showAddDialog">新增申请</el-button>
      </div>

      <!-- 搜索条件 -->
      <div class="search-form">
        <el-form :model="searchForm" ref="searchFormRef" inline>
          <el-form-item label="编号">
            <el-input v-model="searchForm.applicationCode" placeholder="请输入申请编号" clearable style="width: 150px" />
          </el-form-item>
          <el-form-item label="广告位置">
            <el-input v-model="searchForm.positionName" placeholder="请输入广告位置" clearable style="width: 150px" />
          </el-form-item>
          <el-form-item label="面积">
            <el-input-number v-model="searchForm.area" placeholder="请输入面积" :precision="2" style="width: 150px" />
          </el-form-item>
          <el-form-item label="类型">
            <el-input v-model="searchForm.adSettingType" placeholder="请输入设置类型" clearable style="width: 150px" />
          </el-form-item>
          <el-form-item label="广告性质">
            <el-select v-model="searchForm.adNature" placeholder="请选择广告性质" clearable style="width: 150px">
              <el-option label="商业广告" value="商业广告" />
              <el-option label="公益广告" value="公益广告" />
              <el-option label="旅游宣传" value="旅游宣传" />
              <el-option label="政府公告" value="政府公告" />
            </el-select>
          </el-form-item>
          <el-form-item label="审核状态">
            <el-select v-model="searchForm.auditStatus" placeholder="请选择审核状态" clearable style="width: 150px">
              <el-option label="待审核" value="PENDING" />
              <el-option label="已通过" value="APPROVED" />
              <el-option label="已拒绝" value="REJECTED" />
            </el-select>
          </el-form-item>
          <el-form-item label="区域">
            <el-select v-model="searchForm.regionId" placeholder="请选择区域" clearable style="width: 150px">
              <el-option 
                v-for="region in regionOptions" 
                :key="region.id" 
                :label="region.name" 
                :value="region.id" 
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchData">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 申请列表 -->
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="applicationCode" label="编号" width="120" />
        <el-table-column prop="regionName" label="区域" width="120" />
        <el-table-column prop="positionName" label="广告位置" width="200" />
        <el-table-column prop="adSettingType" label="户外广告设置类型" width="150" />
        <el-table-column prop="adNature" label="广告性质" width="120" />
        <el-table-column prop="area" label="面积(㎡)" width="100" />
        <el-table-column prop="auditStatusText" label="审核状态" width="100">
          <template #default="scope">
            <el-tag 
              :type="getStatusTagType(scope.row.auditStatus)"
              effect="dark"
            >
              {{ scope.row.auditStatusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="showDetail(scope.row)">查看</el-button>
            <el-button 
              v-if="scope.row.auditStatus === 'PENDING'" 
              size="small" 
              type="success" 
              @click="showAuditDialog(scope.row)"
            >
              审核
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 新增申请对话框 -->
    <el-dialog
      title="新增广告申请"
      v-model="addDialogVisible"
      width="1300px"
      :close-on-click-modal="false"
      @close="resetForm"
      custom-class="advertisement-dialog"
    >
      <AdvertisementAddForm 
        :key="dialogKey"
        ref="addFormRef"
        @success="handleAddSuccess"
        @cancel="addDialogVisible = false"
      />
    </el-dialog>

    <!-- 申请详情对话框 -->
    <el-dialog
      title="申请详情"
      v-model="detailDialogVisible"
      width="800px"
    >
      <AdvertisementDetail 
        :application="selectedApplication"
        @close="detailDialogVisible = false"
      />
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog
      title="审核申请"
      v-model="auditDialogVisible"
      width="500px"
      @close="resetAuditForm"
    >
      <el-form :model="auditForm" ref="auditFormRef" label-width="80px">
        <el-form-item label="申请编号">
          <el-input v-model="auditForm.applicationCode" readonly />
        </el-form-item>
        <el-form-item label="审核结果" prop="auditStatus" required>
          <el-radio-group v-model="auditForm.auditStatus">
            <el-radio label="APPROVED">通过</el-radio>
            <el-radio label="REJECTED">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注" prop="auditRemark">
          <el-input 
            v-model="auditForm.auditRemark" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入审核备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="auditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAudit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getApplicationPage, auditApplication } from '../api/advertisement'
import { getOrganizationTree } from '../api/organization'
import AdvertisementAddForm from '../components/AdvertisementAddForm.vue'
import AdvertisementDetail from '../components/AdvertisementDetail.vue'

export default {
  name: 'AdvertisementManagement',
  components: {
    AdvertisementAddForm,
    AdvertisementDetail
  },
  setup() {
    const loading = ref(false)
    const tableData = ref([])
    const regionOptions = ref([])
    const addDialogVisible = ref(false)
    const detailDialogVisible = ref(false)
    const auditDialogVisible = ref(false)
    const selectedApplication = ref(null)
    const addFormRef = ref(null)
    const dialogKey = ref(0) // 用于强制重新创建组件

    const searchForm = reactive({
      applicationCode: '',
      positionName: '',
      area: null,
      adSettingType: '',
      adNature: '',
      auditStatus: '',
      regionId: null
    })

    const pagination = reactive({
      page: 1,
      size: 10,
      total: 0
    })

    const auditForm = reactive({
      id: null,
      applicationCode: '',
      auditStatus: '',
      auditRemark: ''
    })

    // 获取状态标签类型
    const getStatusTagType = (status) => {
      const typeMap = {
        'PENDING': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger'
      }
      return typeMap[status] || ''
    }

    // 加载数据
    const loadData = async () => {
      loading.value = true
      try {
        const params = {
          page: pagination.page,
          size: pagination.size,
          ...searchForm
        }
        
        const response = await getApplicationPage(params)
        if (response.code === 200) {
          tableData.value = response.data.records
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
        searchForm[key] = key === 'area' ? null : ''
      })
      searchData()
    }

    // 显示新增对话框
    const showAddDialog = () => {
      addDialogVisible.value = true
      dialogKey.value++
    }

    // 处理新增成功
    const handleAddSuccess = () => {
      addDialogVisible.value = false
      loadData()
      ElMessage.success('申请提交成功')
    }

    // 重置表单
    const resetForm = () => {
      // 组件已通过key重新创建，不需要手动重置
      // if (addFormRef.value && addFormRef.value.resetForm) {
      //   addFormRef.value.resetForm()
      // }
    }

    // 显示详情
    const showDetail = (row) => {
      selectedApplication.value = row
      detailDialogVisible.value = true
    }

    // 显示审核对话框
    const showAuditDialog = (row) => {
      auditForm.id = row.id
      auditForm.applicationCode = row.applicationCode
      auditForm.auditStatus = ''
      auditForm.auditRemark = ''
      auditDialogVisible.value = true
    }

    // 重置审核表单
    const resetAuditForm = () => {
      auditForm.id = null
      auditForm.applicationCode = ''
      auditForm.auditStatus = ''
      auditForm.auditRemark = ''
    }

    // 提交审核
    const submitAudit = async () => {
      if (!auditForm.auditStatus) {
        ElMessage.warning('请选择审核结果')
        return
      }

      try {
        const response = await auditApplication(
          auditForm.id,
          auditForm.auditStatus,
          auditForm.auditRemark
        )
        
        if (response.code === 200) {
          auditDialogVisible.value = false
          loadData()
          ElMessage.success('审核完成')
          
          // 如果申请表单对话框是打开的，通知其刷新广告位状态
          if (addDialogVisible.value && addFormRef.value && addFormRef.value.refreshPositions) {
            addFormRef.value.refreshPositions()
          }
        }
      } catch (error) {
        ElMessage.error('审核失败')
      }
    }

    // 加载区域选项
    const loadRegionOptions = async () => {
      try {
        const response = await getOrganizationTree()
        if (response.code === 200) {
          // 扁平化组织树，获取所有节点作为选项
          const flattenOptions = (nodes) => {
            let options = []
            nodes.forEach(node => {
              options.push({ id: node.id, name: node.name })
              if (node.children && node.children.length > 0) {
                options = options.concat(flattenOptions(node.children))
              }
            })
            return options
          }
          regionOptions.value = flattenOptions(response.data)
        }
      } catch (error) {
        console.error('加载区域选项失败', error)
      }
    }

    onMounted(() => {
      loadData()
      loadRegionOptions()
    })

    return {
      loading,
      tableData,
      regionOptions,
      searchForm,
      pagination,
      addDialogVisible,
      detailDialogVisible,
      auditDialogVisible,
      selectedApplication,
      auditForm,
      dialogKey,
      getStatusTagType,
      loadData,
      searchData,
      resetSearch,
      showAddDialog,
      handleAddSuccess,
      resetForm,
      showDetail,
      showAuditDialog,
      resetAuditForm,
      submitAudit
    }
  }
}
</script>

<style scoped>
.advertisement-management {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 4px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both;
}

.dialog-footer {
  text-align: right;
}

/* 广告申请对话框样式 */
:deep(.advertisement-dialog) {
  max-height: 90vh;
  overflow: hidden;
}

:deep(.advertisement-dialog .el-dialog__body) {
  padding: 0;
  max-height: calc(90vh - 120px);
  overflow-y: auto;
}
</style> 