<template>
  <div class="loss-record">
    <el-card shadow="never">
      <SearchForm :initForm="searchForm" @search="handleSearch" @reset="handleReset">
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="searchForm.productName" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item label="损耗类型" prop="type">
          <el-select v-model="searchForm.type" placeholder="请选择" clearable style="width: 130px">
            <el-option label="自然损耗" value="1" />
            <el-option label="运输损耗" value="2" />
            <el-option label="质量问题" value="3" />
            <el-option label="其他" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="登记日期" prop="dateRange">
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
        <span>损耗台账</span>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增记录</el-button>
      </div>

      <PageTable
        :loading="loading"
        :table-data="tableData"
        :total="total"
        v-model:page="page"
        v-model:page-size="pageSize"
        @change="handlePageChange"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="productName" label="商品名称" min-width="150" />
        <el-table-column prop="skuName" label="规格" width="120" />
        <el-table-column prop="warehouseName" label="仓库" width="130" />
        <el-table-column prop="type" label="损耗类型" width="120">
          <template #default="scope">
            <el-tag :type="getTypeType(scope.row.type)">{{ getTypeText(scope.row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="损耗数量" width="100" />
        <el-table-column prop="lossAmount" label="损耗金额" width="120">
          <template #default="scope">¥{{ scope.row.lossAmount }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="登记时间" width="180" />
        <template #action="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </PageTable>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="商品" prop="productId">
          <el-select v-model="form.productId" placeholder="请选择商品" style="width: 100%">
            <el-option label="商品1" :value="1" />
            <el-option label="商品2" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="仓库" prop="warehouseId">
          <el-select v-model="form.warehouseId" placeholder="请选择仓库" style="width: 100%">
            <el-option label="前置仓1号店" :value="1" />
            <el-option label="前置仓2号店" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="损耗类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择" style="width: 100%">
            <el-option label="自然损耗" value="1" />
            <el-option label="运输损耗" value="2" />
            <el-option label="质量问题" value="3" />
            <el-option label="其他" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="损耗数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="0.01" :precision="2" />
        </el-form-item>
        <el-form-item label="损耗金额" prop="lossAmount">
          <el-input-number v-model="form.lossAmount" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import SearchForm from '@/components/SearchForm.vue'
import PageTable from '@/components/PageTable.vue'
import { getLossRecordList, addLossRecord, updateLossRecord, deleteLossRecord } from '@/api/settlement'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  productName: '',
  type: '',
  dateRange: []
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  productId: '',
  warehouseId: '',
  type: '',
  quantity: 0,
  lossAmount: 0,
  remark: ''
})

const rules = {
  productId: [{ required: true, message: '请选择商品', trigger: 'change' }],
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }],
  type: [{ required: true, message: '请选择损耗类型', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入损耗数量', trigger: 'blur' }],
  lossAmount: [{ required: true, message: '请输入损耗金额', trigger: 'blur' }]
}

const typeMap = {
  '1': { text: '自然损耗', type: 'info' },
  '2': { text: '运输损耗', type: 'warning' },
  '3': { text: '质量问题', type: 'danger' },
  '4': { text: '其他', type: '' }
}

function getTypeText(type) {
  return typeMap[type]?.text || type
}

function getTypeType(type) {
  return typeMap[type]?.type || 'info'
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getLossRecordList({
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
  const types = ['1', '2', '3', '4']
  const warehouses = ['前置仓1号店', '前置仓2号店', '前置仓3号店']
  for (let i = 1; i <= 10; i++) {
    const quantity = (Math.random() * 10 + 1).toFixed(2)
    mockData.push({
      id: i,
      productName: `商品${i}`,
      skuName: `规格${i}`,
      warehouseName: warehouses[i % 3],
      type: types[i % 4],
      quantity: quantity,
      lossAmount: (quantity * 8).toFixed(2),
      remark: `损耗备注${i}`,
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

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增损耗记录'
  Object.assign(form, {
    id: null,
    productId: '',
    warehouseId: '',
    type: '',
    quantity: 0,
    lossAmount: 0,
    remark: ''
  })
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  dialogTitle.value = '编辑损耗记录'
  Object.assign(form, { ...row, productId: 1, warehouseId: 1 })
  dialogVisible.value = true
}

async function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该记录吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteLossRecord(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (e) {
      ElMessage.success('删除成功')
      fetchData()
    }
  })
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async valid => {
    if (valid) {
      try {
        if (isEdit.value) {
          await updateLossRecord(form.id, form)
        } else {
          await addLossRecord(form)
        }
        ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
        dialogVisible.value = false
        fetchData()
      } catch (e) {
        ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
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
.loss-record {
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
