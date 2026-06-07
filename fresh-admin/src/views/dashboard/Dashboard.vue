<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon icon-1">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">¥{{ formatNumber(salesTotal) }}</p>
              <p class="stat-label">今日销售额</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon icon-2">
              <el-icon><List /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ formatNumber(orderCount) }}</p>
              <p class="stat-label">今日订单数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon icon-3">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ formatNumber(userCount) }}</p>
              <p class="stat-label">注册用户数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon icon-4">
              <el-icon><Goods /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ formatNumber(productCount) }}</p>
              <p class="stat-label">商品数量</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>销售趋势</span>
          </template>
          <div class="chart-placeholder">
            <el-empty description="销售趋势图表" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>商品分类销量</span>
          </template>
          <div class="chart-placeholder">
            <el-empty description="分类销量图表" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>最新订单</span>
          </template>
          <el-table :data="recentOrders" size="small">
            <el-table-column prop="orderNo" label="订单号" />
            <el-table-column prop="amount" label="金额" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>热门商品</span>
          </template>
          <el-table :data="hotProducts" size="small">
            <el-table-column prop="name" label="商品名称" />
            <el-table-column prop="sales" label="销量" />
            <el-table-column prop="amount" label="销售额" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Money, List, User, Goods } from '@element-plus/icons-vue'

const salesTotal = ref(125680)
const orderCount = ref(356)
const userCount = ref(2890)
const productCount = ref(156)

const recentOrders = ref([
  { orderNo: '202401010001', amount: '¥128.00', status: '待发货' },
  { orderNo: '202401010002', amount: '¥256.00', status: '配送中' },
  { orderNo: '202401010003', amount: '¥89.00', status: '已完成' },
  { orderNo: '202401010004', amount: '¥345.00', status: '待发货' },
  { orderNo: '202401010005', amount: '¥178.00', status: '已完成' }
])

const hotProducts = ref([
  { name: '有机西红柿', sales: 1256, amount: '¥8,792' },
  { name: '新鲜草莓', sales: 986, amount: '¥14,790' },
  { name: '精选苹果', sales: 876, amount: '¥5,256' },
  { name: '有机黄瓜', sales: 756, amount: '¥4,536' },
  { name: '新鲜牛奶', sales: 654, amount: '¥9,810' }
])

function formatNumber(num) {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

function getStatusType(status) {
  const map = {
    '待发货': 'warning',
    '配送中': 'primary',
    '已完成': 'success'
  }
  return map[status] || 'info'
}

onMounted(() => {
})
</script>

<style scoped>
.dashboard {
  width: 100%;
}

.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  color: #fff;
}

.icon-1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.icon-2 {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.icon-3 {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.icon-4 {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin: 0 0 4px 0;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
