import React, { useState, useEffect } from 'react'
import { View, Text, Image, Button } from '@tarojs/components'
import Taro, { useRouter } from '@tarojs/taro'
import classnames from 'classnames'
import styles from './index.module.scss'
import { getProductById } from '@/data/products'
import { useCartStore } from '@/store/useCartStore'
import { formatPrice, getStorageTypeText } from '@/utils'
import type { ProductItem, ProductSku } from '@/types'

const DetailPage: React.FC = () => {
  const router = useRouter()
  const productId = Number(router.params.id)
  const [product, setProduct] = useState<ProductItem | null>(null)
  const [selectedSku, setSelectedSku] = useState<ProductSku | null>(null)
  const [quantity, setQuantity] = useState(1)
  const { addItem, getTotalCount } = useCartStore()

  useEffect(() => {
    const p = getProductById(productId)
    if (p) {
      setProduct(p)
      setSelectedSku(p.skuList[0] || null)
    }
  }, [productId])

  const handleAddCart = () => {
    if (!product || !selectedSku) return
    addItem({
      skuId: selectedSku.id,
      productId: product.id,
      name: product.name,
      skuName: selectedSku.skuName,
      image: product.mainImage,
      price: selectedSku.price,
      quantity,
      warehouseId: 1,
      warehouseName: '朝阳前置仓'
    })
    Taro.showToast({ title: '已加入购物车', icon: 'success' })
  }

  const handleBuyNow = () => {
    if (!product || !selectedSku) return
    handleAddCart()
    Taro.switchTab({ url: '/pages/cart/index' })
  }

  const handleGoCart = () => {
    Taro.switchTab({ url: '/pages/cart/index' })
  }

  if (!product || !selectedSku) {
    return (
      <View className={styles.page}>
        <Text>加载中...</Text>
      </View>
    )
  }

  return (
    <View className={styles.page}>
      <View className={styles.banner}>
        <Image
          className={styles.bannerImage}
          src={product.images[0] || product.mainImage}
          mode='aspectFill'
        />
      </View>

      <View className={styles.infoSection}>
        <View className={styles.priceRow}>
          <Text className={styles.symbol}>¥</Text>
          <Text className={styles.price}>{formatPrice(selectedSku.price)}</Text>
          <Text className={styles.originalPrice}>¥{formatPrice(selectedSku.originalPrice)}</Text>
        </View>
        <View className={styles.tags}>
          <View className={styles.tag}>
            <Text className={styles.tagText}>{getStorageTypeText(product.storageType)}</Text>
          </View>
          <View className={classnames(styles.tag, styles.orange)}>
            <Text className={styles.tagText}>已售{product.sales}+</Text>
          </View>
        </View>
        <Text className={styles.name}>{product.name}</Text>
        <Text className={styles.subtitle}>{product.subtitle}</Text>
      </View>

      <View className={styles.specSection}>
        <Text className={styles.sectionTitle}>规格选择</Text>
        <View className={styles.specList}>
          {product.skuList.map(sku => (
            <View
              key={sku.id}
              className={classnames(styles.specItem, selectedSku.id === sku.id && styles.active)}
              onClick={() => setSelectedSku(sku)}
            >
              <Text className={styles.specText}>{sku.skuName}</Text>
            </View>
          ))}
        </View>
      </View>

      <View className={styles.descSection}>
        <Text className={styles.descTitle}>商品详情</Text>
        {product.images.map((img, index) => (
          <Image
            key={index}
            className={styles.descImage}
            src={img}
            mode='widthFix'
          />
        ))}
      </View>

      <View className={styles.bottomBar}>
        <View className={styles.iconBtns}>
          <View className={styles.iconBtn} onClick={handleGoCart}>
            <Text className={styles.iconBtnIcon}>🛒</Text>
            <Text className={styles.iconBtnText}>购物车</Text>
          </View>
          <View className={styles.iconBtn}>
            <Text className={styles.iconBtnIcon}>❤️</Text>
            <Text className={styles.iconBtnText}>收藏</Text>
          </View>
        </View>
        <View className={styles.actionBtns}>
          <Button className={classnames(styles.actionBtn, styles.cart)} onClick={handleAddCart}>
            加入购物车
          </Button>
          <Button className={classnames(styles.actionBtn, styles.buy)} onClick={handleBuyNow}>
            立即购买
          </Button>
        </View>
      </View>
    </View>
  )
}

export default DetailPage
