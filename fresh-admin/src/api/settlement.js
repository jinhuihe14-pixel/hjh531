import request from '@/utils/request'

export function getSupplierSettlementList(params) {
  return request({
    url: '/settlement/supplier/list',
    method: 'get',
    params
  })
}

export function settleSupplier(id) {
  return request({
    url: `/settlement/supplier/${id}/settle`,
    method: 'post'
  })
}

export function getPickerSettlementList(params) {
  return request({
    url: '/settlement/picker/list',
    method: 'get',
    params
  })
}

export function settlePicker(id) {
  return request({
    url: `/settlement/picker/${id}/settle`,
    method: 'post'
  })
}

export function getRiderSettlementList(params) {
  return request({
    url: '/settlement/rider/list',
    method: 'get',
    params
  })
}

export function settleRider(id) {
  return request({
    url: `/settlement/rider/${id}/settle`,
    method: 'post'
  })
}

export function getLossRecordList(params) {
  return request({
    url: '/settlement/loss/list',
    method: 'get',
    params
  })
}

export function addLossRecord(data) {
  return request({
    url: '/settlement/loss',
    method: 'post',
    data
  })
}

export function updateLossRecord(id, data) {
  return request({
    url: `/settlement/loss/${id}`,
    method: 'put',
    data
  })
}

export function deleteLossRecord(id) {
  return request({
    url: `/settlement/loss/${id}`,
    method: 'delete'
  })
}
