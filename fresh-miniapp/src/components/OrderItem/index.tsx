import React from 'react'
import { View, Text, Image } from '@tarojs/components'
import Taro from '@tarojs/taro'
import styles from './index.module.scss'
import type { OrderItem as OrderItemType } from '@/types'
import { formatPrice } from '@/utils'

interface Props {
  order: OrderItemType
  onClick?: () => void
}

const OrderItem: React.FC<Props> = ({ order, onClick }) => {
  const handleClick = () => {
    if (onClick) {
      onClick()
    } else {
      Taro.navigateTo({
        url: `/pages/orderDetail/index?id=${order.id}`
      })
    }
  }

  const allItems = order.warehouses.flatMap(w => w.items)
  const firstItem = allItems[0]

  return (
    <View className={styles.card} onClick={handleClick}>
      <View className={styles.header}>
        <Text className={styles.orderNo}>订单号：{order.orderNo}</Text>
        <Text className={styles.status}>{order.statusText}</Text>
      </View>

      {order.warehouses.map(wh => (
        <View key={wh.warehouseId} className={styles.warehouseSection}>
          <Text className={styles.warehouseName}>{wh.warehouseName}</Text>
          {wh.items.map(item => (
            <View key={item.skuId} className={styles.item}>
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
      ))}

      <View className={styles.footer}>
        <Text className={styles.totalText}>
          共{order.totalCount}件商品 实付：
          <Text className={styles.totalPrice}>¥{formatPrice(order.payAmount)}</Text>
        </Text>
      </View>
    </View>
  )
}

export default OrderItem
