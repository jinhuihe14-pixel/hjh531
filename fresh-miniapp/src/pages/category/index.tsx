import React, { useState, useEffect } from 'react'
import { View, Text, ScrollView } from '@tarojs/components'
import styles from './index.module.scss'
import classnames from 'classnames'
import ProductCard from '@/components/ProductCard'
import { categoryList } from '@/data/categories'
import { productList, getProductsByCategory } from '@/data/products'
import type { ProductItem } from '@/types'

const CategoryPage: React.FC = () => {
  const [activeCategory, setActiveCategory] = useState(1)
  const [products, setProducts] = useState<ProductItem[]>([])

  useEffect(() => {
    const list = getProductsByCategory(activeCategory)
    setProducts(list.length > 0 ? list : productList.slice(0, 6))
  }, [activeCategory])

  const activeCat = categoryList.find(c => c.id === activeCategory)

  return (
    <View className={styles.page}>
      <ScrollView scrollY className={styles.sidebar}>
        {categoryList.map(item => (
          <View
            key={item.id}
            className={classnames(styles.sidebarItem, activeCategory === item.id && styles.active)}
            onClick={() => setActiveCategory(item.id)}
          >
            <View className={styles.sidebarIcon}>{item.icon}</View>
            <Text className={styles.sidebarText}>{item.name}</Text>
          </View>
        ))}
      </ScrollView>

      <ScrollView scrollY className={styles.content}>
        <Text className={styles.contentTitle}>{activeCat?.name || '全部商品'}</Text>
        <View className={styles.productGrid}>
          {products.map(item => (
            <View key={item.id} className={styles.productCard}>
              <ProductCard product={item} />
            </View>
          ))}
        </View>
      </ScrollView>
    </View>
  )
}

export default CategoryPage
