import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const Layout = () => import('@/views/Layout.vue')

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '数据概览', icon: 'DataLine' }
      }
    ]
  },
  {
    path: '/product',
    component: Layout,
    meta: { title: '商品管理', icon: 'Goods' },
    children: [
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/product/Category.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'list',
        name: 'ProductList',
        component: () => import('@/views/product/ProductList.vue'),
        meta: { title: '商品列表' }
      },
      {
        path: 'sku',
        name: 'ProductSku',
        component: () => import('@/views/product/ProductSku.vue'),
        meta: { title: 'SKU管理' }
      }
    ]
  },
  {
    path: '/order',
    component: Layout,
    meta: { title: '订单管理', icon: 'List' },
    children: [
      {
        path: 'list',
        name: 'OrderList',
        component: () => import('@/views/order/OrderList.vue'),
        meta: { title: '订单列表' }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    meta: { title: '用户管理', icon: 'User' },
    children: [
      {
        path: 'list',
        name: 'UserList',
        component: () => import('@/views/user/UserList.vue'),
        meta: { title: '用户列表' }
      }
    ]
  },
  {
    path: '/warehouse',
    component: Layout,
    meta: { title: '仓储管理', icon: 'OfficeBuilding' },
    children: [
      {
        path: 'list',
        name: 'WarehouseList',
        component: () => import('@/views/warehouse/WarehouseList.vue'),
        meta: { title: '前置仓管理' }
      },
      {
        path: 'stock',
        name: 'StockList',
        component: () => import('@/views/warehouse/StockList.vue'),
        meta: { title: '库存管理' }
      },
      {
        path: 'transfer',
        name: 'TransferList',
        component: () => import('@/views/warehouse/TransferList.vue'),
        meta: { title: '调拨管理' }
      }
    ]
  },
  {
    path: '/delivery',
    component: Layout,
    meta: { title: '配送管理', icon: 'Van' },
    children: [
      {
        path: 'rider',
        name: 'RiderList',
        component: () => import('@/views/delivery/RiderList.vue'),
        meta: { title: '骑手管理' }
      },
      {
        path: 'list',
        name: 'DeliveryList',
        component: () => import('@/views/delivery/DeliveryList.vue'),
        meta: { title: '配送管理' }
      }
    ]
  },
  {
    path: '/settlement',
    component: Layout,
    meta: { title: '结算管理', icon: 'Wallet' },
    children: [
      {
        path: 'supplier',
        name: 'SupplierSettlement',
        component: () => import('@/views/settlement/SupplierSettlement.vue'),
        meta: { title: '供应商结算' }
      },
      {
        path: 'picker',
        name: 'PickerSettlement',
        component: () => import('@/views/settlement/PickerSettlement.vue'),
        meta: { title: '分拣员结算' }
      },
      {
        path: 'rider',
        name: 'RiderSettlement',
        component: () => import('@/views/settlement/RiderSettlement.vue'),
        meta: { title: '骑手结算' }
      },
      {
        path: 'loss',
        name: 'LossRecord',
        component: () => import('@/views/settlement/LossRecord.vue'),
        meta: { title: '损耗台账' }
      }
    ]
  },
  {
    path: '/aftersale',
    component: Layout,
    meta: { title: '售后管理', icon: 'ChatDotRound' },
    children: [
      {
        path: 'list',
        name: 'AftersaleList',
        component: () => import('@/views/aftersale/AftersaleList.vue'),
        meta: { title: '售后列表' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const whiteList = ['/login']

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 生鲜管理后台` : '生鲜管理后台'
  const token = getToken()
  if (token) {
    if (to.path === '/login') {
      next('/')
    } else {
      next()
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
    }
  }
})

export default router
