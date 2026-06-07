import request from '@/utils/request'

export function getWarehouseList(params) {
  return request({
    url: '/warehouse/list',
    method: 'get',
    params
  })
}

export function getWarehouseDetail(id) {
  return request({
    url: `/warehouse/${id}`,
    method: 'get'
  })
}

export function addWarehouse(data) {
  return request({
    url: '/warehouse',
    method: 'post',
    data
  })
}

export function updateWarehouse(id, data) {
  return request({
    url: `/warehouse/${id}`,
    method: 'put',
    data
  })
}

export function deleteWarehouse(id) {
  return request({
    url: `/warehouse/${id}`,
    method: 'delete'
  })
}

export function getStockList(params) {
  return request({
    url: '/warehouse/stock/list',
    method: 'get',
    params
  })
}

export function updateStock(id, data) {
  return request({
    url: `/warehouse/stock/${id}`,
    method: 'put',
    data
  })
}

export function getTransferList(params) {
  return request({
    url: '/warehouse/transfer/list',
    method: 'get',
    params
  })
}

export function addTransfer(data) {
  return request({
    url: '/warehouse/transfer',
    method: 'post',
    data
  })
}

export function updateTransferStatus(id, status) {
  return request({
    url: `/warehouse/transfer/${id}/status`,
    method: 'put',
    data: { status }
  })
}
