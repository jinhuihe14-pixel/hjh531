import React from 'react'
import { View, Text } from '@tarojs/components'
import Taro from '@tarojs/taro'
import styles from './index.module.scss'
import { useUserStore } from '@/store/useUserStore'

const MinePage: React.FC = () => {
  const { userInfo, logout } = useUserStore()

  const orderStatusList = [
    { icon: '💳', text: '待付款', status: 1, count: 0 },
    { icon: '📦', text: '待发货', status: 2, count: 2 },
    { icon: '🚴', text: '配送中', status: 3, count: 1 },
    { icon: '✅', text: '已完成', status: 4, count: 0 },
    { icon: '🔄', text: '售后', status: 6, count: 0 }
  ]

  const menuList1 = [
    { icon: '📍', text: '收货地址', url: '/pages/address/index' },
    { icon: '❤️', text: '我的收藏', url: '' },
    { icon: '🎫', text: '优惠券', url: '' },
    { icon: '💬', text: '消息中心', url: '' }
  ]

  const menuList2 = [
    { icon: '⚙️', text: '设置', url: '' },
    { icon: '📞', text: '联系客服', url: '' },
    { icon: '❓', text: '帮助中心', url: '' }
  ]

  const handleOrderClick = (status: number) => {
    Taro.navigateTo({
      url: `/pages/orderList/index?status=${status}`
    })
  }

  const handleAllOrders = () => {
    Taro.navigateTo({ url: '/pages/orderList/index?status=0' })
  }

  const handleMenuClick = (url: string) => {
    if (!url) {
      Taro.showToast({ title: '功能开发中', icon: 'none' })
      return
    }
    Taro.navigateTo({ url })
  }

  const handleLogin = () => {
    Taro.navigateTo({ url: '/pages/login/index' })
  }

  const handleLogout = () => {
    Taro.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          logout()
          Taro.showToast({ title: '已退出登录', icon: 'success' })
        }
      }
    })
  }

  return (
    <View className={styles.page}>
      <View className={styles.header}>
        <View className={styles.userInfo}>
          <View className={styles.avatar}>
            <Text>{userInfo?.avatar || '👤'}</Text>
          </View>
          <View className={styles.userText}>
            <Text className={styles.userName} onClick={!userInfo ? handleLogin : undefined}>
              {userInfo?.nickname || userInfo?.username || '点击登录'}
            </Text>
            <Text className={styles.userDesc}>
              {userInfo ? `积分：${userInfo.integral} | ${userInfo.level}级会员` : '登录后享受更多优惠'}
            </Text>
          </View>
        </View>
      </View>

      <View className={styles.orderSection}>
        <View className={styles.orderHeader}>
          <Text className={styles.orderTitle}>我的订单</Text>
          <Text className={styles.orderMore} onClick={handleAllOrders}>全部订单 ›</Text>
        </View>
        <View className={styles.orderStatus}>
          {orderStatusList.map(item => (
            <View
              key={item.status}
              className={styles.orderStatusItem}
              onClick={() => handleOrderClick(item.status)}
            >
              <Text className={styles.orderStatusIcon}>{item.icon}</Text>
              <Text className={styles.orderStatusText}>{item.text}</Text>
              {item.count > 0 && (
                <View className={styles.orderBadge}>
                  <Text className={styles.badgeText}>{item.count}</Text>
                </View>
              )}
            </View>
          ))}
        </View>
      </View>

      <View className={styles.menuSection}>
        {menuList1.map((item, index) => (
          <View
            key={index}
            className={styles.menuItem}
            onClick={() => handleMenuClick(item.url)}
          >
            <Text className={styles.menuIcon}>{item.icon}</Text>
            <Text className={styles.menuText}>{item.text}</Text>
            <Text className={styles.menuArrow}>›</Text>
          </View>
        ))}
      </View>

      <View className={styles.menuSection}>
        {menuList2.map((item, index) => (
          <View
            key={index}
            className={styles.menuItem}
            onClick={() => handleMenuClick(item.url)}
          >
            <Text className={styles.menuIcon}>{item.icon}</Text>
            <Text className={styles.menuText}>{item.text}</Text>
            <Text className={styles.menuArrow}>›</Text>
          </View>
        ))}
      </View>

      {userInfo && (
        <View className={styles.logoutBtn} onClick={handleLogout}>
          <Text>退出登录</Text>
        </View>
      )}
    </View>
  )
}

export default MinePage
