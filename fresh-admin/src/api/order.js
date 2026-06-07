import request from '@/utils/request'

export function getOrderList(params) {
  return request({
    url: '/order/list',
    method: 'get',
    params
  })
}

export function getOrderDetail(id) {
  return request({
    url: `/order/${id}`,
    method: 'get'
  })
}

export function updateOrderStatus(id, status) {
  return request({
    url: `/order/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export function cancelOrder(id, reason) {
  return request({
    url: `/order/${id}/cancel`,
    method: 'post',
    data: { reason }
  })
}
