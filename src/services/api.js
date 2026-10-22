// Cliente HTTP único para o back-end (Java/Spring Boot). Os services por
// recurso (consultores.js, clientes.js) importam este arquivo em vez de
// chamar axios/fetch direto — ver STATUS_INTEGRACAO_BACKEND.md.
//
// O upload de planilha em lote (src/stores/validacaoPlanilha.js) ainda não
// tem endpoint no backend, então continua só no front-end por enquanto.

import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' },
})


api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default api
