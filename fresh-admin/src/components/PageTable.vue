<template>
  <div class="page-table">
    <el-table
      v-loading="loading"
      :data="tableData"
      border
      stripe
      style="width: 100%"
    >
      <el-table-column
        v-if="showIndex"
        type="index"
        label="序号"
        width="60"
        align="center"
      />
      <slot></slot>
      <el-table-column
        v-if="$slots.action"
        label="操作"
        :width="actionWidth"
        align="center"
        fixed="right"
      >
        <template #default="scope">
          <slot name="action" :row="scope.row" :index="scope.$index"></slot>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page="currentPage"
      :page-size="currentPageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      background
      style="margin-top: 16px; justify-content: flex-end"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  loading: {
    type: Boolean,
    default: false
  },
  tableData: {
    type: Array,
    default: () => []
  },
  total: {
    type: Number,
    default: 0
  },
  page: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 10
  },
  showIndex: {
    type: Boolean,
    default: true
  },
  actionWidth: {
    type: [Number, String],
    default: 200
  }
})

const emit = defineEmits(['update:page', 'update:pageSize', 'change'])

const currentPage = ref(props.page)
const currentPageSize = ref(props.pageSize)

watch(() => props.page, val => {
  currentPage.value = val
})

watch(() => props.pageSize, val => {
  currentPageSize.value = val
})

function handleSizeChange(size) {
  currentPageSize.value = size
  emit('update:pageSize', size)
  emit('change', { page: currentPage.value, pageSize: size })
}

function handleCurrentChange(page) {
  currentPage.value = page
  emit('update:page', page)
  emit('change', { page, pageSize: currentPageSize.value })
}
</script>

<style scoped>
.page-table {
  width: 100%;
}

.page-table .el-pagination {
  display: flex;
}
</style>
