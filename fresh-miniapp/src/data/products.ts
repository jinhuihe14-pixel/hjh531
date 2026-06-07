import type { ProductItem } from '@/types'

const foodImageIds = [292, 312, 326, 401, 431, 570, 580, 625, 835, 1080, 225, 230]

const productNames = [
  { name: '有机番茄', subtitle: '自然熟 酸甜可口', categoryId: 1, storageType: 'cold' as const },
  { name: '新鲜黄瓜', subtitle: '清脆爽口 产地直供', categoryId: 1, storageType: 'cold' as const },
  { name: '红富士苹果', subtitle: '脆甜多汁 5斤装', categoryId: 2, storageType: 'normal' as const },
  { name: '海南香蕉', subtitle: '自然熟 香甜软糯', categoryId: 2, storageType: 'normal' as const },
  { name: '土猪五花肉', subtitle: '散养土猪 鲜嫩肥美', categoryId: 3, storageType: 'cold' as const },
  { name: '农家土鸡蛋', subtitle: '散养土鸡蛋 30枚', categoryId: 3, storageType: 'normal' as const },
  { name: '厄瓜多尔白虾', subtitle: '鲜活冷冻 个大饱满', categoryId: 4, storageType: 'frozen' as const },
  { name: '挪威三文鱼', subtitle: '冰鲜三文鱼 刺身级', categoryId: 4, storageType: 'cold' as const },
  { name: '伊利纯牛奶', subtitle: '全脂纯牛奶 250ml*12', categoryId: 5, storageType: 'normal' as const },
  { name: '手工水饺', subtitle: '猪肉白菜馅 500g', categoryId: 8, storageType: 'frozen' as const },
  { name: '五常大米', subtitle: '东北五常稻花香 5kg', categoryId: 7, storageType: 'normal' as const },
  { name: '金龙鱼大豆油', subtitle: '非转基因 5L装', categoryId: 7, storageType: 'normal' as const }
]

function getPrice(basePrice: number) {
  return {
    price: basePrice,
    originalPrice: Math.round(basePrice * 1.3 * 100) / 100
  }
}

function generateProducts(): ProductItem[] {
  return productNames.map((item, index) => {
    const imgId = foodImageIds[index % foodImageIds.length]
    const priceInfo = getPrice(5.9 + index * 3.2)
    
    return {
      id: index + 1,
      categoryId: item.categoryId,
      name: item.name,
      subtitle: item.subtitle,
      mainImage: `https://picsum.photos/id/${imgId}/300/300`,
      images: [
        `https://picsum.photos/id/${imgId}/750/750`,
        `https://picsum.photos/id/${(imgId + 10) % 100}/750/750`
      ],
      storageType: item.storageType,
      sales: 128 + index * 56,
      minPrice: priceInfo.price,
      skuList: [
        {
          id: index * 10 + 1,
          productId: index + 1,
          skuName: '精选 500g',
          price: priceInfo.price,
          originalPrice: priceInfo.originalPrice,
          unit: '份',
          spec: '500g',
          weight: 500,
          stock: 100
        },
        {
          id: index * 10 + 2,
          productId: index + 1,
          skuName: '特惠 1kg',
          price: Math.round(priceInfo.price * 1.8 * 100) / 100,
          originalPrice: Math.round(priceInfo.originalPrice * 1.8 * 100) / 100,
          unit: '份',
          spec: '1kg',
          weight: 1000,
          stock: 50
        }
      ]
    }
  })
}

export const productList: ProductItem[] = generateProducts()

export function getProductById(id: number): ProductItem | undefined {
  return productList.find(p => p.id === id)
}

export function getProductsByCategory(categoryId: number): ProductItem[] {
  if (categoryId === 0) return productList
  return productList.filter(p => p.categoryId === categoryId)
}
