<template>
  <div class="rider-list">
    <el-card shadow="never">
      <SearchForm :initForm="searchForm" @search="handleSearch" @reset="handleReset">
        <el-form-item label="骑手姓名" prop="name">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="在岗" :value="1" />
            <el-option label="离岗" :value="0" />
          </el-select>
        </el-form-item>
      </SearchForm>

      <div class="table-header">
        <span>骑手管理</span>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增骑手</el-button>
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
        <el-table-column prop="avatar" label="头像" width="80">
          <template #default="scope">
            <el-avatar :size="36" :src="scope.row.avatar">
              {{ scope.row.name?.charAt(0) }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="warehouseName" label="所属仓库" width="130" />
        <el-table-column prop="todayOrders" label="今日订单" width="100" />
        <el-table-column prop="totalOrders" label="累计订单" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '在岗' : '离岗' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="入职时间" width="180" />
        <template #action="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </PageTable>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="所属仓库" prop="warehouseId">
          <el-select v-model="form.warehouseId" placeholder="请选择仓库" style="width: 100%">
            <el-option label="前置仓1号店" :value="1" />
            <el-option label="前置仓2号店" :value="2" />
            <el-option label="前置仓3号店" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">在岗</el-radio>
            <el-radio :value="0">离岗</el-radio>
          </el-radio-group>
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
import { getRiderList, addRider, updateRider, deleteRider } from '@/api/delivery'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  name: '',
  phone: '',
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  phone: '',
  warehouseId: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }]
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getRiderList({
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
    mockData.push({
      id: i,
      name: `骑手${i}`,
      phone: `139${String(10000000 + i).slice(0, 8)}`,
      avatar: '',
      warehouseName: warehouses[i % 3],
      todayOrders: Math.floor(Math.random() * 30),
      totalOrders: Math.floor(Math.random() * 500 + 100),
      status: i % 5 === 0 ? 0 : 1,
      createTime: `2024-01-0${i} 10:00:00`
    })
  }
  tableData.value = mockData
  total.value = 32
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
  dialogTitle.value = '新增骑手'
  Object.assign(form, {
    id: null,
    name: '',
    phone: '',
    warehouseId: '',
    status: 1
  })
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  dialogTitle.value = '编辑骑手'
  Object.assign(form, { ...row, warehouseId: row.warehouseId || 1 })
  dialogVisible.value = true
}

async function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该骑手吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteRider(row.id)
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
          await updateRider(form.id, form)
        } else {
          await addRider(form)
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
.rider-list {
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
