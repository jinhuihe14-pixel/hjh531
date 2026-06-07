import React from 'react'
import { View, Text, Image } from '@tarojs/components'
import Taro from '@tarojs/taro'
import classnames from 'classnames'
import styles from './index.module.scss'
import type { CartItem as CartItemType } from '@/types'
import { formatPrice } from '@/utils'

interface Props {
  item: CartItemType
  onSelect?: () => void
  onQuantityChange?: (quantity: number) => void
  onDelete?: () => void
  showSelect?: boolean
  showDelete?: boolean
}

const CartItem: React.FC<Props> = ({
  item,
  onSelect,
  onQuantityChange,
  onDelete,
  showSelect = true,
  showDelete = true
}) => {
  const handleMinus = () => {
    if (item.quantity <= 1) {
      if (onDelete) onDelete()
      return
    }
    onQuantityChange?.(item.quantity - 1)
  }

  const handlePlus = () => {
    onQuantityChange?.(item.quantity + 1)
  }

  const handleClick = () => {
    Taro.navigateTo({
      url: `/pages/detail/index?id=${item.productId}`
    })
  }

  return (
    <View className={styles.item}>
      {showSelect && (
        <View
          className={classnames(styles.checkbox, item.selected && styles.checked)}
          onClick={onSelect}
        >
          {item.selected && <Text className={styles.checkIcon}>✓</Text>}
        </View>
      )}
      <View className={styles.content} onClick={handleClick}>
        <Image
          className={styles.image}
          src={item.image}
          mode='aspectFill'
        />
        <View className={styles.info}>
          <Text className={styles.name}>{item.name}</Text>
          <Text className={styles.sku}>{item.skuName}</Text>
          <View className={styles.bottom}>
            <Text className={styles.price}>¥{formatPrice(item.price)}</Text>
            <View className={styles.quantity}>
              <View className={styles.btn} onClick={handleMinus}>
                <Text className={styles.btnText}>−</Text>
              </View>
              <Text className={styles.num}>{item.quantity}</Text>
              <View className={styles.btn} onClick={handlePlus}>
                <Text className={styles.btnText}>+</Text>
              </View>
            </View>
          </View>
        </View>
      </View>
    </View>
  )
}

export default CartItem
