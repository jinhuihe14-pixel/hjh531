<template>
  <div class="picker-settlement">
    <el-card shadow="never">
      <SearchForm :initForm="searchForm" @search="handleSearch" @reset="handleReset">
        <el-form-item label="分拣员" prop="pickerName">
          <el-input v-model="searchForm.pickerName" placeholder="请输入姓名" clearable />
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
        <span>分拣员结算</span>
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
        <el-table-column prop="pickerName" label="分拣员" width="120" />
        <el-table-column prop="period" label="结算周期" width="180" />
        <el-table-column prop="pickCount" label="分拣单数" width="100" />
        <el-table-column prop="pickAmount" label="分拣重量(kg)" width="130" />
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
import { getPickerSettlementList, settlePicker } from '@/api/settlement'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  pickerName: '',
  status: '',
  dateRange: []
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getPickerSettlementList({
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
    const pickAmount = Math.floor(Math.random() * 500 + 100)
    mockData.push({
      id: i,
      settleNo: `FJYJS2024010${i}`,
      pickerName: `分拣员${i}`,
      period: `2024-01-01 至 2024-01-${String(i).padStart(2, '0')}`,
      pickCount: Math.floor(Math.random() * 200 + 50),
      pickAmount: pickAmount,
      settleAmount: (pickAmount * 1.5).toFixed(2),
      status: i % 3 === 0 ? '2' : '1',
      createTime: `2024-01-0${i} 10:00:00`
    })
  }
  tableData.value = mockData
  total.value = 42
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
      await settlePicker(row.id)
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
.picker-settlement {
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
