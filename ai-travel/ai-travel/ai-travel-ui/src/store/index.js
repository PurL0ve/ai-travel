import { defineStore } from 'pinia'

export const useStore = defineStore('main', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : null,
    plan: null,
    level: 'standard'
  }),

  getters: {
    isAdmin() {
      return this.user?.role === 'ADMIN'
    }
  },

  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },

    setUser(user) {
      this.user = user
      localStorage.setItem('user', JSON.stringify(user))
    },

    setPlan(plan) {
      this.plan = plan
    },

    setLevel(level) {
      this.level = level
    },

    clearToken() {
      this.token = ''
      this.user = null
      this.plan = null
      this.level = 'standard'
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },

    logout() {
      this.clearToken()
    }
  }
})