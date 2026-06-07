import { defineStore } from 'pinia'
import { login, logout, getInfo } from '@/api/auth'
import { getToken, setToken, clearAuth, setUser, getUser } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: getUser() || {}
  }),
  actions: {
    async login(loginForm) {
      const res = await login(loginForm)
      this.token = res.data.token
      this.userInfo = res.data.user
      setToken(res.data.token)
      setUser(res.data.user)
      return res
    },
    async getInfo() {
      const res = await getInfo()
      this.userInfo = res.data
      setUser(res.data)
      return res
    },
    async logout() {
      try {
        await logout()
      } finally {
        this.token = ''
        this.userInfo = {}
        clearAuth()
      }
    },
    resetToken() {
      this.token = ''
      this.userInfo = {}
      clearAuth()
    }
  }
})
