import React from 'react'
import { View, Text, Image } from '@tarojs/components'
import Taro from '@tarojs/taro'
import styles from './index.module.scss'
import type { ProductItem } from '@/types'
import { formatPrice } from '@/utils'

interface Props {
  product: ProductItem
  onClick?: () => void
}

const ProductCard: React.FC<Props> = ({ product, onClick }) => {
  const handleClick = () => {
    if (onClick) {
      onClick()
    } else {
      Taro.navigateTo({
        url: `/pages/detail/index?id=${product.id}`
      })
    }
  }

  const storageTypeText = product.storageType === 'cold' ? '冷藏' : product.storageType === 'frozen' ? '冷冻' : '常温'

  return (
    <View className={styles.card} onClick={handleClick}>
      <View className={styles.imageWrap}>
        <Image
          className={styles.image}
          src={product.mainImage}
          mode='aspectFill'
        />
        <View className={styles.storageTag}>{storageTypeText}</View>
      </View>
      <View className={styles.content}>
        <Text className={styles.name}>{product.name}</Text>
        <Text className={styles.subtitle}>{product.subtitle}</Text>
        <View className={styles.bottom}>
          <View className={styles.priceWrap}>
            <Text className={styles.symbol}>¥</Text>
            <Text className={styles.price}>{formatPrice(product.minPrice)}</Text>
          </View>
          <Text className={styles.sales}>已售{product.sales}</Text>
        </View>
      </View>
    </View>
  )
}

export default ProductCard
