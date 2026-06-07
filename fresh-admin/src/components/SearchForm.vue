<template>
  <div class="search-form">
    <el-form :model="form" inline>
      <slot></slot>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'

const props = defineProps({
  initForm: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['search', 'reset'])

const form = reactive({ ...props.initForm })

function handleSearch() {
  emit('search', { ...form })
}

function handleReset() {
  Object.keys(form).forEach(key => {
    form[key] = props.initForm[key] ?? ''
  })
  emit('reset', { ...form })
}
</script>

<style scoped>
.search-form {
  margin-bottom: 16px;
  padding: 16px;
  background: #fff;
  border-radius: 4px;
}
</style>
