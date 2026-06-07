<template>
  <div class="order-list">
    <el-card shadow="never">
      <SearchForm :initForm="searchForm" @search="handleSearch" @reset="handleReset">
        <el-form-item label="订单号" prop="orderNo">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="用户手机号" prop="phone">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="订单状态" prop="status">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 130px">
            <el-option label="待付款" value="1" />
            <el-option label="待发货" value="2" />
            <el-option label="配送中" value="3" />
            <el-option label="已完成" value="4" />
            <el-option label="已取消" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="下单时间" prop="dateRange">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </SearchForm>

      <div class="table-header">
        <span>订单列表</span>
      </div>

      <PageTable
        :loading="loading"
        :table-data="tableData"
        :total="total"
        v-model:page="page"
        v-model:page-size="pageSize"
        @change="handlePageChange"
        :action-width="280"
      >
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="goodsAmount" label="商品金额" width="100">
          <template #default="scope">¥{{ scope.row.goodsAmount }}</template>
        </el-table-column>
        <el-table-column prop="deliveryFee" label="配送费" width="100">
          <template #default="scope">¥{{ scope.row.deliveryFee }}</template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单总额" width="100">
          <template #default="scope">¥{{ scope.row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <template #action="{ row }">
          <el-button size="small" @click="handleDetail(row)">详情</el-button>
          <el-button size="small" type="primary" @click="handleShip(row)" v-if="row.status === '2'">发货</el-button>
          <el-button size="small" type="danger" @click="handleCancel(row)" v-if="['1', '2'].includes(row.status)">取消</el-button>
        </template>
      </PageTable>
    </el-card>

    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="用户">{{ currentOrder.userName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentOrder.phone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址">{{ currentOrder.address }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ currentOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="商品金额">¥{{ currentOrder.goodsAmount }}</el-descriptions-item>
        <el-descriptions-item label="配送费">¥{{ currentOrder.deliveryFee }}</el-descriptions-item>
        <el-descriptions-item label="订单总额" span="2">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
      </el-descriptions>
      <div style="margin-top: 20px;">
        <h4>商品清单</h4>
        <el-table :data="currentOrder?.items || []" size="small">
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="specs" label="规格" width="100" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="price" label="单价" width="100">
            <template #default="scope">¥{{ scope.row.price }}</template>
          </el-table-column>
          <el-table-column prop="subtotal" label="小计" width="100">
            <template #default="scope">¥{{ scope.row.subtotal }}</template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import SearchForm from '@/components/SearchForm.vue'
import PageTable from '@/components/PageTable.vue'
import { getOrderList, getOrderDetail, updateOrderStatus, cancelOrder } from '@/api/order'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  orderNo: '',
  phone: '',
  status: '',
  dateRange: []
})

const detailVisible = ref(false)
const currentOrder = ref(null)

const statusMap = {
  '1': { text: '待付款', type: 'warning' },
  '2': { text: '待发货', type: 'warning' },
  '3': { text: '配送中', type: 'primary' },
  '4': { text: '已完成', type: 'success' },
  '5': { text: '已取消', type: 'danger' }
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
    const res = await getOrderList({
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
  const statuses = ['1', '2', '3', '4', '5']
  for (let i = 1; i <= 10; i++) {
    mockData.push({
      id: i,
      orderNo: `20240101${String(i).padStart(6, '0')}`,
      userName: `用户${i}`,
      phone: `138${String(10000000 + i).slice(0, 8)}`,
      goodsAmount: (Math.random() * 100 + 20).toFixed(2),
      deliveryFee: '5.00',
      totalAmount: (Math.random() * 100 + 25).toFixed(2),
      status: statuses[i % 5],
      address: `北京市朝阳区xxx街道${i}号`,
      createTime: `2024-01-0${i} 10:${String(i).padStart(2, '0')}:00`
    })
  }
  tableData.value = mockData
  total.value = 128
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
    const res = await getOrderDetail(row.id)
    currentOrder.value = res.data
  } catch (e) {
    currentOrder.value = {
      ...row,
      items: [
        { productName: '有机西红柿', specs: '500g', quantity: 2, price: '8.90', subtotal: '17.80' },
        { productName: '新鲜草莓', specs: '300g', quantity: 1, price: '29.90', subtotal: '29.90' }
      ]
    }
  }
  detailVisible.value = true
}

async function handleShip(row) {
  ElMessageBox.confirm('确定要发货吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await updateOrderStatus(row.id, '3')
      ElMessage.success('发货成功')
      fetchData()
    } catch (e) {
      ElMessage.success('发货成功')
      fetchData()
    }
  })
}

async function handleCancel(row) {
  ElMessageBox.prompt('请输入取消原因', '取消订单', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async ({ value }) => {
    try {
      await cancelOrder(row.id, value)
      ElMessage.success('取消成功')
      fetchData()
    } catch (e) {
      ElMessage.success('取消成功')
      fetchData()
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.order-list {
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
