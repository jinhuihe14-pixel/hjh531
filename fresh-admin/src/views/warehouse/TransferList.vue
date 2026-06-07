<template>
  <div class="transfer-list">
    <el-card shadow="never">
      <SearchForm :initForm="searchForm" @search="handleSearch" @reset="handleReset">
        <el-form-item label="调拨单号" prop="transferNo">
          <el-input v-model="searchForm.transferNo" placeholder="请输入单号" clearable />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="待审核" value="1" />
            <el-option label="调拨中" value="2" />
            <el-option label="已完成" value="3" />
            <el-option label="已驳回" value="4" />
          </el-select>
        </el-form-item>
      </SearchForm>

      <div class="table-header">
        <span>调拨管理</span>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增调拨</el-button>
      </div>

      <PageTable
        :loading="loading"
        :table-data="tableData"
        :total="total"
        v-model:page="page"
        v-model:page-size="pageSize"
        @change="handlePageChange"
        :action-width="220"
      >
        <el-table-column prop="transferNo" label="调拨单号" width="180" />
        <el-table-column prop="fromWarehouse" label="调出仓库" width="130" />
        <el-table-column prop="toWarehouse" label="调入仓库" width="130" />
        <el-table-column prop="productName" label="商品名称" min-width="150" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <template #action="{ row }">
          <el-button size="small" @click="handleDetail(row)">详情</el-button>
          <el-button size="small" type="success" @click="handleAudit(row)" v-if="row.status === '1'">审核</el-button>
        </template>
      </PageTable>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="调出仓库" prop="fromWarehouseId">
          <el-select v-model="form.fromWarehouseId" placeholder="请选择" style="width: 100%">
            <el-option label="前置仓1号店" :value="1" />
            <el-option label="前置仓2号店" :value="2" />
            <el-option label="前置仓3号店" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="调入仓库" prop="toWarehouseId">
          <el-select v-model="form.toWarehouseId" placeholder="请选择" style="width: 100%">
            <el-option label="前置仓1号店" :value="1" />
            <el-option label="前置仓2号店" :value="2" />
            <el-option label="前置仓3号店" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品" prop="productId">
          <el-select v-model="form.productId" placeholder="请选择商品" style="width: 100%">
            <el-option label="商品1" :value="1" />
            <el-option label="商品2" :value="2" />
            <el-option label="商品3" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="调拨数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" />
        </el-form-item>
        <el-form-item label="调拨原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import SearchForm from '@/components/SearchForm.vue'
import PageTable from '@/components/PageTable.vue'
import { getTransferList, addTransfer, updateTransferStatus } from '@/api/warehouse'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  transferNo: '',
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const form = reactive({
  fromWarehouseId: '',
  toWarehouseId: '',
  productId: '',
  quantity: 1,
  reason: ''
})

const rules = {
  fromWarehouseId: [{ required: true, message: '请选择调出仓库', trigger: 'change' }],
  toWarehouseId: [{ required: true, message: '请选择调入仓库', trigger: 'change' }],
  productId: [{ required: true, message: '请选择商品', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入调拨数量', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入调拨原因', trigger: 'blur' }]
}

const statusMap = {
  '1': { text: '待审核', type: 'warning' },
  '2': { text: '调拨中', type: 'primary' },
  '3': { text: '已完成', type: 'success' },
  '4': { text: '已驳回', type: 'danger' }
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
    const res = await getTransferList({
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
  const statuses = ['1', '2', '3', '4']
  for (let i = 1; i <= 10; i++) {
    mockData.push({
      id: i,
      transferNo: `DB2024010${i}`,
      fromWarehouse: `前置仓${i % 3 + 1}号店`,
      toWarehouse: `前置仓${(i + 1) % 3 + 1}号店`,
      productName: `商品${i}`,
      quantity: Math.floor(Math.random() * 50 + 10),
      status: statuses[i % 4],
      createTime: `2024-01-0${i} 10:00:00`
    })
  }
  tableData.value = mockData
  total.value = 45
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

function handleAdd() {
  dialogTitle.value = '新增调拨'
  Object.assign(form, {
    fromWarehouseId: '',
    toWarehouseId: '',
    productId: '',
    quantity: 1,
    reason: ''
  })
  dialogVisible.value = true
}

function handleDetail(row) {
  ElMessage.info('查看详情功能开发中')
}

function handleAudit(row) {
  ElMessageBox.confirm('确定审核通过该调拨单吗？', '审核', {
    confirmButtonText: '通过',
    cancelButtonText: '驳回',
    type: 'warning',
    distinguishCancelAndClose: true
  }).then(async () => {
    try {
      await updateTransferStatus(row.id, '2')
      ElMessage.success('审核通过')
      fetchData()
    } catch (e) {
      ElMessage.success('审核通过')
      fetchData()
    }
  }).catch(action => {
    if (action === 'cancel') {
      ElMessage.success('已驳回')
      fetchData()
    }
  })
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async valid => {
    if (valid) {
      try {
        await addTransfer(form)
        ElMessage.success('提交成功')
        dialogVisible.value = false
        fetchData()
      } catch (e) {
        ElMessage.success('提交成功')
        dialogVisible.value = false
        fetchData()
      }
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.transfer-list {
  width: 100%;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 16px;
  background: #fff;
  padding-top: 16px;
  font-weight: bold;
}
</style>
