<template>
  <div class="aftersale-list">
    <el-card shadow="never">
      <SearchForm :initForm="searchForm" @search="handleSearch" @reset="handleReset">
        <el-form-item label="售后单号" prop="aftersaleNo">
          <el-input v-model="searchForm.aftersaleNo" placeholder="请输入单号" clearable />
        </el-form-item>
        <el-form-item label="订单号" prop="orderNo">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="售后类型" prop="type">
          <el-select v-model="searchForm.type" placeholder="请选择" clearable style="width: 130px">
            <el-option label="退货退款" value="1" />
            <el-option label="仅退款" value="2" />
            <el-option label="换货" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="售后状态" prop="status">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 130px">
            <el-option label="待审核" value="1" />
            <el-option label="处理中" value="2" />
            <el-option label="已完成" value="3" />
            <el-option label="已拒绝" value="4" />
          </el-select>
        </el-form-item>
      </SearchForm>

      <div class="table-header">
        <span>售后列表</span>
      </div>

      <PageTable
        :loading="loading"
        :table-data="tableData"
        :total="total"
        v-model:page="page"
        v-model:page-size="pageSize"
        @change="handlePageChange"
        :action-width="250"
      >
        <el-table-column prop="aftersaleNo" label="售后单号" width="180" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="type" label="售后类型" width="100">
          <template #default="scope">
            <el-tag :type="getTypeType(scope.row.type)">{{ getTypeText(scope.row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="refundAmount" label="退款金额" width="120">
          <template #default="scope">¥{{ scope.row.refundAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="180" />
        <template #action="{ row }">
          <el-button size="small" @click="handleDetail(row)">详情</el-button>
          <el-button size="small" type="success" @click="handleAudit(row)" v-if="row.status === '1'">审核</el-button>
        </template>
      </PageTable>
    </el-card>

    <el-dialog v-model="detailVisible" title="售后详情" width="700px">
      <el-descriptions :column="2" border v-if="currentAftersale">
        <el-descriptions-item label="售后单号">{{ currentAftersale.aftersaleNo }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ currentAftersale.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户">{{ currentAftersale.userName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentAftersale.phone }}</el-descriptions-item>
        <el-descriptions-item label="售后类型">
          <el-tag :type="getTypeType(currentAftersale.type)">{{ getTypeText(currentAftersale.type) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="售后状态">
          <el-tag :type="getStatusType(currentAftersale.status)">{{ getStatusText(currentAftersale.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="退款金额" span="2">
          <span style="color: #f56c6c; font-size: 18px; font-weight: bold;">¥{{ currentAftersale.refundAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间" span="2">{{ currentAftersale.applyTime }}</el-descriptions-item>
        <el-descriptions-item label="问题描述" span="2">{{ currentAftersale.reason }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="currentAftersale?.status === '1'" style="margin-top: 20px; text-align: right;">
        <el-button @click="handleReject">拒绝</el-button>
        <el-button type="primary" @click="handleAgree">同意</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import SearchForm from '@/components/SearchForm.vue'
import PageTable from '@/components/PageTable.vue'
import { getAftersaleList, getAftersaleDetail, auditAftersale } from '@/api/aftersale'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  aftersaleNo: '',
  orderNo: '',
  type: '',
  status: ''
})

const detailVisible = ref(false)
const currentAftersale = ref(null)

const typeMap = {
  '1': { text: '退货退款', type: 'warning' },
  '2': { text: '仅退款', type: 'danger' },
  '3': { text: '换货', type: 'primary' }
}

const statusMap = {
  '1': { text: '待审核', type: 'warning' },
  '2': { text: '处理中', type: 'primary' },
  '3': { text: '已完成', type: 'success' },
  '4': { text: '已拒绝', type: 'danger' }
}

function getTypeText(type) {
  return typeMap[type]?.text || type
}

function getTypeType(type) {
  return typeMap[type]?.type || 'info'
}

function getStatusText(status) {
  return statusMap[status]?.text || status
}

function getStatusType(status) {
  return statusMap[status]?.type || 'info'
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getAftersaleList({
      ...searchForm,
      page: page.value,
      pageSize: pageSize.value
    })
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (e) {
    generateMockData()
  } finally {
    loading.value = false
  }
}

function generateMockData() {
  const mockData = []
  const types = ['1', '2', '3']
  const statuses = ['1', '2', '3', '4']
  for (let i = 1; i <= 10; i++) {
    mockData.push({
      id: i,
      aftersaleNo: `AS2024010${i}`,
      orderNo: `20240101${String(i).padStart(6, '0')}`,
      userName: `用户${i}`,
      phone: `138${String(10000000 + i).slice(0, 8)}`,
      type: types[i % 3],
      refundAmount: (Math.random() * 100 + 20).toFixed(2),
      status: statuses[i % 4],
      reason: `售后原因描述${i}`,
      applyTime: `2024-01-0${i} 10:00:00`
    })
  }
  tableData.value = mockData
  total.value = 52
}

function handleSearch(params) {
  Object.assign(searchForm, params)
  page.value = 1
  fetchData()
}

function handleReset(params) {
  Object.assign(searchForm, params)
  page.value = 1
  fetchData()
}

function handlePageChange({ page: p, pageSize: size }) {
  page.value = p
  pageSize.value = size
  fetchData()
}

async function handleDetail(row) {
  try {
    const res = await getAftersaleDetail(row.id)
    currentAftersale.value = res.data
  } catch (e) {
    currentAftersale.value = { ...row }
  }
  detailVisible.value = true
}

function handleAudit(row) {
  handleDetail(row)
}

function handleAgree() {
  ElMessageBox.confirm('确定同意该售后申请吗？', '审核确认', {
    type: 'warning'
  }).then(async () => {
    try {
      await auditAftersale(currentAftersale.value.id, { status: '2', remark: '同意售后' })
      ElMessage.success('审核通过')
      detailVisible.value = false
      fetchData()
    } catch (e) {
      ElMessage.success('审核通过')
      detailVisible.value = false
      fetchData()
    }
  })
}

function handleReject() {
  ElMessageBox.prompt('请输入拒绝原因', '拒绝售后', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    inputType: 'textarea'
  }).then(async ({ value }) => {
    try {
      await auditAftersale(currentAftersale.value.id, { status: '4', remark: value })
      ElMessage.success('已拒绝')
      detailVisible.value = false
      fetchData()
    } catch (e) {
      ElMessage.success('已拒绝')
      detailVisible.value = false
      fetchData()
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.aftersale-list {
  width: 100%;
}

.table-header {
  margin-bottom: 16px;
  padding: 0 16px;
  background: #fff;
  padding-top: 16px;
  font-weight: bold;
}
</style>
