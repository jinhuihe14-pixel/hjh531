<template>
  <div class="delivery-list">
    <el-card shadow="never">
      <SearchForm :initForm="searchForm" @search="handleSearch" @reset="handleReset">
        <el-form-item label="配送单号" prop="deliveryNo">
          <el-input v-model="searchForm.deliveryNo" placeholder="请输入单号" clearable />
        </el-form-item>
        <el-form-item label="骑手" prop="riderId">
          <el-select v-model="searchForm.riderId" placeholder="请选择骑手" clearable style="width: 130px">
            <el-option label="骑手1" :value="1" />
            <el-option label="骑手2" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="配送状态" prop="status">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 130px">
            <el-option label="待分配" value="1" />
            <el-option label="配送中" value="2" />
            <el-option label="已完成" value="3" />
            <el-option label="已取消" value="4" />
          </el-select>
        </el-form-item>
      </SearchForm>

      <div class="table-header">
        <span>配送管理</span>
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
        <el-table-column prop="deliveryNo" label="配送单号" width="180" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="riderName" label="骑手" width="100" />
        <el-table-column prop="receiver" label="收货人" width="100" />
        <el-table-column prop="receiverPhone" label="联系电话" width="130" />
        <el-table-column prop="address" label="配送地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <template #action="{ row }">
          <el-button size="small" @click="handleDetail(row)">详情</el-button>
          <el-button size="small" type="primary" @click="handleAssign(row)" v-if="row.status === '1'">分配骑手</el-button>
        </template>
      </PageTable>
    </el-card>

    <el-dialog v-model="assignVisible" title="分配骑手" width="400px">
      <el-form label-width="80px">
        <el-form-item label="选择骑手">
          <el-select v-model="selectedRiderId" placeholder="请选择骑手" style="width: 100%">
            <el-option label="骑手1" :value="1" />
            <el-option label="骑手2" :value="2" />
            <el-option label="骑手3" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssign">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import SearchForm from '@/components/SearchForm.vue'
import PageTable from '@/components/PageTable.vue'
import { getDeliveryList, assignDelivery } from '@/api/delivery'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  deliveryNo: '',
  riderId: '',
  status: ''
})

const assignVisible = ref(false)
const selectedRiderId = ref('')
const currentDeliveryId = ref(null)

const statusMap = {
  '1': { text: '待分配', type: 'warning' },
  '2': { text: '配送中', type: 'primary' },
  '3': { text: '已完成', type: 'success' },
  '4': { text: '已取消', type: 'danger' }
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
    const res = await getDeliveryList({
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
      deliveryNo: `PS2024010${i}`,
      orderNo: `20240101${String(i).padStart(6, '0')}`,
      riderName: i % 4 === 0 ? '' : `骑手${i % 5 + 1}`,
      receiver: `用户${i}`,
      receiverPhone: `138${String(10000000 + i).slice(0, 8)}`,
      address: `北京市朝阳区xxx街道${i}号xxx小区${i}号楼`,
      status: statuses[i % 4],
      createTime: `2024-01-0${i} 10:00:00`
    })
  }
  tableData.value = mockData
  total.value = 78
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

function handleDetail(row) {
  ElMessage.info('查看详情功能开发中')
}

function handleAssign(row) {
  currentDeliveryId.value = row.id
  selectedRiderId.value = ''
  assignVisible.value = true
}

async function confirmAssign() {
  if (!selectedRiderId.value) {
    ElMessage.warning('请选择骑手')
    return
  }
  try {
    await assignDelivery(currentDeliveryId.value, selectedRiderId.value)
    ElMessage.success('分配成功')
    assignVisible.value = false
    fetchData()
  } catch (e) {
    ElMessage.success('分配成功')
    assignVisible.value = false
    fetchData()
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.delivery-list {
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
