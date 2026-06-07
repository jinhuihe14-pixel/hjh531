import React, { useState, useEffect } from 'react'
import { View, Text, Image, Button } from '@tarojs/components'
import Taro, { useRouter } from '@tarojs/taro'
import classnames from 'classnames'
import styles from './index.module.scss'
import { getOrderById, deliveryTrackList } from '@/data/orders'
import { formatPrice } from '@/utils'
import type { OrderItem } from '@/types'

const OrderDetailPage: React.FC = () => {
  const router = useRouter()
  const orderId = Number(router.params.id)
  const [order, setOrder] = useState<OrderItem | null>(null)

  useEffect(() => {
    const o = getOrderById(orderId)
    if (o) setOrder(o)
  }, [orderId])

  const handleCancel = () => {
    Taro.showModal({
      title: '提示',
      content: '确定要取消订单吗？',
      success: (res) => {
        if (res.confirm) {
          Taro.showToast({ title: '订单已取消', icon: 'success' })
        }
      }
    })
  }

  const handlePay = () => {
    Taro.showToast({ title: '支付功能开发中', icon: 'none' })
  }

  const handleConfirm = () => {
    Taro.showModal({
      title: '提示',
      content: '确定已收到商品吗？',
      success: (res) => {
        if (res.confirm) {
          Taro.showToast({ title: '确认收货成功', icon: 'success' })
        }
      }
    })
  }

  const handleAftersale = () => {
    Taro.showToast({ title: '售后功能开发中', icon: 'none' })
  }

  if (!order) {
    return (
      <View className={styles.page}>
        <Text>加载中...</Text>
      </View>
    )
  }

  const showCancel = order.status === 1
  const showPay = order.status === 1
  const showConfirm = order.status === 3
  const showAftersale = order.status === 4

  return (
    <View className={styles.page}>
      <View className={styles.statusBar}>
        <Text className={styles.statusTitle}>{order.statusText}</Text>
        <Text className={styles.statusDesc}>
          {order.status === 1 && '请尽快完成支付，超时订单将自动取消'}
          {order.status === 2 && '商品正在准备中，请耐心等待'}
          {order.status === 3 && '骑手正在为您配送，请保持电话畅通'}
          {order.status === 4 && '订单已完成，感谢您的支持'}
          {order.status === 5 && '订单已取消'}
        </Text>
      </View>

      <View className={styles.addressCard}>
        <View className={styles.addressHeader}>
          <Text className={styles.addressIcon}>📍</Text>
          <Text className={styles.receiver}>{order.receiverName}</Text>
          <Text className={styles.phone}>{order.receiverPhone}</Text>
        </View>
        <Text className={styles.addressText}>{order.receiverAddress}</Text>
      </View>

      {order.warehouses.map(wh => (
        <View key={wh.warehouseId} className={styles.section}>
          <View className={styles.sectionHeader}>
            <Text className={styles.sectionTitle}>🏪 {wh.warehouseName}</Text>
            <Text className={styles.warehouseName}>{order.statusText}</Text>
          </View>
          <View className={styles.orderItems}>
            {wh.items.map(item => (
              <View key={item.skuId} className={styles.orderItem}>
                <Image
                  className={styles.itemImage}
                  src={item.image}
                  mode='aspectFill'
                />
                <View className={styles.itemInfo}>
                  <Text className={styles.itemName}>{item.skuName}</Text>
                  <Text className={styles.itemPrice}>¥{formatPrice(item.price)}</Text>
                </View>
                <Text className={styles.itemCount}>x{item.quantity}</Text>
              </View>
            ))}
          </View>
        </View>
      ))}

      {order.status === 3 && (
        <View className={styles.trackSection}>
          <Text className={styles.trackTitle}>配送轨迹</Text>
          <View className={styles.trackList}>
            {deliveryTrackList.map((track, index) => (
              <View
                key={index}
                className={classnames(styles.trackItem, index === 0 && styles.first)}
              >
                <View className={styles.trackDot} />
                <Text className={styles.trackStatus}>{track.status}</Text>
                <Text className={styles.trackDesc}>{track.description}</Text>
                <Text className={styles.trackTime}>{track.time}</Text>
              </View>
            ))}
          </View>
        </View>
      )}

      <View className={styles.section}>
        <View className={styles.sectionHeader}>
          <Text className={styles.sectionTitle}>订单信息</Text>
        </View>
        <View className={styles.orderInfo}>
          <View className={styles.infoRow}>
            <Text className={styles.infoLabel}>订单编号</Text>
            <Text className={styles.infoValue}>{order.orderNo}</Text>
          </View>
          <View className={styles.infoRow}>
            <Text className={styles.infoLabel}>下单时间</Text>
            <Text className={styles.infoValue}>{order.createTime}</Text>
          </View>
          <View className={classnames(styles.infoRow, styles.totalRow)}>
            <Text className={styles.totalLabel}>实付金额</Text>
            <Text className={styles.totalValue}>¥{formatPrice(order.payAmount)}</Text>
          </View>
        </View>
      </View>

      <View className={styles.bottomBar}>
        {showCancel && (
          <Button className={classnames(styles.btn, styles.danger)} onClick={handleCancel}>
            取消订单
          </Button>
        )}
        {showPay && (
          <Button className={classnames(styles.btn, styles.primary)} onClick={handlePay}>
            立即支付
          </Button>
        )}
        {showConfirm && (
          <Button className={classnames(styles.btn, styles.primary)} onClick={handleConfirm}>
            确认收货
          </Button>
        )}
        {showAftersale && (
          <Button className={classnames(styles.btn, styles.outline)} onClick={handleAftersale}>
            申请售后
          </Button>
        )}
      </View>
    </View>
  )
}

export default OrderDetailPage
