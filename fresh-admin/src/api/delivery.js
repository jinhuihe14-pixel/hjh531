import request from '@/utils/request'

export function getRiderList(params) {
  return request({
    url: '/delivery/rider/list',
    method: 'get',
    params
  })
}

export function getRiderDetail(id) {
  return request({
    url: `/delivery/rider/${id}`,
    method: 'get'
  })
}

export function addRider(data) {
  return request({
    url: '/delivery/rider',
    method: 'post',
    data
  })
}

export function updateRider(id, data) {
  return request({
    url: `/delivery/rider/${id}`,
    method: 'put',
    data
  })
}

export function deleteRider(id) {
  return request({
    url: `/delivery/rider/${id}`,
    method: 'delete'
  })
}

export function getDeliveryList(params) {
  return request({
    url: '/delivery/list',
    method: 'get',
    params
  })
}

export function assignDelivery(id, riderId) {
  return request({
    url: `/delivery/${id}/assign`,
    method: 'post',
    data: { riderId }
  })
}

export function updateDeliveryStatus(id, status) {
  return request({
    url: `/delivery/${id}/status`,
    method: 'put',
    data: { status }
  })
}
