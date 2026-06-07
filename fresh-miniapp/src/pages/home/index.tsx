import React, { useState, useRef, useEffect } from 'react'
import { View, Text, Image, Input, Swiper, SwiperItem, ScrollView } from '@tarojs/components'
import Taro from '@tarojs/taro'
import styles from './index.module.scss'
import ProductCard from '@/components/ProductCard'
import { bannerList } from '@/data/banners'
import { categoryList } from '@/data/categories'
import { productList } from '@/data/products'
import type { ProductItem } from '@/types'

const HomePage: React.FC = () => {
  const [searchText, setSearchText] = useState('')
  const [recommendList, setRecommendList] = useState<ProductItem[]>([])
  const [flashSaleList, setFlashSaleList] = useState<ProductItem[]>([])
  const [countdown, setCountdown] = useState({ h: '02', m: '30', s: '45' })

  useEffect(() => {
    setRecommendList(productList.slice(0, 8))
    setFlashSaleList(productList.slice(0, 5))
    startCountdown()
  }, [])

  const startCountdown = () => {
    let totalSeconds = 2 * 3600 + 30 * 60 + 45
    const timer = setInterval(() => {
      totalSeconds--
      if (totalSeconds <= 0) {
        totalSeconds = 2 * 3600 + 30 * 60 + 45
      }
      const h = Math.floor(totalSeconds / 3600).toString().padStart(2, '0')
      const m = Math.floor((totalSeconds % 3600) / 60).toString().padStart(2, '0')
      const s = (totalSeconds % 60).toString().padStart(2, '0')
      setCountdown({ h, m, s })
    }, 1000)
    return () => clearInterval(timer)
  }

  const handleCategoryClick = (id: number) => {
    Taro.switchTab({
      url: '/pages/category/index'
    })
  }

  const handleSearch = () => {
    console.log('[Home] search:', searchText)
    Taro.showToast({ title: '搜索功能开发中', icon: 'none' })
  }

  const handleRefresh = () => {
    setTimeout(() => {
      Taro.stopPullDownRefresh()
      Taro.showToast({ title: '刷新成功', icon: 'success' })
    }, 1000)
  }

  const handleMoreRecommend = () => {
    Taro.switchTab({ url: '/pages/category/index' })
  }

  return (
    <View className={styles.page}>
      <View className={styles.header}>
        <View className={styles.location}>
          <Text className={styles.locationText}>📍 北京市朝阳区</Text>
          <Text className={styles.locationArrow}>▼</Text>
        </View>
        <View className={styles.searchBar}>
          <Text className={styles.searchIcon}>🔍</Text>
          <Input
            className={styles.searchInput}
            placeholder='搜索蔬菜水果、肉禽蛋奶'
            value={searchText}
            onInput={(e) => setSearchText(e.detail.value)}
            onConfirm={handleSearch}
          />
        </View>
      </View>

      <View className={styles.bannerSection}>
        <Swiper
          className={styles.banner}
          autoplay
          circular
          indicatorDots
          indicatorColor='rgba(255,255,255,0.5)'
          indicatorActiveColor='#fff'
        >
          {bannerList.map(item => (
            <SwiperItem key={item.id}>
              <Image
                className={styles.bannerImage}
                src={item.image}
                mode='aspectFill'
              />
            </SwiperItem>
          ))}
        </Swiper>
      </View>

      <View className={styles.categorySection}>
        <View className={styles.categoryGrid}>
          {categoryList.map(item => (
            <View
              key={item.id}
              className={styles.categoryItem}
              onClick={() => handleCategoryClick(item.id)}
            >
              <View className={styles.categoryIcon}>{item.icon}</View>
              <Text className={styles.categoryName}>{item.name}</Text>
            </View>
          ))}
        </View>
      </View>

      <View className={styles.flashSaleSection}>
        <View className={styles.flashSaleHeader}>
          <Text className={styles.flashSaleTitle}>⚡ 限时秒杀</Text>
          <View className={styles.flashSaleTimer}>
            <View className={styles.timeBox}><Text className={styles.timeText}>{countdown.h}</Text></View>
            <Text className={styles.timeText}>:</Text>
            <View className={styles.timeBox}><Text className={styles.timeText}>{countdown.m}</Text></View>
            <Text className={styles.timeText}>:</Text>
            <View className={styles.timeBox}><Text className={styles.timeText}>{countdown.s}</Text></View>
          </View>
        </View>
        <View className={styles.flashSaleList}>
          <ScrollView scrollX className={styles.flashSaleScroll}>
            {flashSaleList.map(item => (
              <View key={item.id} className={styles.flashSaleItem}>
                <Image
                  className={styles.flashSaleImage}
                  src={item.mainImage}
                  mode='aspectFill'
                />
                <View className={styles.flashSaleInfo}>
                  <Text className={styles.flashSaleName}>{item.name}</Text>
                  <View>
                    <Text className={styles.flashSalePrice}>¥{item.minPrice.toFixed(2)}</Text>
                    <Text className={styles.flashSaleOriginal}>¥{(item.minPrice * 1.5).toFixed(2)}</Text>
                  </View>
                </View>
              </View>
            ))}
          </ScrollView>
        </View>
      </View>

      <View className={styles.sectionTitle}>
        <Text className={styles.sectionTitleText}>🥬 精选推荐</Text>
        <Text className={styles.sectionMore} onClick={handleMoreRecommend}>查看更多 ›</Text>
      </View>

      <View className={styles.productSection}>
        <View className={styles.productGrid}>
          {recommendList.map(item => (
            <View key={item.id} className={styles.productCard}>
              <ProductCard product={item} />
            </View>
          ))}
        </View>
      </View>
    </View>
  )
}

export default HomePage
