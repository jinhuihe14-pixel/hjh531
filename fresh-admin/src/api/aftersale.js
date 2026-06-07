import request from '@/utils/request'

export function getAftersaleList(params) {
  return request({
    url: '/aftersale/list',
    method: 'get',
    params
  })
}

export function getAftersaleDetail(id) {
  return request({
    url: `/aftersale/${id}`,
    method: 'get'
  })
}

export function auditAftersale(id, data) {
  return request({
    url: `/aftersale/${id}/audit`,
    method: 'post',
    data
  })
}

export function updateAftersaleStatus(id, status) {
  return request({
    url: `/aftersale/${id}/status`,
    method: 'put',
    data: { status }
  })
}
