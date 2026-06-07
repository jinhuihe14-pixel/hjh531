<template>
  <div class="category">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>分类管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增分类</el-button>
        </div>
      </template>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
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
import { getCategoryList, addCategory, updateCategory, deleteCategory } from '@/api/product'

const tableData = ref([
  { id: 1, name: '蔬菜', sort: 1, status: 1, createTime: '2024-01-01 10:00:00' },
  { id: 2, name: '水果', sort: 2, status: 1, createTime: '2024-01-01 10:00:00' },
  { id: 3, name: '肉类', sort: 3, status: 1, createTime: '2024-01-01 10:00:00' },
  { id: 4, name: '乳制品', sort: 4, status: 1, createTime: '2024-01-01 10:00:00' },
  { id: 5, name: '海鲜', sort: 5, status: 0, createTime: '2024-01-01 10:00:00' }
])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  sort: 0,
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

async function fetchData() {
  try {
    const res = await getCategoryList()
    tableData.value = res.data.list
  } catch (e) {
  }
}

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增分类'
  Object.assign(form, { id: null, name: '', sort: 0, status: 1 })
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  dialogTitle.value = '编辑分类'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

async function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该分类吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCategory(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (e) {
    }
  })
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async valid => {
    if (valid) {
      try {
        if (isEdit.value) {
          await updateCategory(form.id, form)
        } else {
          await addCategory(form)
        }
        ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
        dialogVisible.value = false
        fetchData()
      } catch (e) {
      }
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.category {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
