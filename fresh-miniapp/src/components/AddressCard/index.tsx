import React from 'react'
import { View, Text } from '@tarojs/components'
import classnames from 'classnames'
import styles from './index.module.scss'
import type { UserAddress } from '@/types'

interface Props {
  address: UserAddress
  selected?: boolean
  showEdit?: boolean
  onClick?: () => void
  onEdit?: () => void
  onDelete?: () => void
  onSetDefault?: () => void
}

const AddressCard: React.FC<Props> = ({
  address,
  selected = false,
  showEdit = false,
  onClick,
  onEdit,
  onDelete,
  onSetDefault
}) => {
  return (
    <View className={classnames(styles.card, selected && styles.selected)} onClick={onClick}>
      <View className={styles.header}>
        <Text className={styles.receiver}>{address.receiver}</Text>
        <Text className={styles.phone}>{address.phone}</Text>
        {address.isDefault && (
          <View className={styles.defaultTag}>
            <Text className={styles.defaultText}>默认</Text>
          </View>
        )}
      </View>
      <Text className={styles.address}>{address.fullAddress}</Text>
      {showEdit && (
        <View className={styles.actions}>
          {!address.isDefault && (
            <View className={styles.actionBtn} onClick={(e) => { e.stopPropagation(); onSetDefault?.() }}>
              <Text className={styles.actionText}>设为默认</Text>
            </View>
          )}
          <View className={styles.actionBtn} onClick={(e) => { e.stopPropagation(); onEdit?.() }}>
            <Text className={styles.actionText}>编辑</Text>
          </View>
          <View className={styles.actionBtn} onClick={(e) => { e.stopPropagation(); onDelete?.() }}>
            <Text className={styles.actionText}>删除</Text>
          </View>
        </View>
      )}
    </View>
  )
}

export default AddressCard
