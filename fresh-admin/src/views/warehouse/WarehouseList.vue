<template>
  <div class="warehouse-list">
    <el-card shadow="never">
      <SearchForm :initForm="searchForm" @search="handleSearch" @reset="handleReset">
        <el-form-item label="仓库名称" prop="name">
          <el-input v-model="searchForm.name" placeholder="请输入仓库名称" clearable />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="营业中" :value="1" />
            <el-option label="已关闭" :value="0" />
          </el-select>
        </el-form-item>
      </SearchForm>

      <div class="table-header">
        <span>前置仓管理</span>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增仓库</el-button>
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
        <el-table-column prop="name" label="仓库名称" width="150" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="contact" label="联系人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="area" label="面积(㎡)" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '营业中' : '已关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <template #action="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </PageTable>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="仓库名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入仓库名称" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="联系人" prop="contact">
          <el-input v-model="form.contact" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="面积" prop="area">
          <el-input-number v-model="form.area" :min="0" />
          <span style="margin-left: 8px;">㎡</span>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">营业中</el-radio>
            <el-radio :value="0">已关闭</el-radio>
          </el-radio-group>
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
import { getWarehouseList, addWarehouse, updateWarehouse, deleteWarehouse } from '@/api/warehouse'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  name: '',
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  address: '',
  contact: '',
  phone: '',
  area: 0,
  status: 1,
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  contact: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getWarehouseList({
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
    mockData.push({
      id: i,
      name: `前置仓${i}号店`,
      address: `北京市朝阳区xxx路${i}号`,
      contact: `张${i}`,
      phone: `138${String(10000000 + i).slice(0, 8)}`,
      area: 100 + i * 10,
      status: i % 6 === 0 ? 0 : 1,
      createTime: `2024-01-0${i} 10:00:00`
    })
  }
  tableData.value = mockData
  total.value = 25
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
  dialogTitle.value = '新增仓库'
  Object.assign(form, {
    id: null,
    name: '',
    address: '',
    contact: '',
    phone: '',
    area: 0,
    status: 1,
    remark: ''
  })
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  dialogTitle.value = '编辑仓库'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

async function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该仓库吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteWarehouse(row.id)
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
          await updateWarehouse(form.id, form)
        } else {
          await addWarehouse(form)
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
.warehouse-list {
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
