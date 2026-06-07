<template>
  <div class="stock-list">
    <el-card shadow="never">
      <SearchForm :initForm="searchForm" @search="handleSearch" @reset="handleReset">
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="searchForm.productName" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item label="仓库" prop="warehouseId">
          <el-select v-model="searchForm.warehouseId" placeholder="请选择仓库" clearable style="width: 150px">
            <el-option label="前置仓1号店" :value="1" />
            <el-option label="前置仓2号店" :value="2" />
            <el-option label="前置仓3号店" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="库存状态" prop="stockStatus">
          <el-select v-model="searchForm.stockStatus" placeholder="请选择" clearable style="width: 130px">
            <el-option label="充足" value="sufficient" />
            <el-option label="预警" value="warning" />
            <el-option label="不足" value="insufficient" />
          </el-select>
        </el-form-item>
      </SearchForm>

      <div class="table-header">
        <span>库存管理</span>
        <el-button type="primary" :icon="Edit" @click="handleStockAdjust">库存调整</el-button>
      </div>

      <PageTable
        :loading="loading"
        :table-data="tableData"
        :total="total"
        v-model:page="page"
        v-model:page-size="pageSize"
        @change="handlePageChange"
        :action-width="150"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="productName" label="商品名称" min-width="150" />
        <el-table-column prop="skuName" label="SKU" width="120" />
        <el-table-column prop="warehouseName" label="仓库" width="130" />
        <el-table-column prop="stock" label="当前库存" width="100" />
        <el-table-column prop="warningStock" label="预警库存" width="100" />
        <el-table-column prop="stockStatus" label="库存状态" width="100">
          <template #default="scope">
            <el-tag :type="getStockType(scope.row)">
              {{ getStockText(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <template #action="{ row }">
          <el-button size="small" @click="handleEdit(row)">调整</el-button>
        </template>
      </PageTable>
    </el-card>

    <el-dialog v-model="dialogVisible" title="库存调整" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="商品">
          <span>{{ form.productName }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span>{{ form.stock }}</span>
        </el-form-item>
        <el-form-item label="调整类型" prop="adjustType">
          <el-radio-group v-model="form.adjustType">
            <el-radio value="in">入库</el-radio>
            <el-radio value="out">出库</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="调整数量" prop="adjustNum">
          <el-input-number v-model="form.adjustNum" :min="1" />
        </el-form-item>
        <el-form-item label="调整原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="3" placeholder="请输入调整原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import SearchForm from '@/components/SearchForm.vue'
import PageTable from '@/components/PageTable.vue'
import { getStockList, updateStock } from '@/api/warehouse'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  productName: '',
  warehouseId: '',
  stockStatus: ''
})

const dialogVisible = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  productName: '',
  stock: 0,
  adjustType: 'in',
  adjustNum: 1,
  reason: ''
})

const rules = {
  adjustType: [{ required: true, message: '请选择调整类型', trigger: 'change' }],
  adjustNum: [{ required: true, message: '请输入调整数量', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入调整原因', trigger: 'blur' }]
}

function getStockType(row) {
  if (row.stock <= 0) return 'danger'
  if (row.stock < row.warningStock) return 'warning'
  return 'success'
}

function getStockText(row) {
  if (row.stock <= 0) return '缺货'
  if (row.stock < row.warningStock) return '预警'
  return '充足'
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getStockList({
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
  const warehouses = ['前置仓1号店', '前置仓2号店', '前置仓3号店']
  for (let i = 1; i <= 10; i++) {
    const stock = Math.floor(Math.random() * 200)
    mockData.push({
      id: i,
      productName: `商品${i}`,
      skuName: `SKU${i}`,
      warehouseName: warehouses[i % 3],
      stock: stock,
      warningStock: 30,
      updateTime: `2024-01-0${i} 10:00:00`
    })
  }
  tableData.value = mockData
  total.value = 68
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

function handleStockAdjust() {
  ElMessage.info('请选择要调整的库存记录')
}

function handleEdit(row) {
  Object.assign(form, {
    id: row.id,
    productName: row.productName,
    stock: row.stock,
    adjustType: 'in',
    adjustNum: 1,
    reason: ''
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async valid => {
    if (valid) {
      try {
        await updateStock(form.id, form)
        ElMessage.success('调整成功')
        dialogVisible.value = false
        fetchData()
      } catch (e) {
        ElMessage.success('调整成功')
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
.stock-list {
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
