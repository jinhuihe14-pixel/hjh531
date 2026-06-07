export interface BannerItem {
  id: number
  image: string
  title: string
  linkType?: string
  linkValue?: string
}

export interface CategoryItem {
  id: number
  name: string
  icon: string
  children?: CategoryItem[]
}

export interface ProductSku {
  id: number
  productId: number
  skuName: string
  price: number
  originalPrice: number
  unit: string
  spec: string
  weight: number
  stock: number
}

export interface ProductItem {
  id: number
  categoryId: number
  name: string
  subtitle: string
  mainImage: string
  images: string[]
  storageType: 'normal' | 'cold' | 'frozen'
  sales: number
  skuList: ProductSku[]
  minPrice: number
}

export interface CartItem {
  id: number
  skuId: number
  productId: number
  name: string
  skuName: string
  image: string
  price: number
  quantity: number
  selected: boolean
  warehouseId: number
  warehouseName: string
}

export interface OrderSkuItem {
  skuId: number
  productId: number
  skuName: string
  image: string
  price: number
  quantity: number
}

export interface OrderWarehouse {
  warehouseId: number
  warehouseName: string
  status: number
  items: OrderSkuItem[]
}

export interface OrderItem {
  id: number
  orderNo: string
  totalAmount: number
  payAmount: number
  status: number
  statusText: string
  createTime: string
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  warehouses: OrderWarehouse[]
  totalCount: number
}

export interface UserAddress {
  id: number
  receiver: string
  phone: string
  province: string
  city: string
  district: string
  detail: string
  fullAddress: string
  longitude: number
  latitude: number
  isDefault: boolean
}

export interface UserInfo {
  id: number
  username: string
  nickname: string
  phone: string
  avatar: string
  gender: number
  integral: number
  level: number
}

export interface DeliveryTrack {
  time: string
  status: string
  description: string
}
