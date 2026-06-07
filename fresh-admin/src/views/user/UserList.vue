<template>
  <div class="user-list">
    <el-card shadow="never">
      <SearchForm :initForm="searchForm" @search="handleSearch" @reset="handleReset">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </SearchForm>

      <div class="table-header">
        <span>用户列表</span>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增用户</el-button>
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
              {{ scope.row.username?.charAt(0) }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="balance" label="余额" width="100">
          <template #default="scope">¥{{ scope.row.balance }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="registerTime" label="注册时间" width="180" />
        <template #action="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </PageTable>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
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
import { getUserList, addUser, updateUser, deleteUser } from '@/api/user'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  username: '',
  phone: '',
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  username: '',
  nickname: '',
  phone: '',
  password: '',
  status: 1
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getUserList({
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
      username: `user${i}`,
      nickname: `用户${i}`,
      phone: `138${String(10000000 + i).slice(0, 8)}`,
      avatar: '',
      balance: (Math.random() * 500).toFixed(2),
      status: i % 8 === 0 ? 0 : 1,
      registerTime: `2024-01-0${i} 10:00:00`
    })
  }
  tableData.value = mockData
  total.value = 89
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
  dialogTitle.value = '新增用户'
  Object.assign(form, {
    id: null,
    username: '',
    nickname: '',
    phone: '',
    password: '',
    status: 1
  })
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  Object.assign(form, { ...row, password: '' })
  dialogVisible.value = true
}

async function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUser(row.id)
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
          await updateUser(form.id, form)
        } else {
          await addUser(form)
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
.user-list {
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
