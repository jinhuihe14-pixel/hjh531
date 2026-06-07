import React, { useState, useEffect } from 'react'
import { View, Text, ScrollView } from '@tarojs/components'
import Taro, { useRouter } from '@tarojs/taro'
import classnames from 'classnames'
import styles from './index.module.scss'
import OrderItem from '@/components/OrderItem'
import { getOrdersByStatus } from '@/data/orders'
import type { OrderItem as OrderItemType } from '@/types'

const OrderListPage: React.FC = () => {
  const router = useRouter()
  const initialStatus = Number(router.params.status) || 0

  const tabs = [
    { status: 0, text: '全部' },
    { status: 1, text: '待支付' },
    { status: 2, text: '待发货' },
    { status: 3, text: '配送中' },
    { status: 4, text: '已完成' }
  ]

  const [activeTab, setActiveTab] = useState(initialStatus)
  const [orders, setOrders] = useState<OrderItemType[]>([])

  useEffect(() => {
    const list = getOrdersByStatus(activeTab)
    setOrders(list)
  }, [activeTab])

  return (
    <View className={styles.page}>
      <View className={styles.tabs}>
        {tabs.map(tab => (
          <View
            key={tab.status}
            className={classnames(styles.tabItem, activeTab === tab.status && styles.active)}
            onClick={() => setActiveTab(tab.status)}
          >
            <Text className={styles.tabText}>{tab.text}</Text>
          </View>
        ))}
      </View>

      <ScrollView scrollY className={styles.list}>
        {orders.length > 0 ? (
          orders.map(order => (
            <OrderItem key={order.id} order={order} />
          ))
        ) : (
          <View className={styles.empty}>
            <Text className={styles.emptyIcon}>📋</Text>
            <Text className={styles.emptyText}>暂无订单</Text>
          </View>
        )}
      </ScrollView>
    </View>
  )
}

export default OrderListPage
