import request from '@/utils/request'

export function getCategoryList(params) {
  return request({
    url: '/product/category/list',
    method: 'get',
    params
  })
}

export function getCategoryTree() {
  return request({
    url: '/product/category/tree',
    method: 'get'
  })
}

export function addCategory(data) {
  return request({
    url: '/product/category',
    method: 'post',
    data
  })
}

export function updateCategory(id, data) {
  return request({
    url: `/product/category/${id}`,
    method: 'put',
    data
  })
}

export function deleteCategory(id) {
  return request({
    url: `/product/category/${id}`,
    method: 'delete'
  })
}

export function getProductList(params) {
  return request({
    url: '/product/list',
    method: 'get',
    params
  })
}

export function getProductDetail(id) {
  return request({
    url: `/product/${id}`,
    method: 'get'
  })
}

export function addProduct(data) {
  return request({
    url: '/product',
    method: 'post',
    data
  })
}

export function updateProduct(id, data) {
  return request({
    url: `/product/${id}`,
    method: 'put',
    data
  })
}

export function deleteProduct(id) {
  return request({
    url: `/product/${id}`,
    method: 'delete'
  })
}

export function getSkuList(params) {
  return request({
    url: '/product/sku/list',
    method: 'get',
    params
  })
}

export function addSku(data) {
  return request({
    url: '/product/sku',
    method: 'post',
    data
  })
}

export function updateSku(id, data) {
  return request({
    url: `/product/sku/${id}`,
    method: 'put',
    data
  })
}

export function deleteSku(id) {
  return request({
    url: `/product/sku/${id}`,
    method: 'delete'
  })
}
