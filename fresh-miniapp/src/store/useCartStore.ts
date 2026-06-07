import { create } from 'zustand'
import type { CartItem } from '@/types'
import { persist, createJSONStorage } from 'zustand/middleware'

interface CartStore {
  items: CartItem[]
  addItem: (item: Omit<CartItem, 'id' | 'selected'>) => void
  removeItem: (skuId: number) => void
  updateQuantity: (skuId: number, quantity: number) => void
  toggleSelect: (skuId: number) => void
  toggleSelectAll: (selected: boolean) => void
  clearCart: () => void
  getSelectedItems: () => CartItem[]
  getTotalCount: () => number
  getTotalPrice: () => number
}

export const useCartStore = create<CartStore>()(
  persist(
    (set, get) => ({
      items: [],

      addItem: (item) => {
        const { items } = get()
        const existIndex = items.findIndex(i => i.skuId === item.skuId)
        
        if (existIndex > -1) {
          const newItems = [...items]
          newItems[existIndex].quantity += item.quantity
          set({ items: newItems })
        } else {
          set({
            items: [...items, {
              ...item,
              id: Date.now(),
              selected: true
            }]
          })
        }
      },

      removeItem: (skuId) => {
        set(state => ({
          items: state.items.filter(i => i.skuId !== skuId)
        }))
      },

      updateQuantity: (skuId, quantity) => {
        set(state => ({
          items: state.items.map(item =>
            item.skuId === skuId ? { ...item, quantity } : item
          )
        }))
      },

      toggleSelect: (skuId) => {
        set(state => ({
          items: state.items.map(item =>
            item.skuId === skuId ? { ...item, selected: !item.selected } : item
          )
        }))
      },

      toggleSelectAll: (selected) => {
        set(state => ({
          items: state.items.map(item => ({ ...item, selected }))
        }))
      },

      clearCart: () => set({ items: [] }),

      getSelectedItems: () => {
        return get().items.filter(i => i.selected)
      },

      getTotalCount: () => {
        return get().items.reduce((sum, item) => sum + item.quantity, 0)
      },

      getTotalPrice: () => {
        return get().items
          .filter(i => i.selected)
          .reduce((sum, item) => sum + item.price * item.quantity, 0)
      }
    }),
    {
      name: 'fresh-cart',
      storage: createJSONStorage(() => Taro.getStorageSync ? {
        getItem: (name) => {
          try { return Taro.getStorageSync(name) || null } catch { return null }
        },
        setItem: (name, value) => {
          try { Taro.setStorageSync(name, value) } catch {}
        },
        removeItem: (name) => {
          try { Taro.removeStorageSync(name) } catch {}
        }
      } : undefined)
    }
  )
)
