<template>
  <div class="advertisement-detail" v-if="application">
    <!-- 基本信息 -->
    <div class="detail-section">
      <h3>基本信息</h3>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="detail-item">
            <span class="label">申请编号：</span>
            <span class="value">{{ application.applicationCode }}</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="detail-item">
            <span class="label">区域：</span>
            <span class="value">{{ application.regionName }}</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="detail-item">
            <span class="label">广告位置：</span>
            <span class="value">{{ application.positionName }}</span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="detail-item">
            <span class="label">广告性质：</span>
            <span class="value">{{ application.adNature }}</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="detail-item">
            <span class="label">单位面积：</span>
            <span class="value">{{ application.area }}㎡</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="detail-item">
            <span class="label">审核状态：</span>
            <el-tag :type="getStatusTagType(application.auditStatus)">
              {{ application.auditStatusText }}
            </el-tag>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 申请信息 -->
    <div class="detail-section">
      <h3>申请信息</h3>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="detail-item">
            <span class="label">设置类型：</span>
            <span class="value">{{ application.adSettingType }}</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="detail-item">
            <span class="label">总面积：</span>
            <span class="value">{{ application.totalArea }}㎡</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="detail-item">
            <span class="label">数量：</span>
            <span class="value">{{ application.quantity }}块</span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="detail-item">
            <span class="label">规格：</span>
            <span class="value">{{ application.specification || '暂无' }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="detail-item">
            <span class="label">申请时间：</span>
            <span class="value">{{ formatDate(application.applyTime) }}</span>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 申请材料 -->
    <div class="detail-section">
      <h3>申请材料</h3>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="file-item">
            <span class="file-label">申请表：</span>
            <div v-if="application.applicationFormUrl" class="file-link">
              <el-button size="small" type="primary" @click="previewFile(application.applicationFormUrl)">
                <el-icon><Document /></el-icon>
                查看文件
              </el-button>
            </div>
            <span v-else class="no-file">未上传</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="file-item">
            <span class="file-label">安全承诺书：</span>
            <div v-if="application.safetyCommitmentUrl" class="file-link">
              <el-button size="small" type="primary" @click="previewFile(application.safetyCommitmentUrl)">
                <el-icon><Document /></el-icon>
                查看文件
              </el-button>
            </div>
            <span v-else class="no-file">未上传</span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="file-item">
            <span class="file-label">场地协议：</span>
            <div v-if="application.siteAgreementUrl" class="file-link">
              <el-button size="small" type="primary" @click="previewFile(application.siteAgreementUrl)">
                <el-icon><Document /></el-icon>
                查看文件
              </el-button>
            </div>
            <span v-else class="no-file">未上传</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="file-item">
            <span class="file-label">实景效果图：</span>
            <div v-if="application.effectDrawingUrl" class="file-link">
              <el-button size="small" type="primary" @click="previewFile(application.effectDrawingUrl)">
                <el-icon><Document /></el-icon>
                查看文件
              </el-button>
            </div>
            <span v-else class="no-file">未上传</span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="file-item">
            <span class="file-label">结构设计图：</span>
            <div v-if="application.structureDesignUrl" class="file-link">
              <el-button size="small" type="primary" @click="previewFile(application.structureDesignUrl)">
                <el-icon><Document /></el-icon>
                查看文件
              </el-button>
            </div>
            <span v-else class="no-file">未上传</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="file-item">
            <span class="file-label">建筑物原貌图：</span>
            <div v-if="application.originalDrawingUrl" class="file-link">
              <el-button size="small" type="primary" @click="previewFile(application.originalDrawingUrl)">
                <el-icon><Document /></el-icon>
                查看文件
              </el-button>
            </div>
            <span v-else class="no-file">未上传</span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="file-item">
            <span class="file-label">产权证书：</span>
            <div v-if="application.propertyCertificateUrl" class="file-link">
              <el-button size="small" type="primary" @click="previewFile(application.propertyCertificateUrl)">
                <el-icon><Document /></el-icon>
                查看文件
              </el-button>
            </div>
            <span v-else class="no-file">未上传</span>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 审核信息 -->
    <div class="detail-section" v-if="application.auditStatus !== 'PENDING'">
      <h3>审核信息</h3>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="detail-item">
            <span class="label">审核人：</span>
            <span class="value">{{ application.auditUserName || '暂无' }}</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="detail-item">
            <span class="label">审核时间：</span>
            <span class="value">{{ formatDate(application.auditTime) }}</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="detail-item">
            <span class="label">审核结果：</span>
            <el-tag :type="getStatusTagType(application.auditStatus)">
              {{ application.auditStatusText }}
            </el-tag>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" v-if="application.auditRemark">
        <el-col :span="24">
          <div class="detail-item">
            <span class="label">审核备注：</span>
            <div class="value remark">{{ application.auditRemark }}</div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="detail-footer">
      <el-button @click="$emit('close')">关闭</el-button>
    </div>
  </div>
</template>

<script>
import { Document } from '@element-plus/icons-vue'

export default {
  name: 'AdvertisementDetail',
  components: {
    Document
  },
  props: {
    application: {
      type: Object,
      default: () => null
    }
  },
  emits: ['close'],
  setup() {
    // 获取状态标签类型
    const getStatusTagType = (status) => {
      const typeMap = {
        'PENDING': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger'
      }
      return typeMap[status] || ''
    }

    // 格式化日期
    const formatDate = (date) => {
      if (!date) return '暂无'
      return new Date(date).toLocaleString('zh-CN')
    }

    // 预览文件
    const previewFile = (url) => {
      if (url) {
        const fullUrl = url.startsWith('http') ? url : `http://localhost:8080${url}`
        window.open(fullUrl, '_blank')
      }
    }

    return {
      getStatusTagType,
      formatDate,
      previewFile
    }
  }
}
</script>

<style scoped>
.advertisement-detail {
  max-height: 500px;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.detail-section h3 {
  margin: 0 0 15px 0;
  color: #606266;
  font-size: 14px;
  font-weight: bold;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 8px;
}

.detail-item {
  margin-bottom: 10px;
  display: flex;
  align-items: flex-start;
}

.detail-item .label {
  color: #909399;
  font-size: 14px;
  width: 120px;
  flex-shrink: 0;
}

.detail-item .value {
  color: #606266;
  font-size: 14px;
  flex: 1;
}

.detail-item .value.remark {
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
  line-height: 1.6;
}

.file-item {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.file-label {
  color: #909399;
  font-size: 14px;
  width: 120px;
  flex-shrink: 0;
}

.file-link {
  flex: 1;
}

.no-file {
  color: #c0c4cc;
  font-size: 12px;
}

.detail-footer {
  text-align: right;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}
</style> 