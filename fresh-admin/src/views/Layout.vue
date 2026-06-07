<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <h2 v-show="!isCollapse">生鲜管理</h2>
        <h2 v-show="isCollapse">鲜</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <el-sub-menu v-if="route.children && route.children.length > 1" :index="route.path">
            <template #title>
              <el-icon><component :is="route.meta.icon" /></el-icon>
              <span>{{ route.meta.title }}</span>
            </template>
            <el-menu-item
              v-for="child in route.children"
              :key="child.path"
              :index="`${route.path}/${child.path}`"
            >
              {{ child.meta.title }}
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="`${route.path}/${route.children[0].path}`">
            <el-icon><component :is="route.meta.icon" /></el-icon>
            <span>{{ route.meta.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item
              v-for="item in breadcrumbs"
              :key="item.path"
            >
              {{ item.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="username">{{ userInfo.username || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  Fold,
  Expand,
  ArrowDown,
  UserFilled,
  DataLine,
  Goods,
  List,
  User,
  OfficeBuilding,
  Van,
  Wallet,
  ChatDotRound
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const userInfo = computed(() => userStore.userInfo)

const iconMap = {
  DataLine,
  Goods,
  List,
  User,
  OfficeBuilding,
  Van,
  Wallet,
  ChatDotRound
}

const menuRoutes = computed(() => {
  const routes = router.options.routes.filter(r => r.path !== '/login' && r.path !== '/')
  return routes.map(route => {
    if (route.meta?.icon) {
      route.meta.icon = iconMap[route.meta.icon] || Goods
    }
    return route
  })
})

const activeMenu = computed(() => {
  return route.path
})

const breadcrumbs = computed(() => {
  const matched = route.matched.filter(item => item.meta && item.meta.title)
  return matched
})

function toggleCollapse() {
  isCollapse.value = !isCollapse.value
}

function handleCommand(command) {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      await userStore.logout()
      ElMessage.success('退出成功')
      router.push('/login')
    })
  } else if (command === 'profile') {
    ElMessage.info('个人中心功能开发中')
  }
}
</script>

<style scoped>
.layout-container {
  width: 100%;
  height: 100%;
}

.sidebar {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.sidebar .el-menu {
  border-right: none;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  background: #2b2f3a;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  white-space: nowrap;
  overflow: hidden;
}

.logo h2 {
  margin: 0;
  color: #fff;
  font-size: 18px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: #333;
}

.main-content {
  background: #f0f2f5;
  padding: 20px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
