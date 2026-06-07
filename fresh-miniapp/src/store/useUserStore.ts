import { create } from 'zustand'
import type { UserInfo, UserAddress } from '@/types'
import { persist, createJSONStorage } from 'zustand/middleware'

interface UserStore {
  userInfo: UserInfo | null
  token: string | null
  addressList: UserAddress[]
  login: (token: string, userInfo: UserInfo) => void
  logout: () => void
  updateUserInfo: (info: Partial<UserInfo>) => void
  setAddressList: (list: UserAddress[]) => void
  addAddress: (address: UserAddress) => void
  updateAddress: (address: UserAddress) => void
  deleteAddress: (id: number) => void
  getDefaultAddress: () => UserAddress | undefined
}

export const useUserStore = create<UserStore>()(
  persist(
    (set, get) => ({
      userInfo: null,
      token: null,
      addressList: [
        {
          id: 1,
          receiver: '张三',
          phone: '13812345678',
          province: '北京市',
          city: '北京市',
          district: '朝阳区',
          detail: '望京SOHO T1 1001室',
          fullAddress: '北京市朝阳区望京SOHO T1 1001室',
          longitude: 116.476,
          latitude: 39.996,
          isDefault: true
        },
        {
          id: 2,
          receiver: '李四',
          phone: '13987654321',
          province: '北京市',
          city: '北京市',
          district: '海淀区',
          detail: '中关村软件园二期 5号楼',
          fullAddress: '北京市海淀区中关村软件园二期 5号楼',
          longitude: 116.308,
          latitude: 40.039,
          isDefault: false
        }
      ],

      login: (token, userInfo) => {
        set({ token, userInfo })
      },

      logout: () => {
        set({ token: null, userInfo: null })
      },

      updateUserInfo: (info) => {
        set(state => ({
          userInfo: state.userInfo ? { ...state.userInfo, ...info } : null
        }))
      },

      setAddressList: (list) => set({ addressList: list }),

      addAddress: (address) => {
        set(state => {
          let list = [...state.addressList]
          if (address.isDefault) {
            list = list.map(a => ({ ...a, isDefault: false }))
          }
          if (list.length === 0) {
            address.isDefault = true
          }
          return { addressList: [...list, { ...address, id: Date.now() }] }
        })
      },

      updateAddress: (address) => {
        set(state => {
          let list = state.addressList.map(a => {
            if (a.id === address.id) return address
            if (address.isDefault) return { ...a, isDefault: false }
            return a
          })
          return { addressList: list }
        })
      },

      deleteAddress: (id) => {
        set(state => ({
          addressList: state.addressList.filter(a => a.id !== id)
        }))
      },

      getDefaultAddress: () => {
        const list = get().addressList
        return list.find(a => a.isDefault) || list[0]
      }
    }),
    {
      name: 'fresh-user',
      storage: createJSONStorage(() => {
        try {
          return {
            getItem: (name) => Taro.getStorageSync(name) || null,
            setItem: (name, value) => Taro.setStorageSync(name, value),
            removeItem: (name) => Taro.removeStorageSync(name)
          }
        } catch {
          return undefined
        }
      })
    }
  )
)
