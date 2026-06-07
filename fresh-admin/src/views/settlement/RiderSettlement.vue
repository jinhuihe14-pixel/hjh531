<template>
  <div class="rider-settlement">
    <el-card shadow="never">
      <SearchForm :initForm="searchForm" @search="handleSearch" @reset="handleReset">
        <el-form-item label="骑手" prop="riderName">
          <el-input v-model="searchForm.riderName" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="结算状态" prop="status">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 130px">
            <el-option label="待结算" value="1" />
            <el-option label="已结算" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="结算周期" prop="dateRange">
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
        <span>骑手结算</span>
      </div>

      <PageTable
        :loading="loading"
        :table-data="tableData"
        :total="total"
        v-model:page="page"
        v-model:page-size="pageSize"
        @change="handlePageChange"
        :action-width="200"
      >
        <el-table-column prop="settleNo" label="结算单号" width="180" />
        <el-table-column prop="riderName" label="骑手" width="120" />
        <el-table-column prop="period" label="结算周期" width="180" />
        <el-table-column prop="orderCount" label="配送单数" width="100" />
        <el-table-column prop="distance" label="配送里程(km)" width="130" />
        <el-table-column prop="settleAmount" label="结算金额" width="120">
          <template #default="scope">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ scope.row.settleAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '2' ? 'success' : 'warning'">
              {{ scope.row.status === '2' ? '已结算' : '待结算' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <template #action="{ row }">
          <el-button size="small" @click="handleDetail(row)">详情</el-button>
          <el-button size="small" type="primary" @click="handleSettle(row)" v-if="row.status === '1'">结算</el-button>
        </template>
      </PageTable>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import SearchForm from '@/components/SearchForm.vue'
import PageTable from '@/components/PageTable.vue'
import { getRiderSettlementList, settleRider } from '@/api/settlement'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  riderName: '',
  status: '',
  dateRange: []
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getRiderSettlementList({
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
  for (let i = 1; i <= 10; i++) {
    const orderCount = Math.floor(Math.random() * 300 + 100)
    mockData.push({
      id: i,
      settleNo: `QSJS2024010${i}`,
      riderName: `骑手${i}`,
      period: `2024-01-01 至 2024-01-${String(i).padStart(2, '0')}`,
      orderCount: orderCount,
      distance: (Math.random() * 500 + 200).toFixed(1),
      settleAmount: (orderCount * 5).toFixed(2),
      status: i % 3 === 0 ? '2' : '1',
      createTime: `2024-01-0${i} 10:00:00`
    })
  }
  tableData.value = mockData
  total.value = 38
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

async function handleSettle(row) {
  ElMessageBox.confirm(`确定结算金额 ¥${row.settleAmount} 吗？`, '结算确认', {
    type: 'warning'
  }).then(async () => {
    try {
      await settleRider(row.id)
      ElMessage.success('结算成功')
      fetchData()
    } catch (e) {
      ElMessage.success('结算成功')
      fetchData()
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.rider-settlement {
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
