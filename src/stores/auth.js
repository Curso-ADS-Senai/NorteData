import { defineStore } from 'pinia'
import api from '../services/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token'),
    usuario: JSON.parse(localStorage.getItem('usuario') || 'null'),
  }),

  actions: {
    async login(email, senha) {
      const { data } = await api.post('/api/auth/login', { email, senha })

      this.token = data.token
      this.usuario = data.usuario

      localStorage.setItem('token', data.token)
      localStorage.setItem('usuario', JSON.stringify(data.usuario))
    },

    async cadastrar(nome, email, senha) {
      const { data } = await api.post('/api/auth/register', { nome, email, senha })
      return data
    },

    logout() {
      this.token = null
      this.usuario = null

      localStorage.removeItem('token')
      localStorage.removeItem('usuario')
    },
  },
})
