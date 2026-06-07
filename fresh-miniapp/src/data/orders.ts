import type { OrderItem, DeliveryTrack } from '@/types'
import { productList } from './products'

const orderStatusMap = [
  { status: 1, text: '待支付' },
  { status: 2, text: '待发货' },
  { status: 3, text: '配送中' },
  { status: 4, text: '已完成' },
  { status: 5, text: '已取消' }
]

function generateOrders(): OrderItem[] {
  const orders: OrderItem[] = []
  
  for (let i = 0; i < 6; i++) {
    const status = orderStatusMap[i % 5]
    const items = []
    const product1 = productList[i % productList.length]
    const product2 = productList[(i + 2) % productList.length]
    const sku1 = product1.skuList[0]
    const sku2 = product2.skuList[0]
    
    items.push(
      {
        skuId: sku1.id,
        productId: product1.id,
        skuName: product1.name + ' ' + sku1.spec,
        image: product1.mainImage,
        price: sku1.price,
        quantity: 2
      },
      {
        skuId: sku2.id,
        productId: product2.id,
        skuName: product2.name + ' ' + sku2.spec,
        image: product2.mainImage,
        price: sku2.price,
        quantity: 1
      }
    )
    
    const totalAmount = Math.round((sku1.price * 2 + sku2.price) * 100) / 100
    
    orders.push({
      id: i + 1,
      orderNo: 'FR' + Date.now() + String(i).padStart(4, '0'),
      totalAmount,
      payAmount: Math.round(totalAmount * 95) / 100,
      status: status.status,
      statusText: status.text,
      createTime: '2024-06-0' + (i + 1) + ' 10:' + (20 + i * 8) + ':00',
      receiverName: '张三',
      receiverPhone: '138****1234',
      receiverAddress: '北京市朝阳区xxx街道xxx小区1号楼1001室',
      warehouses: [
        {
          warehouseId: 1,
          warehouseName: '朝阳前置仓',
          status: status.status,
          items: items.slice(0, 1)
        },
        {
          warehouseId: 2,
          warehouseName: '海淀前置仓',
          status: status.status,
          items: items.slice(1)
        }
      ],
      totalCount: 3
    })
  }
  
  return orders
}

export const orderList: OrderItem[] = generateOrders()

export function getOrderById(id: number): OrderItem | undefined {
  return orderList.find(o => o.id === id)
}

export function getOrdersByStatus(status: number): OrderItem[] {
  if (status === 0) return orderList
  return orderList.filter(o => o.status === status)
}

export const deliveryTrackList: DeliveryTrack[] = [
  { time: '2024-06-07 14:30:00', status: '已送达', description: '商品已送达，请您查收' },
  { time: '2024-06-07 14:00:00', status: '配送中', description: '骑手正在配送中，请保持电话畅通' },
  { time: '2024-06-07 13:30:00', status: '骑手取货', description: '骑手已到店取货' },
  { time: '2024-06-07 13:15:00', status: '备货中', description: '仓库正在为您备货' },
  { time: '2024-06-07 13:00:00', status: '订单已支付', description: '支付成功，订单已提交' }
]
