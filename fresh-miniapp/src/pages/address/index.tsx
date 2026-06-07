import React, { useState, useEffect } from 'react'
import { View, Text, Button, Input, Switch } from '@tarojs/components'
import Taro from '@tarojs/taro'
import classnames from 'classnames'
import styles from './index.module.scss'
import AddressCard from '@/components/AddressCard'
import { useUserStore } from '@/store/useUserStore'
import type { UserAddress } from '@/types'

const AddressPage: React.FC = () => {
  const { addressList, addAddress, updateAddress, deleteAddress, setAddressList } = useUserStore()
  const [showModal, setShowModal] = useState(false)
  const [editingAddress, setEditingAddress] = useState<UserAddress | null>(null)
  const [form, setForm] = useState({
    receiver: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: false
  })

  const handleAdd = () => {
    setEditingAddress(null)
    setForm({
      receiver: '',
      phone: '',
      province: '',
      city: '',
      district: '',
      detail: '',
      isDefault: false
    })
    setShowModal(true)
  }

  const handleEdit = (address: UserAddress) => {
    setEditingAddress(address)
    setForm({
      receiver: address.receiver,
      phone: address.phone,
      province: address.province,
      city: address.city,
      district: address.district,
      detail: address.detail,
      isDefault: address.isDefault
    })
    setShowModal(true)
  }

  const handleDelete = (id: number) => {
    Taro.showModal({
      title: '提示',
      content: '确定要删除该地址吗？',
      success: (res) => {
        if (res.confirm) {
          deleteAddress(id)
          Taro.showToast({ title: '删除成功', icon: 'success' })
        }
      }
    })
  }

  const handleSetDefault = (address: UserAddress) => {
    updateAddress({ ...address, isDefault: true })
    Taro.showToast({ title: '设置成功', icon: 'success' })
  }

  const handleSave = () => {
    if (!form.receiver.trim()) {
      Taro.showToast({ title: '请输入收货人', icon: 'none' })
      return
    }
    if (!form.phone.trim()) {
      Taro.showToast({ title: '请输入手机号', icon: 'none' })
      return
    }
    if (!form.detail.trim()) {
      Taro.showToast({ title: '请输入详细地址', icon: 'none' })
      return
    }

    const fullAddress = `${form.province || '北京市'}${form.city || '北京市'}${form.district || '朝阳区'}${form.detail}`

    if (editingAddress) {
      updateAddress({
        ...editingAddress,
        ...form,
        fullAddress
      })
    } else {
      addAddress({
        id: Date.now(),
        ...form,
        fullAddress,
        longitude: 116.407,
        latitude: 39.904
      })
    }

    setShowModal(false)
    Taro.showToast({ title: '保存成功', icon: 'success' })
  }

  const handleClose = () => {
    setShowModal(false)
  }

  const handleInputChange = (field: string, value: string) => {
    setForm(prev => ({ ...prev, [field]: value }))
  }

  return (
    <View className={styles.page}>
      {addressList.length > 0 ? (
        <View className={styles.addressList}>
          {addressList.map(addr => (
            <AddressCard
              key={addr.id}
              address={addr}
              showEdit
              onEdit={() => handleEdit(addr)}
              onDelete={() => handleDelete(addr.id)}
              onSetDefault={() => handleSetDefault(addr)}
            />
          ))}
        </View>
      ) : (
        <View className={styles.empty}>
          <Text className={styles.emptyIcon}>📍</Text>
          <Text className={styles.emptyText}>暂无收货地址</Text>
        </View>
      )}

      <View className={styles.addBtn}>
        <Button className={styles.addBtnInner} onClick={handleAdd}>
          + 新增收货地址
        </Button>
      </View>

      {showModal && (
        <View className={styles.modalMask} onClick={handleClose}>
          <View className={styles.modal} onClick={e => e.stopPropagation()}>
            <View className={styles.modalHeader}>
              <Text className={styles.modalTitle}>
                {editingAddress ? '编辑地址' : '新增地址'}
              </Text>
              <Text className={styles.modalClose} onClick={handleClose}>✕</Text>
            </View>
            <View className={styles.modalBody}>
              <View className={styles.formItem}>
                <Text className={styles.formLabel}>收货人</Text>
                <Input
                  className={styles.formInput}
                  placeholder='请输入收货人姓名'
                  value={form.receiver}
                  onInput={(e) => handleInputChange('receiver', e.detail.value)}
                />
              </View>
              <View className={styles.formItem}>
                <Text className={styles.formLabel}>手机号</Text>
                <Input
                  className={styles.formInput}
                  type='number'
                  placeholder='请输入手机号'
                  value={form.phone}
                  onInput={(e) => handleInputChange('phone', e.detail.value)}
                />
              </View>
              <View className={styles.formRow}>
                <View className={styles.formRowItem}>
                  <Text className={styles.formLabel}>省份</Text>
                  <Input
                    className={styles.formInput}
                    placeholder='省份'
                    value={form.province}
                    onInput={(e) => handleInputChange('province', e.detail.value)}
                  />
                </View>
                <View className={styles.formRowItem}>
                  <Text className={styles.formLabel}>城市</Text>
                  <Input
                    className={styles.formInput}
                    placeholder='城市'
                    value={form.city}
                    onInput={(e) => handleInputChange('city', e.detail.value)}
                  />
                </View>
                <View className={styles.formRowItem}>
                  <Text className={styles.formLabel}>区县</Text>
                  <Input
                    className={styles.formInput}
                    placeholder='区县'
                    value={form.district}
                    onInput={(e) => handleInputChange('district', e.detail.value)}
                  />
                </View>
              </View>
              <View className={styles.formItem}>
                <Text className={styles.formLabel}>详细地址</Text>
                <Input
                  className={styles.formInput}
                  placeholder='请输入详细地址'
                  value={form.detail}
                  onInput={(e) => handleInputChange('detail', e.detail.value)}
                />
              </View>
              <View className={styles.switchRow}>
                <Text className={styles.switchLabel}>设为默认地址</Text>
                <Switch
                  checked={form.isDefault}
                  onChange={(e) => handleInputChange('isDefault', e.detail.value as any)}
                  color='#07C160'
                />
              </View>
            </View>
            <View className={styles.modalFooter}>
              <Button className={styles.saveBtn} onClick={handleSave}>
                保存
              </Button>
            </View>
          </View>
        </View>
      )}
    </View>
  )
}

export default AddressPage
