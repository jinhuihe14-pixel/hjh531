import React, { useMemo } from 'react'
import { View, Text, Button } from '@tarojs/components'
import Taro from '@tarojs/taro'
import classnames from 'classnames'
import styles from './index.module.scss'
import CartItem from '@/components/CartItem'
import { useCartStore } from '@/store/useCartStore'
import { formatPrice } from '@/utils'

const CartPage: React.FC = () => {
  const { items, toggleSelect, toggleSelectAll, updateQuantity, removeItem, getTotalCount, getTotalPrice, getSelectedItems } = useCartStore()

  const allSelected = useMemo(() => {
    if (items.length === 0) return false
    return items.every(i => i.selected)
  }, [items])

  const selectedCount = useMemo(() => {
    return getSelectedItems().reduce((sum, i) => sum + i.quantity, 0)
  }, [items])

  const handleSelectAll = () => {
    toggleSelectAll(!allSelected)
  }

  const handleQuantityChange = (skuId: number, quantity: number) => {
    updateQuantity(skuId, quantity)
  }

  const handleDelete = (skuId: number) => {
    Taro.showModal({
      title: '提示',
      content: '确定要删除该商品吗？',
      success: (res) => {
        if (res.confirm) {
          removeItem(skuId)
        }
      }
    })
  }

  const handleSettle = () => {
    if (selectedCount === 0) {
      Taro.showToast({ title: '请选择商品', icon: 'none' })
      return
    }
    console.log('[Cart] settle items:', getSelectedItems())
    Taro.showToast({ title: '结算功能开发中', icon: 'none' })
  }

  const handleGoShopping = () => {
    Taro.switchTab({ url: '/pages/home/index' })
  }

  if (items.length === 0) {
    return (
      <View className={styles.page}>
        <View className={styles.empty}>
          <Text className={styles.emptyIcon}>🛒</Text>
          <Text className={styles.emptyText}>购物车是空的~</Text>
          <Button className={styles.emptyBtn} onClick={handleGoShopping}>去逛逛</Button>
        </View>
      </View>
    )
  }

  const groupedItems = items.reduce((acc, item) => {
    const key = item.warehouseId
    if (!acc[key]) {
      acc[key] = { name: item.warehouseName, items: [] }
    }
    acc[key].items.push(item)
    return acc
  }, {} as Record<number, { name: string; items: typeof items }>)

  return (
    <View className={styles.page}>
      <View className={styles.cartList}>
        {Object.entries(groupedItems).map(([warehouseId, group]) => {
          const whItems = group.items
          const whAllSelected = whItems.every(i => i.selected)
          return (
            <View key={warehouseId} className={styles.warehouseGroup}>
              <View className={styles.warehouseHeader}>
                <View
                  className={classnames(styles.warehouseCheckbox, whAllSelected && styles.checked)}
                  onClick={() => {
                    const allSelected = whItems.every(i => i.selected)
                    whItems.forEach(item => {
                      if (item.selected !== !allSelected) {
                        toggleSelect(item.skuId)
                      }
                    })
                  }}
                >
                  {whAllSelected && <Text className={styles.warehouseCheckIcon}>✓</Text>}
                </View>
                <Text className={styles.warehouseName}>🏪 {group.name}</Text>
              </View>
              <View className={styles.cartItems}>
                {whItems.map(item => (
                  <CartItem
                    key={item.skuId}
                    item={item}
                    onSelect={() => toggleSelect(item.skuId)}
                    onQuantityChange={(qty) => handleQuantityChange(item.skuId, qty)}
                    onDelete={() => handleDelete(item.skuId)}
                  />
                ))}
              </View>
            </View>
          )
        })}
      </View>

      <View className={styles.bottomBar}>
        <View className={styles.selectAll} onClick={handleSelectAll}>
          <View className={classnames(styles.selectAllCheckbox, allSelected && styles.checked)}>
            {allSelected && <Text className={styles.selectAllCheckIcon}>✓</Text>}
          </View>
          <Text className={styles.selectAllText}>全选</Text>
        </View>
        <View className={styles.totalInfo}>
          <Text className={styles.totalText}>合计：</Text>
          <Text className={styles.totalPrice}>¥{formatPrice(getTotalPrice())}</Text>
        </View>
        <Button
          className={classnames(styles.settleBtn, selectedCount === 0 && styles.disabled)}
          onClick={handleSettle}
        >
          结算({selectedCount})
        </Button>
      </View>
    </View>
  )
}

export default CartPage
